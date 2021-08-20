package com.luxlunaris.noadpadlight.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;

/**
 * This Activity is just a launchpad, and it's never actually
 * shown to the user.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Used to obtain app-wide variables in other classes
     */
    public  static  Context CONTEXT;


    /**
     * The facade controller-singleton that manages Pages.
     */
    Notebook notebook;


    /**
     * If true, onCreate was called at least once
     */
    boolean appStartedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        CONTEXT = this.getApplicationContext();


        notebook  = Notebook.getInstance();
        Log.d("SPEED_TEST", "DONE INITIALIZING NOTEBOOK!");



        //make the pages-listing activity
        Intent goToPagesIntent = new Intent(this, PagesActivity.class);
        startActivity(goToPagesIntent);


        notebook.setListener(PagesActivity.changes);


        Log.d("SPEED_TEST", "CREATED PAGESACTIVITY!");



        //decide whether or not to open a blank page at app launch
        boolean startToBlankPage = Settings.getBoolean(SETTINGS_TAGS.LAUNCH_TO_BLANK_PAGE);

        if(startToBlankPage && !appStartedFlag){

            //set the "launch phase is over" flag
            appStartedFlag = true;

            //jump to the reader activity with a new blank page
            Intent intent = new Intent(this, ReaderActivity.class);
            intent.putExtra(ReaderActivity.PAGE_EXTRA, notebook.newPage());
            startActivity(intent);
        }

        Log.d("SPEED_TEST", "DECIDED WHERE TO GO!");

        Log.d("SPEED_TEST", "DONE????");

    }


}