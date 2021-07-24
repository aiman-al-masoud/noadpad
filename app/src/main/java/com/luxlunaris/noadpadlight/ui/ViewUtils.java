package com.luxlunaris.noadpadlight.ui;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of useful methods to deal with views.
 */
public class ViewUtils {


    /**
     * Get all children views of a view recursively.
     * @param view
     * @return
     */
    public static List<View> getAllChildren(View view) {

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup;
        try{
            viewGroup = (ViewGroup)view;
        }catch (ClassCastException e){
            return result;
        }

        for(int i=0; i<viewGroup.getChildCount(); i++){
            View child  = viewGroup.getChildAt(i);
            result.addAll(getAllChildren(child));
            result.add(child);
        }

        return result;
    }



}
