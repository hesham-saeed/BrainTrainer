package com.example.braintrainer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.braintrainer.Model.User;
import com.example.braintrainer.Utils.ViewUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private TextView profileNameTextView;
    private ImageView profileAvatarImageView;
    private TextView profileScoreTextView;
    private TextView profileTauntingTextView;
    private Button playButton;
    private AlertDialog alertDialog;
    private ViewGroup profileActivityContainer;
    private boolean firstRunPassed = false;
    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        playButton = findViewById(R.id.profile_play_button);

        firebaseAuth = FirebaseAuth.getInstance();

        profileNameTextView = findViewById(R.id.profile_user_name_text_view);
        profileAvatarImageView = findViewById(R.id.profile_user_image_view);
        profileScoreTextView = findViewById(R.id.profile_user_score_text_view);
        profileTauntingTextView = findViewById(R.id.profile_user_taunt_text_view);

        Typeface firaMonoFont = Typeface.createFromAsset(getAssets(), "fonts/fira-mono-bold.otf");

        profileNameTextView.setTypeface(firaMonoFont);
        profileScoreTextView.setTypeface(firaMonoFont);
        profileTauntingTextView.setTypeface(firaMonoFont);

        profileActivityContainer = findViewById(R.id.profile_activity_container);
        profileActivityContainer.setVisibility(View.INVISIBLE);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null)
                    alertDialog.dismiss();
                Intent intent = new Intent(ProfileActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        readProfileDataFromDatabase();
        Log.d(TAG, "onCreate() called");
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart() called");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        super.onDestroy();
    }

    private void readProfileDataFromDatabase() {
        if (alertDialog == null)
            alertDialog = ViewUtils.createAlertDialog(this, "Loading profile data...");
        alertDialog.show();
        final String uid = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (uid != null) {
                    User user = dataSnapshot.child("Users").child(uid).getValue(User.class);
                    profileNameTextView.setText("Welcome, \n" + user.getFullName());
                    int resId = getResources().getIdentifier("avatar" + user.getAvatarNo()
                            , "drawable", "com.example.braintrainer");
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
                    profileAvatarImageView.setImageBitmap(bitmap);
                    String userScore = getString(R.string.profile_user_score_text_message
                            , String.valueOf(user.getScore()));
                    profileScoreTextView.setText(userScore);
                    alertDialog.hide();
                    profileActivityContainer.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onDataChange() called");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                alertDialog.hide();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume() called");
        Log.d(TAG, "FirstRunPassed" + firstRunPassed);
        super.onResume();
        if (firstRunPassed)
            readProfileDataFromDatabase();
        if (!firstRunPassed)
            firstRunPassed = true;
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop() called");
        if (alertDialog != null)
            alertDialog.dismiss();
        databaseReference.removeEventListener(valueEventListener);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
