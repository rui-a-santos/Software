package com.example.softwareproject;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardsAdapter extends RecyclerView.Adapter<LeaderboardsAdapter.MyViewHolder> {
    private ArrayList<User> mUsers;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView rowName;
        public TextView rowRank;
        public TextView rowSteps;
        public MyViewHolder(View v) {
            super(v);
            rowName = v.findViewById(R.id.rowName);
            rowRank = v.findViewById(R.id.rowRank);
            rowSteps = v.findViewById(R.id.rowSteps);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public LeaderboardsAdapter (ArrayList<User> users) {
        mUsers = users;
    }

    public void updateData(ArrayList<User> list){
        mUsers=list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LeaderboardsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = mUsers.get(position);
        holder.rowRank.setText(String.valueOf(position + 1));
        holder.rowName.setText(user.getFirstName() + " " + user.getLastName());
        holder.rowSteps.setText(String.valueOf(user.getSteps()));


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUsers.size();
    }


}
