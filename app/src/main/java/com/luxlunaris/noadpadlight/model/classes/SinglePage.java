package com.luxlunaris.noadpadlight.model.classes;

import android.util.Log;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.model.exceptions.WrongTagTypeException;
import com.luxlunaris.noadpadlight.model.interfaces.HtmlFile;
import com.luxlunaris.noadpadlight.model.interfaces.Metadata;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.interfaces.WordCounter;
import com.luxlunaris.noadpadlight.services.FileIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * SinglePage is a persistent implementation of the Page interface.
 */

public class SinglePage extends File implements Page {

    /**
     * manages this Page's stored metadata
     */
    Metadata metadata;

    /**
     * manages this Page's html source-code text
     */
    HtmlFile htmlFile;

    /**
     * Directory that holds this Page's images.
     */
    private File imageDir;

    /**
     * Directory that holds this Page's audio files.
     */
    private File audioDir;

    /**
     * List of all directories containing multimedia resources for this Page.
     */
    private File[] mediaDirs;

    /**
     * this Page's listeners (Notebook)
     */
    ArrayList<PageListener> listeners;

    /**
     * true if this Page is currently "selected"
     */
    boolean selected = false;

    /**
     * Helps w/ token finding and counting.
     */
    WordCounter wordCounter;

    public SinglePage(String pathname) {
        super(pathname);
        metadata = new MetadataFile(getPath() + File.separator + "metadata");
        htmlFile = new BasicHtmlFile(getPath() + File.separator + "text");
        imageDir = new File(getPath() + File.separator + "images");
        audioDir = new File(getPath() + File.separator + "audios");
        mediaDirs = new File[]{imageDir, audioDir};
        listeners = new ArrayList<>();
    }


    /**
     * Get this Page's WordCounter object.
     *
     * @return
     */
    private WordCounter getWordCounter() {
        if (wordCounter == null) {
            wordCounter = new BasicWordCounter(getRendered());
        }
        return wordCounter;
    }


    /**
     * get this Page's text from the text file.
     * (The raw text with all of the html tags).
     *
     * @return
     */
    @Override
    public String getSourceCode() {
        return htmlFile.getSourceCode();
    }

    /**
     * Save new/edited text to the text file.
     *
     * @param text
     */
    @Override
    public void setSourceCode(String text) {

        if (!getBoolean(TAG_EDITABLE)) {
            return;
        }

        htmlFile.setSourceCode(text);

        try{
            for (PageListener listener : listeners) {
                listener.onModified(this);
            }
        }catch (ConcurrentModificationException e){
            e.printStackTrace();
        }

        //delete any no-longer needed media files.
        for (File dir : mediaDirs) {
            checkDirForDeadFiles(dir);
        }

    }

    /**
     * Get the text of this Page without any html tags.
     *
     * @return
     */
    @Override
    public String getRendered() {
        return htmlFile.getRendered();
    }


    /**
     * Delete this page and all of its contents from disk
     *
     * @return
     */
    @Override
    public boolean delete() {

        //notify the listeners before deletion happens.
        try{
            for (PageListener listener : listeners) {
                listener.onDeleted(this);
            }
        }catch (ConcurrentModificationException e){
            e.printStackTrace();
        }


        FileIO.deleteDirectory(this.getPath());
        return true;
    }

