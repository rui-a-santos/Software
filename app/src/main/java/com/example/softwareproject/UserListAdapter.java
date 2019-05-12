package com.example.softwareproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter {
    private ArrayList<User> userList;

public UserListAdapter(Context context, ArrayList<User> userList){
    super(context, R.layout.user_item, userList);
    this.userList = userList;

}

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final int i = position;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.user_item, parent, false);  //Inflate view
        User user = userList.get(i);

        TextView name = (TextView) view.findViewById(R.id.user_name);
        name.setText(user.getFirstName());

        notifyDataSetChanged();   //Notify data set changed
        return view;





    }

}
