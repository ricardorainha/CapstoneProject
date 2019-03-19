package com.ricardorainha.mustache.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.setUser(AuthManager.getInstance().getUser());
    }
}
