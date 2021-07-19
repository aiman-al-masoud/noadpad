package com.luxlunaris.noadpadlight.ui;

import android.graphics.Color;

public enum THEMES {

    DARK(Color.BLACK, Color.WHITE),
    LIGHT(Color.WHITE, Color.BLACK),
    BEEHIVE(Color.YELLOW, Color.BLACK),
    SEPIA(  Color.WHITE, Color.argb(100,112, 66, 20))
    ;


    int BG_COLOR, FG_COLOR;

    THEMES(int BG_COLOR, int FG_COLOR){
        this.BG_COLOR = BG_COLOR;
        this.FG_COLOR = FG_COLOR;
    }


    public static THEMES getThemeByName(String name){
        try{
            name = name.toUpperCase().trim();
            return valueOf(name);
        }catch (Exception e){
        }

        return DARK;
    }

}
