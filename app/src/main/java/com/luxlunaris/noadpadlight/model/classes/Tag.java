package com.luxlunaris.noadpadlight.model.classes;

import java.io.Serializable;

/**
 * Used by Metadata IF to modify/retrieve values
 * for a given tag. Each Tag also stores
 * a default value.
 *
 */
public class Tag implements Serializable {

    public final String tag;
    public final String defaultValue;


    public Tag(String tag, String defaultValue){
        this.tag = tag;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString(){
        return tag;
    }

}
