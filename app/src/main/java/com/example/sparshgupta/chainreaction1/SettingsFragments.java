package com.example.sparshgupta.chainreaction1;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.example.sparshgupta.chainreaction1.GameSettingsActivityMultiplayer.PREFERENCE_FILE_CHAIN_REACTION;

/**
 * Created by sparshgupta on 30/07/17.
 */

public class SettingsFragments extends Fragment {

    CheckBox musicCheckBox, vibrationCheckBox, soundCheckBox;
    boolean isMusicChecked, isVibrationChecked, isSoundChecked;
    public static final String MUSIC_STATE = "musicState";
    public static final String VIBRATION_STATE = "vibrationState";
    public static final String SOUND_STATE = "soundState";
    TextView doneTextView;
    boolean isVibration, isSound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, null, false);
        ImageView imageView = (ImageView)v.findViewById(R.id.backgroundImageView);
        imageView.setImageBitmap(EfficientImages.decodeSampledBitmapFromResource(getActivity().getResources(), R.drawable.settings, 1080, 1920));
//        musicCheckBox = (CheckBox)v.findViewById(R.id.musicCheckBox);
        vibrationCheckBox = (CheckBox)v.findViewById(R.id.vibrationCheckBox);
        soundCheckBox = (CheckBox)v.findViewById(R.id.gameSoundsCheckBox);
        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(PREFERENCE_FILE_CHAIN_REACTION, MODE_PRIVATE);
//        isMusicChecked = sharedPreferences.getBoolean(MUSIC_STATE, true);
//        musicCheckBox.setChecked(isMusicChecked);
        isSoundChecked = sharedPreferences.getBoolean(SOUND_STATE, true);
        soundCheckBox.setChecked(isSoundChecked);
        isVibrationChecked = sharedPreferences.getBoolean(VIBRATION_STATE, true);
        vibrationCheckBox.setChecked(isVibrationChecked);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
//        musicCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    editor.putBoolean(MUSIC_STATE, true);
//                }else{
//                    editor.putBoolean(MUSIC_STATE, false);
//                }
//            }
//        });
        vibrationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean(VIBRATION_STATE, true);
                    editor.commit();

                }else{
                    editor.putBoolean(VIBRATION_STATE, false);
                    editor.commit();

                }
            }
        });
        soundCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean(SOUND_STATE, true);
                    editor.commit();

                }else{
                    editor.putBoolean(SOUND_STATE, false);
                    editor.commit();
                }
            }
        });
        doneTextView = (TextView)v.findViewById(R.id.doneTextView);
        isVibration = sharedPreferences.getBoolean(VIBRATION_STATE, true);
        isSound = sharedPreferences.getBoolean(SOUND_STATE, true);
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.click);
        TextView textView = (TextView) v.findViewById(R.id.howToPlay);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVibration = sharedPreferences.getBoolean(VIBRATION_STATE, true);
                isSound = sharedPreferences.getBoolean(SOUND_STATE, true);
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
                Intent i = new Intent();
                i.setClass(getActivity(), HelpAvtivity.class);
                startActivity(i);
            }
        });
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVibration = sharedPreferences.getBoolean(VIBRATION_STATE, true);
                isSound = sharedPreferences.getBoolean(SOUND_STATE, true);
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
                Intent i = new Intent();
                getActivity().setResult(RESULT_OK, i);
                getActivity().finish();
            }
        });
        return v;
    }
}
