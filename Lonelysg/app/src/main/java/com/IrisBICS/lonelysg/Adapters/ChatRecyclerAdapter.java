package com.IrisBICS.lonelysg.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.IrisBICS.lonelysg.Utils.FirebaseAuthHelper;
import com.IrisBICS.lonelysg.Models.Message;
import com.IrisBICS.lonelysg.R;

import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.MessageViewHolder>{

    private List<Message> messageList;
    private LayoutInflater mInflater;

    public static final int SENDER_MESSAGE = 1;
    public static final int RECEIVER_MESSAGE = 0;
    String currentUserID = FirebaseAuthHelper.getCurrentUserID();;

    public ChatRecyclerAdapter(Context context, List<Message> messageList) {
        this.mInflater = LayoutInflater.from(context);
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SENDER_MESSAGE){
            View view = mInflater.inflate(R.layout.chat_view_sender, parent, false);
            return new MessageViewHolder(view);
        }
        else {
            View view = mInflater.inflate(R.layout.chat_view_receiver, parent, false);
            return new MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public int getItemViewType(int position){
        if (messageList.get(position).getSender().equals(currentUserID)){
            return SENDER_MESSAGE;
        }
        else return RECEIVER_MESSAGE;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
        }

        public void bind(Message m){
            message.setText(m.getMessage());
        }

    }

}
