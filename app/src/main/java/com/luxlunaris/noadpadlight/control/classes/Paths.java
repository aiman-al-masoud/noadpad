package com.luxlunaris.noadpadlight.control.classes;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.luxlunaris.noadpadlight.ui.MainActivity;

import java.io.File;

/**
 * Stores the most important paths.
 */
public class Paths {

    /**
     * The directory that contains all of this app's internal data.
     */
    public static String APP_DIR_PATH =	MainActivity.CONTEXT.getFilesDir().getPath();

    /**
     * The subdirectory tasked with storing pages.
     */
    public static String PAGES_DIR = Paths.APP_DIR_PATH+File.separator+"pages";

    /**
     * Tmp stores the backup file containing all of the pages.
     */
    public static String PAGES_BACKUP_DIR = Paths.APP_DIR_PATH+File.separator+"pages_backup";


    //public static String TMP_DIR = Paths.APP_DIR_PATH+File.separator+"tmp";



}
