package com.example.rteav1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    Button setupPinCodeBtn, enableTrigger, disableTrigger, btnPinChange;
    EditText enterPin;
    Toolbar toolbarSettings;
    SharedPreferences sharedPreferences;

    public boolean settingChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialize();
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_FILENAME, MODE_PRIVATE);

        setupPinCodeBtn.setOnClickListener(this);
        enableTrigger.setOnClickListener(this);
        disableTrigger.setOnClickListener(this);
        btnPinChange.setOnClickListener(this);

    }

    public void initialize(){
        toolbarSettings = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbarSettings);
        getSupportActionBar().setTitle("Settings...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setupPinCodeBtn = (Button)findViewById(R.id.setupPinCode);
        enableTrigger = (Button)findViewById(R.id.enableTrigger);
        disableTrigger = (Button)findViewById(R.id.disableTrigger);
        enterPin = (EditText)findViewById(R.id.enterPinCode);
        btnPinChange = (Button)findViewById(R.id.changePinCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enableTrigger:
                    startService(new Intent(Settings.this, TriggerOption.class));
                break;

            case R.id.disableTrigger:
                    stopService(new Intent(Settings.this, TriggerOption.class));
                break;

            case R.id.setupPinCode:

                if(enterPin.length()<4){
                    enterPin.setHint("Pin is less than 4 digits");
                }else {

                    String pin = enterPin.getText().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.SP_PinCode, pin);
                    editor.putBoolean(Config.SP_SettingChanged, true);
                    editor.commit();
                    enterPin.setEnabled(false);
                    setupPinCodeBtn.setEnabled(false);
                    btnPinChange.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Pin Code Saved", Toast.LENGTH_LONG).show();

                }
                break;

            case R.id.changePinCode:
                startActivity(new Intent(Settings.this, ChangePinCode.class));
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        settingChanged = sharedPreferences.getBoolean(Config.SP_SettingChanged,false);

        //if we will get true
        if(settingChanged){

            enterPin.setEnabled(false);
            setupPinCodeBtn.setEnabled(false);
            enterPin.setFocusable(false);
            btnPinChange.setEnabled(true);
        }
    }
}
