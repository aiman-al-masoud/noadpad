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

public class BasicBooklet implements Booklet {

    /**
     * The path to which all of the existing pages are stored
     */
    private String PAGES_DIR;

    /**
     * List of pages loaded in memory
     */
    private ArrayList<Page> pagesList;

    /**
     * List of pages selected by the user
     */
    static private ArrayList<Page> selectedPagesList;

    /**
     * Current page index
     */
    private int currentPage;

    /**
     * The Notebook observes a BasicBooklet from above.
     */
    private static Notebook listener;


    public BasicBooklet(Notebook listener, String pagesDir){
        this.listener = listener;
        this.PAGES_DIR = pagesDir;
        pagesList = new ArrayList<>();
        selectedPagesList = new ArrayList<>();
        rewind();
    }


    protected void addPage(Page page){
        page.addListener(this);
        pagesList.add(page);
    }


    @Override
    public Page createPage(String name) {
        SinglePage page = new SinglePage(PAGES_DIR+ File.separator+name);
        addPage(page);
        page.create();
        Log.d("NEW_PAGE", "createPage(str) called"+ page.toString()+" listeners"+page.listeners.get(0) );
        return page;
    }

    @Override
    public void remove(Page page) {

    }

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
    public void getByKeywords(String query) {
        Thread t = new Thread() {
            @Override
            public void run() {

                String[] keywords = query.split("\\s+");
                ArrayList<Page> result = new ArrayList<>(pagesList);

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

        Log.d("NOTEBOOK_DELETED_PAGE", "from basic booklet: "+page.toString());

        listener.onDeleted(page);

        pagesList.remove(page);
        selectedPagesList.remove(page);

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
        //calculating the amount of pages left to deliver
        amount = Math.min(amount, pagesList.size() -currentPage );

        List<Page> result = new ArrayList<>();

        try{
            result = pagesList.subList(currentPage, currentPage+amount);
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
        selectedPagesList = new ArrayList<>(pagesList);
    }


    /**
     * Mark all pages as unselected
     */
    public void unselectAll(){
        selectedPagesList.clear();
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
