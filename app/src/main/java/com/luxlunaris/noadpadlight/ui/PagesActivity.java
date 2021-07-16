package com.luxlunaris.noadpadlight.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.interfaces.NotebookListener;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.util.ArrayList;
import java.util.Random;

public class PagesActivity extends AppCompatActivity{

    /**
     * The Notebook manages the pages.
     */
    Notebook notebook = Notebook.getInstance();



    /**
     * The layout that hosts the page fragments.
     */
    LinearLayout pagesLinLayout;

    /**
     * How many pages are loaded in a batch
     */
    final int PAGES_IN_A_BATCH = 10; //too small makes it impossible to reach the bottom

    /**
     * The page fragments that are on-screen
     */
    ArrayList<PageFragment> pageFragments;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);


        //get the lin layout that will hold the fragments
        pagesLinLayout = findViewById(R.id.pages_linear_layout);

        //initialize list to store fragments
        pageFragments = new ArrayList<>();

        //load first block of pages
        loadNextPagesBlock();

        //defines what the activity does when scrolling occurs
        setOnScrollAction();
    }


    /**
     * Defines the scroll behavior of this activity
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setOnScrollAction(){
        ScrollView scrollView = findViewById(R.id.scroll_view_pages);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                //if can't scroll vertically anymore: bottom reached
                if(!v.canScrollVertically(1)){

                    try{
                        loadNextPagesBlock();
                    }catch (Exception e){

                    }
                }

            }
        });
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

        if(!top){
            //add the new page fragment to the bottom of the list layout
            getSupportFragmentManager().beginTransaction().add(pagesLinLayout.getId(),pgFrag,page.getName()).commit();
        }else{
            //else add the new page fragment on top of all others
            FrameLayout child = new FrameLayout(pagesLinLayout.getContext());
            child.setId(new Random().nextInt(1000000000));
            pagesLinLayout.addView(child, 0);
            getSupportFragmentManager().beginTransaction().add(child.getId(),pgFrag,page.getName()).commit();

        }

        //add the page fragment to the fragment's list
        pageFragments.add(pgFrag);
    }






    /**
     * Loads an array of pages as page fragments
     */
    private void loadPages(Page[] pages){

        for(Page page : pages){

            try{
                addPage(page, false);
            }catch (IllegalStateException e){

            }

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
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        pageFragments.clear();
    }


    /**
     * Removes a fragment without deleting its page
     * @param page
     */
    private void removeFragment(Page page){
        PageFragment frag = getFragment(page);
        pageFragments.remove(frag);
        getSupportFragmentManager().beginTransaction().remove(frag).commit();
    }

    /**
     * Deletes a page and its corresponding fragment
     * @param page
     */
    private void deletePage(Page page){
        removeFragment(page);
        page.delete();
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

        //make a search view for queries on articles
        SearchView searchView = (SearchView)menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Page[] result = notebook.getByKeywords(query);
                removeAllPages();
                loadPages(result);
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
                Page page = notebook.newPage();
                addPage(page, true);
                Intent intent = new Intent(this, ReaderActivity.class);
                intent.putExtra("PAGE",page);
                startActivity(intent);
                break;
            case R.id.edit:
                PopupMenu editMenu = new PopupMenu(this, findViewById(R.id.edit));
                editMenu.getMenuInflater().inflate(R.menu.edit_menu, editMenu.getMenu());

                editMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                for(Page page : notebook.getSelected()){
                                    deletePage(page);
                                }
                                break;
                        }


                        return true;
                    }
                });

                editMenu.show();

                break;


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Action when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        if(notebook.getPagesNum() > pageFragments.size()){
            removeAllPages();
            notebook.selectAll();
            loadPages(notebook.getSelected());
            notebook.unselectAll();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //get the pages that were deleted while this activity was in the
        //background and remove the relative fragments
        for(Page page : notebook.getJustDeleted()){
            removeFragment(page);
        }

        //get the pages that were created while this activity was in the
        //background and add the appropriate fragments
        for(Page page : notebook.getJustCreated()){
            try{
                addPage(page, true);
            }catch (IllegalStateException e){

            }
        }

    }




}