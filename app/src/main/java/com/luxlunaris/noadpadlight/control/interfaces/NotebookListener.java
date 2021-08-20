package com.luxlunaris.noadpadlight.control.interfaces;

import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.io.Serializable;

/**
 * Listens to the Notebook for updates about the status
 * of created, deleted, modified Pages.
 */
public interface NotebookListener extends Serializable {

    /**
     * Called when a page gets created.
     * @param page
     */
    public void onCreated(Page page);


    /**
     * Called when a page gets deleted.
     * @param page
     */
    public void onDeleted(Page page);

    /**
     * Called when a page gets modified.
     * @param page
     */
    public void onModified(Page page);

    /**
     * NotebookListener needs to know if anything at all is
     * selected or not.
     * @param something
     */
    public void onSelected(boolean something);

    /**
     * Called when search results are ready.
     */
    public void onSearchResults();


}
