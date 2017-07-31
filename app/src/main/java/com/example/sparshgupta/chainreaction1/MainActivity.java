package com.example.sparshgupta.chainreaction1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.sparshgupta.chainreaction1.GameSettingsActivityMultiplayer.PREFERENCE_FILE_CHAIN_REACTION;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.SOUND_STATE;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.VIBRATION_STATE;

public class MainActivity extends AppCompatActivity {


    int currentApiVersion;
    SharedPreferences sharedPreferences;
    boolean isVibration, isSound, isMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView)findViewById(R.id.backgroundImageView);
        imageView.setImageBitmap(EfficientImages.decodeSampledBitmapFromResource(this.getResources(), R.drawable.homescreen, 720, 1280));
        setUpMenu();
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

    private void setUpMenu() {
        sharedPreferences = getSharedPreferences(PREFERENCE_FILE_CHAIN_REACTION, MODE_PRIVATE);
        isVibration = sharedPreferences.getBoolean(VIBRATION_STATE, true);
        isSound = sharedPreferences.getBoolean(SOUND_STATE, true);
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        final TextView multiplayer = (TextView) findViewById(R.id.multiplayer);
        TextView textView = (TextView) findViewById(R.id.help);
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
                i.setClass(MainActivity.this, HelpAvtivity.class);
                startActivity(i);
            }
        });
        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
                Intent i = new Intent();
                i.setClass(MainActivity.this, GameSettingsActivityMultiplayer.class);
                MainActivity.this.finish();
                startActivity(i);
            }
        });
        final TextView settingsTextView = (TextView) findViewById(R.id.settings);
        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isSound){
                    mp.start();
                }
                Intent i = new Intent();
                i.setClass(MainActivity.this, SettingsActivity.class);
                startActivityForResult(i, 2);
            }
        });
        TextView quitTextView = (TextView) findViewById(R.id.quit);
        quitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSound){
                    mp.start();
                }
                if(isVibration){
                    vibrator.vibrate(50);
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("Quit Game?");
                dialog.setPositiveButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.create().show();
            }
        });
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage("Quit Game?");
        dialog.setPositiveButton("QUIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndRemoveTask ();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 2){
            if(resultCode == RESULT_OK){
                isVibration = sharedPreferences.getBoolean(VIBRATION_STATE, true);
                isSound = sharedPreferences.getBoolean(SOUND_STATE, true);
            }
        }
    }
}
