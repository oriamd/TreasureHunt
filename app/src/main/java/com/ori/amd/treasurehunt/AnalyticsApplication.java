package com.ori.amd.treasurehunt;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Oriamd on 3/11/2017.
 */

public class AnalyticsApplication extends Application {

    private Tracker mTracker;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }





    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(com.ori.amd.treasurehunt.R.xml.global_tracker);
        }
        return mTracker;
    }

}
