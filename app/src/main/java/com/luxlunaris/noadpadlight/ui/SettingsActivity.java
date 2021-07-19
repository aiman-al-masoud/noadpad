package com.luxlunaris.noadpadlight.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;


import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;

public class SettingsActivity extends ColorActivity {


    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        linearLayout = findViewById(R.id.settings_lin_layout);

        ToggleFragment lauchToBlankPageToggle = ToggleFragment.newInstance("Auto-launch the app to a blank page.", SETTINGS_TAGS.LAUNCH_TO_BLANK_PAGE);
        getSupportFragmentManager().beginTransaction().add(linearLayout.getId(), lauchToBlankPageToggle, "" ).commit();



    }











}