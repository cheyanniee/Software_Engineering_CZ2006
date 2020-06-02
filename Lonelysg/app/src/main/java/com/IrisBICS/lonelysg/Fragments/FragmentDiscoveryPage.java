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

import com.IrisBICS.lonelysg.Activities.ActivityInvitations;
import com.IrisBICS.lonelysg.R;

public class FragmentDiscoveryPage extends Fragment implements View.OnClickListener {
    private CardView allIcon, gamesIcon, foodIcon, movieIcon, othersIcon, sportsIcon, studyIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discovery_ui, container, false);

        // Defining cards
        allIcon = v.findViewById(R.id.allIcon);
        gamesIcon = v.findViewById(R.id.gamesIcon);
        foodIcon = v.findViewById(R.id.foodIcon);
        movieIcon = v.findViewById(R.id.movieIcon);
        othersIcon = v.findViewById(R.id.othersIcon);
        sportsIcon = v.findViewById(R.id.sportsIcon);
        studyIcon = v.findViewById(R.id.studyIcon);

        // Add Click listener to the cards
        allIcon.setOnClickListener(this);
        gamesIcon.setOnClickListener(this);
        foodIcon.setOnClickListener(this);
        movieIcon.setOnClickListener(this);
        othersIcon.setOnClickListener(this);
        sportsIcon.setOnClickListener(this);
        studyIcon.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.allIcon :
                intent = new Intent(this.getActivity(), ActivityInvitations.class);
                intent.putExtra("category", "All");
                startActivity(intent);
                break;

            case R.id.gamesIcon :
                intent = new Intent(this.getActivity(), ActivityInvitations.class);
                intent.putExtra("category", "Games");
                startActivity(intent);
                break;

            case R.id.foodIcon :
                intent = new Intent(this.getActivity(), ActivityInvitations.class);
                intent.putExtra("category", "Food and Drinks");
                startActivity(intent);
                break;

            case R.id.movieIcon :
                intent = new Intent(this.getActivity(), ActivityInvitations.class);
                intent.putExtra("category", "Movies");
                startActivity(intent);
                break;

            case R.id.othersIcon :
                intent = new Intent(this.getActivity(), ActivityInvitations.class);
                intent.putExtra("category", "Others");
                startActivity(intent);
                break;

            case R.id.sportsIcon :
                intent = new Intent(this.getActivity(), ActivityInvitations.class);
                intent.putExtra("category", "Sports");
                startActivity(intent);
                break;

            case R.id.studyIcon :
                intent = new Intent(this.getActivity(), ActivityInvitations.class);
                intent.putExtra("category", "Study");
                startActivity(intent);
                break;

            default :
                break;
        }
    }
}
