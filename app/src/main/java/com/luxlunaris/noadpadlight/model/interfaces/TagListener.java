package com.luxlunaris.noadpadlight.model.interfaces;

import com.luxlunaris.noadpadlight.model.classes.Tag;

/**
 * Listens to the Settings facade controller for live updates
 * on the value of specified SETTING_TAG(S)
 */
public interface TagListener {

    /**
     * Notify listener that given tag got updated.
     * @param tag
     */
    void onTagUpdated(Tag tag);



}
