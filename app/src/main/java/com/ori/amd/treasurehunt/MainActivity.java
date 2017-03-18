package com.ori.amd.treasurehunt;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.mta.sharedutils.AsyncHandler;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,View.OnClickListener
        ,GoogleApiClient.OnConnectionFailedListener{

    final static String tag = "MainActivity_log";


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
    StageManager stageManager;
    Dialog aboutDialog;
    YesNoDialog yesNoDialog;
    private Tracker mTracker;

    //protected GoogleApiClient mGoogleApiClient;
    protected  GoogleApiClientHelper googleApiClientHelper;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        settingsDialog = new SettingsDialog(this);
        settingsDialog.setContentView(R.layout.activity_settings);
        yesNoDialog = new YesNoDialog(this);

        googleApiClientHelper = new GoogleApiClientHelper(this,this,this);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.achievements).setOnClickListener(this);
        settingsDialog.findViewById(R.id.sign_out_button).setOnClickListener(this);
        stageManager = new StageManager(this);

        //Setting sound
        if(musicPlayer == null) {
            musicPlayer = new MyMusicRunnable(this, R.raw.irishmusic);
        }

        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }



        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        //Setting levels textview with prize
        TextView level = (TextView) findViewById(R.id.textView1);
        level.setText(stageManager.getLevel(1).prize+" gold");
        //Setting levels textview with prize
        level = (TextView) findViewById(R.id.textView2);
        level.setText(stageManager.getLevel(2).prize+" gold");
        level = (TextView) findViewById(R.id.textView3);
        level.setText(stageManager.getLevel(3).prize+" gold");


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
    protected void onStop() {
        super.onStop();
        if(googleApiClientHelper.mAutoStartSignInflow) {
            googleApiClientHelper.disconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClientHelper.mAutoStartSignInflow) {
            googleApiClientHelper.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncHandler.post(musicPlayer);
        String name = "MainActivity";
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

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

    /**
     * Level Button Event trigger this
     * @param view
     */
    public void startLvl(View view) {
        soundEffectsUtil.playClickSound();
        switch (view.getId()) {
            case R.id.imageButton: {
                startLvl(stageManager.getLevel(1));
                break;
            }
            case R.id.imageButton2: {
                startLvl(stageManager.getLevel(2));
                break;
            }
            case R.id.imageButton3: {
                startLvl(stageManager.getLevel(3));
                break;
            }
        }
    }

    /**
     * Starting the game with , parameters taken from StageManager.lvl
     * @param lvl obj who wish to start
     */
    public void startLvl(final StageManager.Level lvl) {
        if(!lvl.isUnlocked()){
            if(Integer.parseInt(totalGold) >= lvl.goldToUnlock){

                yesNoDialog.setContentText("Do you want to Unlock this Level? \n" + lvl.goldToUnlock+ " Gold to unlock ");
                yesNoDialog.setYesListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lvl.unlockStage();
                        yesNoDialog.hide();
                    }
                });
                yesNoDialog.show();
                return;
            }else{
                Toast.makeText(this, "Level Locked. Unlock with "+lvl.goldToUnlock+" GOLD",Toast.LENGTH_LONG).show();
                return;
            }
        }
        Intent intent = new Intent(getBaseContext() , MainGameActivity.class);
        //Extra with the gold Radios and the level prize
        intent.putExtra(GOAL_DISTANCE_IN_M , lvl.radios);
        intent.putExtra(PRIZE_AMOUNT,Integer.toString(lvl.prize));
        //Starting game
        startActivity(intent);

    }


    /**
     * Starting settings dialog
     * @param view
     */
    public void startSettings(View view) {
        soundEffectsUtil.playClickSound();
        settingsDialog.show();
    }

    /**
     * Starting Tutorial activity
     * @param view
     */
    public void startTutorial(View view) {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    /**
     * Starting About dialog
     * @param view
     */
    public void startAbout(View view){
        if(aboutDialog == null){
            aboutDialog = new Dialog(this,R.style.AppTheme_noActionBar);
            aboutDialog.setContentView(R.layout.about);
        }

        aboutDialog.show();
    }

    /**
     * Showing Leaderboard
     * @param view
     */
    public void startLeaderboard(View view){
        if(googleApiClientHelper.mGoogleApiClient.isConnected()){
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClientHelper.mGoogleApiClient,
                    getString(R.string.gold_leadeboard)), 603);

        }
    }

    /**
     *  When connecting to google play
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.achievements).setVisibility(View.VISIBLE);
        settingsDialog.findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        settingsDialog.findViewById(R.id.leaderboards).setVisibility(View.VISIBLE);

    }

    /**
     * Failed to connect to google sign in
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleApiClientHelper.onConnectionFailed(connectionResult);
        Log.i(tag, "ConnectctionResukt.ErrorCode : " + connectionResult.getErrorCode());
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        googleApiClientHelper.connect();
    }

    // Call when the sign-in button is clicked
    protected void signInClicked() {
        googleApiClientHelper.mSignInClicked = true;
        googleApiClientHelper.mGoogleApiClient.connect();
    }

    // Call when the sign-out button is clicked
    protected void signOutClicked() {
        googleApiClientHelper.mSignInClicked = false;
        googleApiClientHelper.mAutoStartSignInflow = false;
        googleApiClientHelper.disconnect();
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.achievements).setVisibility(View.GONE);
        settingsDialog.findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        settingsDialog.findViewById(R.id.leaderboards).setVisibility(View.GONE);

    }


    /**
     * Google Sign in onClick
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            signInClicked();
        }
        else if (view.getId() == R.id.sign_out_button) {
            signOutClicked();
        }else if(view.getId() == R.id.achievements){
            startActivityForResult(Games.Achievements.getAchievementsIntent(googleApiClientHelper.mGoogleApiClient),
                    googleApiClientHelper.REQUEST_ACHIEVEMENTS);
        }
    }


}
