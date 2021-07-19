package com.luxlunaris.noadpadlight.ui;

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


        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<Enum>(this.getContext(), R.layout.support_simple_spinner_dropdown_item, optionsEnum  )  );

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings.setTagValue(settingTag, optionsEnum[position].toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

        String tagValue = Settings.getString(settingTag);

        int i;
        for(i =0; i<optionsEnum.length; i++){
            if(tagValue.equals(optionsEnum[i].toString())){
                break;
            }
        }


        spinner.setSelection(i);
    }




}