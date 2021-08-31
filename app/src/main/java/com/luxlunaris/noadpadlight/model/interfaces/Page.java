package com.luxlunaris.noadpadlight.model.interfaces;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.model.classes.Tag;

import java.io.File;
import java.io.Serializable;

/**
 * Page is the storage unit of the Notebook.
 *
 * The user can create several Pages, each Page
 * can contain text, other files, and the relative metadata.
 *
 */
public interface Page extends HtmlFile, Selectable, WordCounter, Serializable {

	/**
	 * Settable tags.
	 * Handled by setTag, getString, getBool ...
	 */
	String TAG_EDITABLE = "editable";
	String TAG_IN_RECYCLE_BIN = "in_recycle_bin";


	Tag TAG_EDITABLE_TAG = new Tag("editable", Metadata.TRUE_STR);
	Tag TAG_IN_RECYCLE_BIN_TAG = new Tag("in_recycle_bin", Metadata.FALSE_STR);
	Tag TAG_LAST_POSITION_TAG = new Tag("LAST_POSITION", 0+"");




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
	 *  Checks if this page contains ALL of the provided keywords
	 * 	(ANDed keywords)
	 * @param keywords
	 * @return
	 */
	boolean contains(String[] keywords);

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
	 * Like metadata, but WrongTypeException should be handled internally to the Page-implementation.
	 * @param tag
	 * @return
	 */
	String getString(String tag);
	boolean getBoolean(String tag);
	void setTag(String tag, String value);





	}
