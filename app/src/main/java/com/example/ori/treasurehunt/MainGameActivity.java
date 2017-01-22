package com.example.ori.treasurehunt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import com.mta.sharedutils.AsyncHandler;

public class MainGameActivity extends AppCompatActivity {

    public static final String tag = "GAME_ACTIVITY_LOG";
    public static final String locationTag = "GAME__Location_LOG";

    public static final String TIME_EXTRA = "time_extra";

    private String prizeAmount;

    private static MyMusicRunnable mediaPlayer;
    private static MySFxRunnable soundEffectsUtil;
    private static IntervalMusicRunnable intervalSound;


    private LocationManager manager;
    private LocationListener listener;
    private Location randLocation = null;   //The "Treasure" location
    private double offsetDistanceToTarget;        //distance from first taken location to randLocation
    private double lastDistanceToTarget;    //the last Distance taken to the randLocation
    private int frequency;
    public static final int TARGET_OFFSET_METER = 20;                   //The radios to the target
    public static final int DISTANCE_TO_START_INTERVAL_METER = 100;    //When in X distance will start changing the interval
    public static final int SOUND_FREQUENCY_INITIAL_MS = 2000;       //The frequency which the sound start with
    public static final int INTERVAL_METER = 10;                    // The frequency  will change every  INTERVAL_METER's
    public static final int INTERVAL_MS =  SOUND_FREQUENCY_INITIAL_MS /INTERVAL_METER; //The frequency  will change by INTERVAL_MS


    //Debug parameters for emulation the user progress
    /*
    private double DEBUG_LATITUDETE_TARGET = 32.165391;
    private double DEBUG_LONGITUDE_TARGET = 34.834500;
    private double DEBUG_LATITUDETE_START = 32.164798;
    private double DEBUG_LONGITUDE_START = 34.835519;
    */


    Chronometer chromoneter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Log.i(tag,"Create()");
        //storing extras
        prizeAmount = getIntent().getExtras().getString(MainActivity.PRIZE_AMOUNT,"0");

        //storing the chronometer
        chromoneter = (Chronometer)findViewById(R.id.chronometer2);


        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Sounds
        if(mediaPlayer == null){
            mediaPlayer = new MyMusicRunnable(this,R.raw.detectorbackground);
        }
        if (soundEffectsUtil == null) {
            soundEffectsUtil = new MySFxRunnable(this);
        }
        if (intervalSound == null){
            intervalSound = new IntervalMusicRunnable(this,R.raw.detectbeep,soundEffectsUtil);
        }else {
            intervalSound.setFrequency(SOUND_FREQUENCY_INITIAL_MS);
            intervalSound.changeRes(R.raw.detectbeep);
        }

