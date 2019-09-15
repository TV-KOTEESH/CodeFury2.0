package com.example.codex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    String event, deptSem;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // set the view now
        setContentView(R.layout.login_activity);

        event = getIntent().getStringExtra("event");
        deptSem = getIntent().getStringExtra("deptSem");

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.sign_up);
        btnLogin = (Button) findViewById(R.id.login);
        // btnReset = (Button) findViewById(R.id.reset_password_btn);
        sharedPreferences = this.getSharedPreferences("RootUser",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        String email,password;
        if(sharedPreferences.contains("email"))
            if(!(email = sharedPreferences.getString("email","")).equalsIgnoreCase(""))
                inputEmail.setText(email);

        if(sharedPreferences.contains("password"))
            if(!(password = sharedPreferences.getString("password","")).equalsIgnoreCase(""))
                inputPassword.setText(password);

        //Toast.makeText(LoginPlacements.this,sharedPreferences.getString("email","")+sharedPreferences.getString("password",""),Toast.LENGTH_LONG).show();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });


        /*btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });*/


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError("Password too short");
                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this, "Sign - in Failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user.isEmailVerified())
                                    {
                                        Intent i = new Intent(LoginActivity.this, GST.class);
                                        i.putExtra("email", email);
                                        startActivity(i);

                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this,"Email has not been verified",Toast.LENGTH_SHORT).show();
                                        // email is not verified, so just prompt the message to the user and restart this activity.
                                        // NOTE: don't forget to log out the user.
                                        FirebaseAuth.getInstance().signOut();
                                        //restart this activity
                                    }

                                }
                            }
                        });
            }
        });
    }
}

