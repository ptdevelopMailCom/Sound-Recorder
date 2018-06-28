package com.example.ptdev.soundrecordercopy.fragment;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.ptdev.soundrecordercopy.MySharedPreferences;
import com.example.ptdev.soundrecordercopy.R;


public class SettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        CheckBoxPreference highQualityPreference = (CheckBoxPreference) findPreference("pref_high_quality");

        highQualityPreference.setChecked(MySharedPreferences.getPrefHighQuality(getActivity()));
        highQualityPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MySharedPreferences.setPrefHighQuality(getActivity(), (boolean) newValue);

                return true;
            }
        });
    }

}
