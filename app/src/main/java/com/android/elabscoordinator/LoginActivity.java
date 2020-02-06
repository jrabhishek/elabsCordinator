package com.android.elabscoordinator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email;
    TextInputEditText password;
     FirebaseAuth mAuth;
    Button login;
    ProgressDialog singnInprogressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dwed);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singnInprogressDialog = new ProgressDialog(LoginActivity.this);
                singnInprogressDialog.setMessage("logging in please wait......");
                singnInprogressDialog.show();
                signInUser(email.getText().toString(),password.getText().toString());
                //Toast.makeText(LoginActivity.this, email.getText().toString()+password.getText().toString(), Toast.LENGTH_SHORT).show();



            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
       // FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void signInUser(String email,String password)
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's informat
                            Toast.makeText(LoginActivity.this, "sucess", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            checkSingleLogin(mAuth.getCurrentUser().getUid());
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void checkSingleLogin(String Uid)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String code = document.get("code").toString();
                                String isOnline = document.get("isOnline").toString();
                                String subject = document.get("name").toString();
                                if (isOnline.equals("false"))
                                {
                                    modifingIsOnline(Uid,subject,code);

                                }
                            }
                        } else {
                            // Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    public void modifingIsOnline(String uid,String subject,String code)
    {
        SharedPreferences sharedpreferences = getSharedPreferences("subject_code", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("code", code);
        editor.apply();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("subject", subject);
        user.put("code", code);
        user.put("isOnline", "true");


// Add a new document with a generated ID
        DocumentReference contact = db.collection(uid).document("info");
        contact.update("isOnline", "true")

                .addOnSuccessListener(new OnSuccessListener < Void > () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();

                    }
                });

    }



}
