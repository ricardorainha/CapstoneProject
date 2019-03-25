package com.ricardorainha.mustache.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.ActivityProfileBinding;
import com.ricardorainha.mustache.utils.SharedPrefUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProviders;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    ProfileActivityViewModel viewModel;

    private static final int REQUEST_CODE_PICK_IMAGE = 2138;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        viewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel.class);
        binding.setViewModel(viewModel);

        SharedPrefUtils.setCompletedProfile(this, true);
        configureObservables();
        configureFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                viewModel.onSignOutClicked();
                return true;

            case R.id.menu_delete_account:
                viewModel.onDeleteAccountClicked();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (data != null) {
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (imageStream != null) {
                    Bitmap profilePhoto = BitmapFactory.decodeStream(imageStream);
                    viewModel.getUserPhotoBitmap().set(profilePhoto);
                }
            }
        }
    }

    private void configureFields() {
        if (viewModel.getUserPhotoReference().get() != null) {
            setUserProfilePhoto();
        }
        else {
            Glide.with(this).load(R.drawable.baseline_person_black_48).apply(RequestOptions.circleCropTransform()).into(binding.userImage);
        }
    }

    private void configureObservables() {
        viewModel.getUserPhotoBitmap().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Glide.with(ProfileActivity.this).load(viewModel.getUserPhotoBitmap().get()).apply(RequestOptions.circleCropTransform()).into(binding.userImage);
            }
        });
        viewModel.getUserPhotoReference().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                setUserProfilePhoto();
            }
        });
        viewModel.getMustFinish().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.getMustFinish().get()) {
                    SharedPrefUtils.setCompletedProfile(ProfileActivity.this, false);
                    finish();
                }
            }
        });
    }

    public void onUserImageClick(View view) {
        Intent pickImageIntent = new Intent();
        pickImageIntent.setType("image/*");
        pickImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickImageIntent, REQUEST_CODE_PICK_IMAGE);
    }

    private void setUserProfilePhoto() {
        viewModel.getUserPhotoReference().get().getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(ProfileActivity.this).load(uri.toString()).apply(RequestOptions.circleCropTransform()).into(binding.userImage))
                .addOnFailureListener(uri -> Glide.with(ProfileActivity.this).load(R.drawable.baseline_person_black_48).apply(RequestOptions.circleCropTransform()).into(binding.userImage));
    }

}
