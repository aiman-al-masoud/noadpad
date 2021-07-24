package com.luxlunaris.noadpadlight.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;

/**
 * Displays various app-wide settings and lets the user decide their preferences.
 */
public class SettingsActivity extends ColorActivity {


    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        linearLayout = findViewById(R.id.settings_lin_layout);

        Button showInfo = new Button(this);
        linearLayout.addView(showInfo, 0);
        showInfo.setText("Credits & more Info");
        showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoFragment infoFrag = InfoFragment.newInstance(getResources().getString(R.string.credits_eng));
                infoFrag.show(getSupportFragmentManager(), "");
            }
        });



        ToggleFragment lauchToBlankPageToggle = ToggleFragment.newInstance("Auto-launch the app to a blank page.", SETTINGS_TAGS.LAUNCH_TO_BLANK_PAGE);
        getSupportFragmentManager().beginTransaction().add(linearLayout.getId(), lauchToBlankPageToggle, "" ).commit();


        SpinnerFragment spinner =  SpinnerFragment.newInstance(SETTINGS_TAGS.THEME, THEMES.values(), "Select the app-theme:");
        getSupportFragmentManager().beginTransaction().add(linearLayout.getId(), spinner, "").commit();



    }











}