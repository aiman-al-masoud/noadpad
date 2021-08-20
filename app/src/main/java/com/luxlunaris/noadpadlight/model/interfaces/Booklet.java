package com.luxlunaris.noadpadlight.model.interfaces;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.control.interfaces.Pageable;
import com.luxlunaris.noadpadlight.model.classes.Compacter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public interface Booklet extends Pageable, Serializable, PageListener {



    Page createPage(String name);


    void load();

    Page[] getSelected();

    /**
     * Mark all Pages as selected
     */
    public void selectAll();

    /**
     * Mark all pages as unselected
     */
    public void unselectAll();



    /**
     * Create a new page that has all of the contents of the selected pages,
     * and delete the selected pages.
     */
    public void compactSelection();

    void searchByKeywords(String query);


    File exportAllPages();

    File exportSelected();


    void importPages(String sourcePath);


    void exitSearch();

    void deleteSelection();


}