        //Location
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {



                //If its the first time we are getting the location we are going to set a random location
                if( randLocation == null){
                    //Getting the radios for setting the target location in range
                    int radios = getIntent().getIntExtra(MainActivity.GOAL_DISTANCE_IN_M,100);

                    //For debug
                    /*
                    location.setLatitude(DEBUG_LATITUDETE_START);
                    location.setLongitude(DEBUG_LONGITUDE_START);
                    */

                    //Now placing the location
                    setRandLocation(location , radios );

                    //distance from start
                    offsetDistanceToTarget = location.distanceTo(randLocation)-TARGET_OFFSET_METER;  //The real distance is les because we have radios
                    //Log.i(locationTag," first location offsetDistanceToTarget :" + offsetDistanceToTarget);

                    //Now that target hs been set we can start the beep sound and start the stopwatch
                    intervalSound.setPlay();
                    changeInterval(offsetDistanceToTarget);
                    AsyncHandler.post(intervalSound);
                    chromoneter.setBase(SystemClock.elapsedRealtime());
                    chromoneter.start();

                    //Saving last Distance
                    lastDistanceToTarget = offsetDistanceToTarget;
                    return;
                }


                offsetDistanceToTarget = location.distanceTo(randLocation)-TARGET_OFFSET_METER;
                //Log.i(locationTag,  "offsetDistanceToTarget: " + offsetDistanceToTarget );


                // We are going to determine if the user getting close or far away from the target and change the sound by it
                if(lastDistanceToTarget < offsetDistanceToTarget){//Getting far

                    Toast.makeText(getBaseContext(),"Getting Far", Toast.LENGTH_SHORT).show();
                    //Log.i(locationTag,"Getting Far");

                    intervalSound.changeRes(R.raw.errorbuzz);
                    intervalSound.setPlay();
                    changeInterval(SOUND_FREQUENCY_INITIAL_MS);

                }else if(lastDistanceToTarget > offsetDistanceToTarget) {//Getting close

                    Toast.makeText(getBaseContext(), "Getting Close", Toast.LENGTH_SHORT).show();
                    //Log.i(locationTag,"Getting Close");
                    intervalSound.changeRes(R.raw.detectbeep);
                    intervalSound.setPlay();
                    changeInterval(offsetDistanceToTarget);
                }
                if(offsetDistanceToTarget <= 0 ){//The player reached the target
                    win(null);
                }

                //Updating last DistanceToTarget
                lastDistanceToTarget = offsetDistanceToTarget;

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        startLocation();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chromoneter.stop();
        intervalSound.setPause();
        manager.removeUpdates(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundEffectsUtil.playClickSound();
        //Log.i(tag,"pause()");
        AsyncHandler.post(mediaPlayer);


    }

    @Override
    protected void onResume() {
        super.onResume();
        soundEffectsUtil.playClickSound();
        //Log.i(tag,"resume()");
        AsyncHandler.post(mediaPlayer);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 10:
                startLocation();
                break;
            default://In case the user have't gave permission he can't play
                Toast.makeText(getApplicationContext(),"Please Allow Gps Permission",Toast.LENGTH_LONG).show();
                break;
        }
    }


    void startLocation(){

        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
        }

        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, listener);
        }else {//User need to turn on GPS provider
            Toast.makeText(getApplicationContext(),"Please turn ON GPS",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    /*
        This will set the target in a rand location inside the radios
     */
    private void setRandLocation(Location location , double radios){

        randLocation = new Location("");

        double x0 = location.getLatitude();
        double y0 = location.getLongitude();

        // Convert radius from meters to degrees
        double radiusInDegrees = radios / 111000f;

        double u = Math.random();
        double v = Math.random();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v ;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;

        randLocation.setLatitude(foundLongitude);
        randLocation.setLongitude(foundLatitude);

        /* //DEBUG
        randLocation.setLatitude(DEBUG_LATITUDETE_TARGET);
        andLocation.setLongitude(DEBUG_LONGITUDE_TARGET);

        TextView view = (TextView) findViewById(R.id.randLocation);
        view.setText("RandLocation : " + randLocation.getLatitude() +", "+randLocation.getLongitude());
        Log.i(locationTag,"RandLocation Set on :" + randLocation.getLatitude() +", "+randLocation.getLongitude() + "real Diatance from player :" + randLocation.distanceTo(location));
        */
    }


    /*
        Player won/reached the target
     */
    public void win(View view) {
        //Log.i(locationTag,"Player Won gold :"+ prizeAmount.toString());
        chromoneter.stop();
        intervalSound.setPause();
        manager.removeUpdates(listener);

        Intent intent = new Intent(getBaseContext(), WinActivity.class);
        intent.putExtra(MainActivity.PRIZE_AMOUNT,prizeAmount.toString());
        String timeStr = chromoneter.getText().toString();
        intent.putExtra(TIME_EXTRA,timeStr);
        startActivity(intent);
    }

    /*
        Changing the frequently which the been sound is being played
     */
    private void changeInterval(double distance ){
        frequency = (((int) (distance / INTERVAL_METER)) + 1) * INTERVAL_MS;
        if(frequency > SOUND_FREQUENCY_INITIAL_MS ) {
            intervalSound.setFrequency(SOUND_FREQUENCY_INITIAL_MS);
        }else {
            intervalSound.setFrequency(frequency);
        }

    }

    @Override
    public void onBackPressed() {
     quitGame(null);
    }

    /*
        Quiting the game.
     */
    public void quitGame(View view) {
        soundEffectsUtil.playClickSound();
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
        builder.setMessage("Are you sure you want to Quit? \n you will loss this progress").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
