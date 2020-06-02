package com.IrisBICS.lonelysg.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.IrisBICS.lonelysg.Adapters.RequestListAdapter;
import com.IrisBICS.lonelysg.Utils.AppController;
import com.IrisBICS.lonelysg.Utils.FirebaseAuthHelper;
import com.IrisBICS.lonelysg.Models.Request;
import com.IrisBICS.lonelysg.Models.User;
import com.IrisBICS.lonelysg.R;
import com.IrisBICS.lonelysg.Utils.RequestCancelDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityPendingRequests extends AppCompatActivity implements RequestCancelDialog.DialogListener, View.OnClickListener, AdapterView.OnItemClickListener{

    private ArrayList<Request> requests;
    private ArrayList<User> hosts;
    private ListView pendingRequestsList;
    private int clickedPos = -1;
    private Button back;

    String currentUserID = FirebaseAuthHelper.getCurrentUserID();
    RequestListAdapter requestListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests_ui);

        requests = new ArrayList<>();
        hosts = new ArrayList<>();

        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);

        TextView emptyText = findViewById(android.R.id.empty);
        pendingRequestsList = findViewById(R.id.pendingRequestsListView);
        requestListAdapter = new RequestListAdapter(this, requests, hosts,"pending");
        pendingRequestsList.setAdapter(requestListAdapter);
        pendingRequestsList.setEmptyView(emptyText);

        pendingRequestsList.setClickable(true);
        pendingRequestsList.setOnItemClickListener(this);

        getPendingRequests();
    }

    private void getPendingRequests() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/RequestsDAO/getPendingRequests/"+currentUserID;

        final JsonArrayRequest getPendingRequestsRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    User user = new User();
                    hosts.add(user);
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Request request = new Request();
                        request.setHost(jsonObject.getString("Host"));
                        request.setInvitation(jsonObject.getString("Invitation"));
                        request.setParticipant(jsonObject.getString("Participant"));
                        request.setRequestID(jsonObject.getString("RequestID"));
                        requests.add(request);
                        getHost(request.getHost(),i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                requestListAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        AppController.getInstance(this).addToRequestQueue(getPendingRequestsRequest);
    }

    private void deleteRequest(final String reqID) {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/RequestsDAO/deleteRequest/"+reqID;
        StringRequest deleteRequestRequest = new StringRequest(com.android.volley.Request.Method.DELETE,URL, new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        requests.remove(requests.get(clickedPos));
                        requestListAdapter.notifyDataSetChanged();
                        Toast.makeText(ActivityPendingRequests.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        AppController.getInstance(this).addToRequestQueue(deleteRequestRequest);
    }

    public void openDialog(){
        RequestCancelDialog requestCancelDialog = new RequestCancelDialog();
        requestCancelDialog.show(getSupportFragmentManager(), "Request Cancel Dialog");
    }

    @Override
    public void cancelRequest() {
        if (clickedPos!=-1){
            deleteRequest(requests.get(clickedPos).getRequestID());
        }
    }

    private void getHost(String userID, final int i) {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/UsersDAO/getUser/"+userID;
        JsonObjectRequest getUserProfileRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            User host = new User();
                            host.setUsername(response.getString("username"));
//                            host.setGender(response.getString("gender"));
//                            host.setAge(response.getString("age"));
//                            host.setOccupation(response.getString("occupation"));
//                            host.setInterests(response.getString("interests"));
                            hosts.set(i, host);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        requestListAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                });
        AppController.getInstance(this).addToRequestQueue(getUserProfileRequest);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        clickedPos = i;
        openDialog();
    }
}
