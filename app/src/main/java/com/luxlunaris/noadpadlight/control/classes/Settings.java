package com.luxlunaris.noadpadlight.control.classes;

import com.luxlunaris.noadpadlight.control.interfaces.SettingsTagListener;
import com.luxlunaris.noadpadlight.model.classes.MetadataFile;
import com.luxlunaris.noadpadlight.model.exceptions.WrongTagTypeException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Facade-controller-singleton that handles ALL of the local settings of the app.
 *
 * Makes use of the Metadata IF.
 *
 *
 * It notifies registered listeners of a tag when the value of
 * that tag changes.
 *
 */
public class Settings {


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
    private static HashMap<SETTINGS_TAGS, ArrayList<SettingsTagListener>> tagListeners;


    /**
     * Registers a new listener interested in being notified when the
     * value of a tag changes.
     * @param tag
     * @param listener
     */
    public static void listenToTag(SETTINGS_TAGS tag, SettingsTagListener listener){

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
        }

        if(!settingsFile.exists()){
            settingsFile.create();
        }

        if(tagListeners==null){
            tagListeners= new HashMap<>();
        }

    }


    /**
     * Get the value of a boolean tag.
     * @param TAG_NAME
     * @return
     */
    public static boolean getBoolean(SETTINGS_TAGS TAG_NAME){

        //set up all singleton parameters
        makeInstance();

        try {
            return settingsFile.getBoolean(TAG_NAME.toString());
        } catch (WrongTagTypeException | NullPointerException e) {
            e.printStackTrace();
        }

        //return the tag's default value
        return (boolean)TAG_NAME.DEFAULT_VAL;

    }


    /**
     * Get the value of an int tag.
     * @param TAG_NAME
     * @return
     */
    public static int getInt(SETTINGS_TAGS TAG_NAME){

        //set up all instance attributes
        makeInstance();

        try {
            return settingsFile.getInt(TAG_NAME.toString());
        } catch (WrongTagTypeException | NullPointerException e) {
            e.printStackTrace();
        }


        //return the default value of the tag
        return (int)TAG_NAME.DEFAULT_VAL;

    }


    /**
     * Get the value of a string tag.
     * @param TAG_NAME
     * @return
     */
    public static String getString(SETTINGS_TAGS TAG_NAME){

        //set up all instance attributes
        makeInstance();

        try{
            return settingsFile.getString(TAG_NAME.toString());
        }catch (NullPointerException e){
        }

        //return the default value of the tag
        return (String) TAG_NAME.DEFAULT_VAL;

    }


    /**
     * Get the value of a float tag.
     * @param TAG_NAME
     * @return
     */
    public static double getFloat(SETTINGS_TAGS TAG_NAME){

        //set up all instance attributes
        makeInstance();


        try {
            return settingsFile.getFloat(TAG_NAME.toString());
        } catch (WrongTagTypeException | NullPointerException  e) {
            e.printStackTrace();
        }

        //default float value
        return 0;
    }


    /**
     * Set the value of any tag (enter it in string form).
     * @param TAG_NAME
     * @param newValue
     */
    public static void setTagValue(SETTINGS_TAGS TAG_NAME, String newValue){

        //set up all instance attributes
        makeInstance();

        //set the new tag value
        settingsFile.setTagValue(TAG_NAME.toString(), newValue);

        //notify all of the listeners of the modified tag that its value changed
        notifyListenersOfTag(TAG_NAME);

    }

    /**
     * notify all of the listeners of the modified tag that its value changed
     * @param tag
     */
    private static void notifyListenersOfTag(SETTINGS_TAGS tag){
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
