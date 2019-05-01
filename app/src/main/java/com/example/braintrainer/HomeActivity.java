package com.example.braintrainer;

import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.braintrainer.Utils.ViewUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private Button registerButton;
    private Button loginButton;
    private Button contOfflineButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    private AlertDialog alertDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = firebaseAuth.getInstance();

        registerButton = findViewById(R.id.btn_register);
        loginButton = findViewById(R.id.btn_login);
        contOfflineButton = findViewById(R.id.btn_offline);
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        final AlphaAnimation buttonClickAnimation = new AlphaAnimation(1F, 0.7F);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClickAnimation);
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                if (alertDialog == null)
                    alertDialog = ViewUtils.createAlertDialog(HomeActivity.this,
                            "Please wait this may take a moment");
                alertDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() != null)
                                task.getException().printStackTrace();
                            try {
                                throw task.getException();
                            } catch (FirebaseNetworkException e) {
                                Toast.makeText(HomeActivity.this,
                                        "Network error, are you connected to the internet?",
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                String message = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                        alertDialog.hide();
                    }
                });
            }
        });
        contOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        alertDialog = null;
    }
}
