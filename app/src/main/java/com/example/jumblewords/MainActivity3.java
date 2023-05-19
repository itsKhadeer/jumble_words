package com.example.jumblewords;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class MainActivity3 extends AppCompatActivity implements selectListener {
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    public static int HighScore = Integer.MIN_VALUE;
    TextView lmao;
    Dialog gameOver;
    public static boolean GameOver = false;
    private RecyclerView contactsRecView;
    TextView answering;
    Button btnHomePage;
    private static StringBuilder sb;
    TextView hintShower;
    public static String currAnsweringWord;
    public static int count = 0;
    private static ArrayList<String> keys;
    private static int score = 0;
    private static final Random r = new Random();
    ArrayList<Contact> contacts;
    public static int sec;
    public static int min;
    private static int gridSize;
    public Context context = MainActivity3.this;
    static int answeredWords = 0;

    TextView tvPopUpHint;
    public Button btnRetry;
    public static HashMap<String, String> retryArr;
    public static HashMap<String, String> wordHintSet;
    TextView timeShower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        if(MainActivity.nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        //heartsMode
        if (savedInstanceState != null) {
            count = savedInstanceState.getInt("count");
            score = savedInstanceState.getInt("score");
        }
        Intent incomingIntent = getIntent();

        gameOver = new Dialog(this);
        gameOver.setCanceledOnTouchOutside (false);
        gameOver.setCancelable(false);
        gameOver.setContentView(R.layout.you_win_popup);
        lmao =  gameOver.findViewById(R.id.tvYourScore);
        btnHomePage =  gameOver.findViewById(R.id.btnHomePage);
        btnRetry = gameOver.findViewById(R.id.btnRetry);
        timeShower = findViewById(R.id.timeShower);
        sec = incomingIntent.getIntExtra("sec",30);
        min = incomingIntent.getIntExtra("min", 1);
        timeLeftInMillis  = (min* 60L +sec)*1000;
        if(savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis", timeLeftInMillis);
        }
        startCountDown();
        wordHintSet = (HashMap<String, String>) incomingIntent.getSerializableExtra("wordHintSet");
        retryArr = wordHintSet;
        hintShower = findViewById(R.id.showingHintTV);
        gridSize = incomingIntent.getIntExtra("gridSize", 4);
        Button reset = findViewById(R.id.btnReset);
        Button check = findViewById(R.id.btnCheck);
        sb = new StringBuilder();
        ContactsRecViewAdapter adapter = new ContactsRecViewAdapter(this, this);
        answering = findViewById(R.id.tvAnswering);
        contactsRecView = findViewById(R.id.contactsRecView);
        contacts = new ArrayList<>();
        keys = new ArrayList<>(wordHintSet.keySet());
        Dialog showHint = new Dialog(this);
        showHint.setContentView(R.layout.hintpopup);
        Button okay =  showHint.findViewById(R.id.btnHintOkay);
        okay.setOnClickListener(v -> showHint.dismiss());
        Dialog info = new Dialog(this);
        ImageView infoButton = findViewById(R.id.imageViewInfoButton2);
        info.setContentView(R.layout.info_dialog_box);
        Button btnInfoOk = info.findViewById(R.id.btnInfoOkay);
        String information = "each correct word guess = 50 point\n" +
                "each heart remaining = 100 points\n" +
                "each reset = -10 points";
        TextView infoDisplay = info.findViewById(R.id.informationShowTV);
        infoDisplay.setText(information);
        btnInfoOk.setOnClickListener(v -> info.dismiss());
        infoButton.setOnClickListener(v -> info.show());
        btnRetry.setOnClickListener(v -> {
            if(HighScore < score) {
                HighScore = score;
            }
            GameOver = false;
            gameOver.dismiss();
            timeLeftInMillis  = (min* 60L +sec)*1000;
            startCountDown();
            count = 0;
            score = 0;
            answeredWords = 0;
            wordHintSet = retryArr;
            keys = new ArrayList<>(wordHintSet.keySet());
            // setting current word for the user to guess
            Collections.shuffle(keys);
            currAnsweringWord = keys.get(0);
            keys.remove(0);
            //setting the answering box to dashes
            sb = new StringBuilder();
            for (int i = 0; i < currAnsweringWord.length(); i++) {
                sb.append("_ ");
            }
            answering.setText(sb.toString());


            //arranging the grid
            contacts.clear();
            for (int i = 0; i < currAnsweringWord.length(); i++) {
                contacts.add(new Contact(String.valueOf(currAnsweringWord.charAt(i)), null, null));
            }
            for (int i = currAnsweringWord.length(); i < gridSize * gridSize; i++) {
                contacts.add(new Contact(String.valueOf((char) (r.nextInt('Z' - 'A') + 'A')), null, null));
            }
            Collections.shuffle(contacts);
            adapter.setContacts(contacts);
            contactsRecView.setAdapter(adapter);
            contactsRecView.setLayoutManager(new GridLayoutManager(MainActivity3.this, gridSize));


            //setting the hint for the current word

            tvPopUpHint = showHint.findViewById(R.id.tvPopUpHint);
            String idk = "Hint: "+wordHintSet.get(currAnsweringWord);
            hintShower.setText(idk);
            tvPopUpHint.setText(idk);
            showHint.show();
            TextView wordCount = findViewById(R.id.wordCountTV);
            String setting = "word:"+ (answeredWords + 1) +"/"+ retryArr.size();
            wordCount.setText(setting);

        });
        btnHomePage.setOnClickListener(v -> {
            if(HighScore < score) {
                HighScore = score;
            }
            GameOver = true;
            MainActivity.hasSetTime = false;
            SharedPreferences HighScoreShrd = getSharedPreferences("highScore", MODE_PRIVATE);
            SharedPreferences.Editor editor = HighScoreShrd.edit();
            editor.putInt("HighScore",HighScore);
            editor.apply();
            Intent intent = new Intent(MainActivity3.this,MainActivity.class);
            gameOver.dismiss();
            count = 0;
            answeredWords = 0;
            finish();
            score = 0;
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        });
        if(savedInstanceState != null) {
            if (savedInstanceState.getBoolean("GameOver")) {
                String lol = "Score: "+score;
                lmao.setText(lol);
                gameOver.show();
            }
        }
        if(savedInstanceState != null) {
            keys = savedInstanceState.getStringArrayList("keys");
        }

        // setting current word for the user to guess
        Collections.shuffle(keys);
        if(savedInstanceState != null) {
            currAnsweringWord = savedInstanceState.getString("currAnsweringWord");
            keys.remove(currAnsweringWord);
        } else {
            currAnsweringWord = keys.get(0);
            keys.remove(0);
        }
        //setting the answering box to dashes
        sb = new StringBuilder();
        for (int i = 0; i < currAnsweringWord.length(); i++) {
            sb.append("_ ");
        }
        if(savedInstanceState != null) {
            sb = new StringBuilder(savedInstanceState.getString("sbString"));
        }
        answering.setText(sb.toString());


        //arranging the grid
        contacts.clear();
        for (int i = 0; i < currAnsweringWord.length(); i++) {
            contacts.add(new Contact(String.valueOf(currAnsweringWord.charAt(i)), null, null));
        }
        for (int i = currAnsweringWord.length(); i < gridSize * gridSize; i++) {
            contacts.add(new Contact(String.valueOf((char) (r.nextInt('Z' - 'A') + 'A')), null, null));
        }
        Collections.shuffle(contacts);
        adapter.setContacts(contacts);
        contactsRecView.setAdapter(adapter);
        contactsRecView.setLayoutManager(new GridLayoutManager(this, gridSize));


        //setting the hint for the current word

        tvPopUpHint = showHint.findViewById(R.id.tvPopUpHint);
        String idk = "Hint: "+wordHintSet.get(currAnsweringWord);
        hintShower.setText(idk);
        tvPopUpHint.setText(idk);
        if(savedInstanceState == null) {
            showHint.show();
        }
        //setting wordCount
        TextView wordCount = findViewById(R.id.wordCountTV);
        String setting = "word:"+ (answeredWords + 1) +"/"+ retryArr.size();
        wordCount.setText(setting);



        check.setOnClickListener(v -> {
            if (!answering.getText().toString().contains("_")){
                if(answering.getText().toString().toUpperCase().replaceAll("\\s", "").equals(currAnsweringWord)) {
                    score += 50;
                    answeredWords++;
                    if(!keys.isEmpty()) {

                        Collections.shuffle(keys);
                        currAnsweringWord = keys.get(0);
                        keys.remove(0);
                        tvPopUpHint = showHint.findViewById(R.id.tvPopUpHint);
                        String idk1 = "Hint: "+wordHintSet.get(currAnsweringWord);
                        tvPopUpHint.setText(idk1);
                        hintShower.setText(idk1);
                        showHint.show();
                        //setting the answering box to dashes
                        sb = new StringBuilder();
                        for (int i = 0; i < currAnsweringWord.length(); i++) {
                            sb.append("_ ");
                        }
                        answering.setText(sb.toString());
                        //arranging the grid
                        contacts.clear();
                        for (int i = 0; i < currAnsweringWord.length(); i++) {
                            contacts.add(new Contact(String.valueOf(currAnsweringWord.charAt(i)), null, null));
                        }
                        for (int i = currAnsweringWord.length(); i < gridSize * gridSize; i++) {
                            contacts.add(new Contact(String.valueOf((char) (r.nextInt('Z' - 'A') + 'A')), null, null));
                        }
                        Collections.shuffle(contacts);
                        adapter.setContacts(contacts);
                        contactsRecView.setAdapter(adapter);
                        contactsRecView.setLayoutManager(new GridLayoutManager(MainActivity3.this, gridSize));
                        //setting the hint for the current word
                        String hintLOL = "Hint: "+wordHintSet.get(currAnsweringWord);
                        hintShower.setText(hintLOL);
                        count = 0;
                        //setting wordCount
                        TextView wordCount1 = findViewById(R.id.wordCountTV);
                        String setting1 = "word:"+ (answeredWords + 1) +"/"+ retryArr.size();
                        wordCount1.setText(setting1);

                        Toast.makeText(MainActivity3.this, "Your answer is correct!!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //you win popup, retry?
                        Toast.makeText(MainActivity3.this, "You win!!", Toast.LENGTH_SHORT).show();
                        score += timeLeftInMillis/100;//timer should come
                        TextView lmao =  gameOver.findViewById(R.id.tvYourScore);
                        GameOver = true;
                        String lol = "Score: "+score;
                        lmao.setText(lol);
                        gameOver.show();

                    }
                } else {
                    {
                        Toast.makeText(MainActivity3.this, "wrong answer!", Toast.LENGTH_SHORT).show();
                        //set answering box to dashes
                        sb = new StringBuilder();
                        for (int i = 0; i < currAnsweringWord.length(); i++) {
                            sb.append("_ ");
                        }
                        answering.setText(sb.toString());
                        // don't set new answering word, but continue the rest function
                        contacts.clear();
                        for (int i = 0; i < currAnsweringWord.length(); i++) {
                            contacts.add(new Contact(String.valueOf(currAnsweringWord.charAt(i)), null, null));
                        }
                        for (int i = currAnsweringWord.length(); i < gridSize * gridSize; i++) {
                            contacts.add(new Contact(String.valueOf((char) (r.nextInt('Z' - 'A') + 'A')), null, null));
                        }
                        Collections.shuffle(contacts);
                        adapter.setContacts(contacts);
                        contactsRecView.setAdapter(adapter);
                        contactsRecView.setLayoutManager(new GridLayoutManager(MainActivity3.this, gridSize));
                        //setting the hint for the current word
                        String hintLOL = "Hint: "+wordHintSet.get(currAnsweringWord);
                        hintShower.setText(hintLOL);
                        count = 0;
                    }
                }
            }
        });
        reset.setOnClickListener(v -> {
            score = score-10;
            sb = new StringBuilder();
            for (int i = 0; i < currAnsweringWord.length(); i++) {
                sb.append("_ ");
            }
            answering.setText(sb.toString());
            // don't set new answering word, but continue the rest function
            contacts.clear();
            for (int i = 0; i < currAnsweringWord.length(); i++) {
                contacts.add(new Contact(String.valueOf(currAnsweringWord.charAt(i)), null, null));
            }
            for (int i = currAnsweringWord.length(); i < gridSize * gridSize; i++) {
                contacts.add(new Contact(String.valueOf((char) (r.nextInt('Z' - 'A') + 'A')), null, null));
            }
            Collections.shuffle(contacts);
            adapter.setContacts(contacts);
            contactsRecView.setAdapter(adapter);
            contactsRecView.setLayoutManager(new GridLayoutManager(MainActivity3.this, gridSize));
            count = 0;
        });

    }

    @Override
    public void onItemClicked(Contact contact) {

        if(count/2 < currAnsweringWord.length()) {
            answering.setText(sb.replace(count,count+1,contact.getName().toUpperCase()));
            count += 2;
        }

        else {
            Toast.makeText(this, "it's a "+(currAnsweringWord.length())+" letter word!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", count);
        outState.putInt("score", score);
        outState.putInt("answeredWords",answeredWords);
        outState.putString("currAnsweringWord",currAnsweringWord);
        outState.putString("sbString",sb.toString());
        outState.putStringArrayList("keys",keys);
        outState.putBoolean("GameOver",gameOver.isShowing());
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
    }
    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                if(GameOver) {
                    countDownTimer.cancel();
                    gameOver.show();
                }
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                GameOver = true;
                TextView lmao =  gameOver.findViewById(R.id.tvYourScore);
                String errorAvoid = "Score: "+ score;
                lmao.setText(errorAvoid);
                gameOver.show();
            }
        }.start();
    }
    private void updateCountDownText(){
        int minutes = (int) (timeLeftInMillis/1000)/60;
        int seconds = (int) (timeLeftInMillis/1000)%60;
        String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        timeShower.setText(timeFormatted);



        if(timeLeftInMillis < 10000) {
            timeShower.setTextColor(Color.RED);
        } else {
            timeShower.setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(context, "On Start!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(context, "On Stop!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(context, "On Destroy!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(context, "On Resume!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(context, "On Pause!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(context, "On Restart!", Toast.LENGTH_SHORT).show();
    }
}