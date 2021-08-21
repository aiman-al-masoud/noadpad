package com.luxlunaris.noadpadlight.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.luxlunaris.noadpadlight.R;

import java.io.File;

/**
 *
 */
public class DoodleActivity extends AppCompatActivity implements YayOrNayDialog.BinaryQuestioner {


    /**
     * Allows drawing on it and converts the bitmap to a suitable image file.
     */
    DoodleView doodleView;

    /**
     * Name of the doodle file extra.
     */
    public static final String DOODLE_FILE_EXTRA = "DOODLE_FILE";


    /**
     * Exit without saving code for BinaryQuestioner callback.
     */
    public static final String EXIT_WITHOUT_SAVING = "EXIT_W_OUT_SAVING";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set title, actionbar theme and layout.
        setTitle(R.string.doodle_activity_title);
        setTheme(R.style.Theme_AppCompat_DayNight_DarkActionBar);
        setContentView(R.layout.activity_doodle);

        //create and add a doodle view
        ConstraintLayout l = findViewById(R.id.doodle_activity_layout);
        doodleView = new DoodleView(this);
        l.addView(doodleView);

    }

    /**
     * Inflate the toolbar menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate the menu's layout xml
        getMenuInflater().inflate(R.menu.doodle_activity_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle toolbar menu commands.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.done_doodling:

                //get an image file from the doodleView
                File doodleFile = doodleView.getSnapshot();
                //make an intent w/ the file as an extra.
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DOODLE_FILE_EXTRA, doodleFile);
                //set result for caller activity
                setResult(RESULT_OK, resultIntent);
                //return to caller activity.
                finish();
                break;
            case R.id.undo:
                doodleView.undo();
                break;
            case R.id.pick_color:
                doodleView.showColorPickerDialog();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        YayOrNayDialog exitDialog = YayOrNayDialog.newInstance(EXIT_WITHOUT_SAVING, getString(R.string.exit_doodle_no_save));
        exitDialog.setListener(this);
        exitDialog.show(getSupportFragmentManager(), "");
    }


    @Override
    public void onUserBinaryAnswer(String tag, int result) {

        switch (tag){
            case EXIT_WITHOUT_SAVING:
                if(result==YayOrNayDialog.POSITIVE_RESPONSE){
                    finish();
                }
                break;
        }
    }




}