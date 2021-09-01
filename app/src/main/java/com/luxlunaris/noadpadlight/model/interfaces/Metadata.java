package com.luxlunaris.noadpadlight.model.interfaces;

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
	 * @param tagName
	 * @return
	 */
	String getString(String tagName);

	/**
	 * Get the value of a tag assuming it's an integer.
	 * @param tagName
	 * @return
	 */
	int getInt(String tagName);

	/**
	 * Get the value of a tag assuming it's a boolean.
	 * @param tagName
	 * @return
	 */
	boolean getBoolean(String tagName);

	/**
	 * Get the value of a tag assuming it's a float.
	 * @param tagName
	 * @return
	 * */
	double getFloat(String tagName);


	void setTagDefault(String tag, String tagDefaultVal);








	/**
	 * Set the value of a tag.
	 * NB: you have to cast the value to a string to save it.
	 * @param tag
	 * @param tagValue
	 */
	void setTag(String tag, String tagValue);



	/**
	 * Delete a tag as well as its value.
	 * @param tagName
	 */
	void removeTag(String tagName);

	/**
	 * "Fily" methods:
	 * @return
	 */
	boolean create();
	boolean delete();



	
}
