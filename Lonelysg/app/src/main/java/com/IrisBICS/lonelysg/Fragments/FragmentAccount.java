package com.IrisBICS.lonelysg.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.IrisBICS.lonelysg.Activities.ActivityChangePassword;
import com.IrisBICS.lonelysg.Activities.ActivityEditProfile;
import com.IrisBICS.lonelysg.Activities.ActivityLogin;
import com.IrisBICS.lonelysg.Models.User;
import com.IrisBICS.lonelysg.R;
import com.IrisBICS.lonelysg.Utils.AppController;
import com.IrisBICS.lonelysg.Utils.DeleteUserDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentAccount extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, DeleteUserDialog.DialogListener {
    private TextView profileName, profileGender, profileAge, profileOccupation, profileInterest, profileUsername;
    private Uri imageUri;
    private Spinner settingsIcon;
    private CircleImageView profilePic;
    private String settings[] = {"Settings", "Change Password", "Delete Account", "Log Out"};
    private ArrayAdapter<String> arrayAdapter;

    private Button editProfile;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private User user= new User();
    private String currentUser = mAuth.getCurrentUser().getUid();

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_account_ui, container, false);

        profileName = v.findViewById(R.id.accountUsername);
        profileAge = v.findViewById(R.id.accountAge);
        profileGender = v.findViewById(R.id.accountGender);
        profileOccupation = v.findViewById(R.id.accountOccupation);
        profileInterest = v.findViewById(R.id.accountInterests);
        profileUsername = v.findViewById(R.id.userName);
        profilePic = v.findViewById(R.id.accountProfilePic);

        // For dropdown settings icon
        settingsIcon = v.findViewById(R.id.moreSettingsicon);
        arrayAdapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_list_item_1, settings);
        settingsIcon.setAdapter(arrayAdapter);
        settingsIcon.setOnItemSelectedListener(this);

        editProfile = v.findViewById(R.id.editProfileButton);
        editProfile.setOnClickListener(this);

        getUserProfile(currentUser);

        return v;
    }

    private void getUserProfile(String userID) {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/UsersDAO/getUser/"+userID;
        JsonObjectRequest getUserProfileRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user.setUsername(response.getString("username"));
                            user.setGender(response.getString("gender"));
                            user.setAge(response.getString("age"));
                            user.setOccupation(response.getString("occupation"));
                            user.setInterests(response.getString("interests"));
                            user.setPassword(response.getString("password"));
                            if (response.has("image")!=false) {
                                String profilePicUri = response.getString("image");
                                imageUri = Uri.parse(profilePicUri);
                                user.setProfilePic(imageUri);
                            }
                            setUserProfile();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                });
        AppController.getInstance(this.getContext()).addToRequestQueue(getUserProfileRequest);
    }

    private void setUserProfile(){
        profileName.setText(user.getUsername());
        profileGender.setText(user.getGender());
        profileAge.setText(user.getAge());
        profileOccupation.setText(user.getOccupation());
        profileInterest.setText(user.getInterests());
        profileUsername.setText(user.getUsername());
        if (user.getProfilePic()!=null) {
            Picasso.get().load(user.getProfilePic()).into(profilePic);
        }
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent (getContext(), ActivityEditProfile.class);
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String next = adapterView.getItemAtPosition(i).toString();
        Intent intent;
        switch (next) {
            case "Settings":
                break;
            case "Change Password":
                intent = new Intent(getActivity(), ActivityChangePassword.class);
                startActivity(intent);
                break;
            case "Delete Account":
                openDialog();
                break;
            case "Log Out":
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "Logging out!", Toast.LENGTH_SHORT).show();
                intent = new Intent(getActivity(), ActivityLogin.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void openDialog(){
        DeleteUserDialog deleteUserDialog = new DeleteUserDialog();
        deleteUserDialog.setTargetFragment(FragmentAccount.this,2);
        deleteUserDialog.show(getActivity().getSupportFragmentManager(), "Delete User Dialog");
    }

    @Override
    public void deleteFirebaseUser(String pw) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), pw);

        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseUser.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            System.out.println("Success delete");
                                            deleteUserMessages();
                                            deleteUserRequests();
                                            deleteUserInvitations();
                                            deleteUser();
                                            Log.d("FragmentAccount", "User account deleted.");
                                            Toast.makeText(getActivity(), "Your account has been deleted.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), ActivityLogin.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                });
    }

    private void deleteUser() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/UsersDAO/deleteUser/"+currentUser;
        StringRequest deleteUserReqest = new StringRequest(com.android.volley.Request.Method.DELETE,URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        AppController.getInstance(this.getContext()).addToRequestQueue(deleteUserReqest);
    }

    private void deleteUserMessages() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/MessagesDAO/deleteUserMessages/"+currentUser;
        StringRequest deleteMessagesRequest = new StringRequest(com.android.volley.Request.Method.DELETE,URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        AppController.getInstance(this.getContext()).addToRequestQueue(deleteMessagesRequest);
    }

    private void deleteUserRequests() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/RequestsDAO/deleteUserRequests/"+currentUser;
        StringRequest deleteRequestsRequest = new StringRequest(com.android.volley.Request.Method.DELETE,URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        AppController.getInstance(this.getContext()).addToRequestQueue(deleteRequestsRequest);
    }

    private void deleteUserInvitations() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/InvitationsDAO/deleteUserInvitations/"+currentUser;
        StringRequest deleteInvitationsRequest = new StringRequest(com.android.volley.Request.Method.DELETE,URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        AppController.getInstance(this.getContext()).addToRequestQueue(deleteInvitationsRequest);
    }

}