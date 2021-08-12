package com.luxlunaris.noadpadlight.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.luxlunaris.noadpadlight.R;


public class YayOrNayDialog extends DialogFragment {


    public static final int POSITIVE_RESPONSE = 1;
    public static final int NEGATIVE_RESPONSE = 2;
    public static final int DISMISSED_ME = 3;


    Button confirmButton;
    Button cancelButton;
    BinaryQuestioner listener;
    TextView textView;


    String TAG;
    String questionText;


    public YayOrNayDialog() {
        // Required empty public constructor
    }

    public static YayOrNayDialog newInstance(String TAG, String questionText) {
        YayOrNayDialog fragment = new YayOrNayDialog();
        fragment.setQuestion(TAG, questionText);
        return fragment;
    }

    public void setQuestion(String TAG, String questionText){
        this.TAG = TAG;
        this.questionText = questionText;
    }

    public void setListener(BinaryQuestioner listener){
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_dialog, container, false);

        confirmButton = view.findViewById(R.id.confirm_button_dialog);
        cancelButton = view.findViewById(R.id.cancel_button_dialog);
        textView = view.findViewById(R.id.textview_dialog);
        confirmButton.setOnClickListener(new ConfirmHandler());
        cancelButton.setOnClickListener(new CancelHandler());
        textView.setText(questionText);

        return view;
    }

    /**
     * If confirm button gets pressed, result = POSITIVE_RESPONSE
     */
    class ConfirmHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            listener.onUserBinaryAnswer(TAG, POSITIVE_RESPONSE);
            dismiss();
        }
    }

    /**
     * If cancel button gets pressed, result = NEGATIVE_RESPONSE
     */
    class CancelHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            listener.onUserBinaryAnswer(TAG, NEGATIVE_RESPONSE );
            dismiss();
        }
    }

    /**
     * If dismissed, result = DISMISSED_ME
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onUserBinaryAnswer(TAG, DISMISSED_ME);
    }

    /**
     * Implemented by the Activity/Fragment that needs
     * to let the user confirm a choice.
     */
    public interface BinaryQuestioner{

        /**
         *
         * @param tag: a request code to identify the question on callback
         * @param result: user response
         */
        public void onUserBinaryAnswer(String tag, int result);
    }




}