package com.luxlunaris.noadpadlight.model.classes;

import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.model.interfaces.Booklet;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import java.io.File;
import java.util.ArrayList;

/**
 * Manages the unlucky pages that got deleted.
 */
public class RecycleBin implements Booklet {

    /**
     * Recycle bin directory's path on disk.
     */
    public final String RECYCLE_BIN_DIR;

    /**
     * List of pages in the recycle bin.
     */
    private ArrayList<Page> pages;

    /**
     * The page listener that created this recycle bin.
     */
    private PageListener pageListener;

    public RecycleBin(String recycleBinDir, PageListener pageListener){
        this.RECYCLE_BIN_DIR = recycleBinDir;
        pages = new ArrayList<>();
        this.pageListener = pageListener;
    }


    @Override
    public Page createPage(String name) {
        return null;
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
            pages.remove(page);
            return;
        }

        SinglePage copy = new SinglePage(RECYCLE_BIN_DIR+ File.separator+page.getName());
        copy.create();
        ArrayList<Page> mockList = new ArrayList<>();
        mockList.add(page);
        new Compacter(false).compact(mockList, copy);
        copy.setInRecycleBin(true);

        pages.add(copy);
        copy.addListener(pageListener);

    }

    @Override
    public void remove(Page page) {

    }

    /**
     * Remove a page from the recycle bin and put it back in the main container.
     * @param deletedPage
     */
    public void restore(Page deletedPage){

        if(!deletedPage.isInRecycleBin()){
            return;
        }

        pages.remove(deletedPage);
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
        for(Page page : pages){
            FileIO.deleteDirectory(((File)page).getPath());
            pageListener.onDeleted(page);
        }
        pages.clear();
    }

    /**
     * Get all of the pages in the recycle bin.
     * @return
     */
    public Page[] get(){
        return pages.toArray(new Page[0]);
    }

    /**
     * Load the pages from disk.
     * (To be called once after constructor).
     */
    public void load(){

        File recycleBinDir = new File(RECYCLE_BIN_DIR);

        if(! recycleBinDir.exists()){
            recycleBinDir.mkdirs();
        }

        for(File file : recycleBinDir.listFiles()){
            SinglePage page = new SinglePage(file.getPath());
            pages.add(page);
            page.addListener(pageListener);
        }

    }

    @Override
    public void resort() {

    }

    @Override
    public void getByKeywords(String query) {

    }


    @Override
    public Page[] getNext(int amount) {
        return new Page[0];
    }

    @Override
    public void rewind() {

    }



}
