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

    private static MyMusicRunnable musicPlayer;

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
    }


    public void startGame(View view) {

        Intent intent = new Intent(getBaseContext() , GameActivity.class);
        intent.putExtra(GOAL_DISTANCE_IN_M , 100);
        intent.putExtra(PRIZE_AMOUNT , 500);
        startActivity(intent);


    }




}
