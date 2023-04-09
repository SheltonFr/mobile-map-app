package com.groupjob.mapapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.groupjob.mapapp.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    /*
     * A senha deve ter pelo menos 6 caracteres
     * */


    private ActivitySignupBinding binding;
    private FirebaseAuth auth;
    private boolean isPassValid, isUserValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.gotLogin.setOnClickListener(e -> startActivity(new Intent(SignupActivity.this, SigninActivity.class)));
        auth = FirebaseAuth.getInstance();

        isUserValid = false;
        isUserValid = false;

    }

    @Override
    protected void onStart() {
        super.onStart();


        binding.button.setEnabled(false);

        binding.button.setOnClickListener(e -> {
            String email = binding.username.getText().toString();
            String password = binding.password.getText().toString();

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("Auth", "DONE");
                    startActivity(new Intent(SignupActivity.this, SigninActivity.class));
                    finish();
                } else {
                    Log.d("Auth", "ERROR");
                    Toast.makeText(SignupActivity.this, "Authentication Faild", Toast.LENGTH_SHORT).show();
                }
            });
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

        binding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    isUserValid = true;
                } else {
                    isUserValid = false;
                }
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void validateFields() {

        if (isPassValid && isUserValid) {

            String pass = binding.password.getText().toString();
            String coPass = binding.confirmPassword.getText().toString();

            if (pass.equalsIgnoreCase(coPass)) {
                Log.d("VALIDATION", "Passwords Are Valid!");
                binding.button.setEnabled(true);
            } else {
                binding.button.setEnabled(false);
            }

        } else {
            binding.button.setEnabled(false);
        }

    }
}