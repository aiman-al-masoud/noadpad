package com.luxlunaris.noadpadlight.ui;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;

public class ImageGetter implements Html.ImageGetter {


    @Override
    public Drawable getDrawable(String source) {

        Log.d("TEST_TEST", source);

        Drawable d = Drawable.createFromPath(source);
        d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());


        Log.d("TEST_TEST", d.toString());

        return d;

    }
}
