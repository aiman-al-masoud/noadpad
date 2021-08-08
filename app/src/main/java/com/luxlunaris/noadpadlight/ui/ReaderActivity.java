package com.luxlunaris.noadpadlight.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;
import com.luxlunaris.noadpadlight.model.classes.SinglePage;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.io.File;

/**
 * The activity responsible for displaying and editing a Page.
 */

public class ReaderActivity extends ColorActivity implements ImportFileFragment.FileRequester {

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
    //String textSizeString = Settings.instance().getString(Settings.TAGS.TEXT_SIZE.toString());
    int TEXT_SIZE = Settings.getInt(SETTINGS_TAGS.TEXT_SIZE);


    /**
     *This instance
     */
    ReaderActivity THIS;


    /**
     * Used to call this activity by an intent.
     */
    public static final String PAGE_EXTRA = "PAGE";


    /**
     * If this is on, the text edited by the user
     * will be interpreted as html source code.
     */
    private boolean HTML_EDIT_MODE = false;

    /**
     * True if onPause is getting called
     * when the activity is being left for good.
     * (False if the activity is being
     * temporarily left to choose a file from the file explorer).
     */
    private boolean EXITING =  true;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        //reference to this ReaderActivity instance
        THIS = this;
        //get the text view
        textView = findViewById(R.id.reader_text_view);
        //set the initial text size
        textView.setTextSize(TEXT_SIZE);
        //retrieve the page that you were called to display
        page = (Page)getIntent().getSerializableExtra(PAGE_EXTRA);
        //set the view's initial text to the Page's text
        reloadText();
        //jump to the last-saved position of the page
        jumpToPosition(page.getLastPosition());

    }

    /**
     * Reload text from current page.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void reloadText(){

        //get the html source code from the Page.
        String text =page.getText();
        Log.d("TEST_IMAGE", "TEXT FROM PAGE-FILE: "+text);
        //convert the html source code to a Spanned object
        Spanned s = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY, new ImageGetter(), null);

        if(HTML_EDIT_MODE){
            //pass raw html text
            textView.setText(text);
        }else{
            //pass the spanned object to the text view.
            textView.setText(s);
        }
    }

    /**
     * Overwrite the page's contents.
     */
    private void saveToPage(){
        String edited = getEdited();
        page.setText(edited);
    }


    /**
     * Get the html source that is currently being rendered.
     * @return
     */
    private String getEdited(){

        if(HTML_EDIT_MODE){
            return textView.getText().toString();
        }

        return Html.toHtml(textView.getEditableText()).toString();

    }


    /**
     * Switch between editing html source directly to
     * editing "text".
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void switchEditMode(){
        saveToPage();
        HTML_EDIT_MODE = !HTML_EDIT_MODE;
        reloadText();
    }



    /**
     * jump to a position in the text
     * @param position
     */
    private void jumpToPosition(int position){
        textView.setFocusable(true);
        textView.requestFocus();
        try{
            textView.setSelection(position);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }


    /**
     * Save the progress when exiting from the activity
     */
    @Override
    protected void onPause() {
        super.onPause();

        //if you're not exiting the activity for good don't run the rest of this method.
        if(!EXITING){
            EXITING = true;
            return;
        }
        
        //get the edited text from the edittext view
        String editedText = getEdited();

        //if the edited text is empty, delete the Page
        if(editedText.trim().isEmpty()){
            boolean t = page.delete();
            return;
        }

        //save the current position on the page
        page.savePosition(textView.getSelectionStart());

        //if the edited text doesn't differ from the text in the page, don't re-write it
        if(Html.toHtml(textView.getEditableText()).equals(page.getText())){
            return;
        }

        //else save the new text
        saveToPage();
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
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

                //set the token to be found in the Page
                page.setTokenToBeFound(query);

                //get the number of such tokens
                int multiplicity = page.numOfTokens(query);

                //auto-jump to its first position
                jumpToPosition(page.nextPosition());

                //display a toast about the multiplicity of said token
                Toast.makeText(THIS, "found "+ multiplicity +" occrrs. vol. up/down to nav.", Toast.LENGTH_LONG).show();
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
     * Define the toolbar's behavior
     * @param item
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.zoom_in:
                //increment the text size
                textView.setTextSize(++TEXT_SIZE);
                //save the new text size
                Settings.setTagValue(SETTINGS_TAGS.TEXT_SIZE, TEXT_SIZE+"");
                break;
            case R.id.zoom_out:
                //decrement the text size
                textView.setTextSize(--TEXT_SIZE);
                //save the new text size
                Settings.setTagValue(SETTINGS_TAGS.TEXT_SIZE, TEXT_SIZE+"");
                break;
            case R.id.importImage:
                EXITING = false;
                ImportFileFragment frag = ImportFileFragment.newInstance();
                frag.setFileRequester(this);
                frag.show(getSupportFragmentManager(), "");
                break;
            case R.id.switch_edit_mode:
                switchEditMode();
                String currentMode = HTML_EDIT_MODE? "you're editing html source!" : "you're back in normal mode.";
                Toast.makeText(this, currentMode, Toast.LENGTH_LONG).show();
                break;
            case R.id.make_bold:
                applyTag("b");
                break;
            case R.id.make_underlined:
                applyTag("u");
                break;
            case R.id.make_italics:
                applyTag("i");
                break;

            case R.id.make_plain:
                int currentPos = textView.getSelectionStart();
                saveToPage();
                page.removeHtmlTags(textView.getSelectionStart());
                reloadText();
                jumpToPosition(currentPos);
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void applyTag(String tag){
        int currentPos = textView.getSelectionStart();
        saveToPage();
        page.addHtmlTag(textView.getSelectionStart(), tag);
        reloadText();
        jumpToPosition(currentPos);
    }



    /**
     * Uses volume keys to navigate up and down between token positions.
     * @param keyCode
     * @param event
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case 25:  //volume down pressed: forth
                jumpToPosition(page.nextPosition());
                return true; //makes sure volume toast doesn't get displayed
            case 24:  //volume up pressed: back
                jumpToPosition(page.previousPosition());
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * When you obtain an image file to be added to the current page.
     * @param file
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFileObtained(File file) {
        Toast.makeText(this, "image imported!", Toast.LENGTH_SHORT).show();
        saveToPage();
        page.addImage(file.getPath());
        reloadText();
    }







}