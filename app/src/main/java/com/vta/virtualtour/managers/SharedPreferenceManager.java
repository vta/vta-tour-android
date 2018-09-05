package com.vta.virtualtour.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.vta.virtualtour.utility.Constants;

/**
 * Created by tushar
 * Created on 13/04/18.
 */

public class SharedPreferenceManager {

    private static SharedPreferenceManager instance;

    public static SharedPreferenceManager getSharedInstance() {
        if (instance == null) {
            instance = new SharedPreferenceManager();
        }
        return instance;
    }

    public void saveValueForKey(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCE_DB, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getValueForkey(Context context, String key) {
        return context.getSharedPreferences(Constants.SHARED_PREFERENCE_DB, Context.MODE_PRIVATE).getBoolean(key, false);
    }
}
