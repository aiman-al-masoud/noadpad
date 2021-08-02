package com.luxlunaris.noadpadlight.model.classes;

import com.luxlunaris.noadpadlight.model.interfaces.Page;

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
        String tail ="--------\n";

        for(Page page : pages){

            head = new Date(page.getLastModifiedTime()).toString()+"\n";
            textBlob+=head;
            textBlob+=page.getText();
            textBlob+=tail;
        }

        blankPage.setText(textBlob);

        //TODO: migrate images

    }




}
