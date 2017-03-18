package com.ori.amd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

/**
 * Created by Oriamd on 3/15/2017.
 */

public class GoogleApiClientHelper implements GoogleApiClient.ConnectionCallbacks
        ,GoogleApiClient.OnConnectionFailedListener{

    public final GoogleApiClient mGoogleApiClient;
    private Activity activity;

    public static int RC_SIGN_IN = 9001;
    public static int  REQUEST_ACHIEVEMENTS = 9002;
    public boolean mResolvingConnectionFailure = false;
    public boolean mAutoStartSignInflow = true;
    public boolean mSignInClicked = false;

    /**
     * @param activity
     * @param connectionFailedListener to implement onConnectionFailed
     * @param connectionCallbacksListener to implement onConnection
     */
    public GoogleApiClientHelper(Activity activity, GoogleApiClient.OnConnectionFailedListener connectionFailedListener,
                                 GoogleApiClient.ConnectionCallbacks connectionCallbacksListener){
        this.activity = activity;

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallbacksListener)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();

    }

    public GoogleApiClientHelper(Activity activity,
                                 GoogleApiClient.ConnectionCallbacks connectionCallbacksListener){
        this.activity = activity;

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallbacksListener)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();

    }

    public GoogleApiClientHelper(Activity activity){
        this.activity = activity;

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();
    }

    /**
     * Connect from google play account
     */
    public void connect(){
        mGoogleApiClient.connect();
    }

    /**
     * Disconnect from google play account
     */
    public void disconnect(){
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(activity, mGoogleApiClient, connectionResult, RC_SIGN_IN, activity.getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(activity,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }


}
