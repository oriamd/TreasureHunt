package com.example.ori.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.mta.sharedutils.AsyncHandler;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final static String goldTrackTag = "TotalGoldTracker";

    //should sound play when onClick
    public static boolean CLICK_SOUND_ENABLE = true;

    final static int LVL_ONE_PRIZE = 100;
    final static int LVL_ONE_RADIOS = 500;
    public final static String GOAL_DISTANCE_IN_M = "distance_to_taget_code";
    public final static String PRIZE_AMOUNT = "prize_amount_code";
    public final static String TOTAL_GOLD_KEY ="total_player_gold_sp_key";
    public final static String VOLUME_KEY="volume_low_high";
    public final static String SOUND_KEY="sound_on_off";
    public static TextView goldTextView;
    public static String totalGold;

    public static final int FIRST_STAGE_PRIZE = 100;

    public static MyMusicRunnable musicPlayer;
    public static MySFxRunnable soundEffectsUtil;

    @Override
    protected void onResume() {
        super.onResume();

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

        if(musicPlayer == null) {
            musicPlayer = new MyMusicRunnable(this, R.raw.irishmusic);
        }

        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                totalGold = sp.getString(TOTAL_GOLD_KEY,"0");
                Log.i(goldTrackTag,"Total Gold : " + totalGold);
                goldTextView = (TextView) findViewById(R.id.textView4);
                goldTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        goldTextView.setText(totalGold);
                    }
                });
            }
        }).start();

    }

    public void startGame(View view) {
        if(CLICK_SOUND_ENABLE){
            soundEffectsUtil.play(R.raw.detectoron);
        }

        Intent intent = new Intent(getBaseContext() , GameActivity.class);
        intent.putExtra(GOAL_DISTANCE_IN_M , 100);
        intent.putExtra(PRIZE_AMOUNT,Integer.toString(FIRST_STAGE_PRIZE));
        startActivity(intent);


    }


    public void startSettings(View view) {
        Intent intent = new Intent(getBaseContext() , SettingsActivity.class);
        startActivity(intent);
    }
}