    /**
     * Create this Page on disk as a directory
     */
    @Override
    public boolean create() {

        mkdir();

        try {
            ((MetadataFile) metadata).createNewFile();
            htmlFile.create();
            imageDir.mkdir();
            audioDir.mkdir();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        //notify the listeners that this got created
        for (PageListener listener : listeners) {
            listener.onCreated(this);
        }

        return true;
    }


    /**
     * Get this Page's name
     *
     * @return
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Get the time of creation of this Page
     *
     * @return
     */
    @Override
    public long getCreationTime() {
        return Long.parseLong(getName());
    }

    /**
     * Get this time this Page got last modified
     *
     * @return
     */
    @Override
    public long lastModified() {
        long mod = htmlFile.lastModified();

        if(mod>=Long.MAX_VALUE  | mod<=Long.MIN_VALUE){
            return 0;
        }

        return mod;
    }

    /**
     * How many times does a token appear in this Page's text?
     *
     * @param token
     * @return
     */
    @Override
    public int numOfTokens(String token) {
        return getWordCounter().numOfTokens(token);
    }

    /**
     * Set the token to be found in this Page
     *
     * @param token
     */
    @Override
    public void setTokenToBeFound(String token) {
        getWordCounter().setTokenToBeFound(token);
    }

    /**
     * Get the next position of the currently sought-after token
     *
     * @return
     */
    @Override
    public int nextPosition() {
        return getWordCounter().nextPosition();
    }

    /**
     * Get the previous position of the currently sought-after token
     *
     * @return
     */
    @Override
    public int previousPosition() {
        return getWordCounter().previousPosition();
    }

    /**
     * Set a "bookmark" within this page
     *
     * @param pos
     */
    @Override
    public void savePosition(int pos) {
        metadata.setTag("LAST_POSITION", pos + "");
    }

    /**
     * Get this Page's "bookmark", aka last position visited.
     *
     * @return
     */
    @Override
    public int getLastPosition() {
        String lastPosString = metadata.getString("LAST_POSITION") == null ? "0" : metadata.getString("LAST_POSITION");
        return Integer.parseInt(lastPosString);
    }

    /**
     * Add a PageListener to this Page
     *
     * @param listener
     */
    @Override
    public void addListener(PageListener listener) {
        listeners.add(listener);
    }

    /**
     * Get a text-based preview of this Page.
     * (The first line).
     *
     * @return
     */
    @Override
    public String getPreview() {
        return htmlFile.getPreview();
    }

    /**
     * Checks if this page contains ALL of the provided keywords
     * (ANDed keywords)
     *
     * @param keywords
     * @return
     */
    public boolean contains(String[] keywords) {

        String text = getRendered().toUpperCase();
        for (String keyword : keywords) {
            if (!text.contains(keyword.toUpperCase())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is this Page's "selected" flag true?
     *
     * @return
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set this Page's "selected" flag
     *
     * @param selected
     */
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        //notify the listener
        for (PageListener listener : listeners) {
            listener.onSelected(this);
        }
    }


    /**
     * Generate an image "tag" given its path.
     *
     * @param path
     * @return
     */
    private String generateImgTag(String path) {
        //opening and closing tags
        String openImgTag = "<img src=\'";
        String closeImgTag = "\' />";
        String element = openImgTag + path + closeImgTag;
        return element;
    }

    /**
     * Add an image to this Page.
     *
     * @param path
     */
    @Override
    public void addImage(String path, int pos) {

        //prepare a new file in this Page's imgDir
        File imageCopy = new File(imageDir.getPath() + File.separator + System.currentTimeMillis());
        //move provided image to this Page's imgDir
        FileIO.moveFile(path, imageCopy.getPath());
        //create the image element in html
        String imgElement = generateImgTag(imageCopy.getPath());
        htmlFile.insertParagraph(imgElement, pos);
    }

    /**
     * Returns this Page's image directory
     *
     * @return
     */
    @Override
    public File getImageDir() {
        return imageDir;
    }

    /**
     * Assumes that the parsed directory is flat, and that
     * the name (getName()) of any file should be
     * contained in the html source in some form.
     *
     * @param dir
     */
    public void checkDirForDeadFiles(File dir) {

        if (!dir.exists()) {
            return;
        }

        //get the html source
        String text = getSourceCode();

        for (File file : dir.listFiles()) {
            String unixTimeName = file.getName();

            //if the name of the file is not in the html source, the file is useless
            if (!text.contains(unixTimeName)) {
                file.delete();
                Log.d("DEAD_FILES", "deleted: " + file);
            }
        }
    }

    /**
     * Surround some text with an html tag and save.
     * (Works on entire paragraphs.)
     *
     * @param pos
     * @param tag: just the core, eg: <p><p/> => p
     */
    @Override
    public void addHtmlTag(int pos, String tag) {
        htmlFile.addHtmlTag(pos, tag);
    }

    /**
     * Remove all html tags from a paragraph.
     *
     * @param pos
     */
    @Override
    public void removeHtmlTags(int pos) {
        htmlFile.removeHtmlTags(pos);
    }

    @Override
    public void replaceParagraph(String replacement, int pos) {
        htmlFile.replaceParagraph(replacement, pos);
    }

    @Override
    public void insertParagraph(String content, int pos) {
        htmlFile.insertParagraph(content, pos);
    }

    @Override
    public String getParagraphAt(int pos) {
        return htmlFile.getParagraphAt(pos);
    }

    @Override
    public int getLine(int pos) {
        return htmlFile.getLine(pos);
    }

    @Override
    public void setTag(String tag, String value) {
        metadata.setTag(tag, value);
    }

    @Override
    public String getString(String tag) {
        return metadata.getString(tag);
    }

    @Override
    public boolean getBoolean(String tag) {

        try {
            return metadata.getBoolean(tag);
        } catch (WrongTagTypeException e) {
            e.printStackTrace();
        }

        //defaults based on the semantics of the tag
        switch (tag) {
            case TAG_EDITABLE:
                return true;
            case TAG_IN_RECYCLE_BIN:
                return false;
        }

        return false;
    }

    @Override
    public void addAudioClip(File audioFile, int pos) {

        //move the audio file to this page's audioDir
        File copy = new File(audioDir + File.separator + System.currentTimeMillis() + ".3gp");
        FileIO.moveFile(audioFile.getPath(), copy.getPath());
        String content = "AUDIO_" + copy.getName();
        htmlFile.insertParagraph(content, pos);

    }

    @Override
    public File getAudioFile(int pos) {

        String paragraph = htmlFile.getParagraphAt(pos);

        if(paragraph==null){
            return null;
        }

        //strip the paragraph
        paragraph = paragraph.replace("<p>", "").replace("</p>", "").replaceAll("<.*>", "").replace("AUDIO_", "");

        paragraph = paragraph.trim();

        if (paragraph.isEmpty()) {
            return null;
        }

        File audioFile = new File(audioDir + File.separator + paragraph);

        if (audioFile.exists()) {
            return audioFile;
        }

        return null;
    }

    @Override
    public File getAudioDir() {
        return audioDir;
    }

    @Override
    public void addLink(String link, int pos) { htmlFile.addLink(link, pos); }


}
