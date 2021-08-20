package com.luxlunaris.noadpadlight.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.classes.ProxyNotebookListener;
import com.luxlunaris.noadpadlight.control.interfaces.NotebookListener;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * This activity allows the user to access, delete, modify
 * Pages, presenting them on a PageFragment each.
 */
public class PagesActivity extends ColorActivity  implements NotebookListener, YayOrNayDialog.BinaryQuestioner {

    /**
     * The Notebook manages the pages.
     */
    Notebook notebook = Notebook.getInstance();

    /**
     * The layout that hosts the page fragments.
     */
    transient LinearLayout pagesLinLayout;

    /**
     * How many pages are loaded in a batch
     */
    final int PAGES_IN_A_BATCH = 10; //too small makes it impossible to reach the bottom

    /**
     * The page fragments that are on-screen
     */
    transient ArrayList<PageFragment> pageFragments;

    /**
     * Keeps track of changes happening to pages while
     * this activity is in the background, so that
     * onResume can know what fragments to add/remove/modify.
     */
    transient static ProxyNotebookListener changes = new ProxyNotebookListener();

    /**
     * The option (toolbar) menu.
     */
    private Menu optionsMenu;

    /**
     * A callback code.
     */
    private final String QUESTION_EMPTY_RECYCLE_BIN = "EMPTY_RECYCLE_BIN";


    /**
     * On create.
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("BLANK_ON_START", "CREATING PAGES ACTIVITY");

        setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);

        //get the lin layout that will hold the fragments
        pagesLinLayout = findViewById(R.id.pages_linear_layout);

        //initialize list to store fragments
        pageFragments = new ArrayList<>();

        //load first block of pages
        loadNextPagesBlock();

        //defines what the activity does when scrolling occurs
        ScrollView scrollView = findViewById(R.id.scroll_view_pages);

        //scroll listener only compatible with android versions >= marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new ScrollHandler());
        }

        //to keep track of changes while in the background
        changes = new ProxyNotebookListener();

        //start listening to notebook
        notebook.setListener(this);

    }

    /**
     * Used to add more pages when you scroll all the
     * way down.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    class ScrollHandler implements View.OnScrollChangeListener{

        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            //if can't scroll vertically anymore: bottom reached
            if(!v.canScrollVertically(1)){
                loadNextPagesBlock();
            }

        }
    }

    /**
     * Ensures that there is ONE and only ONE fragment for each Page in the fragments list.
     * @param page
     * @return
     */
    private PageFragment getFragment(Page page){

        //check if it's "equal" to an already existing one
        for(PageFragment pgFrag : pageFragments){
            if(pgFrag.getPage().getName().equals(page.getName())){
                return pgFrag;
            }
        }
        //create a new fragment
        return PageFragment.newInstance(page);
    }

    /**
     * Add a page fragment to the list
     * @param page
     */
    private void addPage(Page page, boolean top){

        //get the appropriate page fragment
        PageFragment pgFrag = getFragment(page);

        //the id of the future container of pgFrag
        int containerId = -1;

        if(!top){
            //add the new page fragment to the bottom of the list layout
            containerId = pagesLinLayout.getId();
        }else{
            //else add the new page fragment on top of all others
            FrameLayout child = new FrameLayout(pagesLinLayout.getContext());
            child.setId(new Random().nextInt(1000000000));
            pagesLinLayout.addView(child, 0);
            containerId = child.getId();
        }

        //add the fragment to the screen
        try{
            getSupportFragmentManager().beginTransaction().add(containerId,pgFrag,page.getName()).commit();
            getSupportFragmentManager().executePendingTransactions();
        }catch (Exception | Error  e){
            e.printStackTrace();
        }

        //add the page fragment to the fragment's list
        pageFragments.add(pgFrag);
    }


    /**
     * Loads an array of pages as page fragments
     */
    private void loadPages(Page[] pages){

        for(Page page : pages){
            Log.d("FIRST_IN_LIST", page.getPreview());
            addPage(page, false);
        }

    }

    /**
     * Loads the next block of page fragments
     */
    private void loadNextPagesBlock(){
       loadPages(notebook.getNext(PAGES_IN_A_BATCH));
    }

    /**
     * Removes all of currently displayed pages (without deleting the pages)
     */
    private void removeAllPages(){

        //remove all fragments
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            removeFragment(fragment);
        }

        //make sure they're all removed
        getSupportFragmentManager().executePendingTransactions();

        //forget about all fragments
        pageFragments.clear();

