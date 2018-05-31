package com.example.rteav1;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

public class MyProfile extends AppCompatActivity {

    ImageView editProfilePic;
    EditText editFirstName, editLastName, editMobNumber, editEmail, editPassword;
    Toolbar toolbarProfile;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initialize();

    }

    public void initialize(){

        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_FILENAME, MODE_PRIVATE);
        toolbarProfile = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbarProfile);
        getSupportActionBar().setTitle("My Profile...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editProfilePic = (ImageView)findViewById(R.id.editProPic);
        editFirstName = (EditText)findViewById(R.id.editFirstName);
        editLastName = (EditText)findViewById(R.id.editLastName);
        editMobNumber = (EditText)findViewById(R.id.editMobNumber);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);

        String fullname = sharedPreferences.getString(Config.SP_fullname, "");
        String[] parts = fullname.split(" ");
        String firstname = parts[0];
        String lastname = parts[1];

        editFirstName.setText(firstname);
        editLastName.setText(lastname);
        editMobNumber.setText(sharedPreferences.getString(Config.SP_mobilenumber, ""));
        editEmail.setText(sharedPreferences.getString(Config.SP_email, ""));
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
