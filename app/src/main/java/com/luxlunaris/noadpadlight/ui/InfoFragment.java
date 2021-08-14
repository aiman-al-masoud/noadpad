package com.luxlunaris.noadpadlight.ui;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.SETTINGS_TAGS;
import com.luxlunaris.noadpadlight.control.classes.Settings;

/**
 * It's a simple un-interactive prompt that
 * displays some text to the user.
 */
public class InfoFragment extends DialogFragment {

    /**
     * Text displayed as info
     */
    String text;

    /**
     * Dismisses the fragment
     */
    Button gotItButton;

    /**
     * Displays the info text
     */
    TextView textArea;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance(String text) {
        InfoFragment fragment = new InfoFragment();
        fragment.text = text;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        gotItButton = view.findViewById(R.id.info_frag_button);
        gotItButton.setOnClickListener(new GotItHandler());
        textArea = view.findViewById(R.id.infoFragmentText);
        textArea.setText(text);
        THEMES theme = THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME));
        gotItButton.setBackgroundColor(theme.BG_COLOR);
        gotItButton.setTextColor(theme.FG_COLOR);
        textArea.setBackgroundColor(theme.BG_COLOR);
        textArea.setTextColor(theme.FG_COLOR);
        view.setBackgroundColor(theme.BG_COLOR);

        return view;
    }

    class GotItHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            dismiss();
        }
    }



}