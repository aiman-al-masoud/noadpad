package com.luxlunaris.noadpadlight.model.interfaces;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;

import java.io.File;
import java.io.Serializable;

/**
 * Page is the storage unit of the Notebook.
 *
 * The user can create several Pages, each Page
 * can contain text, other files, and the relative metadata.
 *
 */
public interface Page extends Serializable {

	/**
	 * Settable tags.
	 * Handled by setTag, getString, getBool ...
	 */
	String TAG_EDITABLE = "editable";
	String TAG_IN_RECYCLE_BIN = "in_recycle_bin";


	/**
	 * Get the Page's text
	 * @return
	 */
	String getText();

	/**
	 * Modify/set the Page's text
	 * @param text
	 */
	void setText(String text);

	/**
	 * Get the Page's name
	 * @return
	 */
	String getName();

	/**
	 * Get the time the Page was created
	 * @return
	 */
	long getCreationTime();

	/**
	 * Get the last time the Page was modified
	 * @return
	 */
	long getLastModifiedTime();

	/**
	 * Delete the Page
	 * @return
	 */
	boolean delete();

	/**
	 * Create the Page
	 */
	void create();


	/**
	 * get the number of tokens
	 * @param token
	 * @return
	 */
	int numOfTokens(String token);


	/**
	 * Set the token to be found.
	 * @param token
	 */
	void setTokenToBeFound(String token);


	/**
	 * Get the next position of the current token
	 * @return
	 */
	int nextPosition();


	/**
	 * Get the previous position of the current token
	 * @return
	 */
	int previousPosition();


	/**
	 * Save the current position
	 * @param pos
	 */
	void savePosition(int pos);


	/**
	 * Get the last-saved (last visited) position
	 * @return
	 */
	int getLastPosition();

	/**
	 * Add a PageListener to this Page
	 * @param listener
	 */
	void addListener(PageListener listener);

	/**
	 * Get a text based preview of this Page
	 * @return
	 */
	String getPreview();

	/**
	 *  Checks if this page contains ALL of the provided keywords
	 * 	(ANDed keywords)
	 * @param keywords
	 * @return
	 */
	boolean contains(String[] keywords);


	/**
	 * Is this Page currently selected?
	 * @return
	 */
	boolean isSelected();


	/**
	 * Set this Page as selected.
	 * @param select
	 */
	void setSelected(boolean select);


	/**
	 * Add an image to this Page.
	 * @param path
	 */
	void addImage(String path, int pos);


	/**
	 * Returns the image directory of this Page.
	 * @return
	 */
	File getImageDir();

	/**
	 * Surround a part of this Page's text with an html tag.
	 * @param pos
	 * @param tag
	 */
	void addHtmlTag(int pos, String tag);


	/**
	 * Remove all of the html tags from a position.
	 * @param pos
	 */
	void removeHtmlTags(int pos);


	/**
	 * Set a tag of any kind.
	 * (Just convert the value to a string by appending +"" or calling toString())
	 * @param tag
	 * @param value
	 */
	void setTag(String tag, String value);

	/**
	 * Get back a tag as a String.
	 * @param tag
	 * @return
	 */
	String getString(String tag);

	/**
	 * Attempt getting back a tag as a boolean.
	 * @param tag
	 * @return
	 */
	boolean getBoolean(String tag);

	/**
	 * Add an audio clip file at the specified position.
	 * @param audioFile
	 * @param pos
	 */
	void addAudioClip(File audioFile, int pos);

	/**
	 * Get the audio clip file (if any) at the specified position.
	 * @param pos
	 * @return
	 */
	File getAudioFile(int pos);

	/**
	 * Get the audio clip directory.
	 * @return
	 */
	File getAudioDir();

	/**
	 * Add a link at the specified posistion.
	 * @param link
	 * @param pos
	 */
	void addLink(String link, int pos);









	}
