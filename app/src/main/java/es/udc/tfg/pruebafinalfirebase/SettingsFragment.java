package es.udc.tfg.pruebafinalfirebase;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by Usuario on 11/03/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    public final static String KEY_GPS = "accuracy_preference";
    public final static String KEY_INFOWINDOW = "infowindow_preference";
    public final static String KEY_FILTER = "filter_preference";
    public final static String KEY_MESSAGES = "messages_preference";
    public final static String KEY_AUTOZOOM = "autozoom_preference";
    public final static String KEY_INFOWINDOW_MESSAGES = "infowindow_preference_messages";


    private DBManager dbManager;
    private Preference nickPref,phonePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        dbManager = DBManager.getInstance();
        nickPref = getPreferenceManager().findPreference("nick_preference");
        phonePref = getPreferenceManager().findPreference("phone_preference");
        nickPref.setDefaultValue(dbManager.getNick());
        nickPref.setSummary(dbManager.getNick());
        phonePref.setDefaultValue(dbManager.getProfile().getPhoneNumber());
        phonePref.setSummary(dbManager.getProfile().getPhoneNumber());
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }
}
