package com.example.ori.treasurehunt;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.mta.sharedutils.AsyncHandler;

/**
 * Created by Ori on 1/17/2017.
 */


public class SettingsDialog extends Dialog {

    static final String tag = "SettingsDialogTag";
    static final String VOLUME_SETTINGS_KEY = "volume_settings_key";
    static final String CLICK_SOUND_SETTINGS_KEY = "click_sound_settings_key";

    SeekBar seekBar;
    Switch soundSwitch;

    int volume;
    boolean clickSound;

    public SettingsDialog(Context context) {
        super(context, R.style.AppTheme_noActionBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.i(tag,"Settings Dialog OnCreat Called");

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        soundSwitch = (Switch) findViewById(R.id.switch1);

        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                volume = sp.getInt(VOLUME_SETTINGS_KEY,100);
                seekBar.setProgress(volume);

                clickSound = sp.getBoolean(CLICK_SOUND_SETTINGS_KEY,true);
                soundSwitch.setChecked(clickSound);

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

            }
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MainActivity.CLICK_SOUND_ENABLE = b;
                clickSound = b;
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
        Log.i(tag,"hide()");
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(VOLUME_SETTINGS_KEY, volume);
                editor.putBoolean(CLICK_SOUND_SETTINGS_KEY, clickSound);
                editor.commit();
            }
        });


    }
}
