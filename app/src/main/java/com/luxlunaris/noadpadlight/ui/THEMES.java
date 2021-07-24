package com.luxlunaris.noadpadlight.ui;

import android.graphics.Color;

/**
 * A set of useful references to recycled colors.
 */
enum CustomColors{
    ;
    static int OFF_WHITE = Color.argb(100, 235, 235, 235);
    static int SEPIA = Color.argb(100,112, 66, 20);
}


/**
 * THEMES store a background color and a
 * foreground (text) color each.
 */
public enum THEMES {


    DARK(Color.BLACK, Color.WHITE),
    LIGHT(CustomColors.OFF_WHITE, Color.BLACK),
    BEEHIVE(Color.YELLOW, Color.BLACK),
    SEPIA(  CustomColors.OFF_WHITE, CustomColors.SEPIA)
    ;


    /**
     * background and foreground color of a theme.
     */
    int BG_COLOR, FG_COLOR;

    THEMES(int BG_COLOR, int FG_COLOR){
        this.BG_COLOR = BG_COLOR;
        this.FG_COLOR = FG_COLOR;
    }

    /**
     * Get a theme by its string name.
     * @param name
     * @return
     */
    public static THEMES getThemeByName(String name){
        try{
            name = name.toUpperCase().trim();
            return valueOf(name);
        }catch (Exception e){
        }

        return DARK;
    }

}



