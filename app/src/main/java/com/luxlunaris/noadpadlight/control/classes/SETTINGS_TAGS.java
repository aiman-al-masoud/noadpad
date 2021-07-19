package com.luxlunaris.noadpadlight.control.classes;

/**
 * Some tags for the most important settings.
 *
 *
 */
public enum SETTINGS_TAGS {

    
    TEXT_SIZE(TAG_TYPES.INT, 18),

    LAUNCH_TO_BLANK_PAGE(TAG_TYPES.BOOLEAN, false),

    THEME(TAG_TYPES.STRING, "LIGHT");



    /**
     * The type of value stored by the tag.
     */
    public TAG_TYPES TYPE;

    /**
     * The default value of the tag.
     */
    public Object DEFAULT_VAL;

    /**
     * A SETTINGS_TAG needs to know its type and its default value.
     * @param type
     * @param defaultVal
     */
    SETTINGS_TAGS(TAG_TYPES type, Object defaultVal){

        TYPE = type;
        DEFAULT_VAL = defaultVal;

    }


}


/**
 * An enum for the kinds of SETTINGS_TAGS that you can have.
 */
enum TAG_TYPES{

    STRING, BOOLEAN, INT, FLOAT;

}
