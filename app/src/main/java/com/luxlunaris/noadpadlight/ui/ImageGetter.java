package com.luxlunaris.noadpadlight.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

public class ImageGetter implements Html.ImageGetter {

    /**
     * Needs context to get screen-size.
     */
    Context context;

    public ImageGetter(Context context){
        this.context = context;
    }


    @Override
    public Drawable getDrawable(String source) {

        //idea: custom ImageFile class to store the width and
        //height of every single image.
        //OR (less orthodox) maybe put the size in the name of the image file.

        Log.d("GETTING_IMAGE", source);
        Drawable d = Drawable.createFromPath(source);
        Log.d("GETTING_IMAGE", d.getIntrinsicWidth()+"");

        //get the screen size
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels;
        int maxHeight = metrics.heightPixels;

        Log.d("GETTING_IMAGE", "width of screen: "+maxWidth+" height of screen: "+maxHeight);

        //get the image size
        int imageWidth = d.getIntrinsicWidth();
        int imageHeight = d.getIntrinsicHeight();

        //ratio will be < 1 if the image is too big to fit in the screen,
        //and > 1 if it's too small (compensation effect).
        float ratio = Math.min( (float)maxWidth/imageWidth, (float)maxHeight/imageHeight);

        //set the width and height times ratio
        d.setBounds(0,0, (int)((float)ratio*imageWidth) ,(int)((float)ratio*imageHeight));

        Log.d("GETTING_IMAGE", d.toString());

        return d;

    }
}
