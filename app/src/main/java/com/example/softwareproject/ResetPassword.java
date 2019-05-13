package com.example.softwareproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// Reset your password

public class ResetPassword extends AppCompatActivity {
    private TextInputEditText resetEmail = null;
    private TextInputEditText resetEmail2 = null;
    Button reset;
    FirebaseAuth auth;
    Button back;
    private TextInputEditText inputFirstName = null;
    private TextInputEditText inputLastName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        auth = FirebaseAuth.getInstance();
        resetEmail = findViewById(R.id.input_username_reset);
        resetEmail2 = findViewById(R.id.input_user_confirm);
        reset = findViewById(R.id.btn_reset);
        back = findViewById(R.id.btn_back_login);
    }

    // Send instructions to reset the password

    public void resetSend(View view) {
        String emailAddress = resetEmail.getText().toString();



                if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }


                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPassword.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPassword.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
    }

    // Go back to login
    public void loginBack(View view) {
        startActivity(new Intent(ResetPassword.this,Login.class));
    }
}
