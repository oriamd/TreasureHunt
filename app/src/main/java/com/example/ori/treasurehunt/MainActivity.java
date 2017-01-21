package com.example.ori.treasurehunt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.mta.sharedutils.AsyncHandler;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final static String goldTrackTag = "TotalGoldTracker";
    final static String tag = "MainActivity_log";



    final static int LVL_ONE_PRIZE = 100;
    final static int LVL_ONE_RADIOS = 500;
    public final static String GOAL_DISTANCE_IN_M = "distance_to_taget_code";
    public final static String PRIZE_AMOUNT = "prize_amount_code";
    public final static String TOTAL_GOLD_KEY ="total_player_gold_sp_key";
    public final static String VOLUME_KEY="volume_low_high";
    public final static String SOUND_KEY="sound_on_off";
    private TextView goldTextView;
    public static String totalGold;

    public static final int FIRST_STAGE_PRIZE = 100;
    public static final int FIRST_STAGE_RADIOS = 150;

    public static MyMusicRunnable musicPlayer;
    public static MySFxRunnable soundEffectsUtil;

    SettingsDialog settingsDialog;
    Dialog aboutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        if(musicPlayer == null) {
            musicPlayer = new MyMusicRunnable(this, R.raw.irishmusic);
        }

        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }

        settingsDialog = new SettingsDialog(this);

        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                totalGold = sp.getString(TOTAL_GOLD_KEY,"0");
                Log.i(goldTrackTag,"Total Gold : " + totalGold);
                goldTextView = (TextView) findViewById(R.id.textView4);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goldTextView.setText(totalGold);
                    }
                });
            }
        });

        Log.i(tag,"Created");

    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncHandler.post(musicPlayer);

        Log.i(tag,"Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AsyncHandler.post(musicPlayer);

        Log.i(tag,"Pause");

    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    public void startGame(View view) {
        soundEffectsUtil.playClickSound();

        Log.i(tag,"Starting Game");

        Intent intent = new Intent(getBaseContext() , GameActivity.class);
        intent.putExtra(GOAL_DISTANCE_IN_M , FIRST_STAGE_RADIOS);
        intent.putExtra(PRIZE_AMOUNT,Integer.toString(FIRST_STAGE_PRIZE));
        startActivity(intent);



    }


    public void startSettings(View view) {
        soundEffectsUtil.playClickSound();
        settingsDialog.show();
    }

    public void startTutorial(View view) {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    public void startAbout(View view){
        if(aboutDialog == null){
            aboutDialog = new Dialog(this,R.style.AppTheme_noActionBar);
            aboutDialog.setContentView(R.layout.about);
        }

        aboutDialog.show();
    }


}
