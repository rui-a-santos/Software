package com.example.softwareproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter {

    public ArrayList<Message> messages;



    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.message_item, messages);
        this.messages = messages;

    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {



        final int i = position;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.message_item, parent, false);  //Inflate view

        Message item = messages.get(position);

        TextView text1 = (TextView) view.findViewById(R.id.message_time);
        TextView text2 = (TextView) view.findViewById(R.id.message_user);
        BubbleTextView bubble = (BubbleTextView) view.findViewById(R.id.message_text);

        text1.setText(item.getMessageTime().toString()); //Set text to list title
        text2.setText(item.getSender().getFirstName());
        bubble.setText(item.getContent());


        notifyDataSetChanged();   //Notify data set changed
        return view;




    }
}

