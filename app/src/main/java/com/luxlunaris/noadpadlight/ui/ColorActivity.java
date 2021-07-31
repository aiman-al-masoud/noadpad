package com.luxlunaris.noadpadlight.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;
import com.luxlunaris.noadpadlight.control.interfaces.SettingsTagListener;

import java.util.List;

/**
 * Abstract class extended by all Activities.
 * Handles color, themes, style...
 */
public abstract class ColorActivity extends AppCompatActivity implements SettingsTagListener {

    /**
     * The current global theme
     */
    THEMES currentTheme;

    /**
     * true if the activity has just been created
     */
    boolean justCreatedFlag = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //totally unrelated to THEMES
        setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);

        //get the current theme from the global Settings
        currentTheme = THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME));

        //set the theme of this activity
        setTheme(currentTheme);

        Settings.listenToTag(SETTINGS_TAGS.THEME, this);


        super.onCreate(savedInstanceState);
    }

    /**
     * Check if the theme changed, if that's the case
     * repaint all of the views on this activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if(justCreatedFlag){
            repaintViews();
            justCreatedFlag = false;
        }

        THEMES newTheme = THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME));
        if(currentTheme!=newTheme){
            currentTheme = newTheme;
            repaintViews();
        }

    }


    /**
     * Set the current theme.
     * @param theme
     */
    public void setTheme(THEMES theme){
        this.currentTheme = theme;
    }

    /**
     * Repaint all of the views on this activity.
     */
    public void repaintViews(){
        //get all children views of this activity
        for(View view : getAllChildren()){

            view.setBackgroundColor(currentTheme.BG_COLOR);

            try{
                ((TextView)view).setTextColor(currentTheme.FG_COLOR);
            }catch (ClassCastException e){

            }
        }
    }


    /**
     * Get all children views of this activity.
     * @return
     */
    private List<View> getAllChildren(){
        return ViewUtils.getAllChildren(getWindow().getDecorView());
    }


    @Override
    public void onTagUpdated(SETTINGS_TAGS tag) {
        System.out.println("HELLOOO??????????????????");
        setTheme(THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME)));
        repaintViews();
    }


    /**
     * True if the activity is currently visible.
     * @return
     */
    public boolean isInForeground(){
        return  getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
    }



}
