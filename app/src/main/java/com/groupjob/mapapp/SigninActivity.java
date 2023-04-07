package com.groupjob.mapapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.groupjob.mapapp.databinding.ActivityAuthBinding;

public class SigninActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private FirebaseAuth auth;
    private boolean isUserValid, isPassValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isPassValid = false;
        isUserValid = false;

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        binding.goCreate.setOnClickListener(e -> {
            startActivity(new Intent(SigninActivity.this, SignupActivity.class));
            finish();
        });

        binding.button.setEnabled(false);

        binding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    isUserValid = true;
                } else {
                    isPassValid = false;
                }

                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 4) {
                    isPassValid = true;
                } else {
                    isPassValid = false;
                }

                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.button.setOnClickListener(e -> {
            String username = binding.username.getText().toString();
            String password = binding.password.getText().toString();

            auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    startActivity(new Intent(SigninActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(SigninActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }

    protected void validateFields() {

        if (isPassValid && isUserValid) {

            String pass = binding.password.getText().toString();
            Log.d("VALIDATION", "Fields are Filld");
            binding.button.setEnabled(true);
        } else {
            binding.button.setEnabled(false);
        }

    }
}