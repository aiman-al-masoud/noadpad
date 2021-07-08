package com.luxlunaris.noadpadlight.control.interfaces;


import com.luxlunaris.noadpadlight.model.interfaces.Page;

public interface Pageable {
	
	public Page[] getNext(int amount);

}
