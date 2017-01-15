package com.example.ori.treasurehunt;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.mta.sharedutils.AsyncHandler;

public class GameActivity extends AppCompatActivity {

    public static final String tag = "GAME_ACTIVITY_LOG";




    private static MyMusicRunnable mediaPlayer;
    private static MySFxRunnable soundEffectsUtil;
    private static IntervalMusicRunnable intervalSound;



    private LocationManager manager;
    private LocationListener listener;
    private Location randLocation = null;   //The "Treasure" location
    private double distanceToTarget;        //distance from first taken location to randLocation
    private double lastDistanceToTarget;    //the last Distance taken to the randLocation
    private int frequency;
    public static final int TARGET_OFFSET_METER = 10;                   //The radios to the target
    public static final int DISTANCE_TO_START_INTERVAL_METER = 100;    //When in X distance will start changing the interval
    public static final int SOUND_FREQUENCY_INITIAL_MS = 2000;       //The frequency which the sound start with
    public static final int INTERVAL_METER = 10;                    // The frequency  will change every  INTERVAL_METER's
    public static final int INTERVAL_MS =  SOUND_FREQUENCY_INITIAL_MS /INTERVAL_METER; //The frequency  will change by INTERVAL_MS


    //Debug parameters for emulation the user progress
    private double DEBUGLATITUDE = 32.165391;
    private double DEBUGLONGITUDE = 34.834500;


    TextView speedTextView;
    Chronometer chromoneter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
        }

        //Location
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //If its the first time we are getting the location we are going to set a random location
                if( randLocation == null){
                    int radios = getIntent().getIntExtra(MainActivity.GOAL_DISTANCE_IN_M,100);
                    //For debug
                    location.setLatitude(32.164798);
                    location.setLongitude(34.835519);
                    setRandLocation(location , radios );
                    //distance from start
                    distanceToTarget = location.distanceTo(randLocation)-TARGET_OFFSET_METER;  //The real distance is les because we have radios
                    intervalSound.setPlay();
                    intervalSound.setFrequency(SOUND_FREQUENCY_INITIAL_MS);
                    AsyncHandler.post(intervalSound);
                    chromoneter.setBase(SystemClock.elapsedRealtime());
                    chromoneter.start();
                    return;
                }

                float distanceToRandLocation = location.distanceTo(randLocation)-TARGET_OFFSET_METER;
                // If not we are going to determine if the user getting close or far away from the target
                if(lastDistanceToTarget < distanceToRandLocation){//Getting far

                    Toast.makeText(getBaseContext(),"Getting Far", Toast.LENGTH_SHORT).show();

                }else if(lastDistanceToTarget > distanceToRandLocation) {//Getting close
                    Toast.makeText(getBaseContext(), "Getting Close", Toast.LENGTH_SHORT).show();
                    //will change the interval only in DISTANCE_TO_START_INTERVAL_METER radios
                    if (lastDistanceToTarget <= DISTANCE_TO_START_INTERVAL_METER) {
                        //first will start sound
                        intervalSound.setPlay();
                        frequency = (((int) (distanceToRandLocation / INTERVAL_METER)) + 1) * INTERVAL_MS;
                        intervalSound.setFrequency(frequency);
                    }
                }
                if(distanceToRandLocation <= 0 ){//The player reached the target
                    Intent intent = new Intent(getBaseContext(), WinActivity.class);
                    startActivity(intent);
                }

                //Updating last DistanceToTarget
                lastDistanceToTarget = distanceToRandLocation;

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
    protected void onPause() {
        super.onPause();
        soundEffectsUtil.play(R.raw.detectoron);
        AsyncHandler.post(mediaPlayer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundEffectsUtil.play(R.raw.detectoron);
        AsyncHandler.post(mediaPlayer);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 10:
                startLocation();
                break;
            default:
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
            return;
        }

        manager.requestLocationUpdates("gps", 5000, 0, listener);

    }

    private void setRandLocation(Location location , double radios){

        randLocation = new Location("");

        double x0 = location.getLatitude();
        double y0 = location.getLongitude();

        // Convert radius from meters to degrees
        double radiusInDegrees = 500 / 111000f;

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

        //randLocation.setLatitude(foundLongitude);
        //randLocation.setLongitude(foundLatitude);
        randLocation.setLatitude(DEBUGLATITUDE);
        randLocation.setLongitude(DEBUGLONGITUDE);

        TextView view = (TextView) findViewById(R.id.randLocation);
        view.setText("RandLocation : " + randLocation.getLatitude() +", "+randLocation.getLongitude());


        lastDistanceToTarget = location.distanceTo(randLocation);

        Log.d(tag,  "Location : " +location.getLatitude()+","+location.getLongitude() );
        Toast.makeText(getBaseContext()," distanceto : " + location.distanceTo(randLocation) , Toast.LENGTH_LONG).show();


    }


    public void win(View view) {
        Intent intent = new Intent(this, WinActivity.class);
        startActivity(intent);
    }

}
