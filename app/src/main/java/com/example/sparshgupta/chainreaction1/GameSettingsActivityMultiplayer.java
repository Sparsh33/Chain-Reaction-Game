package com.example.sparshgupta.chainreaction1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import static com.example.sparshgupta.chainreaction1.GameplayActivity.GRID_SIZE;
import static com.example.sparshgupta.chainreaction1.GameplayActivity.NO_PLAYERS;
import static com.example.sparshgupta.chainreaction1.PlayerSettingsActivity.FIRST_LOGIN;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.SOUND_STATE;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.VIBRATION_STATE;

public class GameSettingsActivityMultiplayer extends AppCompatActivity {

    CheckBox checkBox;
    int noofPlayers;
    String gridSize;
    int time;
    SharedPreferences sharedPreferences;
    public static final String PREFERENCE_FILE_CHAIN_REACTION = "com.example.chainReaction.preferencefile";
    public static final String NO_PLAYERS_S = "noofplayersSharedPref";
    public static final String GRID_SIZE_S = "gridSizeSharedPref";
    public static final String COLOR_CODES_S = "colorCodesSharedPref";
    public static final String CHECKBOX_CHECKED = "checkboxchecked";
    public static final String TIMER_SELECTION = "timerSelection";
    public static final String TIME = "time";
    TextView warningTextView;
    Spinner spinner;
    Spinner gridSpinner, timerSpinner;
    int currentApiVersion;
    boolean isVibration, isSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings_multiplayer);
        ImageView imageView = (ImageView)findViewById(R.id.settingsImageView);
        imageView.setImageBitmap(EfficientImages.decodeSampledBitmapFromResource(this.getResources(), R.drawable.settings4multi, 720, 1280));
        TextView textView = (TextView)findViewById(R.id.playerSettings);
        warningTextView = (TextView)findViewById(R.id.warningTextView);
        sharedPreferences = getSharedPreferences(PREFERENCE_FILE_CHAIN_REACTION, MODE_PRIVATE);
        spinner = (Spinner)findViewById(R.id.noOfPlayerSpinner);
        gridSpinner = (Spinner)findViewById(R.id.gridSpinner);
        timerSpinner = (Spinner)findViewById(R.id.timerSpinner);
        isVibration = sharedPreferences.getBoolean(VIBRATION_STATE, true);
        isSound = sharedPreferences.getBoolean(SOUND_STATE, true);
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
                Intent i = new Intent();
                i.putExtra(NO_PLAYERS, noofPlayers);
                i.setClass(GameSettingsActivityMultiplayer.this, PlayerSettingsActivity.class);
                GameSettingsActivityMultiplayer.this.finish();
                startActivityForResult(i, 1);
            }
        });
        checkBox = (CheckBox)findViewById(R.id.timerCheckBox);
        final TextView timer = (TextView)findViewById(R.id.timer);
        boolean flag = sharedPreferences.getBoolean(CHECKBOX_CHECKED, true);
        checkBox.setChecked(flag);
        if(checkBox.isChecked()){
            timer.setTextColor(getResources().getColor(R.color.colorWhite));
            setupView();
        }else{
            warningTextView.setVisibility(View.GONE);
            timerSpinner.setClickable(false);
            timerSpinner.setVisibility(View.GONE);
            timer.setText("TIMER");
            timer.setTextColor(getResources().getColor(R.color.colorGrey));
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBox.isChecked()){
                    timer.setTextColor(getResources().getColor(R.color.colorWhite));
                    setupView();
                }else{
                    warningTextView.setVisibility(View.GONE);
                    timerSpinner.setClickable(false);
                    timerSpinner.setVisibility(View.GONE);
                    TextView timer = (TextView)findViewById(R.id.timer);
                    timer.setText("TIMER");
                    timer.setTextColor(getResources().getColor(R.color.colorGrey));
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(CHECKBOX_CHECKED, isChecked);
                editor.apply();
            }
        });
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.noOfPlayers, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(sharedPreferences.getInt(NO_PLAYERS_S, 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String temp = parent.getItemAtPosition(position).toString();
                noofPlayers = Integer.valueOf(temp);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putInt(NO_PLAYERS_S, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                noofPlayers = 2;
            }
        });

        ArrayAdapter<CharSequence> gridAdapter = ArrayAdapter.createFromResource(this, R.array.gridSize, android.R.layout.simple_spinner_item);
        gridAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gridSpinner.setAdapter(gridAdapter);
        gridSpinner.setSelection(sharedPreferences.getInt(GRID_SIZE_S, 1));
        gridSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gridSize = parent.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putInt(GRID_SIZE_S, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gridSize = "Medium";
            }
        });

        TextView startTextView = (TextView)findViewById(R.id.startTextView);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
                Intent i = new Intent();
                i.setClass(GameSettingsActivityMultiplayer.this, GameplayActivity.class);
                i.putExtra(NO_PLAYERS, noofPlayers);
                i.putExtra(TIME, time);
                i.putExtra(CHECKBOX_CHECKED, checkBox.isChecked());
                i.putExtra(GRID_SIZE, gridSize);
                GameSettingsActivityMultiplayer.this.finish();
                startActivity(i);
                //do work (passing timer info)
            }
        });
        TextView backTextView = (TextView)findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
                onBackPressed();
            }
        });
        SharedPreferences sp = getSharedPreferences(PREFERENCE_FILE_CHAIN_REACTION, MODE_PRIVATE);
        boolean firstLogin = sp.getBoolean(FIRST_LOGIN, true);
        if(firstLogin){
            for(int i = 0; i < 8; i++){
                PlayerSettingsClass settingClass = new PlayerSettingsClass();
                settingClass.player = ("Player " + (i + 1));
                settingClass.remoteId = i;
                settingClass.colorPos = i;
                settingClass.save();
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(FIRST_LOGIN, false);
            editor.apply();
        }

        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
    }

    private void setupView() {
        warningTextView.setVisibility(View.VISIBLE);
        timerSpinner.setClickable(true);
        timerSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.timeArray, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timerSpinner.setAdapter(spinnerAdapter);
        int sel = sharedPreferences.getInt(TIMER_SELECTION, 0);
        timerSpinner.setSelection(sel);
        time = Integer.valueOf(timerSpinner.getSelectedItem().toString().substring(0, 2));
        timerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time = Integer.valueOf(timerSpinner.getSelectedItem().toString().substring(0, 2));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(TIMER_SELECTION, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.setClass(GameSettingsActivityMultiplayer.this, MainActivity.class);
        GameSettingsActivityMultiplayer.this.finish();
        startActivity(i);
    }

    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
