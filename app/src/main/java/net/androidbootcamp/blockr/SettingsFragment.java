package net.androidbootcamp.blockr;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    private Context mContext;
    private Activity mActivity;



    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);

        mContext = this.getActivity();
        mActivity = this.getActivity();
        //Create Preferences with keyvalues for later
        final SwitchPreferenceCompat contacts = (SwitchPreferenceCompat) findPreference
                ("contact_switch");
        final SwitchPreferenceCompat whiteList = (SwitchPreferenceCompat) findPreference
                ("whitelist_switch");
        contacts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            // Make only one switch at a time set to true
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if(!contacts.isChecked())
                {
                    whiteList.setChecked(false);
                }
                return true;
            }
        });
        whiteList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if(!whiteList.isChecked())
                {
                    contacts.setChecked(false);
                }
                return true;
            }
        });

    }





}
