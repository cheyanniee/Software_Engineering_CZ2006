package com.IrisBICS.lonelysg.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.IrisBICS.lonelysg.Activities.ActivityChat;
import com.IrisBICS.lonelysg.Activities.ActivityManageRequests;
import com.IrisBICS.lonelysg.R;

public class FragmentActivities extends Fragment implements View.OnClickListener{

    private CardView requestsIcon, chatIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_activities_ui, container, false);

        requestsIcon = v.findViewById(R.id.requestsIcon);
        requestsIcon.setOnClickListener(this);
        chatIcon = v.findViewById(R.id.chatIcon);
        chatIcon.setOnClickListener(this);


        return v;
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.requestsIcon:
                intent = new Intent(this.getActivity(), ActivityManageRequests.class);
                startActivity(intent);
                break;

            case R.id.chatIcon:
                intent = new Intent(this.getActivity(), ActivityChat.class);
                startActivity(intent);
                break;

            default :
                break;
        }
    }
}
