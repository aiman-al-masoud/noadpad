package com.luxlunaris.noadpadlight.model.classes;

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

            //get the date-last-modified string in with locale settings
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm");
            String dateString = dateFormat.format(new Date(page.getLastModifiedTime()));

            head = dateString+"\n";
            textBlob+=head;
            textBlob+=page.getText()+"\n\n";
        }

        //replace the name of the other pages in the paths with the name of this page.
        textBlob = textBlob.replaceAll("/pages/\\d+/images/", "/pages/"+blankPage.getName()+"/images/");

        //set the text of the blank page
        blankPage.setText(textBlob);

        //migrate images from old pages to blank page.
        for(Page page : pages){
            File[] imageFiles = page.getImageDir().listFiles();
            FileIO.copyFilesToDirectory(imageFiles, blankPage.getImageDir().getPath());
        }

    }




}
