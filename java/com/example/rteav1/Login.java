package com.example.rteav1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener{

    //Basic UI components.
    EditText extmobileNumber, extPassword;
    Button login;
    TextView registerHere;

    //Values while logging in
    String loginMobileNumber, loginPassword;

    //Progress dialog to show application progress
    ProgressDialog progress;

    //boolean variable to check user is logged in or not
    //initially it is false
    public boolean loggedIn = false;

    //Defining sharedpreference
    SharedPreferences sharedPreferences;

    //Defining request queue for connecting server.
    StringRequest request;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();

        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_FILENAME, MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);

        progress = new ProgressDialog(Login.this);

        loginEditTextListener();

        login.setOnClickListener(this);
        registerHere.setOnClickListener(this);
    }

    public void initialize(){
        extmobileNumber = (EditText)findViewById(R.id.loginMobNumber);
        extPassword = (EditText)findViewById(R.id.loginPassword);
        login = (Button)findViewById(R.id.login);
        registerHere = (TextView)findViewById(R.id.registerHere);
    }

    public  void loginEditTextListener(){

        //ckecking mobile number or user in database
        extmobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 10){
                    checkUserintoDatabase();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.login:
                 userLogin();
                break;

            case R.id.registerHere:
                 startActivity(new Intent(Login.this, Register.class));
                break;
        }

    }

    //this method actually connects to the database
    // and checks either user is registered or not
    public void checkUserintoDatabase(){
        loginMobileNumber = extmobileNumber.getText().toString().trim();

        progress.setMessage("Checking Number...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        request = new StringRequest(Request.Method.POST, Config.CHECK_USER_IN_DB_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),
                                "SUCCESS: " + jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        extPassword.setEnabled(true);
                        progress.dismiss();

                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Error: " + jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                        extPassword.setEnabled(false);
                        progress.dismiss();

                        extmobileNumber.setError("User not found");

                        openRegisterActivity();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //sending the parameter to the database
                //mobilenumber = the field name of the database
                //loginMobileNumber = the string value to send to the database
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("mobilenumber", loginMobileNumber);
                return hashMap;
            }

            @Override
            public Priority getPriority() {

                return Priority.IMMEDIATE;
            }
        };
        requestQueue.add(request);
    }

    //login method
    private void userLogin(){

        progress.setMessage("Connecting...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        //Getting values from edit texts
        loginMobileNumber = extmobileNumber.getText().toString().trim();
        loginPassword = extPassword.getText().toString().trim();

        Toast.makeText(getApplicationContext(), loginMobileNumber+" "+loginPassword, Toast.LENGTH_LONG).show();

        request = new StringRequest(Request.Method.POST, Config.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response", response);

                if(response.startsWith("[")){
                    Log.d("Response", "Invalid password");
                    Toast.makeText(getApplicationContext(),
                            "Invalid Username or Password.",Toast.LENGTH_SHORT).show();
                    extPassword.setText("");
                    extPassword.setError("Invalid Password");
                    progress.dismiss();
                }else{
                    Log.d("Response", "User found");

                    try {
                        JSONObject jsonObj = new JSONObject(response);

                        Toast.makeText(getApplicationContext(),
                                "SUCCESS: " + jsonObj.getString("fullname"),Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Config.SP_mobilenumber, jsonObj.getString("mobile_number"));
                        editor.putString(Config.SP_fullname, jsonObj.getString("fullname"));
                        editor.putString(Config.SP_eyecolor, jsonObj.getString("eyecolor"));
                        editor.putString(Config.SP_haircolor, jsonObj.getString("haircolor"));
                        editor.putString(Config.SP_height, jsonObj.getString("height"));
                        editor.putString(Config.SP_age, jsonObj.getString("age"));
                        editor.putString(Config.SP_gender, jsonObj.getString("gender"));
                        editor.putString(Config.SP_image, jsonObj.getString("image"));
                        editor.putString(Config.SP_email, jsonObj.getString("email"));
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                        editor.commit();

                        progress.dismiss();

                        Intent i = new Intent(Login.this, MyLocation.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("mobilenumber", loginMobileNumber);
                hashMap.put("ppassword", loginPassword);
                return hashMap;
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onResume fetching valu from Shared preference
        //Fetching the boolean value from sharedpreference
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF,false);

        //if we will get true
        if(loggedIn){
            //we will start the MainActivity
            Intent i = new Intent(Login.this, MyLocation.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    //opening registering activity

    public void openRegisterActivity(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
        alertDialogBuilder.setMessage("User doesn't exist in database, Please register");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //starting register activity
                        Intent i = new Intent(Login.this, Register.class);
                        startActivity(i);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

