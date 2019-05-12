package com.example.softwareproject;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private ListView chatList;
    private ChatListAdapter chatListAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private User currentUser = null;
    private DatabaseReference myRef;
    private HashMap<String, ChatItem> chatMap;
    ArrayList<ChatItem> chatItems;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chat, container, false);
//            // Inflate the layout for this fragment
//
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.chatItems = new ArrayList<ChatItem>();
        this.chatList = view.findViewById(R.id.listChats);
        this.chatListAdapter = new ChatListAdapter(getContext(), chatItems);
        this.chatMap = new HashMap<String, ChatItem>();
        chatList.setAdapter(chatListAdapter);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Chats");
        mAuth = FirebaseAuth.getInstance();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                chatItems.clear();
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ChatItem selectedChat = chatItems.get(position);
                String chatKey = null;

                Iterator it = chatMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();

                    if (chatMap.get(pair.getKey()).equals(selectedChat)) {
                        chatKey = pair.getKey().toString();
                    }


                    it.remove(); // avoids a ConcurrentModificationException
                }


//               Log.v("HELLO", chatKey);

                Intent intent = new Intent(getActivity(), Messaging.class);
                intent.putExtra("ChatItem", selectedChat);
                intent.putExtra("Key", chatKey);
                startActivity(intent);


            }


        });
    }

    private void showData(DataSnapshot dataSnapshot) {


        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            String s = ds.getKey();


            if (s.contains(mAuth.getCurrentUser().getUid())) {

                Date date = ds.child("lastMessage").getValue(Date.class);

                ArrayList<Message> messages = new ArrayList<Message>();
                for (DataSnapshot ds1 : ds.child("messages").getChildren()){
                        Date messageTime = ds1.child("messageTime").getValue(Date.class);
                        String content = ds1.child("content").getValue(String.class);

                        User recipient = ds1.child("recipient").getValue(User.class);
                        User sender = ds1.child("recipient").getValue(User.class);
                        Message message = new Message(sender, recipient, content, messageTime);
                        messages.add(message);

                }
                ArrayList<User> users = new ArrayList<User>();
                for (DataSnapshot ds2 : ds.child("users").getChildren()){
                    User user = ds2.getValue(User.class);
                    users.add(user);
                }

                ChatItem ci = new ChatItem(users, date, messages);



                if (ci != null) {


                    chatMap.put(s, ci);
                }
                chatItems.add(ci);
                chatListAdapter.notifyDataSetChanged();
            }


        }



    }


}


