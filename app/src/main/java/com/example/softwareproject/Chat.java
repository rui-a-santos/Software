package com.example.softwareproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import android.graphics.drawable.ColorDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Chat extends AppCompatActivity {
    private FirebaseUser user = null;
    private FirebaseDatabase database = null;
    private Toolbar chatToolBar;
    private ViewPager chatViewPager;
    private TabLayout chatTabLayout;
    private TabsAccessorAdapter chatTabsAcessorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.database = FirebaseDatabase.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        chatViewPager = (ViewPager) findViewById(R.id.chatPager);
        chatTabsAcessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        chatViewPager.setAdapter(chatTabsAcessorAdapter);
        chatTabLayout = (TabLayout)  findViewById(R.id.chat_tabs);
        chatTabLayout.setupWithViewPager(chatViewPager);



    }






}