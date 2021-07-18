package com.luxlunaris.noadpadlight.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.classes.Settings;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    /**
     * Used to obtain app-wide variables in other classes
     */
    public  static  Context CONTEXT;



    /**
     * Takes you to the pages list
     */
    Button goToPagesButton;


    /**
     * If true, onCreate was called at least once
     */
    boolean appStartedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        CONTEXT = this.getApplicationContext();


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //make the pages-listing activity
        Intent goToPagesIntent = new Intent(this, PagesActivity.class);
        startActivity(goToPagesIntent);


        //decide whether or not to open a blank page at app launch
        //boolean startToBlankPage = Settings.get().getTagValue(Settings.TAGS.LAUNCH_TO_BLANK_PAGE.toString()).equals(Settings.TRUE)? true : false;
        boolean startToBlankPage = Settings.getBoolean(SETTINGS_TAGS.LAUNCH_TO_BLANK_PAGE);

        if(startToBlankPage && !appStartedFlag){

            //set the "launch phase is over" flag
            appStartedFlag = true;

            //jump to the reader activity with a new blank page
            Intent intent = new Intent(this, ReaderActivity.class);
            intent.putExtra("PAGE", Notebook.getInstance().newPage());
            startActivity(intent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

        }

        return super.onOptionsItemSelected(item);
    }



}