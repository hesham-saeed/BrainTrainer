package com.example.braintrainer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";
    private TextView titleTextView;
    private Button registerButton;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private checkInternetTask checkInternetTask;
    private boolean connectedToInternet;
    private Toast connectionToast;
    private boolean firstRun = true;

    private FirebaseAuth firebaseAuth;

    private class checkInternetTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground() called");
            return NetworkUtils.isOnline();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.d(TAG, "onPostExecute() called and returned " + aBoolean.toString());
            connectedToInternet = aBoolean;
            if (firstRun)
                firstRun = false;
            else {
                if (connectionToast != null)
                    connectionToast.cancel();
                if (!aBoolean) {
                    progressBar.setVisibility(View.INVISIBLE);
                    connectionToast = Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT);
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

        titleTextView = findViewById(R.id.tv_title);
        fullNameEditText = findViewById(R.id.et_reg_name);
        emailEditText = findViewById(R.id.et_reg_email);
        passwordEditText = findViewById(R.id.et_reg_password);
        progressBar = findViewById(R.id.reg_progress_bar);

        titleTextView = findViewById(R.id.tv_title);
        Typeface firaMonoFont = Typeface.createFromAsset(getAssets(), "fonts/fira-mono-bold.otf");
        titleTextView.setTypeface(firaMonoFont);

        checkInternetTask = new checkInternetTask();
        checkInternetTask.execute();

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.btn_reg_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectedToInternet == false && checkInternetTask.getStatus() != AsyncTask.Status.RUNNING) {
                    Log.d(TAG, "executing a new InternetTask");
                    progressBar.setVisibility(View.VISIBLE);
                    checkInternetTask = new checkInternetTask();
                    checkInternetTask.execute();
                } else {
                    createAccount();
                }
            }
        });
    }

    private void createAccount(){
        String fullName = fullNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
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
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            } finally {
                                Log.d(TAG, task.getException().getMessage());
                            }
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    protected void onStop() {
        checkInternetTask.cancel(true);
        super.onStop();
    }
}
