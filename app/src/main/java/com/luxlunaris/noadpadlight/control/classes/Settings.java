package com.luxlunaris.noadpadlight.control.classes;

import com.luxlunaris.noadpadlight.model.classes.MetadataFile;
import com.luxlunaris.noadpadlight.model.exceptions.WrongTagTypeException;

import java.io.File;

/**
 * A Facade-controller-singleton that handles ALL of the local settings of the app.
 *
 * Makes use of the Metadata IF.
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
        } catch (WrongTagTypeException e) {
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
        } catch (WrongTagTypeException e) {
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

        return settingsFile.getString(TAG_NAME.toString());
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
        } catch (WrongTagTypeException e) {
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


        settingsFile.setTagValue(TAG_NAME.toString(), newValue);
    }





















}
