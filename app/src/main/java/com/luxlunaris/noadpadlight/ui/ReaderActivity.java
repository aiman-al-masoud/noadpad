package com.luxlunaris.noadpadlight.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Settings;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

/**
 * The activity responsible for displaying and editing a Page.
 */

public class ReaderActivity extends AppCompatActivity {

    /**
     * The currently displayed Page
     */
    Page page;

    /**
     * Displays the Page's text
     */
    EditText textView;

    /**
     * current text size
     * defaults to: 18
     */
    String textSizeString = Settings.get().getTagValue(Settings.TAGS.TEXT_SIZE.toString());
    int TEXT_SIZE = textSizeString==null? 18 : Integer.parseInt(textSizeString.trim());




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        //get the text view
        textView = findViewById(R.id.reader_text_view);
        //set the initial text size
        textView.setTextSize(TEXT_SIZE);
        //retrieve the page that you were called to display
        page = (Page)getIntent().getSerializableExtra("PAGE");
        //set the view's initial text to the Page's text
        textView.setText(page.getText());

    }


    /**
     * jump to a position in the text
     * @param position
     */
    private void jumpToPosition(int position){
        textView.setFocusable(true);
        textView.requestFocus();
        textView.setSelection(position);
    }


    /**
     * Save the progress when exiting from the activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        page.setText(textView.getText().toString());
    }


    /**
     * Create the toolbar for this activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu's layout xml
        getMenuInflater().inflate(R.menu.reader_activity_toolbar, menu);

        //make a search view for queries on articles
        SearchView searchView = (SearchView)menu.findItem(R.id.search_token).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                jumpToPosition(page.nextPosition( query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Define the toolbars behavior
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.zoom_in:
                //increment the text size
                textView.setTextSize(++TEXT_SIZE);
                //save the new text size
                Settings.get().setTagValue(Settings.TAGS.TEXT_SIZE.toString(), TEXT_SIZE+"");
                break;

            case R.id.zoom_out:
                //increment the text size
                textView.setTextSize(--TEXT_SIZE);
                //save the new text size
                Settings.get().setTagValue(Settings.TAGS.TEXT_SIZE.toString(), TEXT_SIZE+"");
                break;
        }

        return super.onOptionsItemSelected(item);
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case 25: //volume up pressed: back
                jumpToPosition(page.nextPosition());
                return true; //makes sure volume toast doesn't get displayed
            case 24: //volume down pressed: forth
                jumpToPosition(page.previousPosition());
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }













}