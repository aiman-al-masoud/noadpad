package com.luxlunaris.noadpadlight.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.SearchView;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.util.ArrayList;

public class PagesActivity extends AppCompatActivity {

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
    final int PAGES_IN_A_BLOCK = 10; //too small makes it impossible to reach the bottom

    /**
     * The page fragments that on screen
     */
    ArrayList<PageFragment> pageFragments;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);
        pagesLinLayout = findViewById(R.id.pages_linear_layout);

        pageFragments = new ArrayList<>();

        //load first block of pages
        loadNextPagesBlock();


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
                    loadNextPagesBlock();
                }

            }
        });
    }


    /**
     * Add a page fragment to the list
     * @param pgFrag
     */
    private void addFragment(PageFragment pgFrag){
        getSupportFragmentManager().beginTransaction().add(pagesLinLayout.getId(),pgFrag,"").commit();
        pageFragments.add(pgFrag);
    }


    /**
     * Loads an array of pages as page fragments
     */
    private void loadPages(Page[] pages){

        for(Page page : pages){
            PageFragment pgFrag = PageFragment.newInstance(page);
            addFragment(pgFrag);
        }

    }


    /**
     * Loads the next block of page fragments
     */
    private void loadNextPagesBlock(){
       loadPages(notebook.getNext(PAGES_IN_A_BLOCK));
    }

    /**
     * Removes all of currently displayed pages
     */
    private void removeAllPages(){
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }


    private void removeFragment(PageFragment pgFrag){
        getSupportFragmentManager().beginTransaction().remove(pgFrag).commit();
    }

    private ArrayList<PageFragment> getSelected(){
        ArrayList<PageFragment> result = new ArrayList<PageFragment>();
        for(PageFragment pgFrag : pageFragments){
            if(pgFrag.isSelected()){
                result.add(pgFrag);
            }
        }
        return result;
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

            case R.id.new_page:
                Page page = notebook.newPage();
                PageFragment pgFrag = PageFragment.newInstance(page);
                addFragment(pgFrag);
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
                                for(PageFragment pgFrag : getSelected()){
                                    pgFrag.delete();
                                    removeFragment(pgFrag);
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


    @Override
    public void onBackPressed() {
        //stay where you are
    }



}