        //tell the notebook that you want to start over cycling through pages.
        notebook.rewind();
    }

    /**
     * Removes a fragment without deleting its page
     * @param page
     */
    private void removeFragment(Page page){
        PageFragment frag = getFragment(page);

        pageFragments.remove(frag);
        removeFragment(frag);
    }

    /**
     * Removes a fragment.
     * @param fragment
     */
    private void removeFragment(Fragment fragment){
        try{
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }catch (Exception | Error e){
            e.printStackTrace();
        }
    }

    /**
     * Enter in the mode which shows the deleted pages
     * in recycle bin.
     */
    private void showRecycleBin(){

        removeAllPages();

        notebook.seeRecycleBin();
        loadNextPagesBlock();

        setTitle(R.string.recycle_bin_title);
        //in options menu
        optionsMenu.findItem(R.id.new_page).setVisible(false);
        optionsMenu.findItem(R.id.load_more_pages).setVisible(false);
        optionsMenu.findItem(R.id.show_recycle_bin).setVisible(false);
        optionsMenu.findItem(R.id.empty_recycle_bin_from_within).setVisible(true);
        optionsMenu.findItem(R.id.restore).setVisible(true);
        optionsMenu.findItem(R.id.compact).setVisible(false);

    }

    /**
     * Exit from the display-recycle-bin-mode.
     */
    private void exitRecycleBin(){
        //in options menu
        optionsMenu.findItem(R.id.new_page).setVisible(true);
        optionsMenu.findItem(R.id.load_more_pages).setVisible(true);
        optionsMenu.findItem(R.id.show_recycle_bin).setVisible(true);
        optionsMenu.findItem(R.id.empty_recycle_bin_from_within).setVisible(false);
        optionsMenu.findItem(R.id.restore).setVisible(false);
        optionsMenu.findItem(R.id.compact).setVisible(true);

        setTitle(R.string.app_name);
        notebook.seePages();

    }


    /**
     * Create the toolbar menu for this activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate the menu's layout xml
        getMenuInflater().inflate(R.menu.pages_activity_toolbar, menu);

        //set option menu to show/hide items from it later
        optionsMenu = menu;

        //if android version < marshmallow, add "load more pages" button as alternative to scrolllistener
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            menu.findItem(R.id.load_more_pages).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        //make a search view for queries on articles
        SearchView searchView = (SearchView)menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                removeAllPages();
                notebook.searchByKeywords(query);
                loadNextPagesBlock();
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
     * Perform an action from the toolbar menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.go_to_settings:
                //start the settings activity
                Intent goToSetIntent = new Intent(this, SettingsActivity.class);
                startActivity(goToSetIntent);
                break;
            case R.id.new_page:
                Intent intent = new Intent(this, ReaderActivity.class);
                intent.putExtra(ReaderActivity.PAGE_EXTRA,notebook.newPage());
                startActivity(intent);
                break;
            case R.id.load_more_pages:
                loadNextPagesBlock();
                break;
            case R.id.show_recycle_bin:
                showRecycleBin();
                break;
            case R.id.empty_recycle_bin_from_within:
                YayOrNayDialog yayOrNayDialog = YayOrNayDialog.newInstance(QUESTION_EMPTY_RECYCLE_BIN, getString(R.string.sure_u_empty_recycle_bin));
                yayOrNayDialog.setListener(this);
                yayOrNayDialog.show(getSupportFragmentManager(), "");
                break;
            case R.id.delete:
                notebook.deleteSelection();
                break;
            case R.id.compact:
                notebook.compactSelection();
                break;
            case R.id.restore:
                notebook.restoreSelection();
                break;
            case R.id.select_all:
                notebook.selectAll();
                break;
            case R.id.unselect_all:
                notebook.unselectAll();
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Handle User responses to yay or nay dialog box questions.
     * @param tag: a request code to identify the question on callback
     * @param result: user response
     */
    @Override
    public void onUserBinaryAnswer(String tag, int result) {

        switch (tag){

            //sure you want to empty recycle bin?
            case QUESTION_EMPTY_RECYCLE_BIN:
                if(result==YayOrNayDialog.POSITIVE_RESPONSE){
                    notebook.emptyRecycleBin();
                }
                break;

        }
    }


    /**
     * Action when the back button is pressed
     */
    @Override
    public void onBackPressed() {

        notebook.exitSearch();
        exitRecycleBin();
        //remove all pages present, and restart adding.
        removeAllPages();
        loadNextPagesBlock();
        notebook.unselectAll();

    }

    /**
     * Called by Notebook when a new page gets created.
     * @param page
     */
    @Override
    public void onCreated(Page page) {

        //if in foreground, just add the page fragment.
        if(isInForeground()) {

            //run on ui thread if onCreated gets called from a different thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addPage(page, true);
                    Log.d("PAGE_FRAGMENTS", "directly, created: "+page.getName());
                }
            });

            return;
        }

        //else you're in background, stash in changes
        changes.onCreated(page);
    }

    /**
     * Called by notebook when a page gets deleted.
     * @param page
     */
    @Override
    public void onDeleted(Page page) {

        //if in foreground simply remove the page's fragment.
        if(isInForeground()){
            removeFragment(page);
            Log.d("PAGE_FRAGMENTS", "directly, deleted: "+page.getName());
            return;
        }

        //else you're in background, stash in changes
        changes.onDeleted(page);
    }

    /**
     * Called by notebook when a page gets modified.
     * @param page
     */
    @Override
    public void onModified(Page page) {

        //if in foreground simply remove and add again
        if(isInForeground()){
            removeFragment(page);
            addPage(page, true);
            Log.d("PAGE_FRAGMENTS", "directly, modified: "+page.getName());
            return;
        }

        //else you're in background, stash in changes
        changes.onModified(page);
    }


    @Override
    public void onSearchResults() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadNextPagesBlock();
            }
        });
    }


    /**
     * On resume, this activity checks if there have been
     * changes to the Pages displayed, and eventually
     * updates the list.
     */
    @Override
    protected void onResume() {
        super.onResume();

        //put the modified pages back on top
        for(Page page : changes.popJustModified()){
            removeFragment(page);
            addPage(page, true);
            Log.d("PAGE_FRAGMENTS", "on resume, modified: "+page.getName());
        }

        //get the pages that were created while this activity was in the
        //background and add the appropriate fragments
        for(Page page : changes.popJustCreated()){
            addPage(page, true);
            Log.d("PAGE_FRAGMENTS", "on resume, created: "+page.getName());
        }

        //get the pages that were deleted while this activity was in the
        //background and remove the relative fragments
        for(Page page : changes.popJustDeleted()){
            removeFragment(page);
            Log.d("PAGE_FRAGMENTS", "on resume, deleted: "+page.getName());
        }

    }


}