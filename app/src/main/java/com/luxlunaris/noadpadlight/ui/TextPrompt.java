package com.luxlunaris.noadpadlight.ui;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.luxlunaris.noadpadlight.R;


public class TextPrompt extends DialogFragment {


    EditText textField;
    TextView userTextField;
    Button confirmButton;

    TextRequester listener;

    String tag;

    String userText;

    public TextPrompt() {
        // Required empty public constructor
    }


    public static TextPrompt newInstance() {
        TextPrompt fragment = new TextPrompt();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text_prompt, container, false);

        confirmButton = view.findViewById(R.id.confirm_text_prompt_button);
        textField = view.findViewById(R.id.enter_text_prompt);
        userTextField = view.findViewById(R.id.text_prompt_user_text);

        confirmButton.setOnClickListener(new HandleConfirm());
        userTextField.setText(userText);

        return view;
    }


    public void setPrompt(String tag, String userText){
        this.tag = tag;
        this.userText = userText;
    }


    class HandleConfirm implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            listener.onTextInputted(tag, textField.getText().toString());
            dismiss();
        }
    }


    public void setListener(TextRequester listener){
        this.listener = listener;
    }


    public interface TextRequester{
        public void onTextInputted(String tag, String userResponse);
    }



}