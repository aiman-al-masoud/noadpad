package com.luxlunaris.noadpadlight.model.interfaces;

import com.luxlunaris.noadpadlight.control.interfaces.PageListener;
import com.luxlunaris.noadpadlight.control.interfaces.Pageable;

import java.io.Serializable;

public interface Booklet extends Pageable, Serializable {


    //void getSelected();

    Page createPage(String name);

    void remove(Page page);

    void load();


    void resort();


    void getByKeywords(String query);







}
