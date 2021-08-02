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



	File imageDir;



	/**
	 * this Page's listeners (Notebook)
	 */
	ArrayList<PageListener> listeners;


	/**
	 * true if this Page is currently "selected"
	 */
	boolean selected = false;


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
	 * get this Page's text from the text file
	 * @return
	 */
	@Override
	public String getText() {
		String text = FileIO.read(textFile.getPath());
		return text ==null? "" : text;
	}

	/**
	 * Save new/edited text to the text file
	 * @param text
	 */
	@Override
	public void setText(String text) {
		Log.d("TEST_IMAGE", "saving: "+text);
		FileIO.write(textFile.getPath(), text);
		for(PageListener listener : listeners){
			listener.onModified(this);
		}

	}


	/**
	 * Delete this page and all of its contents from disk
	 * @return
	 */
	@Override
	public boolean delete() {
		//textFile.delete();
		//((MetadataFile)metadata).delete();
		//boolean del = super.delete();
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
	}


	/**
	 * How many times does a token appear in this Page's text?
	 * @param token
	 * @return
	 */
	@Override
	public int numOfTokens(String token) {
		return getText().toUpperCase().split(token.toUpperCase()).length-1;
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
		String text = getText().toUpperCase();

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
		//String text = getText().toUpperCase();
		String text = Html.fromHtml(getText()).toString().toUpperCase();
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


	@Override
	public void addImage(String path, int position) {

		File imageCopy = new File(imageDir.getPath()+File.separator+System.currentTimeMillis());
		FileIO.copyFile(path, imageCopy.getPath());

		String text = getText();

		String partBefore = text.substring(0, position);
		String partAfter = text.substring(position, text.length()-1);
		String tag = "<img src=\'"+imageCopy.getPath()+"\' />";
		text = partBefore+" "+tag+" "+partAfter;

		setText(text);
	}





}
