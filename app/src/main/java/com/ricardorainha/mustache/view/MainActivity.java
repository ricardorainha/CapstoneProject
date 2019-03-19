package com.ricardorainha.mustache.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.authentication.AuthManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        configureFacebookSDK();
        setContentView(R.layout.activity_main);

        if (shouldShowLogin()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);

            finish();
        }

    }

//    private void configureFacebookSDK() {
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this.getApplication());
//    }

    private boolean shouldShowLogin() {
        return AuthManager.getInstance().needToLogin();
    }

}
