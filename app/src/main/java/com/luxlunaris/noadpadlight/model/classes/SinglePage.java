package com.luxlunaris.noadpadlight.model.classes;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.model.interfaces.Metadata;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class SinglePage extends File implements Page {
	
	Metadata metadata;
	File textFile;
	
	ArrayList<PageListener> listeners;


	Integer[] positionsOfToken;
	String currentToken = "";
	int posIndex = 0;


	boolean selected = false;




	public SinglePage(String pathname) {
		super(pathname);
		metadata = new MetadataFile(getPath()+File.separator+"metadata");
		textFile = new File(getPath()+File.separator+"text");
		listeners = new ArrayList<>();
	}

	@Override
	public String getText() {
		String text = FileIO.read(textFile.getPath());
		return text ==null? "" : text;
	}

	@Override
	public void setText(String text) {
		FileIO.write(textFile.getPath(), text);
		for(PageListener listener : listeners){
			listener.onModified(this);
		}

	}


	@Override
	public boolean delete() {
		textFile.delete();
		((MetadataFile)metadata).delete();
		boolean del = super.delete();

		for(PageListener listener : listeners){
			listener.onDeleted(this);

		}


		return del;
	}

	@Override
	public void create() {
		mkdir();
		
		try {
			((MetadataFile)metadata).createNewFile();
			textFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int numOfTokens(String token) {
		return getText().split(token).length-1;
	}




	@Override
	public String getName(){
		return super.getName();
	}

	@Override
	public long getCreationTime() {
		return Long.parseLong(getName());
	}

	@Override
	public long getLastModifiedTime() {
		return lastModified();
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


	@Override
	public int nextPosition(String token){

		if(!currentToken.equals(token)){
			positionsOfToken = getTokensPositions(token);
			currentToken = token;
			posIndex = 0;
		}


		if(posIndex+1 > positionsOfToken.length-1){
			return positionsOfToken[posIndex];
		}

		//return the due position, THEN increment the index
		return positionsOfToken[posIndex++];
	}

	@Override
	public int previousPosition(String token){

		if(!currentToken.equals(token)){
			positionsOfToken = getTokensPositions(token);
			currentToken = token;
			posIndex = 0;
		}

		if(posIndex-1 < 0){
			return positionsOfToken[posIndex];
		}

		//return the due position, THEN increment the index
		return positionsOfToken[posIndex--];
	}

	@Override
	public int nextPosition() {

		return nextPosition(currentToken);
	}

	@Override
	public int previousPosition() {

		return previousPosition(currentToken);
	}

	@Override
	public void savePosition(int pos) {
		metadata.setTagValue("LAST_POSITION", pos+"");
	}

	@Override
	public int getLastPosition() {
		String lastPosString = metadata.getTagValue("LAST_POSITION")==null? "0" : metadata.getTagValue("LAST_POSITION");
		return Integer.parseInt(lastPosString);
	}

	@Override
	public void addListener(PageListener listener) {
		listeners.add(listener);
	}

	@Override
	public String getPreview() {
		return FileIO.readLine(textFile.getPath())+"...\n" +new Date(getLastModifiedTime()).toString();
	}

	/**
	 * Checks if this page contains all of the passed keywords
	 * (ANDed keywords)
	 * @param keywords
	 * @return
	 */
	public boolean contains(String[] keywords){
		String text = getText().toUpperCase();
		for(String keyword : keywords){
			if(!text.contains(keyword.toUpperCase())){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		//notify the listener
		for(PageListener listener : listeners){
			listener.onSelected(this);
		}
	}






}
