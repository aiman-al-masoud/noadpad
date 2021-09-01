package com.luxlunaris.noadpadlight.model.classes;

/**
 *
 */
public class Tag {

    public final String tag;
    public final String defaultValue;


    public Tag(String tag, String defaultValue){
        this.tag  = tag;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString(){
        return tag;
    }

}
