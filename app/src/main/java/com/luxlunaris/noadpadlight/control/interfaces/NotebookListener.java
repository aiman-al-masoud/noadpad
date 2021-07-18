package com.luxlunaris.noadpadlight.control.interfaces;

import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.io.Serializable;

public interface NotebookListener extends Serializable {

    public void onCreated(Page page);
    public void onDeleted(Page page);
    public void onModified(Page page);

}
