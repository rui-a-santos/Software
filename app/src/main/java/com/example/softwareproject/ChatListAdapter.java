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

import java.util.ArrayList;

public class ChatListAdapter extends ArrayAdapter {
    private ArrayList<ChatItem> chatList;
    private FirebaseAuth mAuth;
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

        if (user.getUid()==chatItem.getUsers().get(0).getId()){

            name.setText(chatItem.getUsers().get(1).getFirstName() + " "+ chatItem.getUsers().get(1).getLastName());
        }

        else{
            name.setText(chatItem.getUsers().get(0).getFirstName() + " "+ chatItem.getUsers().get(0).getLastName());
        }
        TextView date = (TextView) view.findViewById(R.id.chatDate);
        date.setText(chatItem.getLastMessage().toString());
        notifyDataSetChanged();   //Notify data set changed
        return view;





    }

}
