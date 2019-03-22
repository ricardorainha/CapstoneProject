package com.ricardorainha.mustache.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.utils.FirebaseUtils;
import com.ricardorainha.mustache.utils.SharedPrefUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        configureFacebookSDK();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldShowLogin()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else {
            FirebaseUtils.requestUserInfo(AuthManager.getInstance().getUser().getUid(), user -> {
                if (user != null) {
                    Session.getInstance().setUser(user);
                }
                if (!SharedPrefUtils.hasCompletedProfile(MainActivity.this)) {
                    startProfileActivity();
                }
            });
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

    private boolean shouldShowLogin() {
        return AuthManager.getInstance().needToLogin();
    }

    private void startProfileActivity() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

}
