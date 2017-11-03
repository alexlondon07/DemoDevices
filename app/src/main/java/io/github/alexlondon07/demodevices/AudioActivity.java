package io.github.alexlondon07.demodevices;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioActivity extends AppCompatActivity {

    private ProgressBar audioProgressBarAudio;
    private ImageView btn_delete, btn_play;
    private ToggleButton tbn_record_stop;
    private String fileName;
    private CountDownTimer countDownTimer;
    private MediaRecorder mediaRecorder;
    private boolean isReproduction = true;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        loadView();
        loadListener();
        fileName = createPathAudio();
    }

    private String createPathAudio() {

        String timeStamp = new SimpleDateFormat(Constants.FORMAT_DATE_FILE).format(new Date());
        String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        return storageDir + "/" + Constants.PREFIX_FILE_AUDIO +  timeStamp +  Constants.SUFFIX_FILE_AUDIO;
    }

    private void loadListener() {
        tbn_record_stop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isRecord) {
                if (Permissions.isGrantedPermissions(AudioActivity.this, Manifest.permission.RECORD_AUDIO) &&
                        Permissions.isGrantedPermissions(AudioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    managerRecord(isRecord);
                } else {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO};
                    Permissions.verifyPermissions(AudioActivity.this,permissions);
                }
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(isReproduction);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDelete();
            }
        });
    }

    private void onDelete() {

        if (mediaPlayer !=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            Toast.makeText(this, "Audio eliminado", Toast.LENGTH_SHORT).show();
            //btn_delete.setVisibility(View.GONE);
        }
        tbn_record_stop.setBackgroundResource(R.drawable.ic_mic_black_24dp);
    }

    private void onPlay(boolean isReproduction) {

        if (isReproduction) {
            if (mediaPlayer == null) {
                startPlaying();
            }else{
                continueReproduction();
            }
        }else{
            pauseReproduction();
        }
    }

    private void pauseReproduction() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            countDownTimer.cancel();
        }
    }

    private void continueReproduction() {

        //seekto

    }

    private void startPlaying() {
        audioProgressBarAudio.setProgress(0);
        //setmax
        mediaPlayer = new MediaPlayer();

        try {

            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            startProgress();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void managerRecord(boolean isRecord) {
        if (isRecord) {
            tbn_record_stop.setBackgroundResource(R.drawable.ic_stop_black_24dp);
            btn_play.setEnabled(false);
            btn_delete.setEnabled(false);
        }else{
            btn_play.setEnabled(true);
            btn_delete.setEnabled(true);
        }
        onRecord(isRecord);
    }

    private void onRecord(boolean start) {
        if (start) {
            startProgress();
            starRecording();
        }else{
            stopRecording();
        }
    }

    private void stopRecording() {
        tbn_record_stop.setBackgroundResource(R.drawable.ic_mic_black_24dp);
        countDownTimer.cancel();
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void starRecording() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setMaxDuration(Constants.MAX_DURATION);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(fileName);

        try {

            mediaRecorder.prepare();
            mediaRecorder.start();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void startProgress() {

        countDownTimer = new CountDownTimer(Constants.MAX_DURATION, Constants.INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {

                audioProgressBarAudio.setProgress(audioProgressBarAudio.getProgress() + Constants.INTERVAL);

            }

            @Override
            public void onFinish() {
                if (audioProgressBarAudio.getProgress() > 0) {
                    audioProgressBarAudio.setProgress(audioProgressBarAudio.getProgress() + Constants.INTERVAL);
                    tbn_record_stop.setBackgroundResource(R.drawable.ic_mic_black_24dp);
                }
            }
        };
    }

    private void loadView() {
        tbn_record_stop = (ToggleButton) findViewById(R.id.tgbtn_record_stop);
        audioProgressBarAudio = (ProgressBar) findViewById(R.id.audio_progressbar);
        btn_delete = (ImageView) findViewById(R.id.btn_delete);
        btn_play = (ImageView) findViewById(R.id.btn_play);

    }
}
