package com.luxlunaris.noadpadlight.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.luxlunaris.noadpadlight.R;

import java.io.File;
import java.io.IOException;
import java.net.IDN;


public class AudioFragment extends DialogFragment  {

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
    Button playButton;

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
    MediaPlayer player;


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
    final int  STATE_PLAYING = 3;
    final int  STATE_PLAYING_PAUSED = 5;
    private int state = STATE_IDLE;


    public AudioFragment() {
        // Required empty public constructor
    }

    public static AudioFragment newInstance() {
        AudioFragment fragment = new AudioFragment();
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
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        recordButton = view.findViewById(R.id.record);
        playButton = view.findViewById(R.id.play);

        recordButton.setOnClickListener(new RecordHandler());
        playButton.setOnClickListener(new PlayHandler());


        return view;
    }




    /**
     * Reuqest permissions upon create.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //request permission upon launch.
        requestPermissions(permissions, REQUEST_AUDIO_PERMISSION);
    }


    public void setAudioPlaybackFile(File audioFile){
        this.audioFile = audioFile;
    }


    /**
     * Dimiss fragment if permission denied.
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


    private void stopRecording() {
        if(recorder!=null){
            recorder.stop();
            recorder.release();
        }
        recorder = null;
        state = STATE_IDLE;

        listener.onRecordingReady(audioFile);


    }


    private void startPlayer(){


        if(state!=STATE_IDLE){
            return;
        }

        player = new MediaPlayer();
        player.setOnCompletionListener(new PlayingDoneHandler());

        try{
            player.setDataSource(audioFile.getPath());
            player.prepare();
            player.start();

        }catch (IOException e){
            e.printStackTrace();
        }

        Toast.makeText(getContext(), ((float)player.getDuration()/1000)+" " , Toast.LENGTH_LONG).show();

        state = STATE_PLAYING;

    }


    private void stopPlayer(){
        if(player!=null){
            //player.stop();
            player.release();
        }
        player = null;

        state = STATE_IDLE;
    }


    private void pausePlayer(){
        player.pause();
        state = STATE_PLAYING_PAUSED;
    }

    private void resumePlayer(){
        player.start();
        state = STATE_PLAYING;
    }



    class PlayHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (state){

                case STATE_PLAYING:
                    pausePlayer();
                    ((Button)v).setText(R.string.play);
                    //((Button)v).setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);

                    break;
                case STATE_PLAYING_PAUSED:
                    resumePlayer();
                    ((Button)v).setText(R.string.pause);
                    //((Button)v).setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);

                    break;
                default:
                    startPlayer();
                    ((Button)v).setText(R.string.pause);

                    break;
            }
        }
    }


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

    class PlayingDoneHandler implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            state = STATE_IDLE;
            playButton.setText(R.string.play);
        }
    }
















}