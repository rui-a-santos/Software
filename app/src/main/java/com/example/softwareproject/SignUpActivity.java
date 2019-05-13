package com.example.softwareproject;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;


/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity {


    // Variables
    private static final int CAMERA_REQ = 1;
    private TextInputEditText inputPassword = null;
    private TextInputEditText inputPassword2 = null;
    private TextInputEditText inputEmail = null;
    private TextInputEditText inputFirstName = null;
    private TextInputEditText inputLastName = null;
    private TextInputEditText inputWeight = null;
    private ProgressDialog mProgress;

    private static final String REGEX_EMAIL = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    private FirebaseAuth mAuth = null;
    ProgressDialog progressDialog;

    private StorageReference mStorage;

    private ImageView mImageView;
    private Bitmap photo;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {

        }
    }

    // Initialise variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inputPassword = findViewById(R.id.input_password);
        inputPassword2 = findViewById(R.id.re_input_password);
        inputEmail = findViewById(R.id.input_email);
        inputFirstName = findViewById(R.id.input_first_name);
        inputLastName = findViewById(R.id.input_last_name);
        inputWeight = findViewById(R.id.input_weight);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        mImageView = findViewById(R.id.iv);

        progressDialog = new ProgressDialog(SignUpActivity.this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);


        }


    }

    // Create account


    public void signUp(View view) {

        final String email = inputEmail.getText().toString().trim();
        final String firstName = inputFirstName.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
        final String password2 = inputPassword2.getText().toString().trim();
        final String lastName = inputLastName.getText().toString().trim();
        final String weightString = inputWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weightString)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        final double weight = Double.parseDouble(weightString);


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Invalid email!");
            inputEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(getApplicationContext(), "Password reentered does not match", Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(
                                    mAuth.getUid(), firstName, lastName,
                                    email, weight

                            );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateUi();
                                    } else {
                                        //display a failure message
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }


    // go to Login page
    public void updateUi() {
        startActivity(new Intent(SignUpActivity.this, Login.class));
        finish();


    }

    //Already have an account?
    public void alreadySigned(View view) {
        updateUi();
    }


    // Open the camera

    public void takeSelfie(View view) {

        final String email = inputEmail.getText().toString().trim();


        if (!email.matches(REGEX_EMAIL)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        inputEmail.setEnabled(false);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQ);
    }


    // Submit to Firebase Storage
    @SuppressWarnings("VisibleForTests")
    public void submit() {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] b = stream.toByteArray();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("documentImages").child(inputEmail.getText().toString().trim());

        Task<Uri> urlTask = storageReference.putBytes(b).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(SignUpActivity.this, "uploaded", Toast.LENGTH_SHORT).show();

                } else {
                    // Handle failures
                    // ...
                }
            }
        });


        //StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);


    }

    // Gets the picture

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQ && resultCode == RESULT_OK) {

            photo = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(photo);
            submit();
        }
    }


}