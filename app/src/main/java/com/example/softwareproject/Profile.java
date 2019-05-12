package com.example.softwareproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Profile extends AppCompatActivity {
    private TextView userRank;
    private TextView userSteps;
    private TextView userDistanceWalked;
    private TextView userCaloriesBurned;
    private ImageView profilePic;
    private TextView userName;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle  = getIntent().getExtras();
        if(bundle != null) {
            String key = bundle.getString("key");
            this.userID = key;
            Log.v("HALLELUJAH", key);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userName = findViewById(R.id.user_profile_name);
        userRank = findViewById(R.id.rank);
        userSteps = findViewById(R.id.steps_taken);
        userDistanceWalked = findViewById(R.id.distance_walked);
        userCaloriesBurned = findViewById(R.id.calories_burned);
        profilePic = findViewById(R.id.user_profile_photo);

        mAuth = FirebaseAuth.getInstance();
        //add Firebase Database stuff
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        if(userID == null) {
            userID = user.getUid();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d("WORKS", "onAuthStateChanged:signed_in:" + user.getUid());
//                    toastMessage("Successfully signed in with: " + user.getEmail());
//                } else {
//                    // User is signed out
//                    Log.d("RANDOM", "onAuthStateChanged:signed_out");
//                    toastMessage("Successfully signed out.");
//                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
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





    }

    public long getDistanceRun(long steps) {
        long distance = (steps*78)/100;
        return distance;
    }

    private int calculateCaloriesBurnt(float distance, double weight) {
        double calPerMile = 0.5 * weight;
        double distanceInMiles = distance * 0.000621371;
        Log.v("distance", String.valueOf(distanceInMiles));
        return (int)(calPerMile*distanceInMiles);
    }

    private void showData(DataSnapshot dataSnapshot) {
String picReference = null;

        for(DataSnapshot ds : dataSnapshot.getChildren()){
//            User uInfo = new User();
//            if(ds.child(userID).getValue(User.class) != null) {
//                uInfo.setFirstName(ds.child(userID).getValue(User.class).getFirstName()); //set the name
//                uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail()); //set the email
//                uInfo.setLastName(ds.child(userID).getValue(User.class).getLastName());
//                uInfo.setWeight(ds.child(userID).getValue(User.class).getWeight()); //set the name
//            }
            User uInfo;
            if(ds.child(userID).getValue() != null && !(ds.child(userID).getValue() instanceof Long)) {
                uInfo = ds.child(userID).getValue(User.class);
                userName.setText(uInfo.getFirstName() + " " + uInfo.getLastName());
                userRank.setText(uInfo.getFirstName());
                picReference = uInfo.getEmail();
                Log.v("User steps", String.valueOf(uInfo.getSteps()));
                userSteps.setText(uInfo.getSteps() + " steps taken");
                long distance = getDistanceRun(uInfo.getSteps());
                long calories = calculateCaloriesBurnt(distance, uInfo.getWeight());
                userDistanceWalked.setText(distance + " metres travelled");
                userCaloriesBurned.setText(calories + " calories burned");
            }
        }
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("documentImages/" + picReference);

        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    profilePic.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}

    }


    @Override
        public void onStart() {
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }


        /**
         * customizable toast
         * @param message
         */
        private void toastMessage(String message){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }




    }
