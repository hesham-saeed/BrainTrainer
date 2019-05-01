package com.example.braintrainer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.braintrainer.Model.User;
import com.example.braintrainer.Utils.NetworkUtils;
import com.example.braintrainer.Utils.ViewUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";
    private TextView titleTextView;
    private Button registerButton;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private checkInternetTask checkInternetTask;
    private boolean connectedToInternet;
    private Toast connectionToast;
    private boolean firstRun = true;
    private AlertDialog alertDialog;
    private FirebaseAuth firebaseAuth;
    private GridLayout avatarsGridLayout;
    private int avatarSelected = 1;

    public void shadeAvatar(View view) {
        for (int i = 0; i < avatarsGridLayout.getChildCount(); i++) {
            avatarsGridLayout.getChildAt(i).setAlpha(1f);
        }
        view.setAlpha(0.5f);
        avatarSelected = Integer.parseInt(view.getTag().toString());
    }

    private class checkInternetTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground() called");
            return NetworkUtils.isOnline();
        }

        @Override
        protected void onPostExecute(Boolean isOnline) {
            Log.d(TAG, "onPostExecute() called and returned " + isOnline.toString());
            connectedToInternet = isOnline;
            if (firstRun)
                firstRun = false;
            else {
                if (connectionToast != null)
                    connectionToast.cancel();
                if (!isOnline) { // if offline
                    connectionToast = Toast.makeText(getApplicationContext(),
                            "No internet connection",
                            Toast.LENGTH_SHORT);
                    connectionToast.show();
                } else {
                    createAccount();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleTextView = findViewById(R.id.tv_title);
        fullNameEditText = findViewById(R.id.et_reg_name);
        emailEditText = findViewById(R.id.et_reg_email);
        passwordEditText = findViewById(R.id.et_reg_password);
        avatarsGridLayout = findViewById(R.id.avatars_grid_layout);

        titleTextView = findViewById(R.id.tv_title);
        Typeface firaMonoFont = Typeface.createFromAsset(getAssets(), "fonts/fira-mono-bold.otf");
        titleTextView.setTypeface(firaMonoFont);

        checkInternetTask = new checkInternetTask();
        checkInternetTask.execute();

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.btn_reg_register);
        final AlphaAnimation buttonClickAnimation = new AlphaAnimation(1F, 0.7F);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClickAnimation);
                if (!connectedToInternet && checkInternetTask.getStatus() != AsyncTask.Status.RUNNING) {
                    Log.d(TAG, "executing a new InternetTask");
                    checkInternetTask = new checkInternetTask();
                    checkInternetTask.execute();
                } else {
                    createAccount();
                }
            }
        });
    }

    private void createAccount() {
        final String fullName = fullNameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (alertDialog == null)
            alertDialog = ViewUtils.createAlertDialog(this, "Please wait this may take a moment");
        alertDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (alertDialog != null)
                            alertDialog.hide();

                        if (task.isSuccessful() && task.getResult() != null) {
                            String uid = task.getResult().getUser().getUid();
                            addToRealTimeDB(fullName, email, avatarSelected, uid);
                        } else {
                            try {
                                if (task.getException() != null)
                                    throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                passwordEditText.setError(e.getMessage());
                                passwordEditText.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                emailEditText.setError(e.getMessage());
                                emailEditText.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                emailEditText.setError(e.getMessage());
                                emailEditText.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } finally {
                                Log.d(TAG, task.getException().getMessage());
                            }
                        }
                        //progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    protected void onStop() {
        checkInternetTask.cancel(true);
        if (alertDialog != null)
            alertDialog.dismiss();
        super.onStop();
    }

    private void addToRealTimeDB(String fullName, String email, int avatarSelected, final String uid) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        User newUser = new User(fullName, email, avatarSelected);
        usersRef.child(uid).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(RegistrationActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this,
                        "Failed to move data to realtimeDB",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
