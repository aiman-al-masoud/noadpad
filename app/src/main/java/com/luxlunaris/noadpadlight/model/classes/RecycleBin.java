package com.luxlunaris.noadpadlight.model.classes;

import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import java.io.File;
import java.util.ArrayList;

/**
 * Manages the unlucky pages that got deleted.
 */
public class RecycleBin extends BasicBooklet  {

    /**
     * Recycle bin directory's path on disk.
     */
    public final String RECYCLE_BIN_DIR;

    /**
     * The page listener that created this recycle bin.
     */
    private PageListener pageListener;

    public RecycleBin(String recycleBinDir, PageListener pageListener){
        super((Notebook) pageListener, recycleBinDir);
        this.RECYCLE_BIN_DIR = recycleBinDir;
        this.pageListener = pageListener;
    }


    /**
     * Put a page in the recycle bin:
     * this creates a copy of the original page
     * and places it in an alternate directory
     * @param page
     */
    public void put(Page page){

        //if page is already in the recycle bin, remove it.
        //It means it's getting deleted forever.
        if(page.isInRecycleBin()){
            pagesList.remove(page);
            return;
        }

        SinglePage copy = new SinglePage(RECYCLE_BIN_DIR+ File.separator+page.getName());
        copy.create();
        ArrayList<Page> mockList = new ArrayList<>();
        mockList.add(page);
        new Compacter(false).compact(mockList, copy);
        copy.setInRecycleBin(true);

        addPage(copy);
    }

    /**
     * Remove a page from the recycle bin and put it back in the main container.
     * @param deletedPage
     */
    public void restore(Page deletedPage){

        if(!deletedPage.isInRecycleBin()){
            return;
        }

        pagesList.remove(deletedPage);
        Page restoredCopy = Notebook.getInstance().newPage(deletedPage.getName());
        ArrayList<Page> mockList = new ArrayList<>();
        mockList.add(deletedPage);
        new Compacter(false).compact(mockList, restoredCopy);
        restoredCopy.setInRecycleBin(false);
        deletedPage.delete();
    }

    /**
     * Clear the recycle bin (destroys all data permanently).
     */
    public void clear(){
        for(Page page : pagesList){
            FileIO.deleteDirectory(((File)page).getPath());
            pageListener.onDeleted(page);
        }
        pagesList.clear();
    }

    /**
     * Get all of the pages in the recycle bin.
     * @return
     */
    public Page[] get(){
        return pagesList.toArray(new Page[0]);
    }













}
