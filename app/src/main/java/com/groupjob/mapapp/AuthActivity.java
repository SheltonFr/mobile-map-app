package com.groupjob.mapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.groupjob.mapapp.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goCreate.setOnClickListener(e -> startActivity(new Intent(AuthActivity.this, SignupActivity.class)));


        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MapsActivity.class));
            finish();
        }

        binding.button.setOnClickListener(e -> {
            String email = binding.username.getText().toString();
            String password = binding.password.getText().toString();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                startActivity(new Intent(AuthActivity.this, MapsActivity.class));
                            } else {
                                Toast.makeText(AuthActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}