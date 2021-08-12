package com.luxlunaris.noadpadlight.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;
import com.luxlunaris.noadpadlight.control.interfaces.SettingsTagListener;
import com.luxlunaris.noadpadlight.model.interfaces.Page;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PageFragment extends Fragment implements SettingsTagListener {

    /**
     * The Page that this fragment represents
     */
    public Page page;

    /**
     * The button that gets pressed
     */
    transient Button pageButton;


    /**
     * Text color when unselected
     */
    private static int NORMAL_TEXT_COLOR = THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME)).FG_COLOR;


    /**
     * Text color when selected
     */
    private int SELECTED_TEXT_COLOR = Color.RED;



    public static PageFragment newInstance(Page page) {
        PageFragment fragment = new PageFragment();
        fragment.page = page;
        Settings.listenToTag(SETTINGS_TAGS.THEME, fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_page, container, false);

        //get this fragment's button
        pageButton = view.findViewById(R.id.pageButton);

        //set this fragment's text
        setText();

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
                setSelected(!page.isSelected());
                return true;
            }
        });

        //get the theme and set the background color
        THEMES theme = THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME) );
        pageButton.setBackgroundColor(theme.BG_COLOR);

        //if this fragment's page is selected, set the color to selected
        if(page.isSelected()){
            pageButton.setTextColor(SELECTED_TEXT_COLOR);
        }else{
            //else set it to the normal fg theme-color
            pageButton.setTextColor(theme.FG_COLOR);
        }



        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        setText();

        //different color if selected
        if (page.isSelected()) {
            pageButton.setTextColor(SELECTED_TEXT_COLOR);
        }

        //different color if in recycle bin
        if(page.isInRecycleBin()){
            pageButton.setBackgroundColor(Color.DKGRAY);
        }


    }
    /**
     * Set this fragment's text to a preview of the page
     * with the date of last modification in locale format.
     */
    private void setText(){

        String text  = page.getPreview();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm");
        String dateString = dateFormat.format(new Date(page.getLastModifiedTime()));

        text+="\n"+dateString;

        Spanned spanned = Html.fromHtml(text);
        pageButton.setText(spanned);

        //then decide whether to let user change this size
        pageButton.setTextSize(20);
    }

    /**
     * Set this fragment's status as selected
     */
    public void setSelected(boolean selected){

        page.setSelected(selected);

        if(selected){
            pageButton.setTextColor(this.SELECTED_TEXT_COLOR);
        }else{
            pageButton.setTextColor(this.NORMAL_TEXT_COLOR);
        }

    }

    public Page getPage(){
        return page;
    }

    @Override
    public void onTagUpdated(SETTINGS_TAGS tag) {
        THEMES newTheme = THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME));
        NORMAL_TEXT_COLOR =  newTheme.FG_COLOR;
    }


}