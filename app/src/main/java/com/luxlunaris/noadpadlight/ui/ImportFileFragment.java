package com.luxlunaris.noadpadlight.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;



public class ImportFileFragment extends DialogFragment {

    /**
     * Request Code for importing a file.
     */
    final int IMPORT_CODE =1;

    /**
     * Starts the SAF to let the user pick a file.
     */
    Button selectFileButton;

    /**
     * Displays the name of the file that got picked.
     */
    TextView fileSelectedText;

    /**
     * Confirm the file.
     */
    Button confirmSelectionButton;

    /**
     * A copy of the file that got selected.
     */
    private File selectedFile;

    /**
     * The object that calls ImportFileFragment and
     * "waits" for the response.
     */
    private FileRequester listener;

    public ImportFileFragment() {
        // Required empty public constructor
    }

    public static ImportFileFragment newInstance() {
        ImportFileFragment fragment = new ImportFileFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_import_file, container, false);

        selectFileButton = view.findViewById(R.id.select_file_button);
        confirmSelectionButton = view.findViewById(R.id.confirm_file_selected_button);
        fileSelectedText = view.findViewById(R.id.confirm_file_selected_text);

        selectFileButton.setOnClickListener(new HandleImport());
        confirmSelectionButton.setOnClickListener(new HandleConfirm());

        //paint this fragment with the current theme
        THEMES theme = THEMES.getThemeByName(Settings.getString(SETTINGS_TAGS.THEME));
        view.setBackgroundColor(theme.BG_COLOR);
        selectFileButton.setBackgroundColor(theme.BG_COLOR);
        confirmSelectionButton.setBackgroundColor(theme.BG_COLOR);
        fileSelectedText.setBackgroundColor(theme.BG_COLOR);
        selectFileButton.setTextColor(theme.FG_COLOR);
        confirmSelectionButton.setTextColor(theme.FG_COLOR);
        fileSelectedText.setTextColor(theme.FG_COLOR);


        return view;
    }

    /**
     * Calls the SAF to let the user pick a file.
     */
    class HandleImport implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, IMPORT_CODE);
        }
    }

    /**
     * Calls the listener (FileRequester) that
     * requested a file from this fragment.
     */
    class HandleConfirm implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            //halt if not file was selected.
            if(selectedFile==null){
                return;
            }

            //pass the file to the listener
            listener.onFileObtained(selectedFile);

            //dismiss this fragment.
            dismiss();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //halt if result code is not "ok"
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        //halt if "data" is null
        if(data==null){
            return;
        }


        switch (requestCode){

            case IMPORT_CODE:
                Uri uri = data.getData();
                selectedFile = getFileFromUri(uri);
                fileSelectedText.setText(getString(R.string.selected_word)+uri.getPath());
                break;

        }

    }

    /**
     * Given the uri of an external file, make a copy of it
     * in app-internal storage and return it.
     * @param contentUri
     * @return
     */
    private File getFileFromUri(Uri contentUri) {
        //Use content Resolver to get the input stream that it holds the data and copy that in a temp file of your app file directory for your references
        File selectedFile = new File(getActivity().getFilesDir(), "import"); //your app file dir or cache dir you can use

        try {

            InputStream in = getActivity().getContentResolver().openInputStream(contentUri);
            OutputStream out = new FileOutputStream(selectedFile);

            byte[] buf = new byte[1024];
            int len;

            if (in != null) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectedFile;
    }

    /**
     * Set a FileRequester listener to whom to pass
     * the file when obtained.
     * @param listener
     */
    public void setFileRequester(FileRequester listener){
        this.listener = listener;
    }


    /**
     * Listens to an ImportFileFragment and gets
     * notified when the file is ready.
     */
    public interface FileRequester{
        public void onFileObtained(File file);
    }














}