package com.example.sparshgupta.chainreaction1;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.example.sparshgupta.chainreaction1.GameSettingsActivityMultiplayer.PREFERENCE_FILE_CHAIN_REACTION;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.SOUND_STATE;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.VIBRATION_STATE;

/**
 * Created by sparshgupta on 30/07/17.
 */

public class HelpFragment extends Fragment {
    boolean isVibration, isSound;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.help_fragment, null, false);
        TextView textView = (TextView) v.findViewById(R.id.okTextView);
        SharedPreferences sp = getActivity().getSharedPreferences(PREFERENCE_FILE_CHAIN_REACTION, MODE_PRIVATE);
        isVibration = sp.getBoolean(VIBRATION_STATE, true);
        isSound = sp.getBoolean(SOUND_STATE, true);
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.click);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
                getActivity().finish();
            }
        });
        return v;
    }
}
