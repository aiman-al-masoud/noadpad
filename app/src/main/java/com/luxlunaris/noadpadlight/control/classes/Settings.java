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

    static MetadataFile settingsFile;

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
     */
    public enum TAGS{

        TEXT_SIZE;

    }




}
