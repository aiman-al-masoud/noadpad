package com.luxlunaris.noadpadlight.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.model.services.FileIO;

import java.io.File;

/**
 * Calls SAF (Storage Access Framework) and sharing API
 * to import/export pages in the format of a  single zipped file.
 *
 */
public class BackupFragment extends Fragment {

    /**
     * Triggers the sharing API to send the zipped file containing all of the currently saved pages.
     */
    Button exportButton;

    /**
     * Triggers the SAF to choose a file from where to import pages.
     */
    Button importButton;

    /**
     * SAF request code for a backup file to import pages from.
     */
    final int IMPORT_CODE = 2;


    public BackupFragment() {
        // Required empty public constructor
    }

    public static BackupFragment newInstance() {
        BackupFragment fragment = new BackupFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view  =  inflater.inflate(R.layout.fragment_backup, container, false);

        //get import and export buttons
        exportButton = view.findViewById(R.id.exportButton);
        importButton = view.findViewById(R.id.importButton);

        //set the onclick action of said buttons
        exportButton.setOnClickListener(new HandleExport());
        importButton.setOnClickListener(new HandleImport());

        return view;
    }

    /**
     * Calls android's share file api, and shares the
     * backup file.
     */
    class HandleExport implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            //get the backup file from the Notebook
            File backupFile = Notebook.getInstance().generateBackupFile();

            FileIO.exportFile(getContext(), backupFile, "application/zip");

        }
    }

    /**
     * Calls the SAF to let the user pick a zip file from \
     * which to import pages.
     */
    class HandleImport implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/zip");
            startActivityForResult(intent, IMPORT_CODE);
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
                File file = FileIO.getFileFromUri(getContext(), uri);
                Notebook.getInstance().importPages(file.getPath());
                break;

        }

    }





}