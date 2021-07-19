package com.luxlunaris.noadpadlight.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;


/**
 * Provides a list of choices for the user to select just
 * one from.
 *
 *
 * Initializer needs:
 *
 * 1 The SETTINGS_TAGS to indicate the setting that is
 * going to be affected by this spinner.
 *
 * 2 An array of Enum[] objects that are going to define the
 * choices to be provided to the user.
 *
 * 3 Some text, to be displayed for the user.
 *
 *
 *
 */
public class SpinnerFragment extends Fragment {


    /**
     * Defines the options that will be displayed by the spinner
     */
    Enum[] optionsEnum;

    /**
     * Specifies the setting to be changed
     */
    SETTINGS_TAGS settingTag;

    /**
     * Text to be displayed to the user.
     */
    String text;

    /**
     * The spinner view itself.
     */
    Spinner spinner;


    public SpinnerFragment() {
        // Required empty public constructor
    }


    public static SpinnerFragment newInstance(SETTINGS_TAGS settingTag, Enum[] optionsEnum, String text) {
        SpinnerFragment fragment = new SpinnerFragment();
        fragment.optionsEnum = optionsEnum;
        fragment.settingTag = settingTag;
        fragment.text = text;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_spinner, container, false);


        //set the text that outlines the spinner's purpose
        ((TextView)(view.findViewById(R.id.spinner_text))).setText(text);

        //get the spinner view object
        spinner = view.findViewById(R.id.spinner);

        //set the spinner's choices
        spinner.setAdapter(new ArrayAdapter<Enum>(this.getContext(), R.layout.support_simple_spinner_dropdown_item, optionsEnum  )  );

        //set the spinner's behaviour.
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                //set the color of the text on the spinner
                THEMES theme = THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME));
                ((TextView) parent.getChildAt(0)).setTextColor(theme.FG_COLOR);

                //change the setting based on what item is selected
                Settings.setTagValue(settingTag, optionsEnum[position].toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;

    }

    /**
     * Make sure that the spinner is pointing to the currently
     * selected option.
     */
    @Override
    public void onResume() {
        super.onResume();

        //get the tag's value from the settings
        String tagValue = Settings.getString(settingTag);

        //get the corresponding enum value
        int i;
        for(i =0; i<optionsEnum.length; i++){
            if(tagValue.equals(optionsEnum[i].toString())){
                break;
            }
        }

        //set the spinner's selected item.
        spinner.setSelection(i);
    }




}