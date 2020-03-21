package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        Thread myThread = new Thread() {
            @Override
            public void run(){
                try{
                    sleep(1000);
                    Intent it = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(it);
                    finish();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
