package com.example.softwareproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatListAdapter extends ArrayAdapter {
    private ArrayList<ChatItem> chatList;
    private FirebaseAuth mAuth;
    private SimpleDateFormat dateLastMsgFormat;
    private String dateLastMsg;
public ChatListAdapter(Context context, ArrayList<ChatItem> chatList){
    super(context, R.layout.chat_item, chatList);
    this.chatList = chatList;

}

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final int i = position;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.chat_item, parent, false);  //Inflate view

        ChatItem chatItem = chatList.get(i);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        TextView name = (TextView) view.findViewById(R.id.chatPerson);

        if (user.getUid().equals(chatItem.getUsers().get(0).getId())){

            name.setText( chatItem.getUsers().get(1).getFirstName() + " "+ chatItem.getUsers().get(1).getLastName());
        }

        else{
            name.setText( chatItem.getUsers().get(0).getFirstName() + " "+ chatItem.getUsers().get(0).getLastName());
        }

dateLastMsgFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
dateLastMsg = dateLastMsgFormat.format(chatItem.getLastMessage());

        TextView date = (TextView) view.findViewById(R.id.chatDate);

        date.setText(dateLastMsg);
        notifyDataSetChanged();   //Notify data set changed
        return view;





    }

}
