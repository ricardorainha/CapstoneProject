package com.ricardorainha.mustache.view;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.storage.StorageReference;
import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.model.User;
import com.ricardorainha.mustache.utils.FirebaseUtils;
import com.ricardorainha.mustache.utils.SharedPrefUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileActivityViewModel extends ViewModel implements AuthManager.UserStateChange {

    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> passwordConfirmation = new MutableLiveData<>();
    private MutableLiveData<StorageReference> userPhotoReference = new MutableLiveData<>();
    private MutableLiveData<Bitmap> userPhotoBitmap = new MutableLiveData<>();
    private MutableLiveData<Boolean> mustFinish = new MutableLiveData<>();


    public ProfileActivityViewModel() {
        mustFinish.setValue(false);

        if (Session.getInstance().getUser().getValue() != null) {
            userPhotoReference.setValue(Session.getInstance().getUser().getValue().getPhotoReference());
        }

        getUserPhotoBitmap().observeForever(bitmap -> {
            if (bitmap != null) {
                FirebaseUtils.updateUserPhoto(userPhotoBitmap.getValue());
            }
        });
    }

    public MutableLiveData<User> getUser() {
        return Session.getInstance().getUser();
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<String> getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(MutableLiveData<String> passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public MutableLiveData<StorageReference> getUserPhotoReference() {
        return userPhotoReference;
    }

    public MutableLiveData<Bitmap> getUserPhotoBitmap() {
        return userPhotoBitmap;
    }

    public MutableLiveData<Boolean> getMustFinish() {
        return mustFinish;
    }

    public void onSaveProfileClicked(View view) {
        String passwordString = null;
        if (!TextUtils.isEmpty(password.getValue())) {
            if (password.getValue().equals(passwordConfirmation.getValue())) {
                passwordString = password.getValue();
                AuthManager.getInstance().updatePassword(passwordString);
            }
            else {

            }
        }
        FirebaseUtils.updateUserInfo();

        SharedPrefUtils.setCompletedProfile(view.getContext(), true);
        mustFinish.setValue(true);
    }

    public void onSignOutClicked() {
        AuthManager.getInstance().signOut(this);
    }

    public void onRemovePhotoClicked() {
        FirebaseUtils.removeProfilePhoto(task -> {
            if (task.isSuccessful()) {
                getUserPhotoReference().setValue(null);
                getUserPhotoBitmap().setValue(null);
            }
        });
    }

    public void onDeleteAccountClicked() {
        AuthManager.getInstance().deleteAccount(this);
    }

    @Override
    public void onUserSignOut() {
        mustFinish.setValue(true);
    }

}
