package com.luxlunaris.noadpadlight.model.classes;

import android.util.Log;

import com.luxlunaris.noadpadlight.model.interfaces.Metadata;
import com.luxlunaris.noadpadlight.services.FileIO;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MetadataFile extends File implements Metadata {


	/**
	 * Key: Tag, Value: default value in string format.
	 */
	HashMap<String, String> defaultVals;



	public MetadataFile(String pathname) {
		super(pathname);
		defaultVals = new HashMap<>();
	}
	

	@Override
	public boolean create() {
		try {
			return this.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean delete(){
		return this.delete();
	}


	/**
	 * Set a non-persistent default value, meant to be "hardcoded" when this object is initialized.
	 * @param tag
	 * @param tagDefaultVal
	 */
	@Override
	public void setTagDefault(String tag, String tagDefaultVal) {
		defaultVals.put(tag, tagDefaultVal);
	}

	
	/**
	 * inserts a key-value pair in the module. 
	 * If the added key was already in the Module,
	 * its value gets OVERWRITTEN!
	 * @param key
	 * @param value
	 */
	@Override
	public void setTag(String key, String value) {

		//get the old value of the key
		String oldValue = getStringNoDefault(key);
		
		//get this file's text
		String text = FileIO.read(this.getPath());

		if(text==null){
			text = "";
		}

		//if it's the first time you're putting this key in, add a new key-value line
		if(oldValue==null) {
			text+=key+" : "+value+"\n";
		}else {
			//else replace oldValue with new value
			text = text.replace(key+" : "+oldValue, key+" : "+value);
		}
		
		//push changes
		FileIO.write(this.getPath(), text);
		
	}


	/**
	 * Returns the value associated to a key, or its
	 * default value or null if not found.
	 * @param key
	 * @return
	 */
	@Override
	public String getString(String key) {

		String value = getStringNoDefault(key);

		//if value is null, attempt getting the value from default map.
		if(value==null){
			Log.d("WRONG_TAG_TYPE", key+": using default value.");
			value = defaultVals.get(key);
		}

		//return value, could be null.
		return value;
	}


	/**
	 * Just get whatever value is stored on the file, or null if none.
	 * To be called INTERNALLY to this class!
	 * @param key
	 * @return
	 */
	protected String getStringNoDefault(String key){
		//get this file's text
		String text = FileIO.read(this.getPath());
		String value = null;
		try {
			//try matching the pattern: key : value\n
			Pattern pattern = Pattern.compile(key+" : (.*?)\n");

			Matcher matcher = pattern.matcher(text);
			matcher.find();

			//get the value
			value = matcher.group(1);

		}catch(Exception e) {/*do nothing*/}

		return value;
	}


	/**
	 * Get the value of a tag that stores an integer
	 * @param tagName
	 * @return
	 */
	@Override
	public int getInt(String tagName) {

		String value = getString(tagName);

		try{
			return Integer.parseInt(value.trim());
		}catch (NullPointerException | NumberFormatException e){
			Log.d("WRONG_TAG_TYPE", tagName+" is not an int, found value = "+value);
		}

		//int default
		return 0;
	}


	/**
	 * Get the value of a tag that stores a boolean
	 * @param tagName
	 * @return
	 */
	@Override
	public boolean getBoolean(String tagName)  {

		String boolString = getString(tagName);

		if(boolString==null){
			Log.d("WRONG_TAG_TYPE", tagName+" boolean is null!");
		}

		if(boolString.toLowerCase().trim().equals(TRUE_STR)){
			return true;
		}

		//anything that isn't "true" is false:
		return false;

	}

	/**
	 * Get the value of a tag that stores a floating point number.
	 * @param tagName
	 * @return
	 */
	@Override
	public double getFloat(String tagName) {

		String value = getString(tagName);

		try{
			return Double.parseDouble(value.trim());
		}catch (NullPointerException | NumberFormatException e){
			Log.d("WRONG_TAG_TYPE", tagName+" is not an double, found value = "+value);
		}

		//double default
		return 0;
	}

	/**
	 * Removes a key and its associated value.
	 * @param key
	 */
	@Override
	public void removeTag(String key) {
		String value = getString(key);
		if(value==null) {
			return; //no key to remove
		}
		String newText = FileIO.read(this.getPath()).replace(key+" : "+value+"\n", "");
		FileIO.write(this.getPath(), newText);
	}











	
}
