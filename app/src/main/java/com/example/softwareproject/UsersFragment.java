package com.example.softwareproject;


import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    private ListView userList;
    private UserListAdapter userListAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private User currentUser = null;
    private DatabaseReference myRef;

    ArrayList<User> listUser;


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_users, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {

        this.listUser = new ArrayList<User>();
        this.userList = view.findViewById(R.id.list_users);
        this.userListAdapter = new UserListAdapter(getContext(), listUser);
        userList.setAdapter(userListAdapter);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("/Users");
        mAuth = FirebaseAuth.getInstance();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final User selectedUser = listUser.get(position);


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userid = user.getUid();
                DatabaseReference reference;
                reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DatabaseReference reference;
                        currentUser = dataSnapshot.getValue(User.class);


                        String chatKey = currentUser.getId() + selectedUser.getId();
                        ArrayList<Message> messages = new ArrayList<>();

                        Message message = new Message(currentUser, selectedUser, "Hi!");
                        messages.add(message);
                        ArrayList<User> users = new ArrayList<User>();
                        users.add(currentUser);
                        users.add(selectedUser);
                        ChatItem ci = new ChatItem(users, messages);


                        reference = FirebaseDatabase.getInstance().getReference();
                        reference.child("Chats").child(chatKey).setValue(ci);
//                        reference.child("Chats").child(chatKey).child("Person2").setValue(selectedUser);
//                        reference.child("Chats").child(chatKey).child("Messages").setValue(messages);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }

    private void showData(DataSnapshot dataSnapshot) {

        User user;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            user = ds.getValue(User.class);


            if (user != null) {
                if (user.getFirstName() != null) {
                    if (mAuth.getCurrentUser().getUid().equals(user.getId())) {


                    } else {
                        listUser.add(ds.getValue(User.class));
                    }

                    userListAdapter.notifyDataSetChanged();

                }
            }

        }
    }
}
