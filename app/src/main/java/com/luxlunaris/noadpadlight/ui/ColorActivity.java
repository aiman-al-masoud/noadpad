package com.luxlunaris.noadpadlight.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;

import java.util.List;

/**
 * Abstract class extended by all Activities.
 * Handles color, themes, style...
 */
public abstract class ColorActivity extends AppCompatActivity {

    boolean THEME_CHANGED = false;

    boolean GOT_INITIALIZED = false;


    /**
     * Current Theme from THEMES enum
     */
    THEMES theme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);

        if(!GOT_INITIALIZED){
            String themeName = Settings.getString(SETTINGS_TAGS.THEME);
            setTheme(THEMES.getThemeByName(themeName));
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //set look and feel only if it changed or if the activity is being started for the first time
        if(!GOT_INITIALIZED || THEME_CHANGED){
            repaintViews();
            GOT_INITIALIZED = true;
        }
    }


    public void setTheme(THEMES theme){
        this.theme = theme;
        //this.THEME_CHANGED = true;
    }

    public void repaintViews(){
        //get all children views of this activity
        for(View view : getAllChildren()){

            view.setBackgroundColor(theme.BG_COLOR);

            try{
                ((TextView)view).setTextColor(theme.FG_COLOR);
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







}
