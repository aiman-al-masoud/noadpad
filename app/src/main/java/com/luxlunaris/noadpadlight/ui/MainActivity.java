package com.luxlunaris.noadpadlight.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.services.FileIO;

import java.io.File;

/**
 * This Activity is just a launchpad, and it's never actually
 * shown to the user.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Used to obtain app-wide variables in other classes
     */
    public  static  Context CONTEXT;



    public static final String PLAIN_LAUNCH = "PLAIN_LAUNCH";

    public static final String OPEN_ZIPPED_LAUNCH = "application/zip";



    /**
     * If true, onCreate was called at least once
     */
    boolean appStartedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        CONTEXT = this.getApplicationContext();


        FileIO.deleteDirectory(getCacheDir().getPath());


        //notebook  = Notebook.getInstance();
        Notebook.getInstance();

        //get the intent that started the app
        Intent externalIntent = getIntent();
        String type = externalIntent.getType()==null? PLAIN_LAUNCH : externalIntent.getType();

        //decide how to launch the app based on the intent's type
        switch (type){

            case PLAIN_LAUNCH:
                plainLaunch();
                break;
            case OPEN_ZIPPED_LAUNCH:
                Uri uri = externalIntent.getData();
                File zipFile = FileIO.getFileFromUri(this, uri);
                Log.d("EXTERNAL_INTENT", zipFile.getPath()+" exists: "+zipFile.exists()+" size (bytes):"+zipFile.getTotalSpace());
                zippedFileLaunch(zipFile);
                break;
        }

    }


    /**
     * Normal launch that happens when the user starts the app
     * by pressing on its icon.
     */
    public void plainLaunch(){
        startPagesActivity();
        //decide whether or not to open a blank page at app launch
        boolean startToBlankPage = Settings.getBoolean(SETTINGS_TAGS.LAUNCH_TO_BLANK_PAGE);
        if(startToBlankPage && !appStartedFlag){
            //set the "launch phase is over" flag
            appStartedFlag = true;
            startReaderActivity(Notebook.getInstance().newPage());
        }
    }

    public void zippedFileLaunch(File zipFile){
        Notebook.getInstance().importPages(zipFile.getPath());
        startPagesActivity();
    }


    public void startPagesActivity(){
        //make the pages-listing activity
        Intent goToPagesIntent = new Intent(this, PagesActivity.class);
        startActivity(goToPagesIntent);
        Notebook.getInstance().setListener(PagesActivity.changes);
    }

    public void startReaderActivity(Page page){
        //jump to the reader activity with a new blank page
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra(ReaderActivity.PAGE_EXTRA, page);
        startActivity(intent);
    }







}