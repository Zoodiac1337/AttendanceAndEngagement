package com.example.attendanceandengagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            String email = mAuth.getCurrentUser().getEmail();
            if (email.endsWith("@my.ntu.ac.uk"))
                startActivity(new Intent(MainActivity.this, StudentActivity.class).putExtra("email", email));
            else if (email.endsWith("ntu.ac.uk"))
                startActivity(new Intent(MainActivity.this, StudentActivity.class).putExtra("email", email));
            finish();
        }
    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (email.endsWith("@my.ntu.ac.uk"))
                        startActivity(new Intent(MainActivity.this, StudentActivity.class).putExtra("email", email));
                    else if (email.endsWith("ntu.ac.uk"))
                        startActivity(new Intent(MainActivity.this, StudentActivity.class).putExtra("email", email));
                    finish();
                    Toast.makeText(MainActivity.this, email+" logged in", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("SignInActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signInButtonClicked(View view){
        EditText email = findViewById(R.id.edit_textEmail);
        EditText password = findViewById(R.id.edit_textPassword);
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();
        signIn(sEmail, sPassword);
    }
}