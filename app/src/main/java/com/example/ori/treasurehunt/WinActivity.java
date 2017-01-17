package com.example.ori.treasurehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mta.sharedutils.AsyncHandler;

public class WinActivity extends AppCompatActivity {

    static String wonGold;
    private static MyMusicRunnable musicPlayer;
    private static MySFxRunnable soundEffectsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Layout Size
        AsyncHandler.removeAllCallbacks();
        setContentView(R.layout.activity_win);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        if(musicPlayer == null) {
            musicPlayer = new MyMusicRunnable(this, R.raw.wincheer);
        }

        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }

        //Setting the gold text
        TextView text = (TextView)findViewById(R.id.settings);
        wonGold = getIntent().getExtras().getString(MainActivity.PRIZE_AMOUNT,"0");
        text.setText(wonGold);
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                int total = Integer.parseInt(sp.getString(MainActivity.TOTAL_GOLD_KEY, "0"))
                            + Integer.parseInt(wonGold);
                Log.i(MainActivity.goldTrackTag,"Total Gold : " + total);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(MainActivity.TOTAL_GOLD_KEY, Integer.toString(total));
                editor.commit();
            }
        });
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void playAgain(View view) {
        if(MainActivity.CLICK_SOUND_ENABLE){
            soundEffectsUtil.play(R.raw.detectoron);
        }
        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);

    }

}
