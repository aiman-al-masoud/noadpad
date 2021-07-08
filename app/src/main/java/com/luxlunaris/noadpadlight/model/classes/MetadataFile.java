package com.luxlunaris.noadpadlight.model.classes;

import com.luxlunaris.noadpadlight.model.interfaces.Metadata;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MetadataFile extends File implements Metadata {

	
	public MetadataFile(String pathname) {
		super(pathname);	
	}
	
	
	public void create() {
		try {
			this.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * inserts a key-value pair in the module. 
	 * If the added key was already in the Module,
	 * its value gets OVERWRITTEN!
	 * @param key
	 * @param value
	 */
	@Override
	public void setTagValue(String key, String value) {

		//get the old value of the key
		String oldValue = getTagValue(key);
		
		//get this file's text
		String text = FileIO.read(this.getPath());
		
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
	 * Returns the value associated to a key, null if not found.
	 * In case the value is a referenced file, it returns 
	 * the contents of that referenced file.
	 * @param key
	 * @return
	 */
	@Override
	public String getTagValue(String key) {
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
	 * Removes a key and its associated value.
	 * @param key
	 */
	@Override
	public void removeTag(String key) {
		String value = getTagValue(key);
		if(value==null) {
			return; //no key to remove
		}
		String newText = FileIO.read(this.getPath()).replace(key+" : "+value+"\n", "");
		FileIO.write(this.getPath(), newText);
		
	}

	
}
