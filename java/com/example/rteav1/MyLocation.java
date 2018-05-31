package com.example.rteav1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MyLocation extends AppCompatActivity{

    //defining drawer components
    Toolbar toolbarMyLocation;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    //Sharedpreferences
    SharedPreferences sharedPreferences;

    //defining Fragment components
    FragmentTransaction fragmentTransaction;

    //variables for navigation view
    NavigationView navigationView;

    //for the components of the navigation drawer
    LayoutInflater inflater;
    View headerView;
    NetworkImageView userProfilePicture;
    ImageLoader imageLoader;
    TextView userFullName, userEmailAddress;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(getApplicationContext(), "Google Play Service not available.", Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_my_location);
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = inflater.inflate(R.layout.navigation_drawer_header,
                (ViewGroup) findViewById(R.id.navigation_view));

        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_FILENAME, MODE_PRIVATE);
        initialize();
        navigationBarItemClickListener();

        userFullName.setText(sharedPreferences.getString(Config.SP_fullname, ""));
        userEmailAddress.setText(sharedPreferences.getString(Config.SP_email, ""));
        setUserProfilePicture();

        Log.d("Fullname: ", sharedPreferences.getString(Config.SP_fullname, ""));
        Log.d("Email: ", sharedPreferences.getString(Config.SP_email,""));


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //syncing drawer toogle action.
        actionBarDrawerToggle.syncState();
    }

    //initializing all the components
    public void initialize(){

        //initializing drawer components
        toolbarMyLocation = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbarMyLocation);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        getSupportActionBar().setTitle("RTEAV1");

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbarMyLocation, R.string.drawer_open,
                R.string.drawer_close);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //initializing Fragment components
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.home_container, new GoogleMapFragment());
        fragmentTransaction.commit();

        //initializing navigation variable.
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //initializing components of navigation header
        userProfilePicture = (NetworkImageView)headerView.findViewById(R.id.navigation_userProfilePicture);
        userFullName = (TextView)headerView.findViewById(R.id.userFullName);
        userEmailAddress = (TextView)headerView.findViewById(R.id.userEmailAddress);

    }

    public void navigationBarItemClickListener(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_id:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.home_container, new GoogleMapFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("RTEAV1");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.myProfile:
                        drawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), MyProfile.class));
                        break;

                    case R.id.setting:
                        drawerLayout.closeDrawers();
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        break;

                    case R.id.logout://Creating an alert dialog to confirm logout
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyLocation.this);
                        alertDialogBuilder.setMessage("Are you sure, you want to logout?");
                        alertDialogBuilder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //getting out shared preference
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        //Putting the value false for loggedIn
                                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                                        //Putting blank value to all other datas
                                        editor.putString(Config.SP_mobilenumber, "");
                                        editor.putString(Config.SP_fullname, "");
                                        editor.putString(Config.SP_eyecolor, "");
                                        editor.putString(Config.SP_haircolor, "");
                                        editor.putString(Config.SP_height, "");
                                        editor.putString(Config.SP_age, "");
                                        editor.putString(Config.SP_gender, "");
                                        editor.putString(Config.SP_image, "");
                                        editor.putString(Config.SP_email, "");

                                        //saving the sharedPreferences
                                        editor.commit();

                                        stopService(new Intent(MyLocation.this, TriggerOption.class));

                                        //Clearing the url from the cache

                                        CustomVolleyRequest.getInstance(getApplicationContext()).getRequestQueue()
                                                .getCache().invalidate(url, true);

                                        //starting login activity
                                        Intent i = new Intent(MyLocation.this, Login.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                        alertDialogBuilder.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        //Showing the alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        break;
                }
                return false;
            }
        });
    }
    public void setUserProfilePicture(){
        String path = sharedPreferences.getString(Config.SP_image, "");
        url = Config.BASEURL+path;

        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                .getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(userProfilePicture
                , 0, R.drawable.ic_error ));
        userProfilePicture.setImageUrl(url, imageLoader);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
}
