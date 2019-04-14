package com.ricardorainha.mustache.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ricardorainha.mustache.model.Session;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(CONNECTIVITY_ACTION)) {
            Session.getInstance().updateOnlineStatus(context);
        }
    }
}
