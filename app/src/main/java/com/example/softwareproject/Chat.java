package com.example.softwareproject;

import android.app.ActionBar;
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
    private ColorDrawable color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
color = new ColorDrawable(Color.argb(255, 218, 67, 54));
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setBackgroundDrawable(color);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);
        this.getSupportActionBar().setTitle("Chat");
        this.database = FirebaseDatabase.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        chatViewPager = (ViewPager) findViewById(R.id.chatPager);
        chatTabsAcessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        chatViewPager.setAdapter(chatTabsAcessorAdapter);
        chatTabLayout = (TabLayout)  findViewById(R.id.chat_tabs);
        chatTabLayout.setupWithViewPager(chatViewPager);



    }






}