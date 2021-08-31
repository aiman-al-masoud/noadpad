package com.luxlunaris.noadpadlight.control.classes;

import com.luxlunaris.noadpadlight.control.interfaces.SettingsTagListener;
import com.luxlunaris.noadpadlight.model.classes.MetadataFile;
import com.luxlunaris.noadpadlight.model.classes.Tag;
import com.luxlunaris.noadpadlight.model.interfaces.Metadata;
import com.luxlunaris.noadpadlight.ui.THEMES;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Facade-controller-singleton that handles ALL of the local settings of the app.
 *
 * Makes use of the Metadata IF.
 *
 * It notifies registered listeners of a tag when the value of
 * that tag changes.
 *
 */
public class Settings{


    /**
     * Path to the metadatafile that stores the settings tags
     */
    private static String SETTINGS_FILE_PATH = Paths.APP_DIR_PATH+ File.separator+"settings";

    /**
     * The MetadataFile that stores settings tags.
     */
    private static MetadataFile settingsFile;

    /**
     * Associates to (potentially) each tag a list of listeners of that tag.
     */
    private static HashMap<Tag, ArrayList<SettingsTagListener>> tagListeners;

    /**
     * Tags and default values.
     */
    public static Tag TAG_TEXT_SIZE = new Tag("TEXT_SIZE", 18+"");
    public static Tag TAG_LAUNCH_TO_BLANK_PAGE = new Tag("LAUNCH_TO_BLANK_PAGE", Metadata.FALSE_STR);
    public static Tag TAG_THEME = new Tag("THEME", THEMES.LIGHT.toString());


    /**
     * Registers a new listener interested in being notified when the
     * value of a tag changes.
     * @param tag
     * @param listener
     */
    public static void listenToTag(Tag tag, SettingsTagListener listener){

        //get the list of listeners of a tag
        ArrayList<SettingsTagListener> listeners = tagListeners.get(tag);

        //if the tag doesn't have any listeners yet
        if(listeners==null){
            //create the list
            listeners =  new ArrayList<SettingsTagListener>();
            //add the list to the map
            tagListeners.put(tag,listeners);
        }

        //add the new listener to the list of listeners of a certain tag
        listeners.add(listener);
    }


    /**
     * Get the settingsFile metadata file
     * @return
     */
    private static void makeInstance(){

        if(settingsFile==null){
            settingsFile = new MetadataFile(SETTINGS_FILE_PATH);
            initTagValues();
        }

        if(!settingsFile.exists()){
            settingsFile.create();
        }

        if(tagListeners==null){
            tagListeners= new HashMap<>();
        }

    }

    private static void initTagValues(){
        settingsFile.setTagDefault(TAG_LAUNCH_TO_BLANK_PAGE.tag, TAG_LAUNCH_TO_BLANK_PAGE.defaultValue);
        settingsFile.setTagDefault(TAG_TEXT_SIZE.tag, TAG_TEXT_SIZE.defaultValue);
        settingsFile.setTagDefault(TAG_THEME.tag, TAG_THEME.defaultValue);
    }


    /**
     * Get the value of a boolean tag.
     * @param tag
     * @return
     */
    public static boolean getBoolean(Tag tag){
        makeInstance();
        return settingsFile.getBoolean(tag.tag);
    }

    /**
     * Get the value of an int tag.
     * @param tag
     * @return
     */
    public static int getInt(Tag tag){
        makeInstance();
        return settingsFile.getInt(tag.tag);
    }


    /**
     * Get the value of a string tag.
     * @param tag
     * @return
     */
    public static String getString(Tag tag){
        makeInstance();
        return settingsFile.getString(tag.tag);
    }


    /**
     * Get the value of a float tag.
     * @param tag
     * @return
     */
    public static double getFloat(Tag tag){
        makeInstance();
        return settingsFile.getFloat(tag.tag);
    }


    /**
     * Set the value of any tag (enter it in string form).
     * @param tag
     * @param newValue
     */
    public static void setTag(Tag tag, String newValue){
        makeInstance();
        //set the new tag value
        settingsFile.setTag(tag.tag, newValue);
        //notify all of the listeners of the modified tag that its value changed
        notifyListenersOfTag(tag);
    }

    /**
     * notify all of the listeners of the modified tag that its value changed
     * @param tag
     */
    private static void notifyListenersOfTag(Tag tag){
        ArrayList<SettingsTagListener> listenersOfTag = tagListeners.get(tag);
        if(listenersOfTag!=null){
            for(SettingsTagListener listener : listenersOfTag){
                try {
                    listener.onTagUpdated(tag);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }

}
