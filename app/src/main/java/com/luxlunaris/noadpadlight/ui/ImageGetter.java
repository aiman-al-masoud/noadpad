package com.luxlunaris.noadpadlight.ui;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;

public class ImageGetter implements Html.ImageGetter {


    @Override
    public Drawable getDrawable(String source) {


        //idea: custom ImageFile class to store the width and
        //height of every single image.
        //OR (less orthodox) maybe put the size in the name of the image file.

        Log.d("TEST_TEST", source);

        Drawable d = Drawable.createFromPath(source);

        int width = (int) (1*d.getIntrinsicWidth());
        int height = (int) (1*d.getIntrinsicHeight());

        d.setBounds(0,0, width ,height);


        Log.d("TEST_TEST", d.toString());

        return d;

    }
}
