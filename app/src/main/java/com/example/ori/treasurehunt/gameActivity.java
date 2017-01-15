package com.example.ori.treasurehunt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    public static final String tag = "GAME_ACTIVITY_LOG";

    public static final float WIN_RADIOS = 10;

    private LocationManager manager;
    private LocationListener listener;
    private Location randLocation = null;
    private double distanceToTarget;
    private double lastDistanceToTarget;
    private int soundInterval;


    //Debug parameters for emulation the user progress
    private double DEBUGLATITUDE = 32.165716;
    private double DEBUGLONGITUDE = 34.799725;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //If its the first time we are getting the location we are going to set a random location
                if( randLocation == null){
                    int radios = getIntent().getIntExtra(MainActivity.GOAL_DISTANCE_IN_M,100);
                    setRandLocation(location , radios );
                    //distance from start
                    distanceToTarget = location.distanceTo(randLocation);
                    soundInterval = (int)distanceToTarget/10;
                    return;
                }

                float distanceToRandLocation = location.distanceTo(randLocation);
                // If not we are going to determine if the user getting close or far away from the target
                if(lastDistanceToTarget < distanceToRandLocation){//Getting far
                    Toast.makeText(getBaseContext(),"Getting Far", Toast.LENGTH_SHORT).show();

                }else if(lastDistanceToTarget > distanceToRandLocation){//Getting close
                    Toast.makeText(getBaseContext(),"Getting Close", Toast.LENGTH_SHORT).show();

                }else if(distanceToRandLocation <= WIN_RADIOS){//The player reached the target
                    Intent intent = new Intent(getBaseContext(), WinActivity.class);
                    startActivity(intent);
                }

                //Updating last DistanceToTarget
                lastDistanceToTarget = location.distanceTo(randLocation);

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
