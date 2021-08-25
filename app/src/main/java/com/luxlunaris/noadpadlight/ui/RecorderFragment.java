package com.luxlunaris.noadpadlight.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.luxlunaris.noadpadlight.R;

import java.io.File;
import java.io.IOException;


public class RecorderFragment extends DialogFragment  {

    /**
     * Please implement this interface to get the
     * audio-file back when it's ready.
     */
    interface AudioActivity{
        void onRecordingReady(File audioFile);
    }

    /**
     * Buttons and commands.
     */
    Button recordButton;

    /**
     * Audio file (could be the one recorded or the one played, can't record and play simultaneously).
     */
    File audioFile;

    /**
     * Temporary storage directory for a newly recorded audio file.
     */
    String TMP_STORAGE_DIR;


    /**
     * Media player and recorder objects.
     */
    MediaRecorder recorder;

    /**
     * Listener that gets a requested audio file back
     * after the user's done recording.
     */
    AudioActivity listener;

    /**
     * Permissions.
     */
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private final int REQUEST_AUDIO_PERMISSION = 1;

    /**
     * Internal state.
     */
    final int STATE_IDLE = -1;
    final int  STATE_RECORDING = 2;
    private int state = STATE_IDLE;


    public RecorderFragment() {
        // Required empty public constructor
    }

    public static RecorderFragment newInstance() {
        RecorderFragment fragment = new RecorderFragment();
        return fragment;
    }

    /**
     * The listener will receive an audio file upon
     * completion.
     * @param listener
     */
    public void setListener(AudioActivity listener){
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recorder, container, false);
        recordButton = view.findViewById(R.id.record);
        recordButton.setOnClickListener(new RecordHandler());
        recordButton.setBackgroundColor(Color.WHITE);
        recordButton.setTextColor(Color.BLACK);
        return view;
    }

    /**
     * Request permissions upon create.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //request permission upon launch.
        requestPermissions(permissions, REQUEST_AUDIO_PERMISSION);
    }

    /**
     * Dismiss fragment if permission denied.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_AUDIO_PERMISSION:
                permissionToRecordAccepted  = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }

        if(!permissionToRecordAccepted){
            dismiss();
        }
    }

    /**
     * Start the recorder.
     */
    private void startRecording(){

        if(state!=STATE_IDLE){
            return;
        }

        //set up new tmp file path
        TMP_STORAGE_DIR  = getContext().getCacheDir().getPath();
        String pathname = TMP_STORAGE_DIR+File.separator+"voice_rec_"+System.currentTimeMillis();
        audioFile = new File(pathname);

        //prepare recorder
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(audioFile.getPath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //start recording
        recorder.start();

        state = STATE_RECORDING;
    }

    /**
     *  Stop recording and call the listener to
     *  deliver audioFile.
     */
    private void stopRecording() {
        if(recorder!=null){
            recorder.stop();
            recorder.release();
        }
        recorder = null;
        state = STATE_IDLE;

        listener.onRecordingReady(audioFile);
    }

    /**
     * Handle pressing the record button.
     */
    class RecordHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (state){

                case STATE_RECORDING:
                    stopRecording();
                    ((Button)v).setText(R.string.record);
                    break;
                default:
                    startRecording();
                    ((Button)v).setText(R.string.stop);
                    break;
            }
        }
    }













}