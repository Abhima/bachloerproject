package com.example.rteav1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service implements LocationListener {

    //Components to get locations
    LocationManager locationManager;
    Criteria criteria;
    String bestProvider;
    Location location;
    String latitude, longitude;

    Calendar c;
    SimpleDateFormat df, tf;
    String currentDate, currentTime;



    RequestQueue requestQueue;
    StringRequest stringRequest;

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_FILENAME, MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 5000, 0, this);
        Toast.makeText(getApplicationContext(),"Location updates started", Toast.LENGTH_SHORT).show();

        if (location != null){
            onLocationChanged(location);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        tiggerUpdate();
        Log.d("Date: ", currentDate);
        Log.d("Time: ", currentTime);


        Toast.makeText(getApplicationContext(), "Latidude: "+latitude + " Longitude: "+longitude, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        Toast.makeText(getApplicationContext(),"Location updates stopped", Toast.LENGTH_SHORT).show();
    }

    public void tiggerUpdate(){

        df = new SimpleDateFormat("yyyy-MM-dd");
        tf = new SimpleDateFormat("HH:mm:ss a");
        c = Calendar.getInstance();
        currentDate = df.format(c.getTime());
        currentTime = tf.format(c.getTime());


                stringRequest = new StringRequest(Request.Method.POST, Config.TRIGGER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("mobilenumber", sharedPreferences.getString(Config.SP_mobilenumber, ""));
                hashMap.put("triggered_date", currentDate);
                hashMap.put("triggered_time", currentTime);
                hashMap.put("latitude", latitude+", ");
                hashMap.put("longitude", longitude+", ");
                hashMap.put("triggered_status", "1");

                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
