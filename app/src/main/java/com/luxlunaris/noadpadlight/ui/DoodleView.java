package com.luxlunaris.noadpadlight.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.luxlunaris.noadpadlight.R;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class DoodleView extends View   {

    /**
     * List of styled paths. Each StyledPath is a continuously
     * drawn streak of coordinates of the same color and
     * width.
     */
    ArrayList<StyledPath> styledPaths;


    int DEFAULT_COLOR = Color.BLACK;
    int DEFAULT_WIDTH = 15;


    public DoodleView(Context context) {
        super(context);
        styledPaths = new ArrayList<>();
    }

    /**
     * On draw redraws the background and the Path.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        setBackgroundColor(Color.WHITE);

        for(StyledPath styledPath : styledPaths){
            canvas.drawPath(styledPath, styledPath.paint);
        }
    }

    /**
     * Keeps track of the user's gestures on the screen
     * and converts them to "pencil"-strokes.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        Log.d("TOUCH_EVENT", "x: "+x+" y: "+y);

        switch(event.getAction()){

            case MotionEvent.ACTION_MOVE:
                styledPaths.get(styledPaths.size()-1).lineTo(x,y);
                break;
            case MotionEvent.ACTION_DOWN:
                StyledPath styledPath;
                try{
                    styledPath = new StyledPath(styledPaths.get(styledPaths.size()-1).paint.getColor(),   styledPaths.get(styledPaths.size()-1).paint.getStrokeWidth());
                }catch (IndexOutOfBoundsException e){
                    styledPath = new StyledPath(DEFAULT_COLOR, DEFAULT_WIDTH);
                }

                styledPath.moveTo(x,y);
                styledPaths.add(styledPath);
                break;

        }

        //trigger onDraw
        invalidate();

        //very important
        return true;
    }


    /**
     * Converts the contents of the view (the doodle)
     * to an image file and returns it.
     * @return
     */
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

    /**
     * Remove the last continuous stroke (ie the last path).
     */
    public void undo(){

        try{
            styledPaths.remove(styledPaths.size()-1);
        }catch (IndexOutOfBoundsException e){}

        invalidate();
    }

    public void showColorPickerDialog(){

        new ColorPickerDialog.Builder(this.getContext())
                .setTitle(R.string.color_palette)
                .setPreferenceName(getContext().getString(R.string.color_palette))
                .setPositiveButton(this.getContext().getString(R.string.confirm),

                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {

                                StyledPath styledPath;
                                try{
                                    styledPath = new StyledPath(envelope.getColor(), styledPaths.get(styledPaths.size()-1).paint.getStrokeWidth() );
                                }catch (IndexOutOfBoundsException e){
                                    styledPath = new StyledPath(envelope.getColor(), DEFAULT_WIDTH);
                                }


                                styledPaths.add(styledPath);

                            }
                        })
                .setNegativeButton(this.getContext().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show();

    }


    private class StyledPath extends Path{
        public Paint paint;

        StyledPath(int color, float width){
            paint = new Paint();
            paint.setColor(color);
            paint.setStrokeWidth(width);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
    }






}
