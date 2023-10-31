package com.safestree.authentication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safestree.MainActivity;
import com.safestree.R;

public class signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    EditText name,email,password,repassword;
    Button register;

    DatabaseReference mDatabase;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent =new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        repassword = findViewById(R.id.et_repassword);
        register = findViewById(R.id.btn_register);
//        final EditText signUsername = findViewById(R.id.et_name);
//        final EditText signPassword = findViewById(R.id.signPassword);
//        final Button signBtn = findViewById(R.id.signBtn);

        progressBar = findViewById(R.id.signupProgress);

        TextView oldUser = findViewById(R.id.swipeLeft);
        oldUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String signusername = email.getText().toString().trim();
                String signpassword = password.getText().toString().trim();
                String signrepassword = repassword.getText().toString().trim();
                String nameT = name.getText().toString();
                if(signusername.isEmpty() || signpassword.isEmpty() || !signpassword.equals(signrepassword) || nameT.isEmpty())
                {
                    Toast.makeText(signup.this, "Please enter valid data", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(signup.this, " "+" "+signusername+" "+signpassword, Toast.LENGTH_SHORT).show();
                mAuth.createUserWithEmailAndPassword(signusername,signpassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(nameT).build();
                                                assert user != null;
                                                user.updateProfile(profileUpdates);
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(signup.this, "Account Created",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(signup.this, "Please verify your email id", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
//
                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(signup.this, "Account creation failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(signup.this, "Account Failed",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }
}