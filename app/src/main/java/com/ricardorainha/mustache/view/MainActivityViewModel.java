package com.ricardorainha.mustache.view;

import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.utils.FirebaseUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private ObservableField<Boolean> userInfoReceived = new ObservableField<>();

    public MainActivityViewModel() {
        userInfoReceived.set(false);
    }

    public ObservableField<Boolean> getUserInfoReceived() {
        return userInfoReceived;
    }

    public void requestUserInfo() {
        userInfoReceived.set(false);
        FirebaseUtils.requestUserInfo(AuthManager.getInstance().getUser().getUid(), user -> {
            if (user != null) {
                Session.getInstance().setUser(user);
            }

            userInfoReceived.set(true);
        });
    }
}
