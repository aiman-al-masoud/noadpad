package com.luxlunaris.noadpadlight.model.classes;

import android.text.Html;
import android.util.Log;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.model.interfaces.Metadata;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
	 * Directory that contains this Page's images.
	 */
	private File imageDir;

	/**
	 * this Page's listeners (Notebook)
	 */
	ArrayList<PageListener> listeners;


	/**
	 * true if this Page is currently "selected"
	 */
	boolean selected = false;

	/**
	 * Data relative to the currently searched-for token.
	 */
	Integer[] positionsOfToken;
	String currentToken;
	int posIndex = 0;



	public SinglePage(String pathname) {
		super(pathname);
		metadata = new MetadataFile(getPath()+File.separator+"metadata");
		textFile = new File(getPath()+File.separator+"text");
		imageDir = new File(getPath()+File.separator+"images");
		listeners = new ArrayList<>();
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
		Log.d("TEST_IMAGE", "saving: "+text);

		FileIO.write(textFile.getPath(), text);
		for(PageListener listener : listeners){
			listener.onModified(this);
		}

		//delete any non-used image files.
		checkDeleteImages();

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

		FileIO.deleteDirectory(this.getPath());

		//notify the listeners that this got deleted
		for(PageListener listener : listeners){
			listener.onDeleted(this);
		}

		//return del;
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("CREATED_PAGE", "CREATED PAGE: "+getName()+" "+textFile.exists()+" "+imageDir.exists());

	}


	/**
	 * How many times does a token appear in this Page's text?
	 * @param token
	 * @return
	 */
	@Override
	public int numOfTokens(String token) {
		return getTextNoTags().toUpperCase().split(token.toUpperCase()).length-1;
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
	 * Set the token to be found in this Page
	 * @param token
	 */
	@Override
	public void setTokenToBeFound(String token){
		positionsOfToken = getTokensPositions(token);
		currentToken = token;
		posIndex = 0;
	}

	/**
	 * Find all of the positions of a token in this Page
	 * @param token
	 * @return
	 */
	private Integer[] getTokensPositions(String token) {

		//initialize list of positions
		ArrayList<Integer> positions = new ArrayList<Integer>();

		//convert token and text to upper case
		token = token.toUpperCase();

		//get the text (w/out tags, as displayed on screen) and convert it to upper case
		String text = getTextNoTags().toUpperCase();

		//split the text by the token
		String[] parts = text.split(token);

		//first position
		positions.add(parts[0].length());

		//get the other positions
		for(int i =1; i<parts.length-1; i++){
			int lastPos = positions.get(positions.size()-1);
			int nextPos = lastPos+ token.length() +parts[i].length();
			positions.add(nextPos);
		}

		return positions.toArray(new Integer[0]);
	}


	/**
	 *  Get the next position of the currently sought-after token
	 * @return
	 */
	@Override
	public int nextPosition() {

		//if no token, or no positions, return index = 0
		if(currentToken ==null || positionsOfToken.length==0){
			return 0;
		}


		if(posIndex+1 > positionsOfToken.length-1){
			return positionsOfToken[posIndex];
		}

		//return the due position, THEN increment the index
		return positionsOfToken[posIndex++];
	}

	/**
	 *  Get the previous position of the currently sought-after token
	 * @return
	 */

	@Override
	public int previousPosition() {

		//if no token, or no positions, return index = 0
		if(currentToken ==null || positionsOfToken.length==0){
			return 0;
		}

		if(posIndex-1 < 0){
			return positionsOfToken[posIndex];
		}

		//return the due position, THEN increment the index
		return positionsOfToken[posIndex--];
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
	 * (The first line and the time last-modified)
	 * @return
	 */
	@Override
	public String getPreview() {
		return FileIO.readLine(textFile.getPath())+"\n" +new Date(getLastModifiedTime()).toString();
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
	public void addImage(String path) {

		//prepare a new file in this Page's imgDir
		File imageCopy = new File(imageDir.getPath()+File.separator+System.currentTimeMillis());

		//copy provided image to this Page's imgDir
		FileIO.copyFile(path, imageCopy.getPath());

		//get this page's raw html code
		String text = getText();

		//image element
		String imgElement = generateImgTag(imageCopy.getPath());

		//append the image element to rest of the html
		text+=imgElement;

		//overwrite text
		setText(text);
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

			Log.d("IMAGE_DEL", imgFile.getName());

			String nameOfImage = imgFile.getName();

			//if the name of the image is not in the html source, the image file is useless
			if(!text.contains(nameOfImage)){
				imgFile.delete();
				Log.d("IMAGE_DEL", imgFile.getName() + "no longer in use, deleted!");
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

		Log.d("LINE_NUM", "TEXT: "+text);

		String upTillPos = text.substring(0, pos);

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

		//test log how many lines in each paragraph
		for(int i =0; i<numLinesPerPar.length; i++){
			Log.d("LINE_NUM", "par "+i+" has: "+numLinesPerPar[i]+" lines");
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

		int lineNum = getLine(pos);
		Log.d("LINE_NUM", lineNum+"");

		//get the paragraphs
		String[] pars = getParagraphs();

		//convert the line number to the paragraph number.
		lineNum = lineToParagraph(lineNum);

		//make the tags from the inner part
		String startTag = "<"+tag+">";
		String endTag = "</"+tag+">";

		//apply the html tag
		pars[lineNum] = startTag+pars[lineNum]+endTag;

		//re-build the html source from the single paragraphs.
		String newHtml = "";
		for(String par : pars){
			newHtml+=par;
		}

		//save it.
		setText(newHtml);

	}

	/**
	 * Remove all html tags from a paragraph.
	 * @param pos
	 */
	@Override
	public void removeHtmlTags(int pos){

		int lineNum = getLine(pos);

		//get the paragraphs
		String[] pars = getParagraphs();

		//convert the line number to the paragraph number.
		lineNum = lineToParagraph(lineNum);

		String modifiedPar = pars[lineNum];

		//remove all tags other than the paragraph tag. (sort of)
		modifiedPar = modifiedPar.replaceAll("<[abcefghijklmnoqrstuvwxyz]>", "").replaceAll("</[abcefghijklmnoqrstuvwxyz]>", "");

		//replace the paragraph
		pars[lineNum] = modifiedPar;

		//re-build the html source from the single paragraphs.
		String newHtml = "";
		for(String par : pars){
			newHtml+=par;
		}

		//save it.
		setText(newHtml);
	}









}
