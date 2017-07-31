package com.example.sparshgupta.chainreaction1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sparshgupta.chainreaction1.GameSettingsActivityMultiplayer.CHECKBOX_CHECKED;
import static com.example.sparshgupta.chainreaction1.GameSettingsActivityMultiplayer.PREFERENCE_FILE_CHAIN_REACTION;
import static com.example.sparshgupta.chainreaction1.GameSettingsActivityMultiplayer.TIME;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.SOUND_STATE;
import static com.example.sparshgupta.chainreaction1.SettingsFragments.VIBRATION_STATE;

public class GameplayActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout gameLayout;
    int noOfPlayers;
    String gridSize;
    int rows, columns;
    LinearLayout rowsLinearLayout[];
    CustomButton buttons[][];
    boolean isGameOver;
    int playerChance;
    int colorCodes[];
    int playerScore[];
    CircleImageView playerChanceImageView;
    TextView restartTextView, undoTextView, quitTextView;
    int iterations;
    State prevState[][];
    int prevPlayerChance;
    State helperState[][];
    boolean isFirst;
    int currentApiVersion;
    int time;
    boolean isTimerOn;
    CountDownTimer countDownTimer;
    TextView timerTextView;
    boolean checkTimeOver, isVibration, isSound, isMusic;
    ImageView settingsImageView, pauseImageView;
    SharedPreferences sharedPreferences;
    Vibrator vibrator;
    CountDownTimerPausable countDownTimerPausable;


    public static final String NO_PLAYERS = "noofPlayers";
    public static final String GRID_SIZE = "gridSize";
    public static final String TIMER = "timer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        ImageView imageView = (ImageView)findViewById(R.id.gameImageView);
        imageView.setImageBitmap(EfficientImages.decodeSampledBitmapFromResource(this.getResources(), R.drawable.gamescreen, 720, 1280));
        gameLayout = (LinearLayout)findViewById(R.id.GameLinearLayout);
        Intent intent = getIntent();
        time = intent.getIntExtra(TIME, -1);
        isTimerOn = intent.getBooleanExtra(CHECKBOX_CHECKED, false);
        timerTextView = (TextView)findViewById(R.id.timeTextView);
        noOfPlayers = intent.getIntExtra(NO_PLAYERS, 2);
        gridSize = intent.getStringExtra(GRID_SIZE);
        sharedPreferences = getSharedPreferences(PREFERENCE_FILE_CHAIN_REACTION, MODE_PRIVATE);
        isSound = sharedPreferences.getBoolean(SOUND_STATE, true);
        isVibration = sharedPreferences.getBoolean(VIBRATION_STATE, true);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        settingsImageView = (ImageView)findViewById(R.id.settingsImageView);
        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSound){
                    mp.start();
                }
                if(isVibration){
                    vibrator.vibrate(50);
                }
                //pause timer
                pauseTimer();
                Intent i = new Intent();
                i.setClass(GameplayActivity.this, InGameSettingsActivity.class);
                startActivityForResult(i, 1);
                //resume timer
            }
        });
        pauseImageView = (ImageView)findViewById(R.id.pauseImageView);
        pauseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSound){
                    mp.start();
                }
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(countDownTimerPausable != null)
                    countDownTimerPausable.pause();

                AlertDialog.Builder dialog = new AlertDialog.Builder(GameplayActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("GAME PAUSED");
                dialog.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(countDownTimerPausable != null && countDownTimerPausable.isPaused()){
                            countDownTimerPausable.start();
                        }else{
                            dialog.cancel();
                        }
                    }
                });
                dialog.setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(countDownTimerPausable != null){
                            countDownTimerPausable.cancel();
                            countDownTimerPausable = null;
                        }
                        GameplayActivity.this.finish();
                    }
                });
                dialog.create().show();
            }
        });
        int colorLibrary[] = new int[10];
        colorLibrary[0] = R.color.colorRed;
        colorLibrary[1] = R.color.colorGreen;
        colorLibrary[2] = R.color.colorBlue;
        colorLibrary[3] = R.color.colorYellow;
        colorLibrary[4] = R.color.colorGrey;
        colorLibrary[5] = R.color.colorBrown;
        colorLibrary[6] = R.color.colorPurple;
        colorLibrary[7] = R.color.colorWhite;
        colorLibrary[8] = R.color.colorPink;
        colorLibrary[9] = R.color.colorCyan;
        colorCodes = new int[noOfPlayers];
        PlayerSettingsClass playerSettingsClass;
        for(int i = 0; i < noOfPlayers; i++){
            playerSettingsClass = new Select().from(PlayerSettingsClass.class).where("remote_Id = ?", i).executeSingle();
            int temp = playerSettingsClass.colorPos;
            colorCodes[i] = colorLibrary[temp];
        }
        playerChanceImageView = (CircleImageView)findViewById(R.id.playerChanceImageView);
        restartTextView = (TextView)findViewById(R.id.restartTextView);
        restartTextView.setOnClickListener(this);
        undoTextView = (TextView)findViewById(R.id.undoTextView);
        undoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                if(isGameOver)
                    return;
                restorePrevState();
                redoHelperState();
            }
        });
        quitTextView = (TextView)findViewById(R.id.quitTextView);
        quitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVibration){
                    vibrator.vibrate(50);
                }
                //alert dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameplayActivity.this);
                dialog.setTitle("Confirm");
                dialog.setMessage("Do you want to quit the game?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(countDownTimerPausable != null){
                            countDownTimerPausable.cancel();
                            countDownTimerPausable = null;
                        }
                        Intent i = new Intent();
                        i.setClass(GameplayActivity.this, MainActivity.class);
                        GameplayActivity.this.finish();
                        startActivity(i);
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
      //  FullScreencall();
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
        setUpGame();
    }

    @Override
    protected void onPause() {
        if(countDownTimerPausable != null){
            countDownTimerPausable.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(countDownTimerPausable != null){
            countDownTimerPausable.start();
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                isSound = sharedPreferences.getBoolean(SOUND_STATE, true);
                isVibration = sharedPreferences.getBoolean(VIBRATION_STATE, true);
            }
        }
    }

    private void pauseTimer() {

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

    private void redoHelperState() {
        State tempState;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                tempState = prevState[i][j];
                helperState[i][j].setPrevplayer(tempState.getPrevplayer());
                helperState[i][j].setPrevvalue(tempState.getPrevvalue());
            }
        }
    }

    private void setUpGame() {
        if(isTimerOn){
            timerTextView.setText(time + " sec");
        }
        if(countDownTimerPausable!=null){
            countDownTimerPausable.cancel();
            countDownTimerPausable = null;
        }
        checkTimeOver = false;
        isGameOver = false;
        gameLayout.removeAllViews();
        if(gridSize.equals("Small")){
            rows = 7;
            columns = 5;
        }else if(gridSize.equals("Medium")){
            rows = 9;
            columns = 6;
        }
        else{
            rows = 12;
            columns = 8;
        }
        playerScore = new int[noOfPlayers];
        for(int i = 0; i < noOfPlayers; i++){
            playerScore[i] = -1;
        }
        //setColorCodes for each player
        //setting up board
        rowsLinearLayout = new LinearLayout[rows];
        for(int i = 0; i < rows; i++){
            rowsLinearLayout[i] = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
            rowsLinearLayout[i].setLayoutParams(params);
            gameLayout.addView(rowsLinearLayout[i]);
        }

        buttons = new CustomButton[rows][columns];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                buttons[i][j] = new CustomButton(this);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                buttons[i][j].setLayoutParams(p);
                buttons[i][j].setValue(-1);
                if((i == 0 || i == rows - 1) && (j != 0 && j != columns - 1)){
                    buttons[i][j].setMax(3);
                }else if((i == 0 || i == rows - 1) && (j == 0 || j == columns - 1)){
                    buttons[i][j].setMax(2);
                }else if((i != 0 || i != rows - 1) && (j == 0 || j == columns - 1)){
                    buttons[i][j].setMax(3);
                }else{
                    buttons[i][j].setMax(4);
                }
                buttons[i][j].setRow(i);
                buttons[i][j].setColumn(j);
                buttons[i][j].setBackgroundResource(R.drawable.borderbutton3);
                buttons[i][j].setOnClickListener(this);
                buttons[i][j].setPlayer(-1);
                rowsLinearLayout[i].addView(buttons[i][j]);
            }
        }
        prevState = new State[rows][columns];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                prevState[i][j] = new State();
                prevState[i][j].setPrevplayer(-1);
                prevState[i][j].setPrevvalue(-1);
            }
        }
        helperState = new State[rows][columns];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                helperState[i][j] = new State();
                helperState[i][j].setPrevvalue(-1);
                helperState[i][j].setPrevplayer(-1);
            }
        }
        iterations = 0;
        playerChance = 0;
        populateImageViewView();
        prevPlayerChance = -1;
        isFirst = true;
    }

    @Override
    public void onClick(View v) {
        if (isVibration) {
            vibrator.vibrate(50);
        }
        int id = v.getId();
        if (id == R.id.restartTextView) {
            if (isGameOver) {
                setUpGame();
                return;
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Confirm");
            dialog.setMessage("Restart Game?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setUpGame();
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.create().show();
            return;
        }
        if (isGameOver) {
            return;
        }
        CustomButton button = (CustomButton) v;
        if (button.getPlayer() != playerChance && button.getPlayer() != -1) {
            return;
        }
        checkTimeOver = false;
        if (isTimerOn && !isGameOver) {
            if (countDownTimerPausable != null) {
                countDownTimerPausable.cancel();
                countDownTimerPausable = null;
            }
        }
        if (isTimerOn && !isGameOver) {
            if (countDownTimerPausable != null) {
                countDownTimerPausable.cancel();
                countDownTimerPausable = null;
            }
//            countDownTimerPausable = new countDownTimerPausable(time * 1000, 1000){
//
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    timerTextView.setText(millisUntilFinished / 1000 + " sec");
//                }
//
//                @Override
//                public void onFinish() {
//                    checkTimeOver = true;
//                    Toast.makeText(GameplayActivity.this, "OOPS! You missed your chance...time OVER.", Toast.LENGTH_SHORT).show();
//                    iterations++;
//                    int temp = playerChance;
//                    playerChance = (playerChance + 1) % noOfPlayers;
//                    calculateScore();
//                    while(playerScore[playerChance] == -1 && iterations > noOfPlayers) {
//                        playerChance = (playerChance + 1) % noOfPlayers;
//                    }
//                    populateImageViewView();
//                    if(!isFirst){
//                        updatePrevState();
//                        if(prevPlayerChance == -1)
//                            prevPlayerChance = 0;
//                        else
//                            prevPlayerChance = temp;
//                    }
//                    else{
//                        isFirst = false;
//                        prevPlayerChance = 0;
//                    }
//                    updateHelperState();
//                    isGameOver = checkWinner();
//                    if(isGameOver){
//                        AlertDialog.Builder dialog = new AlertDialog.Builder(GameplayActivity.this);
//                        dialog.setTitle("Game Over!");
//                        dialog.setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                setUpGame();
//                            }
//                        });
//                        dialog.setNegativeButton("MENU", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                GameplayActivity.this.finish();
//                            }
//                        });
//                        dialog.create().show();
//                    }
//                    countDownTimerPausable.cancel();
//                    countDownTimerPausable = null;
//                }
//            };
//            countDownTimerPausable.start();
//        }
            countDownTimerPausable = new CountDownTimerPausable(time * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTextView.setText(millisUntilFinished / 1000 + " sec");
                }

                @Override
                public void onFinish() {
                    checkTimeOver = true;
                    Toast.makeText(GameplayActivity.this, "OOPS! You missed your chance...time OVER.", Toast.LENGTH_SHORT).show();
                    iterations++;
                    int temp = playerChance;
                    playerChance = (playerChance + 1) % noOfPlayers;
                    calculateScore();
                    while(playerScore[playerChance] == -1 && iterations > noOfPlayers) {
                        playerChance = (playerChance + 1) % noOfPlayers;
                    }
                    populateImageViewView();
                    if(!isFirst){
                        updatePrevState();
                        if(prevPlayerChance == -1)
                            prevPlayerChance = 0;
                        else
                            prevPlayerChance = temp;
                    }
                    else{
                        isFirst = false;
                        prevPlayerChance = 0;
                    }
                    updateHelperState();
                    isGameOver = checkWinner();
                    if(isGameOver){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(GameplayActivity.this);
                        dialog.setTitle("Game Over!");
                        dialog.setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setUpGame();
                            }
                        });
                        dialog.setNegativeButton("MENU", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GameplayActivity.this.finish();
                            }
                        });
                        dialog.create().show();
                    }
                    countDownTimerPausable.cancel();
                    countDownTimerPausable = null;
                }

            };
            countDownTimerPausable.start();
        }
            if (checkTimeOver) {
                return;
            }
            work(button);
            iterations++;
            int temp = playerChance;
            playerChance = (playerChance + 1) % noOfPlayers;
            calculateScore();
            while (playerScore[playerChance] == -1 && iterations > noOfPlayers) {
                playerChance = (playerChance + 1) % noOfPlayers;
            }
            populateImageViewView();
            if (!isFirst) {
                updatePrevState();
                if (prevPlayerChance == -1)
                    prevPlayerChance = 0;
                else
                    prevPlayerChance = temp;
            } else {
                isFirst = false;
                prevPlayerChance = 0;
            }
            updateHelperState();
            if (isGameOver) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Game Over!");
                dialog.setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setUpGame();
                    }
                });
                dialog.setNegativeButton("MENU", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameplayActivity.this.finish();
                    }
                });
                dialog.create().show();
                if (countDownTimerPausable != null) {
                    countDownTimerPausable.cancel();
                    countDownTimerPausable = null;
                }
            }
        }

    private void restorePrevState() {
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                buttons[i][j].setPlayer(prevState[i][j].getPrevplayer());
                buttons[i][j].setValue(prevState[i][j].getPrevvalue());
                playerChance = prevPlayerChance;
                if(playerChance < 0)
                    playerChance = 0;
                playerChanceImageView.setImageResource(colorCodes[playerChance]);
                if(buttons[i][j].getValue() != -1)
                    buttons[i][j].setText(buttons[i][j].getValue() + "");
                else {
                    buttons[i][j].setBackgroundResource(R.drawable.borderbutton3);
                    buttons[i][j].setText("");
                }
                if(buttons[i][j].getPlayer() != -1)
                    buttons[i][j].setBackgroundResource(colorCodes[buttons[i][j].getPlayer()]);
            }
        }
        if(iterations > 0)
            iterations--;
        if(iterations == 0){
            isFirst = true;
            prevPlayerChance = -1;
        }
    }

    private void updatePrevState() {
        State tempState;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                tempState = helperState[i][j];
                prevState[i][j].setPrevplayer(tempState.getPrevplayer());
                prevState[i][j].setPrevvalue(tempState.getPrevvalue());
            }
        }
    }

    private void updateHelperState() {
        CustomButton button;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                button = buttons[i][j];
                helperState[i][j].setPrevplayer(button.getPlayer());
                helperState[i][j].setPrevvalue(button.getValue());
            }
        }
    }


    private void revertScore() {
        for(int i = 0; i < noOfPlayers; i++){
            playerScore[i] = -1;
        }
    }



    private void calculateScore() {
        revertScore();
        CustomButton button;
        for(int i = 0; i < rows; i++){
            for(int j= 0; j < columns; j++){
                button = buttons[i][j];
                if(button.getPlayer() != -1) {
                    if (playerScore[button.getPlayer()] == -1)
                        playerScore[button.getPlayer()] = 1;
                    else
                        playerScore[button.getPlayer()]++;
                }
            }
        }
    }

    private void work(CustomButton button) {
        if(button.getValue() == -1){
            button.setValue(1);
            button.setPlayer(playerChance);
            button.setText("1");
            button.setBackgroundResource(colorCodes[playerChance]);
        }else{
            if(button.getValue() < button.getMax())
                button.incrementValue();
            button.setPlayer(playerChance);
            button.setText(button.getValue() + "");
            button.setBackgroundResource(colorCodes[playerChance]);
            if(button.getValue() == button.getMax() && !isGameOver){
                int row = button.getRow();
                int column = button.getColumn();
                CustomButton temp;
                //up direction
                if(row - 1 >= 0){
                    temp = buttons[row - 1][column];
                    temp.setPlayer(playerChance);
                    if(temp.getValue() < temp.getMax())
                        temp.incrementValue();
                    button.decrementValue();
                    temp.setText(temp.getValue() + "");
                    temp.setBackgroundResource(colorCodes[playerChance]);
                }
                //down direction
                if(row + 1 < rows){
                    temp = buttons[row + 1][column];
                    temp.setPlayer(playerChance);
                    if(temp.getValue() < temp.getMax())
                        temp.incrementValue();
                    button.decrementValue();
                    temp.setText(temp.getValue() +  "");
                    temp.setBackgroundResource(colorCodes[playerChance]);
                }
                //right direction
                if(column + 1 < columns){
                    temp =  buttons[row][column + 1];
                    temp.setPlayer(playerChance);
                    if(temp.getValue() < temp.getMax())
                        temp.incrementValue();
                    button.decrementValue();
                    temp.setText(temp.getValue() + "");
                    temp.setBackgroundResource(colorCodes[playerChance]);
                }
                if(column - 1 >= 0){
                    temp = buttons[row][column - 1];
                    temp.setPlayer(playerChance);
                    if(temp.getValue() < temp.getMax())
                        temp.incrementValue();
                    button.decrementValue();
                    temp.setText(temp.getValue() + "");
                    temp.setBackgroundResource(colorCodes[playerChance]);
                }
                button.setBackgroundResource(R.drawable.borderbutton3);
                button.setText("");
                button.setPlayer(-1);
                button.setValue(-1);
                if(row - 1 >= 0 && !isGameOver) {
                    temp = buttons[row - 1][column];
                    if(temp.getValue() == temp.getMax())
                        work(temp);
                }

                if(row + 1 < rows && !isGameOver){
                    temp = buttons[row + 1][column];
                    if(temp.getValue() == temp.getMax())
                        work(temp);
                }
                if(column + 1 < columns && !isGameOver){
                    temp = buttons[row][column + 1];
                    if(temp.getValue() == temp.getMax())
                        work(temp);
                }
                if(column - 1 >= 0 && !isGameOver){
                    temp = buttons[row][column - 1];
                    if(temp.getValue() == temp.getMax())
                        work(temp);
                }
                if(iterations > noOfPlayers) {
                    calculateScore();
                    isGameOver = checkWinner();
                }
            }
        }
    }

    private void populateImageViewView() {
        playerChanceImageView.setImageResource(colorCodes[playerChance]);
    }

    private boolean checkWinner() {
        int count = 0;
        for(int i = 0; i < noOfPlayers; i++){
            if(playerScore[i] == -1){
                count++;
            }
            if(count == noOfPlayers - 1){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(GameplayActivity.this);
        dialog.setTitle("Confirm");
        dialog.setMessage("Do you want to quit the game?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(countDownTimerPausable != null){
                    countDownTimerPausable.cancel();
                    countDownTimerPausable = null;
                }
                Intent i = new Intent();
                i.setClass(GameplayActivity.this, MainActivity.class);
                GameplayActivity.this.finish();
                startActivity(i);
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
}
