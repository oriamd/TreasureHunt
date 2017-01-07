package com.example.ori.treasurehunt;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    final static int LVL_ONE_PRIZE = 100;
    final static int LVL_ONE_RADIOS = 500;
    public final static String GOAL_DISTANCE_IN_M = "distance_to_taget_code";
    public final static String PRIZE_AMOUNT = "prize_amount_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void startGame(View view) {

        Intent intent = new Intent(getBaseContext() , GameActivity.class);
        intent.putExtra(GOAL_DISTANCE_IN_M , 100);
        intent.putExtra(PRIZE_AMOUNT , 500);
        startActivity(intent);


    }
}
