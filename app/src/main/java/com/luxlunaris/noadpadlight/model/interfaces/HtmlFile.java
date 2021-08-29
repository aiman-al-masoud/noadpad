package com.luxlunaris.noadpadlight.model.interfaces;

import java.io.Serializable;

/**
 * A wrapper interface for a text file that handles an html document.
 */
public interface HtmlFile extends Serializable {

    /**
     * get the html source code in this file.
     * (The raw text with all of the html tags).
     *
     * @return
     */
    String getSourceCode();

    /**
     * Save new/edited source to the text file.
     *
     * @param text
     */
    void setSourceCode(String text);

    /**
     * Get the rendered text without any html tags.
     *
     * @return
     */
    String getRendered();

    /**
     * Surround a full paragraph with an html tag.
     *
     * @param pos
     * @param tag
     */
    void addHtmlTag(int pos, String tag);

    /**
     * Remove all html tags from a paragraph.
     *
     * @param pos
     */
    void removeHtmlTags(int pos);

    /**
     * Replace an existing paragraph with another one.
     *
     * @param replacement
     * @param pos
     */
    void replaceParagraph(String replacement, int pos);

    /**
     * Insert a new paragraph at a specified position.
     *
     * @param content: stuff between the "p" tags, excluding the tags themselves.
     * @param pos
     */
    void insertParagraph(String content, int pos);

    /**
     * Get the paragraph that contains the specified position
     * (position as in the rendered text).
     *
     * @return
     */
    String getParagraphAt(int pos);

    /**
     * From the position in the rendered text, determine
     * the line.
     *
     * @param pos
     * @return
     */
    int getLine(int pos);

    /**
     * Get a text-based preview of this Page.
     * (The first line).
     *
     * @return
     */
    String getPreview();

    /**
     * Insert a link html element at a specified position.
     *
     * @param link
     * @param pos
     */
    void addLink(String link, int pos);

    /**
     * File stuff:
     * @return
     */
    boolean create();
    boolean delete();
    long lastModified();



}
