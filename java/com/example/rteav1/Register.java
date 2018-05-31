package com.example.rteav1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener{

    Calendar c;
    SimpleDateFormat df;
    String date;


    public static final int PICK_IMG_REQUEST = 20;

    Bitmap bitmap = null;
    ImageView profilePicture;
    EditText firstName, lastName, regAge, regMobNumber, regEmail, regPassword, confirmPassword;
    Spinner hairColor, eyeColor,ftHeight, inHeight;
    RadioGroup radioGenderGroup;
    RadioButton radioGenderButton;
    Button signUp;
    public ProgressDialog loading;

    StringRequest stringRequest;
    RequestQueue requestQueue;

    //this is string to check email pattern
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public String first_name, last_name, strHaircolor, strEyecolor, strHeight, strAge,
            strMobileNumber, strEmailAdd, strPass, strConPass, strGender;

    ArrayAdapter<CharSequence> fthtAdapter, inhtAdapter,hairColorAdapter, eyeColorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing volley request queue
        requestQueue = Volley.newRequestQueue(this);

        //initializing UI components
        initialize();

        //calling spinner functionality
        spinnerAdapter();

        //Listening clicks
        profilePicture.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    //method to initializing all the components of UI
    public void initialize(){
        //ImageView
        profilePicture = (ImageView)findViewById(R.id.chooseProPic);
        //EditText
        firstName = (EditText)findViewById(R.id.firstName);
        lastName = (EditText)findViewById(R.id.lastName);
        regAge = (EditText)findViewById(R.id.age);
        regMobNumber = (EditText)findViewById(R.id.regMobNumber);
        regEmail = (EditText)findViewById(R.id.regEmail);
        regPassword = (EditText)findViewById(R.id.regPassword);
        confirmPassword = (EditText)findViewById(R.id.confirmPassword);
        //Spinner
        ftHeight = (Spinner)findViewById(R.id.ftHeight);
        inHeight = (Spinner)findViewById(R.id.inHeight);
        hairColor = (Spinner)findViewById(R.id.hairColor);
        eyeColor = (Spinner)findViewById(R.id.eyeColor);
        //RadioButtonGroup
        radioGenderGroup = (RadioGroup)findViewById(R.id.radioGenderGroup);
        //Button
        signUp = (Button)findViewById(R.id.signUp);


        regPassword.addTextChangedListener(new passwordMatcher(regPassword));
        confirmPassword.addTextChangedListener(new passwordMatcher(confirmPassword));
    }

    public void spinnerAdapter(){

        hairColorAdapter = ArrayAdapter.createFromResource(this, R.array.hairColor, android.R.layout.simple_spinner_item);
        hairColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hairColor.setAdapter(hairColorAdapter);

        eyeColorAdapter = ArrayAdapter.createFromResource(this, R.array.eyeColor, android.R.layout.simple_spinner_item);
        eyeColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eyeColor.setAdapter(eyeColorAdapter);

        fthtAdapter = ArrayAdapter.createFromResource(this, R.array.ftHeight, android.R.layout.simple_spinner_item);
        fthtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ftHeight.setAdapter(fthtAdapter);

        inhtAdapter = ArrayAdapter.createFromResource(this, R.array.inHeight, android.R.layout.simple_spinner_item);
        inhtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inHeight.setAdapter(inhtAdapter);

    }
    /*public void sendSMS(){
        Log.d("SMS", "SMS Sent");
        String message = "This is your android app.";
        try{
            SmsManager smsmg = SmsManager.getDefault();
            smsmg.sendTextMessage("9801199198", "9802804914", message, null, null);
            Toast.makeText(getApplicationContext(),"SMS Sent", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"SMS failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }*/

    //For Selecting profile picture

    public String getStringImage (Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImg = Base64.encodeToString(imageBytes,
                Base64.DEFAULT);
        return encodedImg;
    }

    public  void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //getting bitmap from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                profilePicture.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.signUp:

                int selectedID = radioGenderGroup.getCheckedRadioButtonId();
                radioGenderButton = (RadioButton)findViewById(selectedID);

                first_name = firstName.getText().toString();
                last_name = lastName.getText().toString();
                strHaircolor = hairColor.getSelectedItem().toString();
                strEyecolor = eyeColor.getSelectedItem().toString();
                strHeight = ftHeight.getSelectedItem().toString()+" ft " + inHeight.getSelectedItem().toString()+ " in";
                strAge = regAge.getText().toString();
                strMobileNumber = regMobNumber.getText().toString();
                strEmailAdd = regEmail.getText().toString();
                strPass = regPassword.getText().toString();
                strConPass = confirmPassword.getText().toString();
                strGender = radioGenderButton.getText().toString();

                if(checkEditText()){
                    Toast.makeText(getApplicationContext(), "Valid", Toast.LENGTH_LONG).show();

                    String signUpData = firstName.getText().toString()
                            + "::" + lastName.getText().toString()
                            + "::" + hairColor.getSelectedItem().toString()
                            + "::" + eyeColor.getSelectedItem().toString()
                            + "::" + ftHeight.getSelectedItem().toString()
                            + " ft " + inHeight.getSelectedItem().toString()+ " in "
                            + regAge.getText().toString()
                            + "::" + regMobNumber.getText().toString()
                            + "::" +regEmail.getText().toString()
                            + "::" +regPassword.getText().toString()
                            + "::" +confirmPassword.getText().toString()
                            + "::" +strGender;

                    Log.d("Values", signUpData);

                    Toast.makeText(getApplicationContext(), signUpData, Toast.LENGTH_LONG).show();

                    uploadData();

                }else{
                    Toast.makeText(getApplicationContext(),"Invalid", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.chooseProPic:
                showFileChooser();
                break;
        }
    }

    public boolean checkEditText(){

        Boolean regFormValidity = false;

        if (bitmap == null){
            Toast.makeText(getApplicationContext(), "Please select a profile picture.",
                    Toast.LENGTH_LONG).show();
        }else if(firstName.length() == 0){
            firstName.setError("First name not entered");
            firstName.requestFocus();
        }/*else {
            firstName.setError(null);
        }*/
        else if (lastName.length() == 0){
            lastName.setError("Last name not entered");
            lastName.requestFocus();
        }/*else {
            lastName.setError(null);
        }*/

        else if(regAge.length() == 0){
            regAge.setError("Enter age");
            regAge.requestFocus();
        }/*else {
            age.setError(null);
        }*/

        else if(!strMobileNumber.startsWith("98") || regMobNumber.length()!=10){
            regMobNumber.setError("Invalid mobile number");
            regMobNumber.requestFocus();
        }/*else {
            regMobNumber.setError(null);
        }*/

        else if(!checkEmail(regEmail.getText().toString())){
            regEmail.setError("Invalid email address");
            regEmail.requestFocus();
        }/*else {
            regEmail.setError(null);
        }*/

        else if(strPass.length()<8){
            regPassword.setError("Invalid password");
            regPassword.requestFocus();
        }else if(strConPass.length()<8 || !strConPass.equals(regPassword.getText().toString())){
            confirmPassword.setError("Invalid password");
            confirmPassword.requestFocus();
        }else {
            firstName.setError(null);
            lastName.setError(null);
            regMobNumber.setError(null);
            regAge.setError(null);
            regEmail.setError(null);
            regPassword.setError(null);
            regPassword.setError(null);
            regFormValidity = true;
        }
        return regFormValidity;
    }

    //this is the class that is created to check the password and confirm password

    private class passwordMatcher implements TextWatcher {

        private View view;
        private passwordMatcher(View view) {

            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            switch(view.getId()){
                case R.id.regPassword:
                    if(text.length() < 8){
                        regPassword.setError("Invalid password");
                        regPassword.requestFocus();
                    }else{
                        regPassword.setError(null);
                    }
                    break;
                case R.id.confirmPassword:
                    if(!text.equals(regPassword.getText().toString())){
                        confirmPassword.setError("Invalid password");
                        confirmPassword.requestFocus();
                    }else{
                        confirmPassword.setError(null);
                    }
                    break;
            }
        }
    }

    private void uploadData(){

        //Showing progess dialog
        loading = ProgressDialog.show(this, "Registering...",
                "Please Wait...", false, false);


        /*c = Calendar.getInstance();
        df = new SimpleDateFormat("-MM-dd HH:mm:ss");
        date = df.format(c.getTime());*/

        stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        Log.d("Response: ", response);


                            if(response.startsWith("[")){
                                Toast.makeText(getApplicationContext(), "Registration error!",
                                        Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Successfully registered",
                                        Toast.LENGTH_LONG).show();

                                Intent i = new Intent(Register.this, Login.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String strImage = getStringImage(bitmap);

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("mobilenumber", strMobileNumber);
                hashMap.put("full_name", first_name+" "+last_name);
                hashMap.put("eye_color", strEyecolor);
                hashMap.put("hair_color", strHaircolor);
                hashMap.put("height", strHeight);
                hashMap.put("age", strAge);
                hashMap.put("gender", strGender);
                hashMap.put("image", strImage);
                hashMap.put("email", strEmailAdd);
                hashMap.put("ppassword", strConPass);

                return hashMap;
            }
        };
        requestQueue.add(stringRequest);

    }

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}
