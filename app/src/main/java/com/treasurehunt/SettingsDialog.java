package com.treasurehunt;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.example.ori.treasurehunt.R;
import com.mta.sharedutils.AsyncHandler;

/**
 * Created by Ori on 1/17/2017.
 */


public class SettingsDialog extends Dialog {

    static final String tag = "SettingsDialogTag";
    static final String VOLUME_SETTINGS_KEY = "volume_settings_key";
    static final String CLICK_SOUND_SETTINGS_KEY = "click_sound_settings_key";

    //should sound play when onClick
    public static boolean CLICK_SOUND_ENABLE = true;


    SeekBar seekBar;
    Switch soundSwitch;

    int volume;

    public SettingsDialog(Context context) {
        super(context, R.style.AppTheme_noActionBar_popUp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        soundSwitch = (Switch) findViewById(R.id.switch1);

        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                volume = sp.getInt(VOLUME_SETTINGS_KEY,100);
                seekBar.setProgress(volume);

                //Log.i(tag,"Setting view SharedPreferences Volume is : "+volume);

                CLICK_SOUND_ENABLE = sp.getBoolean(CLICK_SOUND_SETTINGS_KEY,true);
                soundSwitch.setChecked(CLICK_SOUND_ENABLE);

                //Log.i(tag,"Setting view SharedPreferences ClickSound is : "+CLICK_SOUND_ENABLE);

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                MainActivity.musicPlayer.changeVolume(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                //Log.i(tag,"Volume Seekbar Stop at :" + seekBar.getProgress());
                volume = progress;
            }
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CLICK_SOUND_ENABLE = b;
                //Log.i(tag,"ClickSound Switch changed to:" + b);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hide();
    }

    @Override
    public void hide() {
        super.hide();
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sp.edit();
                //Log.i(tag,"Putting SharedPreferences volume to "+volume);
                editor.putInt(VOLUME_SETTINGS_KEY, volume);
                //Log.i(tag,"Putting SharedPreferences clickSound to "+CLICK_SOUND_ENABLE);
                editor.putBoolean(CLICK_SOUND_SETTINGS_KEY, CLICK_SOUND_ENABLE);
                editor.commit();
            }
        });


    }




}
