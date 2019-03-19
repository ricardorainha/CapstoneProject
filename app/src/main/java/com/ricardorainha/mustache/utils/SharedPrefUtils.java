package com.ricardorainha.mustache.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {

    private static final String SHARED_PREFERENCES_NAME = "Mustache";

    private static final String COMPLETED_PROFILE = "COMPLETED_PROFILE";

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static boolean hasCompletedProfile(Context context) {
        return getSharedPref(context).getBoolean(COMPLETED_PROFILE, false);
    }

    public void setCompletedProfile(Context context, boolean completed) {
        getSharedPref(context).edit().putBoolean(COMPLETED_PROFILE, completed).apply();
    }

}
