package com.luxlunaris.noadpadlight.model.classes;

import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class RecycleBin implements Serializable {

    public final String RECYCLE_BIN_DIR;

    private ArrayList<Page> pages;

    private PageListener pageListener;

    public RecycleBin(String recycleBinDir, PageListener pageListener){
        this.RECYCLE_BIN_DIR = recycleBinDir;
        pages = new ArrayList<>();
        this.pageListener = pageListener;
    }


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

        //return copy;


        //copy.addListener(this);
    }


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

        //return restoredCopy;
    }


    public void clear(){
        for(Page page : pages){
            //FileIO.deleteDirectory(((File)page).getPath() );
            //listener.onDeleted(page);
            page.delete();
        }
        pages.clear();
    }


    public Page[] get(){
        return pages.toArray(new Page[0]);
    }


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















}
