package com.IrisBICS.lonelysg.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.IrisBICS.lonelysg.Utils.AppController;
import com.IrisBICS.lonelysg.Models.Invitation;
import com.IrisBICS.lonelysg.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityIndividualUserInvitation extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    private Uri imageUri;
    private CircleImageView userInvImage;
    private Button editInvitation, deleteInvitation, back;
    private TextView activityTitle, activityDateTime,activityDesc, activityLocation;
    private Invitation invitation;
    private String invitationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_user_invitation_ui);

        Intent receivedIntent = getIntent();
        invitationID = receivedIntent.getStringExtra("invitationID");
        invitation = new Invitation("","","","","","","Loading...",invitationID,"","","",imageUri);

        back = findViewById(R.id.backButton);
        activityDateTime = findViewById(R.id.activityDateTime);
        activityDesc = findViewById(R.id.activityDesc);
        activityTitle = findViewById(R.id.activityTitle);
        userInvImage = findViewById(R.id.indUserInvImage);
        activityLocation = findViewById(R.id.activityLocation);
        editInvitation = findViewById(R.id.editInvitation);
        deleteInvitation = findViewById(R.id.deleteInvitation);

        back.setOnClickListener(this);
        editInvitation.setOnClickListener(this);
        deleteInvitation.setOnClickListener(this);

        updateTextView();
        getInvitation();

    }

    private void getInvitation() {
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
                            invitation.setLatitude(response.getString("Latitude"));
                            invitation.setLongitude(response.getString("Longitude"));
                            invitation.setInvitationID(invitationID);
                            if (response.has("Image")!=false) {
                                String InvPicUri = response.getString("Image");
                                imageUri = Uri.parse(InvPicUri);
                                invitation.setInvPic(imageUri);
                            }
                            updateTextView();
                            //MAP
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(ActivityIndividualUserInvitation.this);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Get Invitation", error.toString());
                    }
                });
        AppController.getInstance(this).addToRequestQueue(getInvitationRequest);
    }

    private void deleteInvitation() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/InvitationsDAO/deleteInvitation/"+invitationID;
        StringRequest deleteInvitationRequest = new StringRequest(com.android.volley.Request.Method.DELETE,URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Toast.makeText(ActivityIndividualUserInvitation.this, "Invitation deleted.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ActivityUserInvitations.class);
                startActivity(intent);
                finish();
            }
        },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) { }
            }
        );
        AppController.getInstance(this).addToRequestQueue(deleteInvitationRequest);
    }

    public void updateTextView(){
        activityDateTime.setText(invitation.getDate()+" "+invitation.getStartTime()+" - " +invitation.getEndTime());
        activityTitle.setText(invitation.getTitle());
        activityDesc.setText(invitation.getDesc());

        if (invitation.getInvPic()!=null) {
            Picasso.get().load(invitation.getInvPic()).into(userInvImage);
        }

        activityLocation.setText(invitation.getLocationName());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(Double.parseDouble(invitation.getLatitude()),Double.parseDouble(invitation.getLongitude()));
        googleMap.addMarker(new MarkerOptions().position(latLng)
                .title(invitation.getLocationName()));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.backButton :
                intent = new Intent(getApplicationContext(), ActivityUserInvitations.class);
                startActivity(intent);
                finish();
                break;

            case R.id.editInvitation :
                intent = new Intent(getApplicationContext(), ActivityEditInvitation.class);
                intent.putExtra("invitationID", invitationID);
                startActivity(intent);
                finish();
                break;

            case R.id.deleteInvitation :
                deleteInvitation();
                break;

            default :
                break;
        }
    }
}
