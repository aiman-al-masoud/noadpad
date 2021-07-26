package com.luxlunaris.noadpadlight.control.classes;

import com.luxlunaris.noadpadlight.control.interfaces.NotebookListener;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.util.ArrayList;

/**
 * Listens to Notebook and keeps track of all of the changes
 * that occur to the Pages stored therein, so that the GUI
 * in charge of displaying the Pages can know what changes were made
 * while it was in the background.
 */
public class ProxyNotebookListener implements NotebookListener {


    /**
     * Buffers to keep track of all of the changes made to Pages
     * while concerned GUI is in the background, and needed upon restart.
     */
    static private ArrayList<Page> justDeletedList = new ArrayList<>();
    static private ArrayList<Page> justCreatedList = new ArrayList<>();
    static private ArrayList<Page> justModifiedList = new ArrayList<>();


    /**
     * Called by Notebook when a Page gets created.
     * Adds the Page to the "justCreatedList".
     * @param page
     */
    @Override
    public void onCreated(Page page) {
        justCreatedList.add(page);
    }

    /**
     * Called by Notebook when a Page gets deleted.
     * Adds the Page to the "justDeletedList",
     * and removes it from anywhere else.
     * @param page
     */
    @Override
    public void onDeleted(Page page) {
        justDeletedList.add(page);
        justCreatedList.remove(page);
        justModifiedList.remove(page);
    }

    /**
     * Called by Notebook when a Page gets modified.
     * Adds the Page to the "justModifiedList".
     * @param page
     */
    @Override
    public void onModified(Page page) {

        //add a page to the list of modified items,
        //except if it was just created.
        if(!justCreatedList.contains(page)){
            justModifiedList.add(page);
        }

    }


    /**
     * Returns just deleted pages, forgetting about them.
     * @return
     */
    public Page[] popJustDeleted(){
        Page[] result = justDeletedList.toArray(new Page[0]);
        justDeletedList.clear();
        return result;
    }

    /**
     * Returns just modified pages, forgetting about them.
     * @return
     */
    public Page[] popJustModified(){
        Page[] result = justModifiedList.toArray(new Page[0]);
        justModifiedList.clear();
        return result;
    }

    /**
     * Returns just created pages, forgetting about them.
     * @return
     */
    public Page[] popJustCreated(){
        Page[] result = justCreatedList.toArray(new Page[0]);
        justCreatedList.clear();
        return result;
    }












}
