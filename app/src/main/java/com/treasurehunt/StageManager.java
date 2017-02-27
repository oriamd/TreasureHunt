package com.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ori.treasurehunt.R;
import com.mta.sharedutils.AsyncHandler;

/**
 * Created by Ori on 2/26/2017.
 */

public class StageManager {

    public final static int LVL_ONE_PRIZE = 100;
    public final static int LVL_ONE_RADIOS = 500;
    public final static int LVL_TWO_UNLOCK_PRICE = 600;
    public final static int LVL_TWO_PRIZE = 300;
    public final static int LVL_TWO_RADIOS = 1000;
    public final static int LVL_THREE_UNLOCK_PRICE = 1500;
    public final static int LVL_THREE_PRIZE = 500;
    public final static int LVL_THREE_RADIOS = 1500;

    static boolean LVL_TWO_UNLOCKED;
    static boolean LVL_THREE_UNLOCKED;

    public final static String LVL_TWO_UNLOCK_KEY="lvl_two_unlock_key";
    public final static String LVL_THREE_UNLOCK_KEY="lvl_three_unlock_key";

    ImageView lvlTwoCover;
    ImageView lvlThreeCover;

    Activity activity;

    public StageManager(final Activity activity){
        this.activity = activity;

        lvlTwoCover = (ImageView) activity.findViewById(R.id.imageView7);
        lvlThreeCover = (ImageView) activity.findViewById(R.id.imageView8);

        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                LVL_TWO_UNLOCKED = sp.getBoolean(LVL_TWO_UNLOCK_KEY,false);
                LVL_THREE_UNLOCKED = sp.getBoolean(LVL_THREE_UNLOCK_KEY,false);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(LVL_TWO_UNLOCKED)
                            lvlTwoCover.setVisibility(View.INVISIBLE);
                        else
                            lvlTwoCover.setVisibility(View.VISIBLE);
                        if(LVL_THREE_UNLOCKED)
                            lvlThreeCover.setVisibility(View.INVISIBLE);
                        else
                            lvlThreeCover.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

    }

    public void unlockStageOne(){
        //Showing a dialog to ask if the user is sure
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        return;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want unlock the  ? \n you will loss this progress").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
