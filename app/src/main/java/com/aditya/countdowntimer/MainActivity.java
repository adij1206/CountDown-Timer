package com.aditya.countdowntimer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MediaPlayer mediaPlayer,player;
    CircularView circularView;
    EditText times_val;
    Button playBtn,pauseBtn,resumeBtn,stopoBtn,goBtn;

    boolean isPause;
    long time;
    int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }

        init();

        mediaPlayer = new MediaPlayer();
        player = new MediaPlayer();

        player = MediaPlayer.create(getApplicationContext(),R.raw.stop);
    }

    private void init() {
        playBtn = (Button) findViewById(R.id.play);
        pauseBtn = (Button) findViewById(R.id.pause);
        resumeBtn = (Button) findViewById(R.id.resume);
        stopoBtn = (Button) findViewById(R.id.stop);
        goBtn = (Button) findViewById(R.id.go);

        playBtn.setOnClickListener(this);
        resumeBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        goBtn.setOnClickListener(this);
        stopoBtn.setOnClickListener(this);

        playBtn.setEnabled(false);
        stopoBtn.setEnabled(false);
        resumeBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
    }

    public void go() {
        times_val = (EditText) findViewById(R.id.Time);

        if(TextUtils.isEmpty(times_val.getText())){
            Toast.makeText(this, "Please Enter The Time Limit", Toast.LENGTH_SHORT).show();
        }
        else{
            time = Long.parseLong(times_val.getText().toString());
            circularView = (CircularView) findViewById(R.id.circularView);
            CircularView.OptionsBuilder optionsBuilder =
                    new CircularView.OptionsBuilder()
                    .shouldDisplayText(true)
                    .setCounterInSeconds(time)
                    .setCircularViewCallback(new CircularViewCallback() {
                        @Override
                        public void onTimerFinish() {
                            Toast.makeText(MainActivity.this, "Timer Finished", Toast.LENGTH_SHORT).show();
                            if(mediaPlayer!=null){
                                mediaPlayer.stop();
                            }
                            player.start();
                            goBtn.setEnabled(true);
                        }

                        @Override
                        public void onTimerCancelled() {
                            Toast.makeText(MainActivity.this, "Timer Cancelled", Toast.LENGTH_SHORT).show();
                            goBtn.setEnabled(true);
                        }
                    });
            circularView.setOptions(optionsBuilder);
        }
    }

    public void start_btn() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.song);
        mediaPlayer.start();
        circularView.startTimer();

    }

    public void stop_btn() {
        circularView.stopTimer();
        if(mediaPlayer.isPlaying()){
            if(mediaPlayer!=null){
                mediaPlayer.stop();
            }
        }
        player.start();
    }

    public void pause_btn() {
        if(circularView.pauseTimer()){
            isPause = true;
            Toast.makeText(this, "Timer Paused!", Toast.LENGTH_SHORT).show();
            mediaPlayer.pause();
            length = mediaPlayer.getCurrentPosition();
        }
        else{
            Toast.makeText(this, "Timer is Finished Before Pause", Toast.LENGTH_SHORT).show();
        }
    }

    public void resume_btn() {
        if(isPause){
            circularView.resumeTimer();
            isPause = false;
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
            Toast.makeText(this, "Timer Is Resumed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Timer is Not Paused", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        circularView.stopTimer();
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if(player!=null && player.isPlaying()){
            player.stop();
            player.release();
            player = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        pause_btn();
        super.onPause();
    }

    @Override
    protected void onResume() {
        resume_btn();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go:
                goBtn.setEnabled(false);
                playBtn.setEnabled(true);
                go();
                break;
            case R.id.play:
                stopoBtn.setEnabled(true);
                pauseBtn.setEnabled(true);
                start_btn();
                playBtn.setEnabled(false);
                break;
            case R.id.stop:
                stop_btn();
                playBtn.setEnabled(false);
                stopoBtn.setEnabled(false);
                resumeBtn.setEnabled(false);
                pauseBtn.setEnabled(false);
                break;
            case R.id.resume:
                pauseBtn.setEnabled(true);
                resume_btn();
                resumeBtn.setEnabled(false);
                break;
            case R.id.pause:
                resumeBtn.setEnabled(true);
                pause_btn();
                pauseBtn.setEnabled(false);
                break;
        }
    }
}
