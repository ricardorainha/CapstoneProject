package com.ricardorainha.mustache.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.MutableLiveData;

public class Session {
    private static final Session ourInstance = new Session();
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> onlineStatus = new MutableLiveData<>();
    private boolean askedForPermissions = false;

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

    public MutableLiveData<Boolean> getOnlineStatus() {
        return onlineStatus;
    }

    public boolean hasAskedForPermissions() {
        return askedForPermissions;
    }

    public void setAskedForPermissions(boolean askedForPermissions) {
        this.askedForPermissions = askedForPermissions;
    }

    public void updateOnlineStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        getOnlineStatus().setValue(connected);
    }

    public interface Callback {
        void onUserInfoReceived(User user);
    }
}
