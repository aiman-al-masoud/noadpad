package com.luxlunaris.noadpadlight.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Settings;

/**
 *
 */
public class ToggleFragment extends Fragment {

    /**
     * The switch view
     */
    private Switch toggleSwitch;

    /**
     * Describes what the switch is supposed to do
     */
    private String text;

    /**
     * The tag of a binary setting
     */
    private Enum SETTING_TAG;

    /**
     * Tag's value in the settings file when setting enabled
     */
    public static final String ENABLED = "true";

    /**
     *  Tag's value in the settings file when setting disabled
     */
    public static final String DISABLED = "false";


    public ToggleFragment() {
        // Required empty public constructor
    }


    public static ToggleFragment newInstance(String text, Enum SETTING_TAG) {
        ToggleFragment fragment = new ToggleFragment();
        fragment.text = text;
        fragment.SETTING_TAG = SETTING_TAG;
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_toggle, container, false);

        //get the switch
        toggleSwitch = view.findViewById(R.id.toggle_switch);

        //set the text to be displayed
        toggleSwitch.setText(text);

        //set the correct switch position for the currently selected choice
        toggleSwitch.setChecked(getCurrentValue());

        //set the switch's behavior
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                System.out.println("I got clicked "+isChecked);

                //if checked set the value of the setting to true
                if(isChecked){
                    Settings.get().setTagValue(SETTING_TAG.toString(), ENABLED);
                }else{
                    Settings.get().setTagValue(SETTING_TAG.toString(), DISABLED);
                }

            }
        });


        return view;
    }


    /**
     * Get the current value of the binary setting
     * @return
     */
    private boolean getCurrentValue(){

        //get the current value of the binary setting
        String currentSettingValue = Settings.get().getTagValue(SETTING_TAG.toString());

        //if no value is currently saved, assume setting is "false"
        if(currentSettingValue==null){
            return false;
        }

        //if the value saved on the file is "true":
        if(currentSettingValue.toLowerCase().trim().equals(ENABLED)){
            return true;
        }



        return false;
    }

    /**
     * On resume, make sure that the switch is set to the right position
     */
    @Override
    public void onResume() {
        super.onResume();
        //set the correct switch position for the currently selected choice
        toggleSwitch.setChecked(getCurrentValue());
    }


}