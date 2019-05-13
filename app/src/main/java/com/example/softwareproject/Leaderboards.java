package com.example.softwareproject;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Leaderboards extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> userKeys = new ArrayList();
    private ArrayList<User> users = new ArrayList<>();
    private FirebaseUser user;
private ColorDrawable color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);
        recyclerView = findViewById(R.id.my_recycler_view);
        color = new ColorDrawable(Color.argb(255, 218, 67, 54));
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setBackgroundDrawable(color);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);
        this.getSupportActionBar().setTitle("Leaderboard");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();



        DatabaseReference stepsRef = database.getReference("Steps");

        stepsRef.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userKeys.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    userKeys.add(ds.getKey());
                }
                Log.v("leaderboard", userKeys.toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference usersRef = database.getReference("Users");
        usersRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(int i = 0; i < userKeys.size(); i++) {
                    Log.v("number " + i, userKeys.get(i));
                        if(dataSnapshot.getKey().equals(userKeys.get(i))) {
                            User user = dataSnapshot.getValue(User.class);
                            users.add(user);
                            Log.v("username", users.get(0).getFirstName());
                    }

                }

                ArrayList<User> sorted = new ArrayList<>();

                for(int i = 0; i < userKeys.size(); i++) {
                    for(int j = 0; j < users.size(); j++) {
                        User user = users.get(j);
                        if(user.getId().equals(userKeys.get(i))) {
                            sorted.add(user);
                        }
                    }

                }

                if(mAdapter == null) {
                    mAdapter = new LeaderboardsAdapter(sorted);
                    recyclerView.setAdapter(mAdapter);
                } else {
                    ((LeaderboardsAdapter)mAdapter).updateData(sorted);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        usersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                users.clear();
//                for(int i = 0; i < userKeys.size(); i++) {
//                        if(dataSnapshot.getKey() == userKeys.get(i)) {
//                            User user = dataSnapshot.getValue(User.class);
//                            users.add(user);
//                        }
//                }
//                if(mAdapter == null) {
//                    Log.v("plshelp", users.toString());
//                    mAdapter = new LeaderboardsAdapter(users);
//                    recyclerView.setAdapter(mAdapter);
//                }
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        // specify an adapter (see also next example)

    }
    // ...
}
