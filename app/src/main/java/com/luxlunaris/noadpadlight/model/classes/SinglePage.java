package com.luxlunaris.noadpadlight.model.classes;

import android.text.Html;
import android.util.Log;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.model.exceptions.WrongTagTypeException;
import com.luxlunaris.noadpadlight.model.interfaces.Metadata;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SinglePage is a persistent implementation of the Page interface.
 */

public class SinglePage extends File implements Page {

	/**
	 * manages this Page's stored metadata
	 */
	Metadata metadata;

	/**
	 * contains this Page's user-generated text
	 */
	File textFile;

	/**
	 * Directory that holds this Page's images.
	 */
	private File imageDir;

	/**
	 * Directory that holds this Page's audio files.
	 */
	private File audioDir;

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
		metadata = new MetadataFile(getPath()+File.separator+"metadata");
		textFile = new File(getPath()+File.separator+"text");
		imageDir = new File(getPath()+File.separator+"images");
		audioDir = new File(getPath()+File.separator+"audios");
		listeners = new ArrayList<>();
	}


	/**
	 * Get this Page's WordCounter object.
	 * @return
	 */
	private WordCounter getWordCounter(){
		if(wordCounter== null){
			wordCounter = new WordCounter(getTextNoTags());
		}
		return wordCounter;
	}


	/**
	 * get this Page's text from the text file.
	 * (The raw text with all of the html tags).
	 * @return
	 */
	@Override
	public String getText() {
		String text = FileIO.read(textFile.getPath());
		return text ==null? "" : text;
	}

	/**
	 * Save new/edited text to the text file.
	 * @param text
	 */
	@Override
	public void setText(String text) {

		if(!getBoolean(TAG_EDITABLE)){
			return;
		}

		FileIO.write(textFile.getPath(), text);

		try{
			for(PageListener listener : listeners){
				listener.onModified(this);
			}
		}catch (ConcurrentModificationException e){
			e.printStackTrace();
		}

		//delete any non-used media files.
		checkDeleteImages();
		checkDeleteAudio();
	}

	/**
	 * Get the text of this Page without any html tags.
	 * @return
	 */
	public String getTextNoTags(){
		return Html.fromHtml(getText()).toString();
	}


	/**
	 * Delete this page and all of its contents from disk
	 * @return
	 */
	@Override
	public boolean delete() {

		//notify the listeners that this got deleted
		try{
			for(PageListener listener : listeners){
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
	public void create() {


		mkdir();
		
		try {
			((MetadataFile)metadata).createNewFile();
			textFile.createNewFile();
			imageDir.mkdir();
			audioDir.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}


		//notify the listeners that this got created
		for(PageListener listener : listeners){
			listener.onCreated(this);
		}

	}


	/**
	 * Get this Page's name
	 * @return
	 */
	@Override
	public String getName(){
		return super.getName();
	}

	/**
	 * Get the time of creation of this Page
	 * @return
	 */
	@Override
	public long getCreationTime() {
		return Long.parseLong(getName());
	}

	/**
	 * Get this time this Page got last modified
	 * @return
	 */
	@Override
	public long getLastModifiedTime() {
		return textFile.lastModified();
	}

	/**
	 * How many times does a token appear in this Page's text?
	 * @param token
	 * @return
	 */
	@Override
	public int numOfTokens(String token) {
		return getWordCounter().numOfTokens(token);
	}

	/**
	 * Set the token to be found in this Page
	 * @param token
	 */
	@Override
	public void setTokenToBeFound(String token){
		getWordCounter().setTokenToBeFound(token);
	}

	/**
	 *  Get the next position of the currently sought-after token
	 * @return
	 */
	@Override
	public int nextPosition() {
		return getWordCounter().nextPosition();
	}

	/**
	 *  Get the previous position of the currently sought-after token
	 * @return
	 */
	@Override
	public int previousPosition() {
		return getWordCounter().previousPosition();
	}

	/**
	 * Set a "bookmark" within this page
	 * @param pos
	 */
	@Override
	public void savePosition(int pos) {
		metadata.setTagValue("LAST_POSITION", pos+"");
	}

	/**
	 * Get this Page's "bookmark", aka last position visited.
	 * @return
	 */
	@Override
	public int getLastPosition() {
		String lastPosString = metadata.getString("LAST_POSITION")==null? "0" : metadata.getString("LAST_POSITION");
		return Integer.parseInt(lastPosString);
	}

	/**
	 * Add a PageListener to this Page
	 * @param listener
	 */
	@Override
	public void addListener(PageListener listener) {
		listeners.add(listener);
	}

	/**
	 * Get a text-based preview of this Page.
	 * (The first line).
	 * @return
	 */
	@Override
	public String getPreview() {
		return FileIO.readLine(textFile.getPath())+"\n";
	}

	/**
	 * Checks if this page contains ALL of the provided keywords
	 * (ANDed keywords)
	 * @param keywords
	 * @return
	 */
	public boolean contains(String[] keywords){

		String text =  getTextNoTags().toUpperCase();
		for(String keyword : keywords){
			if(!text.contains(keyword.toUpperCase())){
				return false;
			}
		}
		return true;
	}

	/**
	 * Is this Page's "selected" flag true?
	 * @return
	 */
	@Override
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set this Page's "selected" flag
	 * @param selected
	 */
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		//notify the listener
		for(PageListener listener : listeners){
			listener.onSelected(this);
		}
	}


	/**
	 * Generate an image "tag" given its path.
	 * @param path
	 * @return
	 */
	private String generateImgTag(String path){
		//opening and closing tags
		String openImgTag = "<img src=\'";
		String closeImgTag = "\' />";

		String element = openImgTag+path+closeImgTag;

		return element;
	}


	/**
	 * Add an image to this Page.
	 * @param path
	 */
	@Override
	public void addImage(String path, int pos) {

		//prepare a new file in this Page's imgDir
		File imageCopy = new File(imageDir.getPath()+File.separator+System.currentTimeMillis());

		//move provided image to this Page's imgDir
		FileIO.moveFile(path, imageCopy.getPath());

		//create the image element in html
		String imgElement = generateImgTag(imageCopy.getPath());

		insertParagraph(imgElement, pos);
	}

	/**
	 * Returns this Page's image directory
	 * @return
	 */
	@Override
	public File getImageDir() {
		return imageDir;
	}

	/**
	 * Checks if there are any images that
	 * don't have a corresponding tag in the
	 * html source and deletes them from the
	 * imageDir.
	 */
	private void checkDeleteImages(){

		//get the html source
		String text = getText();

		//for each image...
		for(File imgFile : imageDir.listFiles()){

			String nameOfImage = imgFile.getName();

			//if the name of the image is not in the html source, the image file is useless
			if(!text.contains(nameOfImage)){
				imgFile.delete();
			}

		}

	}

	private void checkDeleteAudio() {
		//get the html source
		String text = getText();


		if(!audioDir.exists()){
			return;
		}

		//for each image...
		for (File audioFile : audioDir.listFiles()) {
			String unixTimeOfAudio = audioFile.getName().replaceAll("\\w+", "");

			//if the name of the image is not in the html source, the image file is useless
			if (!text.contains(unixTimeOfAudio)) {
				audioFile.delete();
			}
		}
	}


	/**
	 * From the position in the rendered text, determine
	 * the line.
	 * @param pos
	 * @return
	 */
	private int getLine(int pos){

		String text = getTextNoTags();

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
	 * Given a line number find the paragraph containing it.
	 * @param lineNum
	 * @return
	 */
	private int lineToParagraph(int lineNum){

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
	 * Get the html source as a list of paragraphs.
	 * @return
	 */
	private String[] getParagraphs(){
		//split the html source by end of paragraph tags
		String[] pars = getText().split("</p>");

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
	 * Surround some text with an html tag and save.
	 * (Works on entire paragraphs.)
	 * @param pos
	 * @param tag
	 */
	@Override
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
	@Override
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
	protected void replaceParagraph(String replacement, int pos){

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
		setText(newHtml);
	}

	@Override
	public void setTag(String tag, String value) {
		metadata.setTagValue(tag, value);
	}

	@Override
	public String getString(String tag) {
		return metadata.getString(tag);
	}

	@Override
	public boolean getBoolean(String tag){

		try {
			return metadata.getBoolean(tag);
		} catch (WrongTagTypeException e) {
			e.printStackTrace();
		}

		//defaults based on the semantics of the tag
		switch (tag){
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
		File copy = new File(audioDir+File.separator+System.currentTimeMillis()+".3gp");
		FileIO.moveFile(audioFile.getPath(), copy.getPath());

		String content = "AUDIO_"+copy.getName();
		insertParagraph(content, pos);
	}

	@Override
	public File getAudioFile(int pos) {

		String[] pars =  getParagraphs();

		if(pars.length==0){
			return null;
		}

		String paragraph = pars[lineToParagraph(getLine(pos))];

		//strip the paragraph
		paragraph = paragraph.replace("<p>", "").replace("</p>", "").replaceAll("<.*>", "").replace("AUDIO_", "");

		paragraph = paragraph.trim();

		if(paragraph.isEmpty()){
			return null;
		}

		File audioFile = new File(audioDir+File.separator+paragraph);

		if(audioFile.exists()){
			return audioFile;
		}

		return null;
	}

	@Override
	public File getAudioDir() {
		return audioDir;
	}

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


	/**
	 * Insert a new paragraph at a specified position.
	 * @param content: stuff between the "p" tags, excluding the tags themselves.
	 * @param pos
	 */
	private void insertParagraph(String content, int pos){

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
		setText(newHtml);

	}





















}
