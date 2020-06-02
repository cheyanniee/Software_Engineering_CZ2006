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

import com.IrisBICS.lonelysg.Activities.ActivityCreateInvitation;
import com.IrisBICS.lonelysg.Activities.ActivityUserInvitations;
import com.IrisBICS.lonelysg.R;

public class FragmentInvitations extends Fragment implements View.OnClickListener{

    private CardView createInvitation, viewInvitations;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_invitations_ui, container, false);

        createInvitation = v.findViewById(R.id.createInvitation);
        createInvitation.setOnClickListener(this);
        viewInvitations = v.findViewById(R.id.viewInvitations);
        viewInvitations.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.createInvitation:
                intent = new Intent(this.getActivity(), ActivityCreateInvitation.class);
                startActivity(intent);
                break;

            case R.id.viewInvitations:
                intent = new Intent(this.getActivity(), ActivityUserInvitations.class);
                startActivity(intent);
                break;

            default :
                break;
        }
    }
}
