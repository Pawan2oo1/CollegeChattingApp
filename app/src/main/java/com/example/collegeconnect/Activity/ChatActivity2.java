package com.example.collegeconnect.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collegeconnect.Adapter.MessagesAdapter;
import com.example.collegeconnect.ModelClass.Messages;
import com.example.collegeconnect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatActivity2 extends AppCompatActivity {

    String ReciverImage,ReciverUID,ReciverName,SenderUID;
    ImageView profileImage;
    TextView reciverName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String sImage;
    public static String rImage;

    CardView sendBtn;
    EditText editMessage;

    String senderRoom,reciverRoom;

    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;

    MessagesAdapter Adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        ReciverName=getIntent().getStringExtra("name");
        ReciverImage=getIntent().getStringExtra("ReciverImage");
        ReciverUID=getIntent().getStringExtra("uid");

        messagesArrayList=new ArrayList<>();

        profileImage=findViewById(R.id.ProfileImage);
        reciverName=findViewById(R.id.reciverName);

        messageAdapter=findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        Adapter=new MessagesAdapter(ChatActivity2.this,messagesArrayList);
        messageAdapter.setAdapter(Adapter);

        sendBtn=findViewById(R.id.sendBtn);
        editMessage=findViewById(R.id.editMessage);

        Picasso.get().load(ReciverImage).into(profileImage);
        reciverName.setText(""+ReciverName);

        SenderUID=firebaseAuth.getUid();

        senderRoom=SenderUID+ReciverUID;
        reciverRoom=ReciverUID+SenderUID;

        DatabaseReference reference=database.getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
        DatabaseReference chatReference=database.getReference().child("chats").child(senderRoom).child("messages");



        chatReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                Adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage= Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString();
                rImage=ReciverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=editMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity2.this, "Please Enter The Text Before Sending", Toast.LENGTH_SHORT).show();
                    return;
                }
                editMessage.setText("");
                Date date=new Date();

                Messages messages=new Messages(message,SenderUID, date.getTime());

                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(reciverRoom)
                                .child("messages")
                                .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });



    }
}
