package com.ricardorainha.mustache.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.databinding.ActivityMainBinding;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.receivers.ConnectionChangeReceiver;
import com.ricardorainha.mustache.utils.SharedPrefUtils;
import com.ricardorainha.mustache.view.fragments.BarbershopsFragment;
import com.ricardorainha.mustache.view.fragments.FavoritesFragment;
import com.ricardorainha.mustache.view.fragments.MyScheduleFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private MenuItem profileItem;
    private ConnectionChangeReceiver connectionChangeReceiver;
    private boolean decidedFirstFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        connectionChangeReceiver = new ConnectionChangeReceiver();

        configureObservables();
        configureFields();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AuthManager.getInstance().needToLogin()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else {
            viewModel.requestUserInfo();
            registerConnectionReceiver();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterConnectionReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.profileItem = menu.findItem(R.id.menu_profile);
        configureProfilePicture();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            startProfileActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureObservables() {
        viewModel.getUserInfoReceived().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.getUserInfoReceived().get()) {
                    if (!SharedPrefUtils.hasCompletedProfile(MainActivity.this)) {
                        startProfileActivity();
                    }

                    configureProfilePicture();
                    Session.getInstance().updateOnlineStatus(MainActivity.this);
                }
            }
        });

        Session.getInstance().getOnlineStatus().observe(this, connected -> {
            binding.navigationBar.getMenu().getItem(0).setEnabled(connected);

            if (!decidedFirstFragment) {
                decidedFirstFragment = true;
                binding.navigationBar.getMenu().getItem(connected ? 0 : 1).setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, connected ? new BarbershopsFragment() : new FavoritesFragment()).commit();
            }
            else if (!connected && binding.navigationBar.getMenu().getItem(0).isChecked()) {
                binding.navigationBar.getMenu().getItem(1).setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavoritesFragment()).commit();
            }

            if (!connected) {
                Snackbar.make(binding.getRoot(), getString(R.string.app_now_in_offline_mode), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void configureFields() {
        binding.navigationBar.setOnNavigationItemSelectedListener(navigationListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener = menuItem -> {

        Fragment activeFragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_barbershops:
                activeFragment = new BarbershopsFragment();
                break;
            case R.id.navigation_favorites:
                activeFragment = new FavoritesFragment();
                break;
            case R.id.navigation_my_schedule:
                activeFragment = new MyScheduleFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, activeFragment).commit();

        return true;
    };

    private void configureProfilePicture() {
        if (profileItem != null) {
            if (Session.getInstance().getUser().getValue() != null) {
                Session.getInstance().getUser().getValue().getPhotoReference().getDownloadUrl()
                        .addOnSuccessListener(uri -> Glide.with(getApplicationContext()).load(uri.toString()).diskCacheStrategy(DiskCacheStrategy.ALL).apply(RequestOptions.circleCropTransform()).into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                profileItem.setIcon(resource);
                            }
                        }))
                        .addOnFailureListener(e -> profileItem.setIcon(R.drawable.ic_account_white_36));
            }
        }
    }

    private void startProfileActivity() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void registerConnectionReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionChangeReceiver, intentFilter);
    }

    private void unregisterConnectionReceiver() {
        unregisterReceiver(connectionChangeReceiver);
    }
}
