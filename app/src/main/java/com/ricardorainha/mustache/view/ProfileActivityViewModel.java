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

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileActivityViewModel extends ViewModel implements AuthManager.UserStateChange {

    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> passwordConfirmation = new MutableLiveData<>();
    private ObservableField<StorageReference> userPhotoReference = new ObservableField<>();
    private ObservableField<Bitmap> userPhotoBitmap = new ObservableField<>();
    private ObservableField<Boolean> mustFinish = new ObservableField<>();


    public ProfileActivityViewModel() {
        mustFinish.set(false);

        if (Session.getInstance().getUser().getValue() != null) {
            userPhotoReference.set(Session.getInstance().getUser().getValue().getPhotoReference());
        }
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

    public ObservableField<StorageReference> getUserPhotoReference() {
        return userPhotoReference;
    }

    public ObservableField<Bitmap> getUserPhotoBitmap() {
        return userPhotoBitmap;
    }

    public ObservableField<Boolean> getMustFinish() {
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

        if (userPhotoBitmap.get() != null) {
            FirebaseUtils.updateUserPhoto(userPhotoBitmap.get());
        }

        SharedPrefUtils.setCompletedProfile(view.getContext(), true);
        mustFinish.set(true);
    }

    public void onSignOutClicked() {
        AuthManager.getInstance().signOut(this);
    }

    public void onDeleteAccountClicked() {
        AuthManager.getInstance().deleteAccount(this);
    }

    @Override
    public void onUserSignOut() {
        mustFinish.set(true);
    }
}
