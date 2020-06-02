package com.IrisBICS.lonelysg.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.IrisBICS.lonelysg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout loginStuff, passwordSignUpBar;

    private EditText username;
    private EditText password;
    private Button signIn;
    private Button signUp;
    private Button forgotPW;
    private FirebaseAuth mAuth;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loginStuff.setVisibility(View.VISIBLE);
            passwordSignUpBar.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        //If logged in, go straight to next page
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            Toast.makeText(ActivityLogin.this, "You are logged in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ActivityLogin.this, ActivityNavigationBar.class);
            startActivity(intent);
        } else {
            Toast.makeText(ActivityLogin.this, "Please log in.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);

        loginStuff = findViewById(R.id.loginStuff);
        passwordSignUpBar = findViewById(R.id.passwordSignUpBar);

        ImageView logo = findViewById(R.id.logo);
        logo.animate().alpha(0f).setDuration(2600);

        handler.postDelayed(runnable, 3000); // Timeout for the splash

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.usernameInput);
        password = findViewById(R.id.passwordInput);
        signIn = findViewById(R.id.signInButton);
        signUp = findViewById(R.id.signUpButton);
        forgotPW = findViewById(R.id.forgotPasswordButton);

        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotPW.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        String email;
        switch (v.getId()) {
            case R.id.signInButton :
                email = username.getText().toString();
                String pwd = password.getText().toString();
                if (!email.isEmpty() && !pwd.isEmpty()) {
                    //FIREBASE LOGIN AUTHENTICATION
                    mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginUI", "signInWithEmail:success");
                                Toast.makeText(ActivityLogin.this, "Sign in success!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityLogin.this, ActivityNavigationBar.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginUI", "signInWithEmail:failure", task.getException());
                                Toast.makeText(ActivityLogin.this, task.getException().getLocalizedMessage()+ " Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;

            case R.id.signUpButton :
                Intent intent = new Intent(ActivityLogin.this, ActivitySignUp.class);
                startActivity(intent);
                break;

            case R.id.forgotPasswordButton:
                email = username.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(ActivityLogin.this, "Please enter your registered email first.", Toast.LENGTH_SHORT).show();
                }
                if (!email.isEmpty()) {
                    //call password reset api
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("LoginUI", "Email sent.");
                                Toast.makeText(ActivityLogin.this, "Password reset sent to your email!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityLogin.this, "Please make sure email is already registered.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ActivityLogin.this, "Error occurred :(", Toast.LENGTH_SHORT).show();
                }

            default :
                break;
        }
    }

}