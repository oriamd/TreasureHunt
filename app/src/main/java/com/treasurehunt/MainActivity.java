package com.treasurehunt;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.example.ori.treasurehunt.R;
import com.mta.sharedutils.AsyncHandler;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {


    final static String goldTrackTag = "TotalGoldTracker";
    final static String tag = "MainActivity_log";



    final static int LVL_ONE_PRIZE = 100;
    final static int LVL_ONE_RADIOS = 500;

    public final static String GOAL_DISTANCE_IN_M = "distance_to_taget_code";
    public final static String PRIZE_AMOUNT = "prize_amount_code";
    public final static String TOTAL_GOLD_KEY ="total_player_gold_sp_key";
    public final static String FIRST_TIME_PLAYING_KEY ="first_time_Playing_sp_key";
    public final static String VOLUME_KEY="volume_low_high";
    public final static String SOUND_KEY="sound_on_off";
    private TextView goldTextView;
    public static String totalGold;
    public static boolean firstTimePlaying;


    public static MyMusicRunnable musicPlayer;
    public static MySFxRunnable soundEffectsUtil;

    SettingsDialog settingsDialog;
    Dialog aboutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        //Setting sound
        if(musicPlayer == null) {
            musicPlayer = new MyMusicRunnable(this, R.raw.irishmusic);
        }

        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }

        settingsDialog = new SettingsDialog(this);

        //Setting levels textview with prize
        TextView level = (TextView) findViewById(R.id.textView1);
        level.setText(LVL_ONE_PRIZE+" gold");

        //Starting music
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                totalGold = sp.getString(TOTAL_GOLD_KEY,"0");
                //Log.i(goldTrackTag,"Total Gold : " + totalGold);
                goldTextView = (TextView) findViewById(R.id.textView4);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goldTextView.setText(totalGold);
                    }
                });

                firstTimePlaying = sp.getBoolean(FIRST_TIME_PLAYING_KEY, true);

            }
        });

        //Log.i(tag,"Created");

    }

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
    public void onBackPressed() {
        this.finishAffinity();
    }

    public void startLvlOne(View view) {

        soundEffectsUtil.playClickSound();

        //Log.i(tag,"Starting Game");

        Intent intent = new Intent(getBaseContext() , MainGameActivity.class);
        //Extra with the gold Radios and the level prize
        intent.putExtra(GOAL_DISTANCE_IN_M , LVL_ONE_RADIOS);
        intent.putExtra(PRIZE_AMOUNT,Integer.toString(LVL_ONE_PRIZE));
        //Starting game
        startActivity(intent);

    }

    public void startLvlTwo(View view) {

        Toast.makeText(getBaseContext(), "Not available on beta version", Toast.LENGTH_SHORT).show();

    }
    public void startLvlThree(View view) {

        Toast.makeText(getBaseContext(), "Not available on beta version", Toast.LENGTH_SHORT).show();

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
