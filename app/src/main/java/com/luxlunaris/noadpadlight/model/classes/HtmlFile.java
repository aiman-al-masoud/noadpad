package com.luxlunaris.noadpadlight.model.classes;


import android.text.Html;
import android.util.Log;

import com.luxlunaris.noadpadlight.services.FileIO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A wrapper for a text file that handles an html document.
 */
public class HtmlFile extends File {

    public HtmlFile(String pathname) {
        super(pathname);
    }

    /**
     * get the html source code in this file.
     * (The raw text with all of the html tags).
     * @return
     */
    public String getSourceCode() {
        String text = FileIO.read(getPath());
        return text ==null? "" : text;
    }

    /**
     * Save new/edited source to the text file.
     * @param text
     */
    public void setSourceCode(String text) {
        FileIO.write(getPath(), text);
    }

    /**
     * Get the rendered text without any html tags.
     * @return
     */
    public String getRendered(){
        return Html.fromHtml(getSourceCode()).toString();
    }

    /**
     * Surround some text with an html tag and save.
     * (Works on entire paragraphs.)
     * @param pos
     * @param tag
     */
    public void addHtmlTag(int pos, String tag){
        //replacement = original sandwitched between two tags.
        String startTag = "<"+tag+">";
        String endTag = "</"+tag+">";
        String replacement =startTag+getParagraphs()[lineToParagraph(getLine(pos))]+endTag;
        replaceParagraph(replacement, pos);
    }

    /**
     * Remove all html tags from a paragraph.
     * @param pos
     */
    public void removeHtmlTags(int pos){
        //remove all tags other than the paragraph tag. (sort of)
        String replacement = getParagraphs()[lineToParagraph(getLine(pos))].replaceAll("<[abcefghijklmnoqrstuvwxyz]>", "").replaceAll("</[abcefghijklmnoqrstuvwxyz]>", "");
        replaceParagraph(replacement, pos);
    }

    /**
     * Replace an existing paragraph with another one.
     * @param replacement
     * @param pos
     */
    public void replaceParagraph(String replacement, int pos){

        //get all of the paragraphs
        String[] pars = getParagraphs();

        //get the paragraph num from the position
        int parNum = lineToParagraph(getLine(pos));

        //replace the paragraph
        pars[parNum] = replacement;

        //re-build the html source from paragraph-array.
        String newHtml = "";
        for(String par : pars){
            newHtml+=par;
        }

        //save it.
        setSourceCode(newHtml);
    }

    /**
     * Insert a new paragraph at a specified position.
     * @param content: stuff between the "p" tags, excluding the tags themselves.
     * @param pos
     */
    public void insertParagraph(String content, int pos){

        //GET THE NEW POSITION THE INSERTED PAR SHOULD STAY AT:
        //find paragraph from position
        int parNum = lineToParagraph( getLine(pos));
        //(add the audio "element" as a new paragraph after the one selected, hence: +1)
        parNum+=1;

        //prepare the paragraph:
        String newPar = "<p>"+content+"</p>";

        //get the paragraphs of this page
        String[] pars = getParagraphs();
        //convert the paragraphs array to a mutable list
        List<String> parsList = new ArrayList<>(Arrays.asList(pars));
        //add a new audio "element" at the specified position.
        parsList.add(parNum, newPar);
        //recompose the html source from the paragraphs' list.
        String newHtml = "";
        for(String par : parsList){
            newHtml+=par;
        }
        //save the new html source
        setSourceCode(newHtml);

    }

    /**
     * Get the paragraph that contains the specified position
     * (position as in the rendered text).
     * @return
     */
    public String getParagraphAt(int pos){

        try{
            return getParagraphs()[lineToParagraph(getLine(pos))];
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * From the position in the rendered text, determine
     * the line.
     * @param pos
     * @return
     */
    public int getLine(int pos){

        String text = getRendered();

        //if length
        if(text.length()==0){
            return 0;
        }

        String upTillPos ="";
        try{
            upTillPos = text.substring(0, Math.min(pos, text.length()-1));
        }catch (IndexOutOfBoundsException e){

        }

        int newLines = upTillPos.split("\n").length;

        return newLines;
    }

    /**
     * Get a text-based preview of this Page.
     * (The first line).
     * @return
     */
    public String getPreview() {
        return FileIO.readLine(getPath())+"\n";
    }

    /**
     * Get the html source as a list of paragraphs.
     * @return
     */
    protected String[] getParagraphs(){
        //split the html source by end of paragraph tags
        String[] pars = getSourceCode().split("</p>");

        //remove the last empty "paragraph"
        pars = Arrays.copyOf(pars, pars.length-1);

        //adjust each paragraph
        for(int i =0; i<pars.length; i++){
            pars[i] = pars[i].replaceAll("\n", "");
            pars[i] = pars[i]+" </p>";
        }

        return pars;
    }


    /**
     * Given a line number find the paragraph containing it.
     * @param lineNum
     * @return
     */
    protected int lineToParagraph(int lineNum){

        //get the paragraphs in this Page
        String[] pars = getParagraphs();

        //get the number of lines in each paragraph
        int[] numLinesPerPar =  new int[pars.length];
        for(int i =0; i<pars.length; i++){
            numLinesPerPar[i] = pars[i].split("<br>").length;
            if(numLinesPerPar[i]==1){
                numLinesPerPar[i] = 2;
            }
        }

        //convert the lineNum to a paragraph num
        int accumulLines = 0;
        for(int i =0; i<numLinesPerPar.length; i++){
            accumulLines += numLinesPerPar[i];
            if(lineNum <= accumulLines){
                Log.d("LINE_NUM", "line "+lineNum+ " is in paragraph: "+i);
                return i;
            }
        }

        return numLinesPerPar.length-1;
    }


    /**
     * Insert a link html element at a specified position.
     * @param link
     * @param pos
     */
    public void addLink(String link, int pos){

        if(!link.contains("http")){
            link = "http://"+link;
        }

        //stupid way of "cleaning" the link a bit for presentation.
        String linkName = link;
        linkName = linkName.replace("https", "").replace("http","").replace("://", "").replace("www", "").replaceAll("\\W*", "");

        String content = "<a href='"+link+"'>"+linkName+"</a>";

        insertParagraph(content, pos);
    }

















}
