package com.example.ori.treasurehunt;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Ori on 1/15/2017.
 */


public class IntervalMusicRunnable implements Runnable{

    static final String tag = "MyMusicRunnable";

    static public final boolean PLAY = true;
    static public final boolean PAUSE = false;

    private static MySFxRunnable soundEffectsUtil;

    Context appContext;

    int resId;

    long frequency = 1000; //Default is 1 second
    boolean action;

    /**
     *
     * @param c
     * @param resId the music res
     */
    public IntervalMusicRunnable(Context c, int resId, MySFxRunnable soundEffectsUtil ) {
        // be careful not to leak the activity context.
        // can keep the app context instead.
        appContext = c.getApplicationContext();

        this.soundEffectsUtil = soundEffectsUtil;

        this.resId = resId;
    }

    /**
     * Will change the Resource of the Sound while playing for for nest start
     * Source must be in MySFxRunnable
     * @param resId
     */
    public void changeRes(int resId){
        this.resId = resId;
    }

    /**
     * Setting the frequency which the sound is playing
     * @param frequency in milliseconds
     */
    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }


    public void setPlay(){
        action = PLAY;
    }
    public void setPause(){
        action = PAUSE;
    }

    /**
     * toggles the music player state
     * called asynchronously every time the play/pause button is pressed
     */
    @Override
    public void run() {
        action = PLAY;
        try{

        while (action == PLAY){
            Log.i(tag,"Playing");
            soundEffectsUtil.play(resId);
            Thread.sleep(frequency);
        }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
