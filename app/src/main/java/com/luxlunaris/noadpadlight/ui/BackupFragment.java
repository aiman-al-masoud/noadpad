package com.luxlunaris.noadpadlight.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.control.classes.Notebook;
import com.luxlunaris.noadpadlight.control.classes.Paths;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


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

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/zip");


            startActivityForResult(intent, IMPORT_CODE);



        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();

        Log.d("IMPORT_TEST", "uri: "  +uri);

        File file = getFileFromUri(uri);

        Log.d("IMPORT_TEST", "file: "  +file);
        Log.d("IMPORT_TEST", "file: "  +file.exists());


        Notebook.getInstance().importPages(file.getPath());







    }



    private File getFileFromUri(Uri contentUri) {
        //Use content Resolver to get the input stream that it holds the data and copy that in a temp file of your app file directory for your references
        File selectedFile = new File(getActivity().getFilesDir(), "import.zip"); //your app file dir or cache dir you can use

        //File selectedFile = new File(Paths.TMP_DIR+File.separator+"import.zip");



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

        //after this you will get file from the selected file and you can do

        return selectedFile;
    }



}