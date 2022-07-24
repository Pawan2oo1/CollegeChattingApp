package com.example.collegeconnect;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeconnect.Activity.ChatActivity;
import com.example.collegeconnect.Activity.ChatActivity2;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {
    Context chatActivity;
    ArrayList<Users> usersArrayList;

    public UserAdapter(ChatActivity chatActivity, ArrayList<Users> usersArrayList) {
        this.chatActivity=chatActivity;
        this.usersArrayList=usersArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(chatActivity).inflate(R.layout.item_user_row,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Users users= usersArrayList.get(position);

        //if( FirebaseAuth.getInstance().getCurrentUser().getUid().equals(users.getUid()))
        //{
        //    holder.itemView.setVisibility(View.GONE);
        //}

        holder.user_name.setText(users.name);
        holder.user_status.setText(users.status);
        Picasso.get().load(users.imageUri).into(holder.user_profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(chatActivity, ChatActivity2.class);
                intent.putExtra("name",users.getName());
                intent.putExtra("ReciverImage",users.getImageUri());
                intent.putExtra("uid",users.getUid());
                chatActivity.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        ImageView user_profile;
        TextView user_name;
        TextView user_status;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            user_profile=itemView.findViewById(R.id.User_Image);
            user_name=itemView.findViewById(R.id.User_Name);
            user_status=itemView.findViewById(R.id.User_Status);
        }
    }
}

