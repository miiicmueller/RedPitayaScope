package com.apps.darkone.redpitayascope.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.apps.darkone.redpitayascope.R;


/**
 * Created by DarkOne on 01.11.15.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    private Context mContext;

    private final String LOG_TAG_SETTINGS_MAIN = "MAIN_SETTINGS";


    public static SettingsFragment newInstance() {
        SettingsFragment settingFragment = new SettingsFragment();


        return settingFragment;
    }


    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.registerOnSharedPreferenceChangeListener(this);

        EditTextPreference editTextPrefBoardIP = (EditTextPreference) findPreference(getString(R.string.menu_settings_board_ip_key));
        editTextPrefBoardIP
                .setSummary(prefs.getString(getString(R.string.menu_settings_board_ip_key), ""));

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        Log.d(LOG_TAG_SETTINGS_MAIN, "onCreatePreferences");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.menu_settings_board_ip_key))) {
            EditTextPreference editTextPrefBoardIP = (EditTextPreference) findPreference(getString(R.string.menu_settings_board_ip_key));
            editTextPrefBoardIP
                    .setSummary(sharedPreferences.getString(getString(R.string.menu_settings_board_ip_key), ""));
        }
    }
}