package com.luxlunaris.noadpadlight.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Paths;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.services.FileIO;

import java.io.File;

/**
 * The activity responsible for displaying and editing a Page.
 */

public class ReaderActivity extends ColorActivity implements ImportFileFragment.FileRequester, RecorderFragment.AudioActivity, TextPrompt.TextRequester {

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
    int TEXT_SIZE = Settings.getInt(SETTINGS_TAGS.TEXT_SIZE);

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
     * Callback-request codes.
     */
    private static final int REQUEST_DOODLE =  1;
    private static final String GET_WEB_LINK  = "2";
    private static final int REQUEST_IMAGE = 3;
    private static final int REQUEST_AUDIO = 4;
    private static final int REQUEST_IMAGE_CAPTURE = 5;


    /**
     * The toolbar menu.
     */
    private Menu optionsMenu;

    /**
     * Fragment that plays audio files.
     */
    private AudioPlayerFragment playerFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        setTitle(R.string.reader_activity_title_normal);


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

        //for audio files
        textView.setOnClickListener(new ParaClickHandler());
        //for weblinks
        textView.setMovementMethod(LinkMovementMethod.getInstance());

    }


    /**
     * Reload text from current page.
     */
    private void reloadText(){

        //get the html source code from the Page.
        String text =page.getSourceCode();

        //convert the html source code to a Spanned object (using deprecated version for legacy support).
        //TODO: Solve: when the activity starts back after being killed in the bg, html source's image tags are ok, but span doesn't contain images.
        Spanned s = Html.fromHtml (text, new ImageGetter(this), null);

        if(HTML_EDIT_MODE){
            //pass raw html text
            textView.setText(text);
        }else{
            //pass the spanned object to the text view.
            textView.setText(s);
        }
    }

    /**
     * Overwrite the page's text contents.
     */
    private void saveToPage(){
        String edited = getEdited();
        page.setSourceCode(edited);
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
    private void switchEditMode(){
        saveToPage();
        HTML_EDIT_MODE = !HTML_EDIT_MODE;
        reloadText();

        if(HTML_EDIT_MODE){
            setTitle(R.string.reader_activity_title_html_editor);
        }else{
            setTitle(R.string.reader_activity_title_normal);
        }

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
        saveProgress();
    }


    /**
     * Save the text and context of the currently edited page.
     */
    private void saveProgress(){
        //save the current position on the page
        page.savePosition(textView.getSelectionStart());

        //if the edited text doesn't differ from the text in the page, don't re-write it
        if(Html.toHtml(textView.getEditableText()).equals(page.getSourceCode())){
            return;
        }

        if(!page.getBoolean(Page.TAG_EDITABLE)){
            return;
        }

        //else save the new text
        saveToPage();
        Toast.makeText(this, R.string.saved_page_changed_toast, Toast.LENGTH_SHORT).show();
    }


    /**
     * Set the page's editability.
     * @param editable
     */
    private void setEditable(boolean editable){
        textView.setEnabled(editable);
        optionsMenu.findItem(R.id.importImage).setVisible(editable);
        optionsMenu.findItem(R.id.make_doodle).setVisible(editable);
        optionsMenu.findItem(R.id.styles_menu).setVisible(editable);
        optionsMenu.findItem(R.id.capture_image).setVisible(editable);
        optionsMenu.findItem(R.id.add_link).setVisible(editable);
        optionsMenu.findItem(R.id.record_audio).setVisible(editable);
        optionsMenu.findItem(R.id.import_audio).setVisible(editable);
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

        optionsMenu = menu;

        //if page is not editable, prevent editing.
        if(!page.getBoolean(Page.TAG_EDITABLE)){
            setEditable(false);
        }else{
            setEditable(true);
        }

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
                Toast.makeText(getApplicationContext(), getString(R.string.occurrences_found_toast)+ multiplicity , Toast.LENGTH_LONG).show();


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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.zoom_in:
                //increment the text size
                textView.setTextSize(++TEXT_SIZE);
                //save the new text size
                Settings.setTag(SETTINGS_TAGS.TEXT_SIZE, TEXT_SIZE+"");
                break;
            case R.id.zoom_out:
                //decrement the text size
                textView.setTextSize(--TEXT_SIZE);
                //save the new text size
                Settings.setTag(SETTINGS_TAGS.TEXT_SIZE, TEXT_SIZE+"");
                break;
            case R.id.importImage:
                ImportFileFragment frag = ImportFileFragment.newInstance();
                frag.setFileRequester(this);
                frag.setTag(REQUEST_IMAGE);
                frag.show(getSupportFragmentManager(), "");
                break;
            case R.id.switch_edit_mode:
                switchEditMode();
                String currentMode = HTML_EDIT_MODE? getString(R.string.editing_html_mode_ON) :   getString(R.string.editing_html_mode_OFF);
                Toast.makeText(this, currentMode, Toast.LENGTH_LONG).show();
                break;
            case R.id.make_doodle:
                //launch the DoodleActivity requesting a doodle.
                Intent intent = new Intent(this, DoodleActivity.class);
                startActivityForResult(intent, REQUEST_DOODLE);
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
            case R.id.record_audio:
                RecorderFragment recorderFragment = new RecorderFragment();
                recorderFragment.setListener(this);
                recorderFragment.show(getSupportFragmentManager(), "");
                break;
            case R.id.add_link:
                TextPrompt prompt = TextPrompt.newInstance();
                prompt.setListener(this);
                prompt.setPrompt(GET_WEB_LINK, getResources().getString(R.string.input_link_prompt));
                prompt.show(getSupportFragmentManager(), "");
                break;
            case R.id.import_audio:
                ImportFileFragment f = ImportFileFragment.newInstance();
                f.setFileRequester(this);
                f.setTag(REQUEST_AUDIO);
                f.show(getSupportFragmentManager(), "");
                break;
            case R.id.capture_image:
                requestImageCapture();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Tell a page to add a tag at the location pointed
     * to by the user.
     * @param tag
     */
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
     * Called by the ImportFileFragment when its done getting
     * the file from the user.
     * @param file
     */
    @Override
    public void onFileObtained(int tag, File file) {

        switch(tag){

            case REQUEST_IMAGE:
                Toast.makeText(this, R.string.image_imported, Toast.LENGTH_SHORT).show();
                addImage(file.getPath());
                break;
            case REQUEST_AUDIO:
                page.addAudioClip(file, textView.getSelectionStart());
                reloadText();
                break;

        }

    }

    /**
     * Add an image to the current page at the position
     * pointed to by the cursor.
     * @param imagePath
     */
    private void addImage(String imagePath){
        saveToPage();
        page.addImage(imagePath, textView.getSelectionStart());
        reloadText();
        jumpToPosition(page.getLastPosition());
    }

    /**
     * Receive requested results from called activities.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //halt if result code is not "ok", or if data is null
        if(resultCode != Activity.RESULT_OK   || data==null){
            return;
        }

        switch (requestCode){

            //in case a doodle was requested from DoodleActivity
            case REQUEST_DOODLE:
                //get the doodle file extra
                File doodleFile = (File)data.getSerializableExtra(DoodleActivity.DOODLE_FILE_EXTRA);
                //put this image file in the page
                addImage(doodleFile.getPath());
                break;
            case REQUEST_IMAGE_CAPTURE:
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                File tmpImg = new File(Paths.APP_DIR_PATH+File.separator+"img_capture");
                FileIO.writeBitmap(imageBitmap, tmpImg.getPath());
                addImage(tmpImg.getPath());
                break;


        }

    }

    /**
     * Save progress if page isn't empty.
     * Delete page if it's empty.
     */
    @Override
    public void onBackPressed() {

        //get the edited text from the edittext view
        String editedText = getEdited();

        //if the edited text is empty, delete the Page
        if(editedText.trim().isEmpty()){
            page.delete();
        }else{
        //else save the progress
            saveProgress();
        }

        //return to the previous activity
        finish();
    }


    /**
     * When edittext gets clicked, the position is conveyed to page,
     * and page returns the file (if any) associated to the clicked paragraph.
     */
    class ParaClickHandler implements View.OnClickListener {

            public void onClick(View v) {
                File audioFile = page.getAudioFile(textView.getSelectionStart());

                if(audioFile==null){
                    return;
                }

                playerFrag = playerFrag==null? AudioPlayerFragment.newInstance(): playerFrag;
                playerFrag.setAudioPlaybackFile(audioFile);
                playerFrag.show(getSupportFragmentManager(), "");
            }

    }

    /**
     * Called by RecorderFragment when it's done recording
     * an audio file.
     * @param audioFile
     */
    @Override
    public void onRecordingReady(File audioFile) {
        page.addAudioClip(audioFile, textView.getSelectionStart());
        reloadText();
    }

    /**
     * Callback for text input prompts.
     * @param tag
     * @param userResponse
     */
    @Override
    public void onTextInputted(String tag, String userResponse) {
        switch (tag){

        case GET_WEB_LINK:
            page.addLink(userResponse, textView.getSelectionStart());
            reloadText();
            break;
        }
    }

    /**
     * Request that a photo be taken and sent back by the camera app.
     */
    private void requestImageCapture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_camera_app, Toast.LENGTH_LONG).show();
        }
    }










}