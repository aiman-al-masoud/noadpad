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

        long timeOne = p1.lastModified();
        long timeTwo = p2.lastModified();

        //Log.d("LONG_COMPARED", timeOne+"  "+timeTwo);
        return (int)((timeTwo-timeOne)/1000000);
    }
}
