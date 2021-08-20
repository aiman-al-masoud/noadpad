package com.luxlunaris.noadpadlight.model.classes;

import android.util.Log;

import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.classes.Paths;
import com.luxlunaris.noadpadlight.model.classes.comparators.LastModifiedComparator;
import com.luxlunaris.noadpadlight.model.interfaces.Booklet;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A booklet manages a set of pages stored on a folder on disk.
 */
public class BasicBooklet implements Booklet {

    /**
     * The path to which all of the existing pages are stored
     */
    private String PAGES_DIR;

    /**
     * List of pages loaded in memory
     */
    protected ArrayList<Page> pagesList;

    /**
     * The list of pages that is currently gonna be
     * used by getNext() to procure pages for who's asking.
     */
    protected ArrayList<Page> listOnDisplay;

    /**
     * List of pages selected by the user
     */
    static protected ArrayList<Page> selectedPagesList;

    /**
     * Current page index
     */
    protected int currentPage;

    /**
     * The Notebook observes a BasicBooklet from above.
     */
    protected static Notebook listener;




    private Page seventiesTest;


    public BasicBooklet(Notebook listener, String pagesDir){
        this.listener = listener;
        this.PAGES_DIR = pagesDir;
        pagesList = new ArrayList<>();
        selectedPagesList = new ArrayList<>();
        rewind();

        listOnDisplay = pagesList;
    }


    /**
     * Add a page to the list and start listening to it.
     * @param page
     */
    protected void addPage(Page page){
        page.addListener(this);
        pagesList.add(0, page);
    }

    /**
     * Create a new page in this Booklet's directory.
     * @param name
     * @return
     */
    @Override
    public Page createPage(String name) {
        SinglePage page = new SinglePage(PAGES_DIR+ File.separator+name);
        addPage(page);
        page.create();
        return page;
    }

    /**
     * Load pages from disk.
     * (To be called once after constructor).
     */
    @Override
    public void load() {

        //create the pages dir if it doesn't exist yet
        File pagesDir = new File(PAGES_DIR);
        if(!pagesDir.exists()){
            pagesDir.mkdirs();
        }

        //list and load all of the folders in there
        for(File file : pagesDir.listFiles()) {
            Page page = new SinglePage(file.getPath());
            addPage(page);
        }

        //sort the pages by time of creation
        Collections.sort(pagesList, new LastModifiedComparator());
    }

    /**
     * Returns an array of the selected pages
     */
    public Page[] getSelected(){
        return selectedPagesList.toArray(new Page[0]);
    }


    @Override
    public void searchByKeywords(String query) {


        //TODO: put this back into a thread.

        ArrayList<Page> results = new ArrayList<>();

        String[] keywords = query.split("\\s+");

        for (Page page : pagesList) {
            if(page.contains(keywords)){
                results.add(page);
            }
        }

        listOnDisplay = results;
        rewind();



        /*
        Thread t = new Thread() {
            @Override
            public void run() {

                String[] keywords = query.split("\\s+");

                for (Page page : pagesList) {
                    if (page.contains(keywords)) {

                        //as soon as you find a page that fits the keywords tell the
                        //listener to display it.
                        listener.onCreated(page);

                    }
                }

            }
        };

        t.start();

         */





    }

    @Override
    public File exportPages() {
        return FileIO.zipDir(PAGES_DIR, Paths.PAGES_BACKUP_DIR);
    }

    @Override
    public void importPages(String sourcePath) {
        File unzipped = FileIO.unzipDir(sourcePath, sourcePath+"unzipped");

        File pagesFolder = new File(unzipped.getPath()+File.separator+"pages");

        for(File file : pagesFolder.listFiles()){

            //copy each file from the unzipped file
            try {
                FileUtils.copyDirectory(file, new File(PAGES_DIR+File.separator+file.getName()));
                Page page = new SinglePage(file.getPath());
                addPage(page);
                listener.onCreated(page);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Collections.sort(pagesList, new LastModifiedComparator());
        }
    }

    @Override
    public void exitSearch() {
        listOnDisplay = pagesList;
        rewind();
    }

    @Override
    public void deleteSelection() {
        for(Page page : getSelected()){
            page.delete();
        }
    }

    @Override
    public void onSelected(Page page) {
        Log.d("SELECTED", page.toString()+"got selected");
        if(page.isSelected()){
            selectedPagesList.add(page);
        }else{
            selectedPagesList.remove(page);
        }
    }

    @Override
    public void onDeleted(Page page) {

        seventiesTest = page;

        //TODO: figure out why this stupid Page isn't getting out of the way, despite getting removed.
        Log.d("70s", "from basic booklet, on deleted page: "+page.toString());
        listener.onDeleted(page);
        pagesList.remove(page);
        selectedPagesList.remove(page);
        listOnDisplay.remove(page);
        Log.d("70s", "contained "+pagesList.contains(page)+" "+listOnDisplay.contains(page)+" "+selectedPagesList.contains(page));

    }

    @Override
    public void onModified(Page page) {

        Collections.sort(pagesList, new LastModifiedComparator());
        listener.onModified(page);

    }

    @Override
    public void onCreated(Page page) {

        if(!pagesList.contains(page)){
            pagesList.add(page);
        }

        listener.onCreated(page);
    }

    @Override
    public Page[] getNext(int amount) {


        Log.d("70s", "contains: "+listOnDisplay.contains(seventiesTest));

        //TODO: this is expensive!!! And it's not a permanent fix
        for(int i =0; i<listOnDisplay.size(); i++){
            if(listOnDisplay.get(i).getPreview().trim().isEmpty()){
                listOnDisplay.remove(i);
            }
        }


        //calculating the amount of pages left to deliver
        amount = Math.min(amount, listOnDisplay.size() -currentPage );

        List<Page> result = new ArrayList<>();

        try{
            result = listOnDisplay.subList(currentPage, currentPage+amount);
            currentPage+=amount;
        }catch (Exception e){

        }

        return result.toArray(new Page[0]);
    }

    @Override
    public void rewind() {
        currentPage = 0;
    }


    /**
     * Mark all Pages as selected
     */
    public void selectAll(){
        for(Page page : listOnDisplay){
            page.setSelected(true);
        }
    }


    /**
     * Mark all pages as unselected
     */
    public void unselectAll(){
        for(Page page : listOnDisplay){
            page.setSelected(false);
        }
    }

    /**
     * Create a new page that has all of the contents of the selected pages,
     * and delete the selected pages.
     */
    public void compactSelection(){

        //create a new blank page in this booklet's folder
        Page page = createPage(System.currentTimeMillis()+"");

        //write the contents of the selected pages onto the blank page
        new Compacter().compact(selectedPagesList, page);

        //copy due to concurrent modification exception
        ArrayList<Page> copy = new ArrayList<>(selectedPagesList);
        //delete the old pages
        for(int i=0; i<copy.size(); i++){
            copy.get(i).delete();
        }

    }




}
