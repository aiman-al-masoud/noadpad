package com.luxlunaris.noadpadlight.control.interfaces;


import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.io.Serializable;

public interface PageListener extends Serializable {

	public void onDeleted(Page page);
		
	
}
