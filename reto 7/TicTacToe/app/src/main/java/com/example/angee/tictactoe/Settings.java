package com.example.angee.tictactoe;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by angee on 16/10/2017.
 */

public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
