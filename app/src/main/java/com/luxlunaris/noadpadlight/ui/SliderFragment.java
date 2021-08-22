package com.luxlunaris.noadpadlight.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.luxlunaris.noadpadlight.R;


public class SliderFragment extends DialogFragment {


    SeekBar seekBar;
    TextView textView;
    String tag;

    SliderListener listener;
    private String text;
    private int startProgress;

    public SliderFragment() {
        // Required empty public constructor
    }

    public static SliderFragment newInstance() {
        SliderFragment fragment = new SliderFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider, container, false);

        seekBar = view.findViewById(R.id.seekBar);
        textView = view.findViewById(R.id.seekBarText);


        seekBar.setOnSeekBarChangeListener(new SeekBarListener());


        seekBar.setProgress(startProgress);
        textView.setText(text);
        return view;
    }


    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            listener.onSliderReleased(tag, seekBar.getProgress());
            dismiss();
        }
    }


    public interface SliderListener{
        void onSliderReleased(String tag, int newProgressLevel);
    }

    public SliderFragment setListener(SliderListener listener){
        this.listener = listener;
        return this;
    }

    public SliderFragment setText(String text){
        this.text = text;
        return this;
    }

    public SliderFragment setTag(String tag){
        this.tag = tag;
        return this;
    }

    public SliderFragment setStartProgress(int startProgress){
        this.startProgress = startProgress;
        return this;
    }




}