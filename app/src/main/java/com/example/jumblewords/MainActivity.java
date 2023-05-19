package com.example.jumblewords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static HashMap<String, String> wordHintSet;
    public static boolean speedModeOn;
    private static int largestWordLen = Integer.MIN_VALUE;
    public static boolean hasSetTime = false;
    ArrayList<String>  keyArr,valArr;
    TextView minShower;
    TextView secShower;
    Button minIncBtn;
    Button minDecBtn;
    Button secIncBtn;
    Button secDecBtn;
    Button timeSetterSaveBtn;
    Dialog timeSetter;
    static int min = 1;
    static int sec = 30;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch gameMode;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switcher;
    Dialog settings;
    static boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static int HighScore = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        timeSetter = new Dialog(this);
        timeSetter.setContentView(R.layout.give_time);
        timeSetter.setCanceledOnTouchOutside(false);
        minShower = timeSetter.findViewById(R.id.minShower);
        secShower = timeSetter.findViewById(R.id.secShower);
        minIncBtn = timeSetter.findViewById(R.id.minIncBtn);
        minDecBtn = timeSetter.findViewById(R.id.minDecBtn);
        secIncBtn = timeSetter.findViewById(R.id.secIncBtn);
        secDecBtn = timeSetter.findViewById(R.id.secDecBtn);
        timeSetterSaveBtn = timeSetter.findViewById(R.id.timeSetterSaveBtn);
        minIncBtn.setOnClickListener(v -> {
            if(min < 60) {
                min++;
            }
            minShower.setText(String.valueOf(min));
        });
        minDecBtn.setOnClickListener(v -> {
            if(min > 0) {
                min--;
            }
            minShower.setText(String.valueOf(min));
        });
        secIncBtn.setOnClickListener(v -> {
            if(sec < 60) {
                sec++;
            }
            secShower.setText(String.valueOf(sec));
        });
        secDecBtn.setOnClickListener(v -> {
            if(sec > 0) {
                sec--;
            }
            secShower.setText(String.valueOf(sec));
        });
        timeSetterSaveBtn.setOnClickListener(v -> timeSetter.dismiss());
        settings = new Dialog(this);
        settings.setContentView(R.layout.settings);
        settings.setCanceledOnTouchOutside(false);
        ImageView openSettings = findViewById(R.id.openSettings);
        openSettings.setOnClickListener(v -> settings.show());
        if(savedInstanceState != null) {
            hasSetTime = savedInstanceState.getBoolean("hasSetTime", true);
        }
        Button settingsSave = settings.findViewById(R.id.settingsSave);
        settingsSave.setOnClickListener(v -> {
            settings.dismiss();
            if(gameMode.isChecked() && !hasSetTime) {
                timeSetter.show();
                hasSetTime = true;
            }
        });
        gameMode = settings.findViewById(R.id.gameMode);
        if(savedInstanceState != null) {
            gameMode.setChecked(savedInstanceState.getBoolean("TimerModeOn",false));
            if(gameMode.isChecked()) {
                String avoidErrorLol = "Switch to Normal Mode";
                gameMode.setText(avoidErrorLol);
            } else {
                String avoidErrorLol = "Switch to Timer Mode";
                gameMode.setText(avoidErrorLol);
                hasSetTime = false;
                minShower.setText("");secShower.setText("");
            }
        }
        gameMode.setOnClickListener(v -> {
            if(gameMode.isChecked()) {
                String avoidErrorLol = "Switch to Normal Mode";
                gameMode.setText(avoidErrorLol);
            } else {
                String avoidErrorLol = "Switch to Timer Mode";
                gameMode.setText(avoidErrorLol);
                hasSetTime = false;
            }
        });
        getSupportActionBar();
        switcher = settings.findViewById(R.id.switcher);
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        if(nightMode){
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        switcher.setOnClickListener(v -> {
            if(nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", true);
            }
            editor.apply();
        });

        SharedPreferences getHighScoreShrd = getSharedPreferences("highScore",MODE_PRIVATE);
        HighScore = getHighScoreShrd.getInt("HighScore",0);
        TextView tvHighScore = findViewById(R.id.HighScore);
        String avoidError = "High Score: "+HighScore;
        tvHighScore.setText(avoidError);
        TextView wordNText = findViewById(R.id.wordNText);
        TextView hintNText = findViewById(R.id.hintNText);
        EditText wordInput = findViewById(R.id.editTextWord);
        EditText hintInput = findViewById(R.id.editTextHint);
        EditText gridSizeInput = findViewById(R.id.editTextGridSize);
        LinearLayout infoButton = settings.findViewById(R.id.imageViewInfoButton);

        if (savedInstanceState != null) {
            wordHintSet = arrayToHashMap(savedInstanceState.getStringArrayList("keys"), savedInstanceState.getStringArrayList("values"));
            String avoidError2 = "Hint "+ (wordHintSet.size() + 1);
            String avoidError3 = "Word "+ (wordHintSet.size() + 1);
            hintNText.setText(avoidError2);
            wordNText.setText(avoidError3);
            if(savedInstanceState.getBoolean("settingsShowing", false)) {
                settings.show();
            }
            sec =  savedInstanceState.getInt("sec");
            min = savedInstanceState.getInt("min");
            if(savedInstanceState.getBoolean("timerSetterShowing")){

                timeSetter.show();
            }
        } else {
            wordHintSet = new HashMap<>();

        }
        Dialog info = new Dialog(this);
        info.setContentView(R.layout.info_dialog_box);
        Button btnInfoOk = info.findViewById(R.id.btnInfoOkay);
        String information = "after enter the word and the hint of the word, press enter on your\n" +
                " smart phone devices to enter next word and its corresponding hint.\n" +
                "you can enter as many valid words and their corresponding hints as you want";
        TextView infoDisplay = info.findViewById(R.id.informationShowTV);
        infoDisplay.setText(information);
        btnInfoOk.setOnClickListener(v -> info.dismiss());
        infoButton.setOnClickListener(v -> info.show());
        Button EnterBtn = findViewById(R.id.EnterBtn);
        EnterBtn.setOnClickListener(v -> {
            String currWord = wordInput.getText().toString().toUpperCase().replaceAll("\\s","");
            if (alphabetChecker(currWord)) {
                String currHint = hintInput.getText().toString();
                if(!currHint.equals("") && !currHint.equals(" ")) {
                    largestWordLen = Math.max(largestWordLen, currWord.length());
                    wordHintSet.put(currWord, currHint);
                    String hintDisplay = "Hint " + (wordHintSet.size() + 1);
                    String wordDisplay = "Word " + (wordHintSet.size() + 1);
                    hintNText.setText(hintDisplay);
                    wordNText.setText(wordDisplay);
                    wordInput.setText("");
                    hintInput.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "enter a valid hint", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "enter a valid word", Toast.LENGTH_SHORT).show();
            }
        });

        Button button = findViewById(R.id.button2);
        button.setOnClickListener(v -> {
            if(wordHintSet.size() == 0) {
                Toast.makeText(MainActivity.this, "enter at least one word to continue!", Toast.LENGTH_SHORT).show();
            }
            else if(gridSizeInput.getText().toString().equals("")) {
                Toast.makeText(MainActivity.this, "enter grid size to proceed", Toast.LENGTH_SHORT).show();
            } else if(Math.pow(Integer.parseInt(gridSizeInput.getText().toString()),2) > largestWordLen) {
                speedModeOn = gameMode.isChecked();
                if(!speedModeOn) {
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    keyArr = new ArrayList<>(wordHintSet.keySet());
                    valArr = new ArrayList<>(wordHintSet.values());
                    intent.putExtra("wordHintSet", wordHintSet);
                    intent.putExtra("gridSize",Integer.parseInt(gridSizeInput.getText().toString()));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                    keyArr = new ArrayList<>(wordHintSet.keySet());
                    valArr = new ArrayList<>(wordHintSet.values());
                    intent.putExtra("wordHintSet", wordHintSet);
                    intent.putExtra("gridSize",Integer.parseInt(gridSizeInput.getText().toString()));
                    intent.putExtra("sec",sec);
                    intent.putExtra("min",min);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            } else {
                int i = 1;
                while ((i * i) < largestWordLen) {
                    i++;
                }
                Toast.makeText(MainActivity.this, "The grid size must be at least "+ i, Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("keys",new ArrayList<>(wordHintSet.keySet()));
        outState.putStringArrayList("values",new ArrayList<>(wordHintSet.values()));
        outState.putBoolean("settingsShowing",settings.isShowing());
        outState.putBoolean("TimerModeOn", gameMode.isChecked());
        outState.putBoolean("timerSetterShowing", timeSetter.isShowing());
        outState.putBoolean("hasSetTime", hasSetTime);
        outState.putInt("sec", sec);
        outState.putInt("min",min);
    }

    public boolean alphabetChecker(String word){
        if(Objects.equals(word, "")) {
            return false;
        }
        for( int j = 0; j < word.toUpperCase().length(); j++) {
            if(word.charAt(j) < 'A' ||  word.charAt(j) > 'Z') {
                return false;
            }
        }
        return true;
    }
    public static HashMap<String,String> arrayToHashMap(ArrayList<String> keys, ArrayList<String> values) {
        HashMap<String, String> newHashmap = new HashMap<>();
        if(keys.size() != values.size()) {
            return newHashmap;
        } else if(keys.isEmpty()){
            return newHashmap;
        } else {
            for(int i = 0; i < keys.size(); i++) {
                newHashmap.put(keys.get(i),values.get(i));
            }
            return newHashmap;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "On Start!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "On Stop!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "On Destroy!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "On Resume!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "On Pause!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "On Restart!", Toast.LENGTH_SHORT).show();
    }
}