package com.luxlunaris.noadpadlight.model.interfaces;

import java.io.Serializable;

public interface Page extends Serializable {
	
	public String getText();
	
	public void setText(String text);

	public String getName();

	public long getCreationTime();

	public long getLastModifiedTime();

	public boolean delete();
	
	public void create();





	public int nextPosition(String token);
	public int previousPosition(String token);


	public int nextPosition();
	public int previousPosition();



}
