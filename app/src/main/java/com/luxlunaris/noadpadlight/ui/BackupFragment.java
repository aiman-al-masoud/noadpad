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
import com.luxlunaris.noadpadlight.control.classes.Paths;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
        // Inflate the layout for this fragment
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

            //get the backup file's uri from the FileProvider (needed for android permissions and useless ostentation of security)
            Uri uri = FileProvider.getUriForFile(getContext(), "com.luxlunaris.fileprovider", backupFile);

            //create an intent to share the backup file with another app
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/zip");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, getString(R.string.total_data_export_title)));

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
                File file = getFileFromUri(uri);
                Notebook.getInstance().importPages(file.getPath());
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
        File selectedFile = new File(getActivity().getFilesDir(), "import.zip"); //your app file dir or cache dir you can use

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



}