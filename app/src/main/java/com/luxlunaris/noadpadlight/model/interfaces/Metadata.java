package com.luxlunaris.noadpadlight.model.interfaces;

import com.luxlunaris.noadpadlight.model.classes.Tag;

/**
 * A simple persistent dictionary.
 */
public interface Metadata {

	/**
	 * String representing the bool values
	 */
	String TRUE_STR = "true";
	String FALSE_STR = "false";

	/**
	 * Get the string value of a tag.
	 * @param tag
	 * @return
	 */
	String getString(Tag tag);

	/**
	 * Get the value of a tag assuming it's an integer.
	 * @param tag
	 * @return
	 */
	int getInt(Tag tag);

	/**
	 * Get the value of a tag assuming it's a boolean.
	 * @param tag
	 * @return
	 */
	boolean getBoolean(Tag tag);

	/**
	 * Get the value of a tag assuming it's a float.
	 * @param tag
	 * @return
	 * */
	double getFloat(Tag tag);

	/**
	 * Set the value of a tag.
	 * NB: you have to cast the value to a string to save it.
	 * @param tag
	 * @param tagValue
	 */
	void setTag(Tag tag, String tagValue);

	/**
	 * Delete a tag as well as its value.
	 * @param tag
	 */
	void removeTag(Tag tag);

	/**
	 * "Fily" methods:
	 * @return
	 */
	boolean create();
	boolean delete();




	
}
