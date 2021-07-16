package com.luxlunaris.noadpadlight.control.classes;

import com.luxlunaris.noadpadlight.model.classes.MetadataFile;
import com.luxlunaris.noadpadlight.model.interfaces.Metadata;

import java.io.File;

/**
 * A singleton that handles ALL of the local settings of the app.
 *
 * Makes use of the Metadata IF.
 */
public class Settings {


    private static String SETTINGS_FILE_PATH = Paths.APP_DIR_PATH+ File.separator+"settings";

    private static MetadataFile settingsFile;


    public static String TRUE = "true";

    public static String FALSE = "false";


    /**
     * Get the settingsFile metadata file
     * @return
     */
    public static Metadata get(){

        if(settingsFile==null){
            settingsFile = new MetadataFile(SETTINGS_FILE_PATH);
        }

        if(!settingsFile.exists()){
            settingsFile.create();
        }

        return settingsFile;
    }


    /**
     * Some tags for the most important settings.
     *
     * NB: when using these to access a tagValue via the getTagValue method
     * of Metadata you need to convert a TAGS tag to a string. (toString())
     */
    public enum TAGS{

        TEXT_SIZE, LAUNCH_TO_BLANK_PAGE;

    }




}
