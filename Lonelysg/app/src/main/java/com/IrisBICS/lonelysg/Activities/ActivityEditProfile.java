package com.IrisBICS.lonelysg.Activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.IrisBICS.lonelysg.Utils.AppController;
import com.IrisBICS.lonelysg.Models.User;
import com.IrisBICS.lonelysg.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityEditProfile extends Activity implements View.OnClickListener {

    private CircleImageView editProfilePic;
    private Uri imageUri, downloadProfileUri;
    private EditText editName, editAge, editOccupation, editInterest;
    private String name, age, occupation, interests, gender;
    private Button confirmButton, cancelButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final int PICK_IMAGE = 1;
    private String userID = mAuth.getCurrentUser().getUid();
    private Task<Uri> downloadUrl;
    private User user = new User();
    private StorageReference mStorageRef;

    // For dropdown box
    Spinner dropdownbox;
    String categories[] = {"Choose your Gender", "Male", "Female", "Non-binary", "Transgender", "Intersex", "Gender Non-Conforming", "Others"};
    ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_ui);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        editName = findViewById(R.id.editProfileName);
        editAge = findViewById(R.id.editProfileAge);
        editOccupation = findViewById(R.id.editProfileOccupation);
        editInterest = findViewById(R.id.editProfileInterests);

        setProfileHint(userID);

        // For dropdown box (category selection)
        dropdownbox = findViewById(R.id.genderCategoryDropBox);
        arrayAdapter = new ArrayAdapter<>(ActivityEditProfile.this, android.R.layout.simple_list_item_1, categories);
        dropdownbox.setAdapter(arrayAdapter);

        editProfilePic = findViewById(R.id.editProfilePic);
        confirmButton = findViewById(R.id.editProfileConfirmButton);
        cancelButton = findViewById(R.id.editProfileCancelButton);

        editProfilePic.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    private void updateProfileWithPic() {
        final StorageReference fileRef = mStorageRef.child(userID+ "." + getFileExtension(imageUri));

        UploadTask uploadTask = fileRef.putFile(imageUri);
        downloadUrl = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadProfileUri = task.getResult();
                    try {
                        JSONObject jsonBody = new JSONObject();

                        jsonBody.put("username", name);
                        jsonBody.put("age", age);
                        jsonBody.put("occupation", occupation);
                        jsonBody.put("interests", interests);
                        if (!gender.matches("Choose your Gender")) {
                            jsonBody.put("gender", dropdownbox.getSelectedItem().toString());
                        }
                        jsonBody.put("image",downloadProfileUri.toString());
                        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/UsersDAO/updateUser/"+userID;

                        JsonObjectRequest updateUserRequest = new JsonObjectRequest(Request.Method.PUT, URL, jsonBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(ActivityEditProfile.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", error.toString());
                                Toast.makeText(ActivityEditProfile.this, "Profile update failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AppController.getInstance(ActivityEditProfile.this).addToRequestQueue(updateUserRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateProfileWithoutPic(){
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("username", name);
            jsonBody.put("age", age);
            jsonBody.put("occupation", occupation);
            jsonBody.put("interests", interests);
            if (!gender.matches("Choose your Gender")) {
                jsonBody.put("gender", dropdownbox.getSelectedItem().toString());
            }
            String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/UsersDAO/updateUser/"+userID;

            JsonObjectRequest updateUserRequest = new JsonObjectRequest(Request.Method.PUT, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(ActivityEditProfile.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());
                    Toast.makeText(ActivityEditProfile.this, "Profile update failed.", Toast.LENGTH_SHORT).show();
                }
            });
            AppController.getInstance(ActivityEditProfile.this).addToRequestQueue(updateUserRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setProfileHint(String userID) {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/UsersDAO/getUser/"+userID;
        JsonObjectRequest getUserRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user.setUsername(response.getString("username"));
                            user.setGender(response.getString("gender"));
                            user.setAge(response.getString("age"));
                            user.setOccupation(response.getString("occupation"));
                            user.setInterests(response.getString("interests"));
                            if (response.has("image")!=false) {
                                String profilePicUri = response.getString("image");
                                Uri profileUri = Uri.parse(profilePicUri);
                                user.setProfilePic(profileUri);
                            }
                            editName.setHint(user.getUsername());
                            editAge.setHint(user.getAge());
                            editOccupation.setHint(user.getOccupation());
                            editInterest.setHint(user.getInterests());
                            if (user.getProfilePic()!=null) {
                                Picasso.get().load(user.getProfilePic()).into(editProfilePic);
                            }
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
        AppController.getInstance(this).addToRequestQueue(getUserRequest);
    };

    private void openImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(editProfilePic);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editProfileCancelButton :
                Toast.makeText(ActivityEditProfile.this, "Cancelled", Toast.LENGTH_SHORT).show();
                finish();
                break;

            case R.id.editProfileConfirmButton:
                name = editName.getText().toString();
                age = editAge.getText().toString();
                occupation = editOccupation.getText().toString();
                interests = editInterest.getText().toString();
                gender = dropdownbox.getSelectedItem().toString();

                if (name.matches("")){
                    name = editName.getHint().toString();
                }
                if (age.matches("")){
                    age = editAge.getHint().toString();
                }
                if (occupation.matches("")){
                    occupation = editOccupation.getHint().toString();
                }
                if (interests.matches("")){
                    interests = editInterest.getHint().toString();
                }

                if (name.matches("Type New Name")){
                    Toast.makeText(this, "Enter username!", Toast.LENGTH_SHORT).show();
                }
                else if ((gender.matches("Choose your Gender")&(user.getGender()==null))){
                    Toast.makeText(this, "Select gender!", Toast.LENGTH_SHORT).show();
                }
                else if (age.matches("Type New Age")){
                    Toast.makeText(this, "Enter age!", Toast.LENGTH_SHORT).show();
                }
                else if (occupation.matches("Type New Occupation")){
                    Toast.makeText(this, "Enter occupation!", Toast.LENGTH_SHORT).show();
                }
                else if (interests.matches("Type New Interest")){
                    Toast.makeText(this, "Enter interests!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (imageUri != null) { updateProfileWithPic(); }
                    else {updateProfileWithoutPic();}
                    Intent i = new Intent (ActivityEditProfile.this, ActivityNavigationBar.class);
                    startActivity(i);
                }

                break;

            case R.id.editProfilePic :
                openImageChooser();
                break;

            default :
                break;
        }
    }
}