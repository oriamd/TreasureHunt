package com.example.ori.treasurehunt;

import android.content.Context;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.mta.sharedutils.AsyncHandler;
import com.mta.sharedutils.Compat.Compat;

/**
 * Created by Ori on 1/15/2017.
 */

public class MySFxRunnable implements Runnable {
    Context appContext;
    SoundPool soundPool;
    /**
     * like a hash map, but more efficient
     */
    SparseIntArray soundsMap = new SparseIntArray();
    private boolean prepared = false;

    public MySFxRunnable(Context c) {
        // be careful not to leak the activity context.
        // can keep the app context instead.
        appContext = c.getApplicationContext();

        // init this object on a user thread.
        // The rest of the use of this class can be on the UI thread
        AsyncHandler.post(this);
    }

    /**
     * load and init the sound effects, so later I'll be able to play them instantly from the
     * UI thread.
     */
    @Override
    public void run() {

        soundPool = Compat.createSoundPool();

        /**
         * a callback when prepared -- we need to prevent playing before prepared
         */
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                prepared = true;
            }
        });

        /**
         * the load() returns a stream id that can be used to play the sound.
         * I use the "R.raw.xyz" integer as key, because it's useless to invent new keys for
         * them
         */

        soundsMap.put(R.raw.detectbeep, soundPool.load(appContext, R.raw.detectbeep, 1));
        soundsMap.put(R.raw.detectoron, soundPool.load(appContext, R.raw.detectoron, 1));
        soundsMap.put(R.raw.errorbuzz, soundPool.load(appContext, R.raw.errorbuzz, 1));


    }

    public void play(int soundKey) {
        if (soundPool == null || !prepared) {
            return;
        }
        soundPool.play(soundsMap.get(soundKey), 1.0f, 1.0f, 1, 0, 1.0f);
    }
}