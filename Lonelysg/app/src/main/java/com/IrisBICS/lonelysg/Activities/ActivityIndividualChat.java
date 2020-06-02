package com.IrisBICS.lonelysg.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.IrisBICS.lonelysg.Adapters.ChatRecyclerAdapter;
import com.IrisBICS.lonelysg.Utils.AppController;
import com.IrisBICS.lonelysg.Utils.FirebaseAuthHelper;
import com.IrisBICS.lonelysg.Models.Message;
import com.IrisBICS.lonelysg.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityIndividualChat extends AppCompatActivity implements View.OnClickListener {

    String currentUserID = FirebaseAuthHelper.getCurrentUserID();

    private RecyclerView recyclerView;
    private EditText typeMessage;
    private Button sendButton, back;
    private TextView receiverName;
    private ImageButton refresh;
    private String receiverID, receiver;
    private ArrayList<Message> messages;
    ChatRecyclerAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat_ui);

        Intent receivedIntent = getIntent();
        Bundle extras = receivedIntent.getExtras();
        receiverID = extras.getString("receiver_id");
        receiver = extras.getString("receiver_name");
        messages = new ArrayList<>();

        receiverName = findViewById(R.id.receiverName);
        receiverName.setText(receiver);
        typeMessage = findViewById(R.id.typeMessage);
        sendButton = findViewById(R.id.sendButton);
        refresh = findViewById(R.id.refreshButton);
        recyclerView = findViewById(R.id.chatView);
        back = findViewById(R.id.backButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatRecyclerAdapter(this, messages);
        recyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(this);
        back.setOnClickListener(this);
        refresh.setOnClickListener(this);

        getMessages();

    }

    private void getMessages() {
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/MessagesDAO/getMessages/"+currentUserID+"/"+receiverID;

        final JsonArrayRequest getMessagesRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Message message = new Message();
                        message.setMessage(jsonObject.getString("Message"));
                        message.setReceiver(jsonObject.getString("Receiver"));
                        message.setSender(jsonObject.getString("Sender"));
                        messages.add(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        AppController.getInstance(this).addToRequestQueue(getMessagesRequest);
    }

    private void sendMessage(final String text) {
        try {
            String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/MessagesDAO/sendMessage";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("Message", text);
            jsonBody.put("Receiver", receiverID);
            jsonBody.put("Sender", currentUserID);

            JsonObjectRequest sendMessageRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    Message message = new Message();
                    message.setMessage(text);
                    message.setReceiver(receiverID);
                    message.setSender(currentUserID);
                    messages.add(message);
                    chatAdapter.notifyDataSetChanged();
                    sendChatNotif();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());
                    System.out.println("error occurred");
                }
            });
            AppController.getInstance(this).addToRequestQueue(sendMessageRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendChatNotif(){
        String URL = "https://us-central1-lonely-4a186.cloudfunctions.net/app/NotificationsAPI/sendChatNotif/"+receiverID;
        StringRequest sendChatNotifRequest = new StringRequest(com.android.volley.Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ActivityIndividualChat.this,"C!",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ChatNotif", error.toString());
            }
        });
        AppController.getInstance(this).addToRequestQueue(sendChatNotifRequest);
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

            case R.id.sendButton:
                String text = typeMessage.getText().toString().trim();
                if ((text!="")&(text!=null)){
                    sendMessage(text);
                    typeMessage.setText("");
                }
                break;

            default :
                break;
        }
    }
}




