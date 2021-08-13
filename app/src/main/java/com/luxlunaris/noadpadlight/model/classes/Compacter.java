package com.luxlunaris.noadpadlight.model.classes;

import android.util.Log;

import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Combines old pages into a single new one.
 */
public class Compacter {

    boolean withHead = true;

    public Compacter(){

    }

    public Compacter(boolean withHead){
        this.withHead = withHead;
    }


    /**
     * Takes a blank Page and a list of pages,
     * writes the "aggregate" content of the list of Pages
     * onto the blank page.
     * @param pages
     * @param blankPage
     */
    public void compact(List<Page> pages, Page blankPage){

        String textBlob = "";
        String head = "";

        //lump all of the text content of the pages together.
        for(Page page : pages){


            if(withHead){

                //get the date-last-modified string in with locale settings
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm");
                String dateString = dateFormat.format(new Date(page.getLastModifiedTime()));

                head = dateString+"\n";
                textBlob+=head;
            }

            textBlob+=page.getText()+"\n\n";
        }


        Log.d("COMPACTER", "text blob before: "+textBlob+"\n\n\n");

        //this is the path in the html that will replace the older ones.
        String blankPagesPath = ((File)blankPage).getPath();
        //due to slight differences across devices in initial part of path, just replace the part from "com.luxlunaris..." onwards
        int start = blankPagesPath.indexOf("com");
        blankPagesPath = blankPagesPath.substring(start, blankPagesPath.length()-1);


        Log.d("COMPACTER", "replacement: " +blankPagesPath);


        //migrate images from old pages to blank page.
        for(Page page : pages){
            //textBlob = textBlob.replaceAll(((File)page).getPath(), ((File)blankPage).getPath());

            Log.d("COMPACTER", "to be replaced: "+((File)page).getPath());

            //these are the paths to be replaced
            String oldPathToBeReplaced = ((File)page).getPath();
            //due to slight differences across devices in initial part of path, just replace the part from "com.luxlunaris..." onwards
            start = oldPathToBeReplaced.indexOf("com");
            oldPathToBeReplaced = oldPathToBeReplaced.substring(start, oldPathToBeReplaced.length()-1);

            //replace all instances of old path w/ path of new blank page
            textBlob = textBlob.replaceAll(oldPathToBeReplaced, blankPagesPath);

            //copy the actual image files to the new blank page's directory
            File[] imageFiles = page.getImageDir().listFiles();
            FileIO.copyFilesToDirectory(imageFiles, blankPage.getImageDir().getPath());

        }

        Log.d("COMPACTER", "text blob after: "+textBlob+"\n\n\n");


        //set the text of the blank page
        blankPage.setText(textBlob);


    }




}
