package com.example.ori.treasurehunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

    public void playFarSound(View view) {
        soundEffectsUtil.play(R.raw.detectbeep);
    }

    public void playCloseSound(View view) {
        soundEffectsUtil.play(R.raw.errorbuzz);
    }
}
