package com.groupjob.mapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SpashAcreen extends Activity {

    private static int SPALH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_acreen);

        new Handler().postDelayed(() -> {
            // RUNNABLE FUNCTIONAL INTERFACE

            Intent i = new Intent(SpashAcreen.this, MainActivity.class);
            startActivity(i);

            finish();
        }, SPALH_TIME_OUT);
    }
}