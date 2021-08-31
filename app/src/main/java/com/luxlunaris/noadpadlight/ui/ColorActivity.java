package com.luxlunaris.noadpadlight.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Settings;
import com.luxlunaris.noadpadlight.control.interfaces.SettingsTagListener;
import com.luxlunaris.noadpadlight.model.classes.Tag;

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

        try{

        //totally unrelated to THEMES
        setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);

        //get the current theme from the global Settings
        currentTheme = THEMES.getThemeByName(Settings.getString(Settings.TAG_THEME));

        //set the theme of this activity
        setTheme(currentTheme);

        Settings.listenToTag(Settings.TAG_THEME, this);


        super.onCreate(savedInstanceState);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Check if the theme changed, if that's the case
     * repaint all of the views on this activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        try{

        if(justCreatedFlag){
            repaintViews();
            justCreatedFlag = false;
        }

        THEMES newTheme = THEMES.getThemeByName(Settings.getString(Settings.TAG_THEME));
        if(currentTheme!=newTheme){
            currentTheme = newTheme;
            repaintViews();
        }

        }catch (Exception e){
            e.printStackTrace();
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
    public void onTagUpdated(Tag tag) {
        System.out.println("HELLOOO??????????????????");
        setTheme(THEMES.getThemeByName(Settings.getString(Settings.TAG_THEME)));
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
