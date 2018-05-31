package com.example.rteav1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_splash);


        Thread timer = new Thread() {

            public void run() {
                try {
                    sleep(3000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent loginPage = new Intent(WelcomeSplash.this, Login.class);
                    startActivity(loginPage);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
