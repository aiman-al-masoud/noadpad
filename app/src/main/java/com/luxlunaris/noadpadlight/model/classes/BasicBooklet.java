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
    protected volatile ArrayList<Page> pagesList;

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

        new Thread(){

            public void run(){

                ArrayList<Page> results = new ArrayList<>();

                listOnDisplay = results;
                rewind();

                String[] keywords = query.split("\\s+");

                for (Page page : new ArrayList<>(pagesList)) {
                    if(page.contains(keywords)){
                        results.add(page);

                       if(results.size() >=10){
                            listener.onSearchResults();
                       }

                    }
                }

                listener.onSearchResults();
            }

        }.start();

    }

    @Override
    public File exportAllPages() {
        return FileIO.zipDir(PAGES_DIR, Paths.PAGES_BACKUP_DIR);
    }



    @Override
    public File exportSelected() {

        Page[] pages = getSelected();

        File[] files = new File[pages.length];

        for(int i =0; i<pages.length; i++){
            files[i] = (File)pages[i];
        }

        FileIO.deleteDirectory(Paths.TMP_DIR);
        String dir = new File(Paths.TMP_DIR+File.separator+"pages").getPath();

        FileIO.copyFilesToDirectory(files,dir );

        return FileIO.zipDir(Paths.TMP_DIR+File.separator+"pages", Paths.PAGES_BACKUP_DIR);
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

        //remove deleted page from pagesList
        for(Page p : new ArrayList<>(pagesList)){
            if(p.getName().equals(page.getName())){
                pagesList.remove(p);
            }
        }

        //remove deleted page from selectedPagesList
        for(Page p : new ArrayList<>(selectedPagesList)){
            if(p.getName().equals(page.getName())){
                selectedPagesList.remove(p);
            }
        }


        listener.onDeleted(page);
        //pagesList.remove(page);
        //selectedPagesList.remove(page);
        listOnDisplay.remove(page);
    }

    @Override
    public void onModified(Page page) {

        Collections.sort(pagesList, new LastModifiedComparator());
        Log.d("FIRST_IN_LIST", pagesList.get(0).getPreview());
        Log.d("FIRST_IN_LIST", listOnDisplay.get(0).getPreview());
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

        //TODO: why does this have to be called here, if it's already been called in onModified?????!
        Collections.sort(pagesList, new LastModifiedComparator());

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
