package com.example.braintrainer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.braintrainer.Model.User;
import com.example.braintrainer.Utils.QuestionBank;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
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
    private TextView finalScoreTextView;
    private TextView answerTextView;
    private GridLayout answersGridButtons;
    private int storedScore;
    private int storedGamesPlayed;
    private Button leaderBoardButton;

    private CountDownTimer countDownTimer;

    private QuestionBank questionBank = new QuestionBank();

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private int score;
    private int questionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        questionTextView = findViewById(R.id.questionTextView);
        finalScoreTextView = findViewById(R.id.finalScoreTextView);
        firstQuestionButton = findViewById(R.id.firstNumberButton);
        secondQuestionButton = findViewById(R.id.secondNumberButton);
        thirdQuestionButton = findViewById(R.id.thirdNumberButton);
        fourthQuestionButton = findViewById(R.id.fourthNumberButton);
        answerTextView = findViewById(R.id.answerTextView);
        answersGridButtons = findViewById(R.id.answersGridLayout);
        leaderBoardButton = findViewById(R.id.leaderBoardButton);
        Typeface firaMonoFont = Typeface.createFromAsset(getAssets(), "fonts/fira-mono-bold.otf");
        answerTextView.setTypeface(firaMonoFont);

        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    builder.setMessage("You must be logged in");
                    builder.setPositiveButton("OK", null);
                    builder.create().show();
                } else {
                    Intent intent = new Intent(GameActivity.this, LeaderBoardActivity.class);
                    startActivity(intent);
                }
            }
        });

        initializeGame();
        final AlphaAnimation buttonClickAnimation = new AlphaAnimation(1F, 0.7F);

        playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAgainButton.startAnimation(buttonClickAnimation);
                initializeGame();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String uid = FirebaseAuth.getInstance().getUid();
                    if (uid != null) {
                        User userInfo = dataSnapshot.child(uid).getValue(User.class);
                        storedScore = userInfo.getScore();
                        storedGamesPlayed = userInfo.getGamesPlayed();
                        Log.d(TAG, "Firebase score " + userInfo.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Log.d(TAG, "onCreate() called");
    }

    private void initializeGame() {
        Log.d(TAG, "initializeGame() called");
        if (countDownTimer != null)
            countDownTimer.cancel();
        generateQuestions();
        startTimer();
        resetUI();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart() called");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop() called");
        countDownTimer.cancel();
        databaseReference.removeEventListener(valueEventListener);
        super.onStop();
    }

    private void resetUI() {
        leaderBoardButton.setVisibility(View.INVISIBLE);
        finalScoreTextView.setVisibility(View.INVISIBLE);
        scoreTextView.setText("0");
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
        //gameOverFrameLayout.setVisibility(View.VISIBLE);
        leaderBoardButton.setVisibility(View.VISIBLE);
        finalScoreTextView.setVisibility(View.VISIBLE);
        answerTextView.setVisibility(View.INVISIBLE);
        finalScoreTextView.setText("Your score: " + scoreTextView.getText());
        timerTextView.setText("0s");
        for (int i = 0; i < answersGridButtons.getChildCount(); i++) {
            answersGridButtons.getChildAt(i).setEnabled(false);
        }
        updateScore();
    }

    private void updateScore() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            int latestScore = Integer.parseInt(scoreTextView.getText().toString());
            Map<String, Object> map = new HashMap<>();
            map.put("gamesPlayed", ++storedGamesPlayed);
            if (latestScore > storedScore) {
                map.put("score", latestScore);
                Log.d(TAG, "score updated " + scoreTextView.getText().toString());
            }
            if (uid != null)
                databaseReference.child(uid).updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Firebase: new high score is updated");
                            }
                        });
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
        scoreTextView.setText(String.valueOf(score));
        generateQuestions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            countDownTimer.cancel();
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Intent intent = new Intent(GameActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause() called");
        super.onPause();
    }
}
