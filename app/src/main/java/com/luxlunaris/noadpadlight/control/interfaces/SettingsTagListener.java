package com.luxlunaris.noadpadlight.control.interfaces;

import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;

/**
 * Listens to the Settings facade controller for live updates
 * on the value of specified SETTING_TAG(S)
 */
public interface SettingsTagListener {

    /**
     * Notify listener that given tag got updated.
     * @param tag
     */
    public void onTagUpdated(SETTINGS_TAGS tag);

}