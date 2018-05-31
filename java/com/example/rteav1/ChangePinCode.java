package com.example.rteav1;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePinCode extends AppCompatActivity {

    EditText oldPin, newPin;
    Button changePinBtn;
    SharedPreferences sharedPreferences;
    Toolbar toolbarSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin_code);

        toolbarSettings = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbarSettings);
        getSupportActionBar().setTitle("Change Pin Code...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_FILENAME, MODE_PRIVATE);

        oldPin = (EditText)findViewById(R.id.oldPinCode);
        newPin = (EditText)findViewById(R.id.newPinCode);
        changePinBtn = (Button)findViewById(R.id.changePinButton);

        oldPin.addTextChangedListener(new pinMatcher(oldPin));

        changePinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPin.length()<4){
                    newPin.setError("Pin is less than 4 digits");
                }else {

                    String pin = newPin.getText().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.SP_PinCode, pin);
                    editor.commit();

                    oldPin.setEnabled(true);
                    newPin.setEnabled(false);
                    changePinBtn.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Pin Code Changed and Saved", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    private class pinMatcher implements TextWatcher {

        private View view;
        private pinMatcher(View view) {

            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            switch(view.getId()){

                case R.id.oldPinCode:

                    String oldPinCodeFromSP = sharedPreferences.getString(Config.SP_PinCode, "");
                    if(!oldPinCodeFromSP.equals(oldPin.getText().toString())){
                        oldPin.setError("Invalid pin");
                        oldPin.requestFocus();
                        newPin.setEnabled(false);
                    }else{
                        oldPin.setError(null);
                        oldPin.setEnabled(false);
                        newPin.setEnabled(true);
                        newPin.setText(null);
                        changePinBtn.setEnabled(true);
                    }
                    break;
            }
        }
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
}
