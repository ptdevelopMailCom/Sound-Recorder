package com.example.ptdev.soundrecordercopy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

public class MySharedPreferences {
    private static final String TAG = "MySharedPreferences";
    public static final String PREF_HIGH_QUALITY = "pref_high_quality";

    public static void setPrefHighQuality(Context context, boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_HIGH_QUALITY, isEnabled);

        Log.i(TAG, String.valueOf(isEnabled));
        editor.apply();
    }

    public static boolean getPrefHighQuality(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getBoolean(PREF_HIGH_QUALITY, false);
    }


}
