package com.luxlunaris.noadpadlight.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

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
     * How many pages should be on screen at any time.
     */
    final int PAGES_IN_A_BLOCK = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);
        pagesLinLayout = findViewById(R.id.pages_linear_layout);

        //load first block of pages
        loadNextPagesBlock();

    }


    /**
     * Loads an array of pages as page fragments
     */
    private void loadPages(Page[] pages){
        for(Page page : pages){
            getSupportFragmentManager().beginTransaction().add(pagesLinLayout.getId(),PageFragment.newInstance(page),"").commit();
        }
    }


    /**
     * Loads the next block of page fragments
     */
    private void loadNextPagesBlock(){
       loadPages(notebook.getNext(PAGES_IN_A_BLOCK));
    }

    /**
     * Loads the previous block of page fragments
     */
    private void loadPreviousPagesBlock(){
        loadPages(notebook.getPrevious(PAGES_IN_A_BLOCK));
    }


    /**
     * Removes all of currently displayed pages
     */
    private void removeAllPages(){
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
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

            case R.id.next:
                removeAllPages();
                loadNextPagesBlock();
                break;
            case R.id.previous:
                removeAllPages();
                loadPreviousPagesBlock();
                break;
            case R.id.new_page:
                Page page = notebook.newPage();
                Intent intent = new Intent(this, ReaderActivity.class);
                intent.putExtra("PAGE",page);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }



}