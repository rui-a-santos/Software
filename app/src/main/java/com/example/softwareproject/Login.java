package com.example.softwareproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    FirebaseAuth auth ;
    Intent intent = getIntent();
    TextInputEditText inputEmail;
    TextInputEditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputEmail = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_passwordFinal);
        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MapActivity.class));
        }


    }

    public void login(View view) {
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }


        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Works?", "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();

                            startActivity(new Intent(Login.this, MapActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("dskdlsdlsdls", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed." + email + password,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }

    public void toRegister(View view) {
        startActivity(new Intent(Login.this, SignUpActivity.class));
    }


    public void goToReset(View view) {
        startActivity(new Intent(Login.this, ResetPassword.class));
    }
}
