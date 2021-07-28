package com.luxlunaris.noadpadlight.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.classes.Paths;

import java.io.File;


public class BackupFragment extends Fragment {

    Button exportButton;
    Button importButton;

    final int EXPORT_CODE = 1;
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
        // Inflate the layout for this fragment
        View view  =  inflater.inflate(R.layout.fragment_backup, container, false);

        exportButton = view.findViewById(R.id.exportButton);
        importButton = view.findViewById(R.id.importButton);

        exportButton.setOnClickListener(new HandleExport());

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
            File backupFile = Notebook.getInstance().generateBackupFile(Paths.BACKUP_DIR);

            Log.d("BACKUP_TEST", backupFile.exists()+" file existss");

            //get the backup file's uri from the FileProvider (needed for android permissions and useless ostentation of security)
            Uri uri = FileProvider.getUriForFile(getContext(), "com.luxlunaris.fileprovider", backupFile);


            //create an intent to share the backup file with another app
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/zip");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Export your data"));

        }
    }


    class HandleImport implements View.OnClickListener{

        @Override
        public void onClick(View v) {

        }
    }














}