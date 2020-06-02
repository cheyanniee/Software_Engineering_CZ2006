package com.IrisBICS.lonelysg.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.IrisBICS.lonelysg.Adapters.InvitationsListAdapter;
import com.IrisBICS.lonelysg.Utils.AppController;
import com.IrisBICS.lonelysg.Utils.FirebaseAuthHelper;
import com.IrisBICS.lonelysg.Models.Invitation;
import com.IrisBICS.lonelysg.R;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityUserInvitations extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView userInvitationsList;
    private Button back;

    private ArrayList<Invitation> userInvitations;
    InvitationsListAdapter invitationsListAdapter;
    String currentUserID = FirebaseAuthHelper.getCurrentUserID();
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_invitations_ui);

        userInvitations = new ArrayList<>();

        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);

        TextView emptyText = findViewById(android.R.id.empty);
        userInvitationsList = findViewById(R.id.userInvitationsListView);
        invitationsListAdapter = new InvitationsListAdapter(this, userInvitations);
        userInvitationsList.setAdapter(invitationsListAdapter);
        userInvitationsList.setEmptyView(emptyText);

        userInvitationsList.setOnItemClickListener(this);

        getUserInvitations();
    }

    private void getUserInvitations() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/InvitationsDAO/getUserInvitations/"+currentUserID;

        final JsonArrayRequest getUserInvitationsRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Invitation invitation = new Invitation();
                        invitation.setTitle(jsonObject.getString("Title"));
                        invitation.setStartTime(jsonObject.getString("Start Time"));
                        invitation.setEndTime(jsonObject.getString("End Time"));
                        invitation.setHost(jsonObject.getString("Host"));
                        invitation.setDesc(jsonObject.getString("Description"));
                        invitation.setDate(jsonObject.getString("Date"));
                        invitation.setCategory(jsonObject.getString("Category"));
                        invitation.setInvitationID(jsonObject.getString("InvitationID"));
                        invitation.setLocationName(jsonObject.getString("Location"));
                        if (jsonObject.has("Image")!=false) {
                            String InvPicUri = jsonObject.getString("Image");
                            imageUri = Uri.parse(InvPicUri);
                            invitation.setInvPic(imageUri);
                        }
                        userInvitations.add(invitation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                invitationsListAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        AppController.getInstance(this).addToRequestQueue(getUserInvitationsRequest);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getApplicationContext(), ActivityIndividualUserInvitation.class);
        intent.putExtra("invitationID", userInvitations.get(i).getInvitationID());
        startActivity(intent);
        finish();
    }
}
