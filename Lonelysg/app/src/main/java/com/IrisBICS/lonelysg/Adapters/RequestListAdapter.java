package com.IrisBICS.lonelysg.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.IrisBICS.lonelysg.Models.Request;
import com.IrisBICS.lonelysg.Models.User;
import com.IrisBICS.lonelysg.R;

import java.util.List;

public class RequestListAdapter extends ArrayAdapter<Request> {

    private List<Request> requestList;
    private List<User> userList;
    private Activity context;
    private String viewType;

    public RequestListAdapter(Activity context, List<Request> requests, List<User> users, String viewType) {
        super(context, R.layout.request_list_layout, requests);
        this.context = context;
        this.requestList = requests;
        this.userList = users;
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;
        ViewHolder viewHolder;
        if(r==null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.request_list_layout,null,true);
            viewHolder = new RequestListAdapter.ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) r.getTag();
        viewHolder.requestInvitation.setText(requestList.get(position).getInvitation());
        if (viewType=="received")
            viewHolder.requestUser.setText("Participant: "+userList.get(position).getUsername());
        else
            viewHolder.requestUser.setText("Host: "+userList.get(position).getUsername());
        return r;
    }

    class ViewHolder {
        TextView requestInvitation;
        TextView requestUser;
        ViewHolder(View v) {
            requestInvitation = v.findViewById(R.id.requestInvitation);
            requestUser = v.findViewById(R.id.requestUser);
        }
    }
}
