package com.ori.amd.treasurehunt;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.mta.sharedutils.AsyncHandler;

import java.io.IOException;

/**
 * Created by Ori on 1/15/2017.
 */

public class MyMusicRunnable implements Runnable, MediaPlayer.OnCompletionListener {

    static final String tag = "MyMusicRunnable";
    static int volume = 100;

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

        appContext = c.getApplicationContext();

        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
                int newVolume = sp.getInt(SettingsDialog.VOLUME_SETTINGS_KEY, 100);
                MyMusicRunnable.volume = newVolume;
                //Log.i(tag,"SharedPref new volume is :" + newVolume);
                boolean newClickSound = sp.getBoolean(SettingsDialog.CLICK_SOUND_SETTINGS_KEY, true);
                SettingsDialog.CLICK_SOUND_ENABLE = newClickSound;
                //Log.i(tag,"SharedPref new clickSound is :" + newClickSound);
            }
        });

        this.resId = resId;
    }

    public void changeVolume(int volume){
        MyMusicRunnable.volume = volume;
        float volumeToSet = (float) (1 - (Math.log(100 - volume) / Math.log(100)));
        mPlayer.setVolume(volumeToSet,volumeToSet);

        //Log.i(tag,"changeVolume() to : " + volumeToSet );
    }

    void changeVolume(){
        float volumeToSet = (float) (1 - (Math.log(100 - MyMusicRunnable.volume) / Math.log(100)));
        mPlayer.setVolume(volumeToSet,volumeToSet);
        //Log.i(tag,"changeVolume() to : " + volumeToSet );
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
                //Log.i(tag,"volume is : " + MyMusicRunnable.volume );
                changeVolume();
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
