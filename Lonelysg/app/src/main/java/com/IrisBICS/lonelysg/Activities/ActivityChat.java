package com.IrisBICS.lonelysg.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.IrisBICS.lonelysg.Adapters.ChatListAdapter;
import com.IrisBICS.lonelysg.Utils.AppController;
import com.IrisBICS.lonelysg.Utils.FirebaseAuthHelper;
import com.IrisBICS.lonelysg.Models.User;
import com.IrisBICS.lonelysg.R;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityChat extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView chatList;
    private ImageButton refresh;
    private Button back;
    private ArrayList<User> chatUsersList;
    ChatListAdapter chatListAdapter;
    String currentUserID = FirebaseAuthHelper.getCurrentUserID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ui);

        chatUsersList = new ArrayList<>();
        refresh = findViewById(R.id.refreshButton);
        back = findViewById(R.id.backButton);
        TextView emptyText = findViewById(android.R.id.empty);
        chatList = findViewById(R.id.chatList);
        chatListAdapter = new ChatListAdapter(this,chatUsersList);
        chatList.setAdapter(chatListAdapter);
        chatList.setEmptyView(emptyText);
        chatList.setClickable(true);
        chatList.setOnItemClickListener(this);

        refresh.setOnClickListener(this);
        back.setOnClickListener(this);

        getChatUsersList();

    }

    private void getChatUsersList() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/MessagesDAO/getChatUsersList/"+currentUserID;

        final JsonArrayRequest getChatUsersListRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String chatUser = response.getString(i);
                        getChatUsers(chatUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        AppController.getInstance(this).addToRequestQueue(getChatUsersListRequest);
    }

    private void getChatUsers(String userID) {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/UsersDAO/getUser/"+userID;
        JsonObjectRequest getChatUserRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            User receiver = new User();
                            receiver.setUsername(response.getString("username"));
                            receiver.setUserID(response.getString("UserID"));
                            if (response.has("image")!=false) {
                                String profilePicUri = response.getString("image");
                                Uri imageUri = Uri.parse(profilePicUri);
                                receiver.setProfilePic(imageUri);
                            }
                            chatUsersList.add(receiver);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        chatListAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                });
        AppController.getInstance(this).addToRequestQueue(getChatUserRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton :
                finish();
                break;

            case R.id.refreshButton :
                recreate();
                break;

            default :
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent;
        intent = new Intent(ActivityChat.this, ActivityIndividualChat.class);
        Bundle extras = new Bundle();
        extras.putString("receiver_name", chatUsersList.get(i).getUsername());
        extras.putString("receiver_id", chatUsersList.get(i).getUserID());
        intent.putExtras(extras);
        startActivity(intent);
    }
}
