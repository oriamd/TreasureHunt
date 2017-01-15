package com.example.ori.treasurehunt;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import com.mta.sharedutils.AsyncHandler;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final static int LVL_ONE_PRIZE = 100;
    final static int LVL_ONE_RADIOS = 500;
    public final static String GOAL_DISTANCE_IN_M = "distance_to_taget_code";
    public final static String PRIZE_AMOUNT = "prize_amount_code";

    IrishMusicRunnable musicPlayer;

    @Override
    protected void onStart() {
        super.onStart();

        AsyncHandler.post(musicPlayer);
    }

    @Override
    protected void onPause() {
        super.onPause();

        AsyncHandler.post(musicPlayer);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicPlayer = new IrishMusicRunnable(this);
    }


    public void startGame(View view) {

        Intent intent = new Intent(getBaseContext() , GameActivity.class);
        intent.putExtra(GOAL_DISTANCE_IN_M , 100);
        intent.putExtra(PRIZE_AMOUNT , 500);
        startActivity(intent);


    }


    static class IrishMusicRunnable implements Runnable, MediaPlayer.OnCompletionListener {
        Context appContext;
        MediaPlayer mPlayer;
        boolean musicIsPlaying = false;

        public IrishMusicRunnable(Context c) {
            // be careful not to leak the activity context.
            // can keep the app context instead.
            appContext = c.getApplicationContext();
        }

        public boolean isMusicIsPlaying() {
            return musicIsPlaying;
        }

        /**
         * MediaPlayer.OnCompletionListener callback
         *
         * @param mp
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
            // loop back - play again
            if (musicIsPlaying && mPlayer != null) {
                mPlayer.start();
            }
        }

        /**
         * toggles the music player state
         * called asynchronously every time the play/pause button is pressed
         */
        @Override
        public void run() {

            if (musicIsPlaying) {
                mPlayer.stop();
                musicIsPlaying = false;
            } else {
                if (mPlayer == null) {
                    mPlayer = MediaPlayer.create(appContext, R.raw.irishmusic);
                    mPlayer.start();
                    mPlayer.setOnCompletionListener(this); // MediaPlayer.OnCompletionListener
                } else {
                    try {
                        mPlayer.prepare();
                        mPlayer.setLooping(true);
                        mPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                musicIsPlaying = true;
            }

        }

    }


}
