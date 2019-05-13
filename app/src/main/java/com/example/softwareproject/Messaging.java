package com.example.softwareproject;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rockerhieu.emojicon.EmojiconEditText;

import java.util.ArrayList;
import java.util.Date;

public class Messaging extends AppCompatActivity {
    EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private ListView listView;
private ColorDrawable color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        color = new ColorDrawable(Color.argb(255, 218, 67, 54));
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setBackgroundDrawable(color);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);
        this.getSupportActionBar().setTitle("Messages");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        emojiButton = (ImageView) findViewById(R.id.emoji_button);
        submitButton = (ImageView) findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                ChatItem ci = (ChatItem) intent.getSerializableExtra("ChatItem");
                Message message;
                if (mUser.getUid().equals(ci.getUsers().get(1).getId())) {
                    Log.v("Hello", "First IF");
                    message = new Message(ci.getUsers().get(0), ci.getUsers().get(1), emojiconEditText.getText().toString());
                } else {
                    Log.v("Hello", "Second IF");
                    message = new Message(ci.getUsers().get(1), ci.getUsers().get(0), emojiconEditText.getText().toString());

                }


                ci.addMessage(message);
                FirebaseDatabase.getInstance().getReference("Chats").child(intent.getStringExtra("Key")) .child("messages").push().setValue(message);
                emojiconEditText.setText("");
                emojiconEditText.requestFocus();

            }
        });


        this.messages = new ArrayList<Message>();
        this.messageAdapter = new MessageAdapter(this, this.messages);
        this.listView = (ListView) findViewById(R.id.list_of_message);
        listView.setAdapter(messageAdapter);


        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Chats/" + getIntent().getStringExtra("Key") + "/messages");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
    messages.clear();

                for (DataSnapshot ds1 : ds.getChildren()) {
                    Date messageTime = ds1.child("messageTime").getValue(Date.class);
                    String content = ds1.child("content").getValue(String.class);

                    User recipient = ds1.child("recipient").getValue(User.class);
                    User sender = ds1.child("recipient").getValue(User.class);
                    Message message = new Message(sender, recipient, content, messageTime);
                    messages.add(message);
                    messageAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });



        this.messageAdapter.notifyDataSetChanged();

    }
}

