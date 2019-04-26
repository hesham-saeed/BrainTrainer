package com.example.braintrainer;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int GAME_TIME = 30000;
    private static final int TIMER_INTERVAL = 1000;

    private TextView scoreTextView;
    private TextView timerTextView;
    private TextView questionTextView;
    private Button firstQuestionButton;
    private Button secondQuestionButton;
    private Button thirdQuestionButton;
    private Button fourthQuestionButton;
    private Button playAgainButton;
    private FrameLayout gameOverFrameLayout;
    private TextView finalScoreTextView;
    private TextView answerTextView;
    private GridLayout answersGridButtons;
    private String storedScore;

    private CountDownTimer countDownTimer;

    private QuestionBank questionBank = new QuestionBank();

    private DatabaseReference databaseReference;

    private int score;
    private int questionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        questionTextView = findViewById(R.id.questionTextView);
        finalScoreTextView = findViewById(R.id.finalScoreTextView);
        firstQuestionButton = findViewById(R.id.firstNumberButton);
        secondQuestionButton = findViewById(R.id.secondNumberButton);
        thirdQuestionButton = findViewById(R.id.thirdNumberButton);
        fourthQuestionButton = findViewById(R.id.fourthNumberButton);
        gameOverFrameLayout = findViewById(R.id.gameOverFrameLayout);
        answerTextView = findViewById(R.id.answerTextView);
        answersGridButtons = findViewById(R.id.answersGridLayout);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        initializeGame();

        playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeGame();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("leaderboard");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String score = (String) dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue();
                    Log.d(TAG, "Firebase score " + score);
                    storedScore = score;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeGame() {
        generateQuestions();
        startTimer();
        resetUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

    private void resetUI() {
        gameOverFrameLayout.setVisibility(View.INVISIBLE);
        scoreTextView.setText("0/0");
        answerTextView.setVisibility(View.VISIBLE);
        answerTextView.setText("");
        for (int i = 0; i < answersGridButtons.getChildCount(); i++) {
            answersGridButtons.getChildAt(i).setEnabled(true);
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(GAME_TIME, TIMER_INTERVAL) {
            @Override
            public void onTick(final long millisUntilFinished) {
                if (millisUntilFinished < 10000)
                    timerTextView.setText("0" + String.valueOf(millisUntilFinished / 1000) + "s");
                else
                    timerTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                endGame();
            }
        }.start();
    }

    private void endGame() {
        gameOverFrameLayout.setVisibility(View.VISIBLE);
        answerTextView.setVisibility(View.INVISIBLE);
        finalScoreTextView.setText("Your score: " + scoreTextView.getText());
        timerTextView.setText("0s");
        for (int i = 0; i < answersGridButtons.getChildCount(); i++) {
            answersGridButtons.getChildAt(i).setEnabled(false);
        }
        updateScore();
    }

    private void updateScore() {
        if (!TextUtils.isEmpty(storedScore)) {
            int previousScore = Integer.parseInt(storedScore.split("/")[0]);
            int currentScore = Integer.parseInt(scoreTextView.getText().toString().split("/")[0]);
            if (currentScore > previousScore) {
                databaseReference.child(FirebaseAuth.getInstance().getUid())
                        .setValue(scoreTextView.getText().toString());
                Log.d(TAG, "score updated " + scoreTextView.getText().toString());
            }
        } else {
            databaseReference.child(FirebaseAuth.getInstance().getUid())
                    .setValue(scoreTextView.getText().toString());
            Log.d(TAG, "score updated" + scoreTextView.getText().toString());
        }
    }

    private void generateQuestions() {
        int question[] = questionBank.generateTwoNumbers("easy");
        int answers[] = questionBank.generateAnswers(question[0], question[1], "add");
        firstQuestionButton.setText(String.valueOf(answers[0]));
        secondQuestionButton.setText(String.valueOf(answers[1]));
        thirdQuestionButton.setText(String.valueOf(answers[2]));
        fourthQuestionButton.setText(String.valueOf(answers[3]));
        questionTextView.setText(question[0] + " + " + question[1]);
    }

    public void submitAnswer(View view) {
        int answer = Integer.parseInt(((TextView) view).getText().toString());
        String question = questionTextView.getText().toString();
        int num1 = Integer.parseInt(question.split(" ")[0]);
        int num2 = Integer.parseInt(question.split(" ")[2]);
        boolean correctAnswer = questionBank.checkAnswers(num1, num2, answer, "add");
        if (correctAnswer) {
            score = score + 1;
            answerTextView.setText("Correct!");
        } else
            answerTextView.setText("Wrong!");
        questionCount++;
        scoreTextView.setText(score + "/" + questionCount);
        generateQuestions();
    }
}
