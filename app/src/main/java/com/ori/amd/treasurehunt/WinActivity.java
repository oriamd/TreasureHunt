package com.ori.amd.treasurehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.mta.sharedutils.AsyncHandler;

/***
 * Win activity stats when the player reaches the target and will gold.
 * Will update Shared preferences , unlock google achievements and update leaderboard
 */
public class WinActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{

    static final String tag = "Win_Activity_Log";

    static String wonGold;
    private static MyMusicRunnable musicPlayer;
    private static MySFxRunnable soundEffectsUtil;

    GoogleApiClientHelper googleApiClientHelper;

    TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Layout Size
        AsyncHandler.removeAllCallbacks();
        setContentView(com.ori.amd.treasurehunt.R.layout.activity_win);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        if(musicPlayer == null) {
            musicPlayer = new MyMusicRunnable(this, com.ori.amd.treasurehunt.R.raw.wincheer);
        }

        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }

        googleApiClientHelper = new GoogleApiClientHelper(this,this);

        //Setting the gold text
        Intent intent = getIntent();
        TextView goldText = (TextView)findViewById(com.ori.amd.treasurehunt.R.id.settings);

        //We need to add the gold he won to SharedPreferences total gold
        wonGold = intent.getExtras().getString(MainActivity.PRIZE_AMOUNT,"0");
        goldText.setText(wonGold);
        timeText = (TextView) findViewById(com.ori.amd.treasurehunt.R.id.textView16);
        timeText.setText(intent.getExtras().getString(MainGameActivity.TIME_EXTRA,"00"));
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                int total = Integer.parseInt(sp.getString(MainActivity.TOTAL_GOLD_KEY, "0"))
                            + Integer.parseInt(wonGold);
                MainActivity.totalGold = Integer.toString(total);
                //Log.i(MainActivity.goldTrackTag,"Total Gold : " + total);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(MainActivity.TOTAL_GOLD_KEY, Integer.toString(total));
                editor.commit();
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClientHelper.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClientHelper.connect();

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
        soundEffectsUtil.playClickSound();
        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);

    }

    /**
     * when connected to google submit score to leaderboard and unlock achievements
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiClientHelper.onConnected(bundle);

        Games.Leaderboards.submitScore(googleApiClientHelper.mGoogleApiClient, getString(R.string.gold_leadeboard),
                Integer.parseInt(MainActivity.totalGold));

        if (Integer.parseInt(MainActivity.totalGold) >= 5000){
            Games.Achievements.unlock(googleApiClientHelper.mGoogleApiClient,getString(R.string.my_full_pockets_id));
        }
        if (Integer.parseInt(MainActivity.totalGold) >= 10000){
            if(googleApiClientHelper.mGoogleApiClient.isConnected())
                Games.Achievements.unlock(googleApiClientHelper.mGoogleApiClient,getString(R.string.my_bank_account_id));
        }
        try {
            String[] strArr = timeText.getText().toString().split(":");
            int time = Integer.parseInt(strArr[0])*100 + Integer.parseInt(strArr[1]);
            if (time <= 700) {
                if(googleApiClientHelper.mGoogleApiClient.isConnected()) {
                    Games.Achievements.unlock(googleApiClientHelper.mGoogleApiClient, getString(R.string.my_fast_as_lightning_id));
                }
            }
        }
        catch (NumberFormatException e){
            Log.i(tag, e.getMessage());
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClientHelper.onConnectionSuspended(i);
    }
}
