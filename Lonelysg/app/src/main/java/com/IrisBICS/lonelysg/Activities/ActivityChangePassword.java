package com.IrisBICS.lonelysg.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.IrisBICS.lonelysg.R;
import com.IrisBICS.lonelysg.Utils.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityChangePassword extends AppCompatActivity implements View.OnClickListener {
    private EditText oldPasswordInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button changePassword;
    private Button forgotPassword;
    private Button back;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_ui);

        mAuth = FirebaseAuth.getInstance();
        oldPasswordInput = findViewById(R.id.oldPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        changePassword = findViewById(R.id.changePasswordButton);
        forgotPassword = findViewById(R.id.forgotPasswordButton);
        back = findViewById(R.id.backButton);

        changePassword.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        back.setOnClickListener(this);
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changePasswordButton :
                final String oldPw = oldPasswordInput.getText().toString();
                final String newPw = newPasswordInput.getText().toString();
                final String cfmPw = confirmPasswordInput.getText().toString();

                if (!oldPw.isEmpty() && !newPw.isEmpty() && !cfmPw.isEmpty()) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPw);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ActivityChangePassword", "User re-authenticated.");
                                if (newPw.equals(cfmPw)) {
                                    user.updatePassword(newPw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("ActivityChangePassword", "User password updated.");
                                                updatePassword(newPw);
                                            }
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(ActivityChangePassword.this, "Error: New password does not match confirm password", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Log.d("ActivityChangePassword", "Failed to re-authenticate user.");
                                Toast.makeText(ActivityChangePassword.this, "Error: Incorrect existing password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(ActivityChangePassword.this, "Error occured. Please try again!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.forgotPasswordButton :
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ActivityChangePassword", "Email sent.");
                                Toast.makeText(ActivityChangePassword.this, "Password reset sent to your email!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(ActivityChangePassword.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.backButton :
                finish();
                break;

            default :
                break;
        }
    }

    private void updatePassword(String pw) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("password", pw);
            String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/XQ/updateUser/"+mAuth.getCurrentUser().getUid();
            JsonObjectRequest updateUserRequest = new JsonObjectRequest(Request.Method.PUT, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());
                }
            });
            AppController.getInstance(ActivityChangePassword.this).addToRequestQueue(updateUserRequest);
            Toast.makeText(ActivityChangePassword.this, "Password change successful!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
