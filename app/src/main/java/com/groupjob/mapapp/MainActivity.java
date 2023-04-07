package com.groupjob.mapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        validateAuth();

        findViewById(R.id.button2).setOnClickListener(e -> {
            auth.signOut();
            validateAuth();
        });
    }

    private void validateAuth() {
        if(auth.getCurrentUser() == null ){
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
        } else {
            Log.i("EMAIL", auth.getCurrentUser().getEmail());
        }

    }
}