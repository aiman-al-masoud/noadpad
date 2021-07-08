package com.luxlunaris.noadpadlight.model.interfaces;

public interface Metadata {
	
	public String getTagValue(String tagName);
	
	public void setTagValue(String tagName, String tagValue);
	
	public void removeTag(String tagName);
	
	
}
