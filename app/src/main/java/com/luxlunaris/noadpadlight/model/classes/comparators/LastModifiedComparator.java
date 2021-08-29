package com.luxlunaris.noadpadlight.model.classes.comparators;

import com.luxlunaris.noadpadlight.model.interfaces.Page;

import java.util.Comparator;

/**
 * Compares two pages by time-last-modified
 */
public class LastModifiedComparator implements Comparator<Page> {
    @Override
    public int compare(Page p1, Page p2) {
        return (int)(p2.getLastModifiedTime() - p1.getLastModifiedTime() );
    }
}
