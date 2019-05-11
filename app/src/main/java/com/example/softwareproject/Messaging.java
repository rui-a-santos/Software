package com.example.softwareproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rockerhieu.emojicon.EmojiconEditText;
public class Messaging extends AppCompatActivity {
    EmojiconEditText emojiconEditText;
    ImageView emojiButton,submitButton;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        emojiButton = (ImageView)findViewById(R.id.emoji_button);
        submitButton = (ImageView)findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText)findViewById(R.id.emojicon_edit_text);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                ChatItem ci = (ChatItem )intent.getSerializableExtra("ChatItem");
                Message message;
                if(mUser.getUid().equals(ci.getUsers().get(0))){
                     message = new Message(ci.getUsers().get(0), ci.getUsers().get(1), emojiconEditText.getText().toString());
                }
                else {

                     message = new Message(ci.getUsers().get(1), ci.getUsers().get(0), emojiconEditText.getText().toString());

                }


                ci.addMessage(message);
                FirebaseDatabase.getInstance().getReference("Chats/" + intent.getStringExtra("Key") +"/messages").setValue(message);
                emojiconEditText.setText("");
                emojiconEditText.requestFocus();

            }
        });










    }




}
