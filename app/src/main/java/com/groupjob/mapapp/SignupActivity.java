package com.groupjob.mapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.groupjob.mapapp.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    /*
     * A senha deve ter pelo menos 6 caracteres
     * */


    private ActivitySignupBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.gotLogin.setOnClickListener(e -> startActivity(new Intent(SignupActivity.this, SigninActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();

        binding.button.setOnClickListener(e -> {
            String email = binding.username.getText().toString();
            String password = binding.password.getText().toString();

            auth.createUserWithEmailAndPassword("shelton@gmail.com", "000000")
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
                                Log.d("Auth", "DONE");
                            } else {
                                Log.d("Auth", "ERROR");
                                Toast.makeText(SignupActivity.this, "Authentication Faild", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}