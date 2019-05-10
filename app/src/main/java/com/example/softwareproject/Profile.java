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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    TextView firstName;
    TextView lastName;
    TextView email;
    TextView weight;
    ImageView profilePic;
    FirebaseUser user;
    DatabaseReference rootRef;
    DatabaseReference userRef;
    ValueEventListener valueEventListener;
    private double lat = 0;
    private double lng = 0;


    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firstName = findViewById(R.id.my_first_name);
        lastName = findViewById(R.id.my_last_name);
        email = findViewById(R.id.my_email);
        weight = findViewById(R.id.my_weight);
        profilePic = findViewById(R.id.pp);







        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();






        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("WORKS", "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d("RANDOM", "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
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

    private void showData(DataSnapshot dataSnapshot) {


        for(DataSnapshot ds : dataSnapshot.getChildren()){
            User uInfo = new User();
            if(ds.child(userID).getValue(User.class) != null) {
                uInfo.setFirstName(ds.child(userID).getValue(User.class).getFirstName()); //set the name
                uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail()); //set the email
                uInfo.setLastName(ds.child(userID).getValue(User.class).getLastName());
                uInfo.setWeight(ds.child(userID).getValue(User.class).getWeight()); //set the name
            }


            firstName.setText(uInfo.getFirstName());
            lastName.setText(uInfo.getLastName());
            email.setText(uInfo.getEmail());
            weight.setText(uInfo.getWeight() + " LBS");


        }


        // Reference to an image file in Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("documentImages/noplateImg");


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
