package com.IrisBICS.lonelysg.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.IrisBICS.lonelysg.R;

public class ActivityManageRequests extends AppCompatActivity implements View.OnClickListener {

    private CardView viewPending, viewReceived;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests_ui);

        back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewPending = findViewById(R.id.pendingRequests);
        viewPending.setOnClickListener(this);

        viewReceived = findViewById(R.id.receivedRequests);
        viewReceived.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.pendingRequests :
                intent = new Intent(ActivityManageRequests.this, ActivityPendingRequests.class);
                startActivity(intent);
                break;

            case R.id.receivedRequests :
                intent = new Intent(ActivityManageRequests.this, ActivityReceivedRequests.class);
                startActivity(intent);
                break;

            default :
                break;
        }
    }
}
