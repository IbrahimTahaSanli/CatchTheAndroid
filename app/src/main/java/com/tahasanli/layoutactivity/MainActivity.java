package com.tahasanli.layoutactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public enum State{
        WaitingStart,
        Playing,
        Paused,
        Finished
    }

    public Handler handler;
    public Runnable runnable;

    public ImageButton[] images;
    public int currentImage;

    public State currentState;

    public Button pauseButton;

    public TextView timeText;

    public TextView scoreText;

    public TextView maxScoreText;

    public int score;
    public int time;

    public SharedPreferences pref;

    public static final int playTime = 6;
    public static final String MaxScoreKey = "MaxScoreCache";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        images = new ImageButton[9];
        int i = 0;
        for (int id: new int[]{R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5, R.id.img6, R.id.img7, R.id.img8, R.id.img9} )
            images[i++] = (ImageButton) findViewById(id);

        images[4].setVisibility(View.VISIBLE);
        currentImage = 4;

        currentState = State.WaitingStart;

        pauseButton = (Button) findViewById(R.id.pause);
        pauseButton.setEnabled(false);

        timeText = (TextView)findViewById(R.id.time);

        scoreText = (TextView)findViewById(R.id.score);

        maxScoreText = (TextView)findViewById(R.id.maxScore);

        score = 0;
        time = 0;

        pref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        maxScoreText.setText(getString(R.string.maxScore) + pref.getInt(MaxScoreKey, 0));

    }

    public void ballOnClick(View view){
        if(currentState == State.WaitingStart || currentState == State.Finished){
            currentState = State.Playing;

            pauseButton.setEnabled(true);

            time = playTime;
            score = 0;

            handler = new Handler();

            runnable = new Runnable() {
                @Override
                public void run() {
                    moveBall();

                    time--;
                    timeText.setText(getString(R.string.time) + time);

                    if(time == 0) {
                        finish();

                        return;
                    }



                    handler.postDelayed(this, 1000);
                }
            };

            handler.post(runnable);
        }
        moveBall();

        score++;
        scoreText.setText(getString(R.string.score) + score);

    }

    public void finish(){
        currentState = State.Finished;

        images[currentImage].setVisibility(View.INVISIBLE);

        currentImage = 4;
        images[currentImage].setVisibility(View.VISIBLE);


        scoreText.setText(R.string.start);

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(R.string.finishTitle);
        dialog.setMessage(R.string.finishMessage);

        if(score > pref.getInt(MaxScoreKey,0)) {
            pref.edit().putInt(MaxScoreKey, score).apply();
            dialog.setMessage(R.string.finishMessageMax);
        }
        maxScoreText.setText(getString(R.string.maxScore) + pref.getInt(MaxScoreKey, 0));

        dialog.show();

        pauseButton.setEnabled(false);

    }
    public void moveBall(){
        images[currentImage].setVisibility(View.INVISIBLE);

        currentImage = (new Random()).nextInt(9);
        images[currentImage].setVisibility(View.VISIBLE);
    }

    public void pause(View view){
        if(currentState != State.Paused){
            currentState = State.Paused;

            pauseButton.setText(R.string.resume);

            images[currentImage].setVisibility(View.INVISIBLE);
            handler.removeCallbacks(runnable);
        }
        else{
            currentState = State.Playing;

            pauseButton.setText(R.string.Pause);

            handler.post(runnable);
        }
    }
}