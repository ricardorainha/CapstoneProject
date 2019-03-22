package com.ricardorainha.mustache.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.databinding.ActivityMainBinding;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.utils.FirebaseUtils;
import com.ricardorainha.mustache.utils.SharedPrefUtils;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        configureFacebookSDK();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            startProfileActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    //    private void configureFacebookSDK() {
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this.getApplication());
//    }

    private void configureObservables() {
        viewModel.getUserInfoReceived().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.getUserInfoReceived().get()) {
                    if (!SharedPrefUtils.hasCompletedProfile(MainActivity.this)) {
                        startProfileActivity();
                    }
                }
            }
        });
    }

    private void configureFields() {

    }

    private void startProfileActivity() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

}
