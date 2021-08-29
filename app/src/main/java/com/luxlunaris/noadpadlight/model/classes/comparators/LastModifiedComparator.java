package com.luxlunaris.noadpadlight.model.classes.comparators;

import android.util.Log;

import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.util.Comparator;

/**
 * Compares two pages by time-last-modified
 */
public class LastModifiedComparator implements Comparator<Page> {
    @Override
    public int compare(Page p1, Page p2) {
        //Log.d("COMPARING","p1: "+p1.getLastModifiedTime()+"   p2: "+p2.getLastModifiedTime() );
        return (int)(p2.getLastModifiedTime() - p1.getLastModifiedTime() );
    }
}
