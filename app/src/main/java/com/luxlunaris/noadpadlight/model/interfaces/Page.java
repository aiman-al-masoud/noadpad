package com.luxlunaris.noadpadlight.model.interfaces;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;

import java.io.Serializable;

public interface Page extends Serializable {
	
	public String getText();
	
	public void setText(String text);

	public String getName();

	public long getCreationTime();

	public long getLastModifiedTime();

	public boolean delete();
	
	public void create();


	/**
	 * get the number of tokens
	 * @param token
	 * @return
	 */
	public int numOfTokens(String token);


	/**
	 * Get the next position of a token
	 * @param token
	 * @return
	 */
	public int nextPosition(String token);

	/**
	 * Get the previous position of a token.
	 * @param token
	 * @return
	 */
	public int previousPosition(String token);


	/**
	 * Get the next position of the current token
	 * @return
	 */
	public int nextPosition();


	/**
	 * Get the previous position of the current token
	 * @return
	 */
	public int previousPosition();


	public void savePosition(int pos);


	public int getLastPosition();


	public void addListener(PageListener listener);

	public String getPreview();

	public boolean contains(String[] keywords);


	public boolean isSelected();
	public void setSelected(boolean select);




	}
