package com.ori.amd.treasurehunt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mta.sharedutils.AsyncHandler;

/**
 * Created by Ori on 2/26/2017.
 */

public class StageManager {

    Level lvls[];

    ImageView lvlTwoCover;
    ImageView lvlThreeCover;

    Activity activity;

    public StageManager(final Activity activity){

        this.activity = activity;

        lvls = new Level[]{
            new Level(1, 100, 500, 0),
            new Level(2, 300, 1000, 600),
            new Level(3, 600, 1500, 1000)
        };

        //Getting the button Cover view
        lvlTwoCover = (ImageView) activity.findViewById(R.id.imageView7);
        lvlThreeCover = (ImageView) activity.findViewById(R.id.imageView8);

        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                //Need to init the Levels unlock status
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                for(int i=0 ; i < lvls.length ; i++){
                    if(i==0){
                        lvls[i].setIsUnlocked( sp.getBoolean(lvls[i].LVL_UNLOCK_KEY,true) );
                    }else {
                        lvls[i].setIsUnlocked(sp.getBoolean(lvls[i].LVL_UNLOCK_KEY,false));
                    }
                }
                //Now need to set Stage Buttons cover
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(lvls[1].isUnlocked())
                            lvlTwoCover.setVisibility(View.INVISIBLE);
                        else
                            lvlTwoCover.setVisibility(View.VISIBLE);
                        if(lvls[2].isUnlocked())
                            lvlThreeCover.setVisibility(View.INVISIBLE);
                        else
                            lvlThreeCover.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

    }

    /**
     * will return Level object by the name of the level, return null if no such
     * @param numOfLevel
     * @return
     */
    public Level getLevel(int numOfLevel){
        for(Level lvl : lvls){
            if(lvl.numOfLevel == numOfLevel){
                return lvl;
            }
        }
        return null;
    }

    class Level{
        public  int numOfLevel;
        //Amount of gold prize
        public int prize;
        //radios from player's to place target
        public int radios;
        //How much cost to unlock level
        public int goldToUnlock;
        private boolean isUnlock;
        //SP key
        private String LVL_UNLOCK_KEY;
        /**
         * @param numOfLevel The number who represent the Level
         * @param prize Amount of gold prize
         * @param radios radios from player's to place target
         * @param goldToUnlock How much cost to unlock level
         * isUnlock means if it's locked
         */
        public Level(int numOfLevel, int prize, int radios, int goldToUnlock) {
            this.numOfLevel = numOfLevel;
            this.prize = prize;
            this.radios = radios;
            this.goldToUnlock = goldToUnlock;
            this.isUnlock = true;
            LVL_UNLOCK_KEY = "lvl_" + numOfLevel + "_unlock_key";
        }

        /**
         * Will unlock stage , subtracting from player's total gold
         */
        public void unlockStage(){
            AsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(LVL_UNLOCK_KEY, true);
                    final int totalGold = Integer.parseInt(MainActivity.totalGold);
                    editor.putString(MainActivity.TOTAL_GOLD_KEY,String.valueOf( totalGold - goldToUnlock));
                    MainActivity.totalGold = String.valueOf(totalGold);
                    editor.commit();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)activity.findViewById(R.id.textView4)).setText(String.valueOf(totalGold));
                        }
                    });
                }
            });
            switch (numOfLevel){
                case 2:
                    ((ImageView)activity.findViewById(R.id.imageView7)).setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    ((ImageView)activity.findViewById(R.id.imageView8)).setVisibility(View.INVISIBLE);
                    break;
            }

            isUnlock = true;
        }

        /**
         * Will set the object member, does not influence SP or others.
         * Should only used when init the obj
         * @param is
         */
        public void setIsUnlocked(boolean is){
            isUnlock = is;
        }

        /**
         * Return id the level is unlocked or not, based of the object not SP
         * @return
         */
        public boolean isUnlocked(){
            return isUnlock;
        }


    }

}
