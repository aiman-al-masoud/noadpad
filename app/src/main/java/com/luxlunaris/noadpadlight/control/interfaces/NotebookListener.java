package com.luxlunaris.noadpadlight.control.interfaces;

import com.luxlunaris.noadpadlight.model.interfaces.Page;

public interface NotebookListener {

    public void onCreated(Page page);
    public void onDeleted(Page page);
    public void onModified(Page page);

}
