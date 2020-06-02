package com.IrisBICS.lonelysg.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.IrisBICS.lonelysg.Utils.AppController;
import com.IrisBICS.lonelysg.Models.Invitation;
import com.IrisBICS.lonelysg.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityEditInvitation extends AppCompatActivity implements View.OnClickListener, PlaceSelectionListener {

    private EditText editInvTitle;
    private EditText editInvDesc;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private StorageReference mStorage = FirebaseStorage.getInstance().getReference("invitations");

    private Invitation invitation = new Invitation();
    private String invitationID;

    // For dropdown box
    private Spinner editInvCategory;
    String categories[] = {"Choose your invitation category", "Games", "Food and Drinks", "Movies", "Sports", "Study", "Others"};
    ArrayAdapter<String >arrayAdapter;

    private Button confirmButton, cancelButton, editStartTime, editEndTime, editInvDate;
    private String dateString,startTimeString,endTimeString,locationName,longitude,latitude;
    private CircleImageView editInvPic;
    private Uri imageUri;
    private Uri downloadInvPicUri;
    private Task<Uri> downloadUrl;
    private static final int PICK_IMAGE = 1;

    Place location;
    AutocompleteSupportFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_invitation_ui);

        editInvTitle = findViewById(R.id.editTitle);
        editInvDesc = findViewById(R.id.editDesc);
        editInvDate = findViewById(R.id.newDatePick);
        editStartTime = findViewById(R.id.newStartTimePick);
        editEndTime = findViewById(R.id.newEndTimePick);
        editInvPic = findViewById(R.id.editInvitationPic);

        Intent receivedIntent = getIntent();
        invitationID = receivedIntent.getStringExtra("invitationID");
        setInvitationHint(invitationID);

        // For dropdown box (category selection)
        editInvCategory = findViewById(R.id.editCategoryDropBox);
        arrayAdapter = new ArrayAdapter<String>(ActivityEditInvitation.this, android.R.layout.simple_list_item_1, categories);
        editInvCategory.setAdapter(arrayAdapter);

        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelEditButton);

        // For invitation pic selection
        editInvPic.setOnClickListener(this);
        // For date selection
        editInvDate.setOnClickListener(this);
        // For time selection
        editStartTime.setOnClickListener(this);
        editEndTime.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        //Places API
        Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
        PlacesClient placesClient = Places.createClient(this);
        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setCountries("SG");
        autocompleteFragment.setOnPlaceSelectedListener(this);

    }

    private void updateInvWithPic() {
        final StorageReference fileRef = mStorage.child(invitationID+ "." + getFileExtension(imageUri));

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
                    downloadInvPicUri = task.getResult();
                    try {
                        JSONObject jsonBody = new JSONObject();
                        if (!(editInvTitle.getText().toString().matches(""))) {
                            jsonBody.put("Title", editInvTitle.getText());
                        }
                        if (!editInvCategory.getSelectedItem().toString().matches("Choose your invitation category")) {
                            jsonBody.put("Category", editInvCategory.getSelectedItem().toString());
                        }
                        if (!(editInvDesc.getText().toString().matches(""))) {
                            jsonBody.put("Description", editInvDesc.getText());
                        }

                        if (!(latitude==null)){
                            jsonBody.put("Location", locationName);
                            jsonBody.put("Latitude", latitude);
                            jsonBody.put("Longitude", longitude);
                        }

                        jsonBody.put("Start Time", editStartTime.getText());
                        jsonBody.put("End Time", editEndTime.getText());
                        jsonBody.put("Date", editInvDate.getText());
                        jsonBody.put("Image",downloadInvPicUri.toString());
                        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/InvitationsDAO/updateInvitation/" + invitationID;
                        JsonObjectRequest updateInvitationRequest = new JsonObjectRequest(Request.Method.PUT, URL, jsonBody,
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
                        AppController.getInstance(ActivityEditInvitation.this).addToRequestQueue(updateInvitationRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateInvWithoutPic() {
        try {
            JSONObject jsonBody = new JSONObject();
            if (!(editInvTitle.getText().toString().matches(""))) {
                jsonBody.put("Title", editInvTitle.getText());
            }
          
            if (!editInvCategory.getSelectedItem().toString().matches("Choose your invitation category")) {
                jsonBody.put("Category", editInvCategory.getSelectedItem().toString());
            }
            if (!(editInvDesc.getText().toString().matches(""))) {
                jsonBody.put("Description", editInvDesc.getText());
            }

            if (!(latitude==null)){
                jsonBody.put("Location", locationName);
                jsonBody.put("Latitude", latitude);
                jsonBody.put("Longitude", longitude);
            }

            jsonBody.put("Start Time", editStartTime.getText());
            jsonBody.put("End Time", editEndTime.getText());
            jsonBody.put("Date", editInvDate.getText());
            String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/InvitationsDAO/updateInvitation/"+invitationID;
            JsonObjectRequest updateInvitationRequest = new JsonObjectRequest(Request.Method.PUT, URL, jsonBody,
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
            AppController.getInstance(ActivityEditInvitation.this).addToRequestQueue(updateInvitationRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setInvitationHint(String invitationID) {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/InvitationsDAO/getInvitation/"+invitationID;

        JsonObjectRequest getInvitationRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            invitation.setCategory(response.getString("Category"));
                            invitation.setTitle(response.getString("Title"));
                            invitation.setStartTime(response.getString("Start Time"));
                            invitation.setEndTime(response.getString("End Time"));
                            invitation.setHost(response.getString("Host"));
                            invitation.setDesc(response.getString("Description"));
                            invitation.setDate(response.getString("Date"));
                            invitation.setLocationName(response.getString("Location"));
                            if (response.has("Image")!=false) {
                                String invPicUri = response.getString("Image");
                                Uri oldInvPicUri = Uri.parse(invPicUri);
                                invitation.setInvPic(oldInvPicUri);
                            }
                            editInvTitle.setHint(invitation.getTitle());
                            editInvDesc.setHint(invitation.getDesc());
                            editStartTime.setText(invitation.getStartTime());
                            editEndTime.setText(invitation.getEndTime());
                            editInvDate.setText(invitation.getDate());
                            autocompleteFragment.setHint(invitation.getLocationName());
                            if (invitation.getInvPic()!=null) {
                                Picasso.get().load(invitation.getInvPic()).into(editInvPic);
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
        AppController.getInstance(this).addToRequestQueue(getInvitationRequest);

    }

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
            Picasso.get().load(imageUri).into(editInvPic);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Calendar calendar;
        TimePickerDialog timePickerDialog;
        switch (v.getId()) {
            case R.id.cancelEditButton :
                Toast.makeText(ActivityEditInvitation.this, "Cancelled", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, ActivityIndividualUserInvitation.class);
                intent.putExtra("invitationID", invitationID);
                startActivity(intent);
                finish();
                break;

            case R.id.confirmButton :
                if (imageUri != null) {
                    updateInvWithPic();
                }
                else {updateInvWithoutPic();}
                intent = new Intent(this, ActivityIndividualUserInvitation.class);
                intent.putExtra("invitationID", invitationID);
                startActivity(intent);
                finish();
                break;

            case R.id.newStartTimePick:
                calendar = Calendar.getInstance();
                // Current time shown when button is clicked
                int startHour = calendar.get(Calendar.HOUR);
                int startMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(ActivityEditInvitation.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        if (minute<10)
                            startTimeString = hour + ":0" + minute;
                        else
                            startTimeString = hour + ":" + minute;
                        editStartTime.setText(startTimeString);
                    }
                }, startHour, startMinute, true);
                timePickerDialog.show();
                break;

            case R.id.newEndTimePick:
                calendar = Calendar.getInstance();
                // Current time shown when button is clicked
                int endHour = calendar.get(Calendar.HOUR);
                int endMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(ActivityEditInvitation.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        if (minute<10)
                            endTimeString = hour + ":0" + minute;
                        else
                            endTimeString = hour + ":" + minute;
                        editEndTime.setText(endTimeString);
                    }
                }, endHour, endMinute, true);
                timePickerDialog.show();
                break;

            case R.id.newDatePick:
                calendar = Calendar.getInstance();
                // Current date shown when button is clicked
                int YEAR = calendar.get(Calendar.YEAR);
                int MONTH = calendar.get(Calendar.MONTH); // Month 0 is January
                int DATE = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityEditInvitation.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        int correctMonth = month+1;
                        dateString = year + "/" + correctMonth + "/" + date;
                        editInvDate.setText(dateString);
                    }
                }, YEAR, MONTH, DATE);

                datePickerDialog.show();
                break;

            case R.id.editInvitationPic:
                openImageChooser();
                break;

            default :
                break;
        }
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        location = place;
        locationName = location.getName();
        LatLng loc = location.getLatLng();
        latitude = String.valueOf(loc.latitude);
        longitude = String.valueOf(loc.longitude);
        Toast.makeText(ActivityEditInvitation.this, "Location is: " + place.getName(), Toast.LENGTH_SHORT).show();
        Log.i("Create Invitation UI", "Place: " + place.getName() + ", " + place.getId());
    }

    @Override
    public void onError(@NonNull Status status) {
        // TODO: Handle the error.
        Log.i("Create Invitation UI", "An error occurred: " + status);
    }
}