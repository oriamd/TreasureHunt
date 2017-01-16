package com.example.ori.treasurehunt;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Ori on 1/15/2017.
 */

public class MyMusicRunnable implements Runnable, MediaPlayer.OnCompletionListener {

    static final String tag = "MyMusicRunnable";
    static float volume = 1;

    Context appContext;
    MediaPlayer mPlayer;
    boolean musicIsPlaying = false;
    int resId;

    /**
     *
     * @param c
     * @param resId the music res
     */
    public MyMusicRunnable(Context c, int resId ) {
        // be careful not to leak the activity context.
        // can keep the app context instead.
        appContext = c.getApplicationContext();


        this.resId = resId;
    }

    void changeVolume(float volume){
        MyMusicRunnable.volume = volume;

        mPlayer.setVolume(volume,volume);

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
                mPlayer = MediaPlayer.create(appContext, resId);
                mPlayer.setLooping(true);
                mPlayer.setVolume(volume,volume);
                mPlayer.start();
                mPlayer.setOnCompletionListener(this); // MediaPlayer.OnCompletionListener
            } else {
                try {
                    mPlayer.prepare();
                    mPlayer.setLooping(true);
                    mPlayer.start();
                    mPlayer.setVolume(volume,volume);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            musicIsPlaying = true;
        }

    }

}
