package com.example.sparshgupta.chainreaction1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import static com.example.sparshgupta.chainreaction1.GameSettingsActivityMultiplayer.PREFERENCE_FILE_CHAIN_REACTION;
import static com.example.sparshgupta.chainreaction1.GameplayActivity.NO_PLAYERS;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.SOUND_STATE;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.VIBRATION_STATE;

public class PlayerSettingsActivity extends AppCompatActivity {

    int noOfPlayers;
    ListView playerListView;
    int currentApiVersion;
    PlayerAdapter playerAdapter;
    TextView doneTextView;
    ArrayList<PlayerSettingsClass> players;
    SharedPreferences sp;
    public static final String FIRST_LOGIN = "firstLogin";
    boolean isVibration, isSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_settings);
        ImageView imageView = (ImageView)findViewById(R.id.settingsImageView);
        imageView.setImageBitmap(EfficientImages.decodeSampledBitmapFromResource(this.getResources(), R.drawable.playersettings, 720, 1280));
        Intent intent = getIntent();
        doneTextView = (TextView)findViewById(R.id.doneTextView);
        sp = getSharedPreferences(PREFERENCE_FILE_CHAIN_REACTION, MODE_PRIVATE);
        isVibration = sp.getBoolean(VIBRATION_STATE, true);
        isSound = sp.getBoolean(SOUND_STATE, true);
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
               PlayerSettingsActivity.this.finish();
                Intent i = new Intent();
                i.setClass(PlayerSettingsActivity.this, GameSettingsActivityMultiplayer.class);
                startActivity(i);
            }
        });
        noOfPlayers = intent.getIntExtra(NO_PLAYERS, -1);
        playerListView = (ListView)findViewById(R.id.playersListView);
        players = new ArrayList<>(noOfPlayers);
        playerAdapter = new PlayerAdapter(this, players);
        playerListView.setAdapter(playerAdapter);
        setUpView();
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().getDecorView().setSystemUiVisibility(flags);
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

    private void setUpView() {
        players.clear();
        List<PlayerSettingsClass> list = new Select().from(PlayerSettingsClass.class).execute();
        for(int i = 0; i < noOfPlayers; i++){
            players.add(list.get(i));
        }
        playerAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        PlayerSettingsActivity.this.finish();
        Intent i = new Intent();
        i.setClass(PlayerSettingsActivity.this, GameSettingsActivityMultiplayer.class);
        startActivity(i);
    }
}
