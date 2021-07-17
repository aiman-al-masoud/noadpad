package com.luxlunaris.noadpadlight.model.interfaces;

import com.luxlunaris.noadpadlight.model.classes.MetadataFile;
import com.luxlunaris.noadpadlight.model.exceptions.WrongTagTypeException;

public interface Metadata {

	/**
	 * Get the string value of a tag.
	 * @param tagName
	 * @return
	 */
	public String getString(String tagName);

	/**
	 * Get the value of a tag assuming it's an integer.
	 * @param tagName
	 * @return
	 * @throws WrongTagTypeException
	 */
	public int getInt(String tagName) throws WrongTagTypeException;

	/**
	 * Get the value of a tag assuming it's a boolean.
	 * @param tagName
	 * @return
	 * @throws WrongTagTypeException
	 */
	public boolean getBoolean(String tagName) throws WrongTagTypeException;

	/**
	 * Get the value of a tag assuming it's a float.
	 * @param tagName
	 * @return
	 * @throws WrongTagTypeException
	 */
	public double getFloat(String tagName) throws WrongTagTypeException;


	/**
	 * Set the value of a tag.
	 * NB: you have to cast the value to a string to save it.
	 * @param tagName
	 * @param tagValue
	 */
	public void setTagValue(String tagName, String tagValue);

	/**
	 * Delete a tag as well as its value.
	 * @param tagName
	 */
	public void removeTag(String tagName);
	
	
}
