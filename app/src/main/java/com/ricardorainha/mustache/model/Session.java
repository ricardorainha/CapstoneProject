package com.ricardorainha.mustache.model;

import androidx.lifecycle.MutableLiveData;

public class Session {
    private static final Session ourInstance = new Session();
    private MutableLiveData<User> user = new MutableLiveData<>();

    public static Session getInstance() {
        return ourInstance;
    }

    private Session() {
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(MutableLiveData<User> user) {
        this.user = user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public interface Callback {
        void onUserInfoReceived(User user);
    }
}
