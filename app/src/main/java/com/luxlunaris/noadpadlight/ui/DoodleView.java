package com.luxlunaris.noadpadlight.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DoodleView extends View {


    Paint paint;

    Path path;


    public DoodleView(Context context) {
        super(context);
        paint = new Paint();
        setColor(Color.BLACK);
        setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        path =new Path();
    }



    public void setColor(int color){
        paint.setColor(color);
    }

    public void setStrokeWidth(int width){
        paint.setStrokeWidth(width);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        setBackgroundColor(Color.WHITE);
        canvas.drawPath(path, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        Log.d("TOUCH_EVENT", "x: "+x+" y: "+y);


        switch(event.getAction()){

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;

        }

        //trigger onDraw
        invalidate();

        //very important
        return true;
    }


    public File getSnapshot(){

        File doodleFile = new File(getContext().getFilesDir(), "doodle.png");

        try {
            FileOutputStream fos  =  new FileOutputStream(doodleFile);
            setDrawingCacheEnabled(true);///
            Bitmap b = Bitmap.createBitmap( getDrawingCache());
            setDrawingCacheEnabled(false);///

            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doodleFile;
    }













}
