package com.ori.amd.treasurehunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/***
 * The game Tutorial
 */
public class TutorialActivity extends AppCompatActivity {

    private MySFxRunnable soundEffectsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }
    }

    /**
     * playing sound who plays when player getting far from target
     * @param view
     */
    public void playFarSound(View view) {
        soundEffectsUtil.play(R.raw.detectbeep);
    }

    /**
     * playing sound who plays when player getting close from target
     * @param view
     */
    public void playCloseSound(View view) {
        soundEffectsUtil.play(R.raw.errorbuzz);
    }
}
