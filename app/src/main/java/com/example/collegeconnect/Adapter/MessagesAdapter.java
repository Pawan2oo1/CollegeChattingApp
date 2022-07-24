package com.example.collegeconnect.Adapter;

import static com.example.collegeconnect.Activity.ChatActivity2.rImage;
import static com.example.collegeconnect.Activity.ChatActivity2.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeconnect.ModelClass.Messages;
import com.example.collegeconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;

    public MessagesAdapter(Context context,ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==ITEM_SEND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            return new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout_item,parent,false);
            return new ReciverViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages=messagesArrayList.get(position);

        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder=(SenderViewHolder )holder;
            viewHolder.txtmessage.setText(messages.getMessage());

            Picasso.get().load(sImage).into(viewHolder.ProfileImage);
        }else
        {
            ReciverViewHolder viewHolder=(ReciverViewHolder )holder;
            viewHolder.txtmessage.setText(messages.getMessage());

            Picasso.get().load(rImage).into(viewHolder.ProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);
        if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(messages.getSenderId())){
            return ITEM_SEND;
        }else {
            return ITEM_RECIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {

        ImageView ProfileImage;
        TextView txtmessage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            ProfileImage=itemView.findViewById(R.id.ProfileImage);
            txtmessage=itemView.findViewById(R.id.txtMessages);

        }
    }

    class  ReciverViewHolder extends RecyclerView.ViewHolder {

        ImageView ProfileImage;
        TextView txtmessage;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);

            ProfileImage=itemView.findViewById(R.id.ProfileImage);
            txtmessage=itemView.findViewById(R.id.txtMessages);

        }
    }
}
