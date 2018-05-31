package com.example.rteav1;

import android.content.ComponentName;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    Button googleMap, loginActivity, registerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        clickListener();
    }

    public void initialize(){

        googleMap = (Button)findViewById(R.id.googleMap);
        loginActivity = (Button)findViewById(R.id.loginActivity);
        registerActivity = (Button) findViewById(R.id.registerActivity);
    }

    public void clickListener(){
        googleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyLocation.class));
            }
        });

        loginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        registerActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

    }
}
