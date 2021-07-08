package com.luxlunaris.noadpadlight.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

public class PageFragment extends Fragment {

    /**
     * The Page that this fragment represents
     */
    public Page page;

    /**
     * The button that gets pressed
     */
    Button pageButton;

    /**
     * Is this fragment selected?
     */
    private boolean selected = false;


    /**
     * Text color when unselected
     */
    private int NORMAL_TEXT_COLOR = Color.WHITE;


    /**
     * Text color when selected
     */
    private int SELECTED_TEXT_COLOR = Color.RED;


    public static PageFragment newInstance(Page page) {
        PageFragment fragment = new PageFragment();
        fragment.page = page;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_page, container, false);

        pageButton = (Button)view.findViewById(R.id.pageButton);

        pageButton.setText(page.getPreview());

        //set the button's on-click action
        pageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ReaderActivity.class);
                intent.putExtra("PAGE", page);
                startActivity(intent);
            }
        });

        //set the button's long click action
        pageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setSelected(!selected);
                return true;
            }
        });


        return view;
    }


    /**
     * Set this fragment's status as selected
     */
    public void setSelected(boolean selected){

        this.selected = selected;

        if(selected){
            pageButton.setTextColor(this.SELECTED_TEXT_COLOR);
        }else{
            pageButton.setTextColor(this.NORMAL_TEXT_COLOR);
        }

    }

    /**
     * Is this fragment currently selected?
     * @return
     */
    public boolean isSelected(){
        return selected;
    }


    /**
     * Delete page
     */
    public void delete(){
        page.delete();
    }





}