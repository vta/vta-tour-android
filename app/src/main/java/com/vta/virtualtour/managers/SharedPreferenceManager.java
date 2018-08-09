package com.vta.virtualtour.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.vta.virtualtour.utility.Constants;

/**
 * Created by tushar on 13/04/18.
 */

public class SharedPreferenceManager {

    private static SharedPreferenceManager instance;
    private Context context;

    public static SharedPreferenceManager getSharedInstance(Context context) {
        if(instance == null) {
            instance = new SharedPreferenceManager();
            instance.context = context;
        }
        return instance;
    }

    public void saveValueForKey(String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCE_DB, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getValueForkey(String key) {
        return context.getSharedPreferences(Constants.SHARED_PREFERENCE_DB, Context.MODE_PRIVATE).getBoolean(key, false);
    }
}
