package com.IrisBICS.lonelysg.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.IrisBICS.lonelysg.Models.User;
import com.IrisBICS.lonelysg.R;
import com.google.android.gms.maps.model.Circle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends ArrayAdapter<User> {

    private ArrayList<User> chatUsers;
    private Activity context;

    public ChatListAdapter(Activity context, ArrayList<User> users) {
        super(context, R.layout.chat_list_layout, users);
        this.context = context;
        this.chatUsers = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;
        ViewHolder viewHolder;
        if(r==null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.chat_list_layout,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) r.getTag();
        viewHolder.chatUser.setText(chatUsers.get(position).getUsername());
        if (chatUsers.get(position).getProfilePic()!=null) {
            Picasso.get().load(chatUsers.get(position).getProfilePic()).into(viewHolder.chatUserProfilePic);
        }
        return r;
    }

    class ViewHolder {
        TextView chatUser;
        CircleImageView chatUserProfilePic;
        ViewHolder(View v) {
            chatUser = (TextView) v.findViewById(R.id.chatUser);
            chatUserProfilePic = v.findViewById(R.id.chatListImage);
        }
    }

}
