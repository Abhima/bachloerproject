package com.example.rteav1;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GoogleMapFragment extends Fragment implements LocationListener {

    MapView mapView;
    GoogleMap googleMap;

    Button helpMeButton;
    ToggleButton locationOnOff;
    Spinner mapType;

    ArrayAdapter<CharSequence> mapTypeAdapter;

    //Components to get locations
    LocationManager locationManager;
    Criteria criteria;
    String bestProvider;
    Location location;
    double latitude, longitude;

    View view;
    public GoogleMapFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_google_map, container, false);

        //initializing the component of map view.
        mapView = (MapView)view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();// Needed to get the map to display immediately

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch (Exception e){
            e.printStackTrace();
        }
        googleMap = mapView.getMap();

        //initializing other components
        helpMeButton = (Button)view.findViewById(R.id.helpMeButton);
        locationOnOff = (ToggleButton)view.findViewById(R.id.locationOnOff);
        //spinner
        mapType = (Spinner)view.findViewById(R.id.mapType);
        mapTypeAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.mapType,
                R.layout.spinner_text);
        mapTypeAdapter.setDropDownViewResource(R.layout.spinner_text);
        mapType.setAdapter(mapTypeAdapter);
        spinnerClickListener();

        clickListeners();

        getLocationUpdates();
        return view;

    }

    public void clickListeners(){
        helpMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Please Help Me!!!", Toast.LENGTH_LONG).show();
            }
        });

        locationOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    Toast.makeText(getActivity().getApplicationContext(), "Status: "+isChecked + "::Location ON::", Toast.LENGTH_SHORT).show();
                    locationManager.requestLocationUpdates(bestProvider, 20000, 0, GoogleMapFragment.this);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Status: "+isChecked + "::Location Off::", Toast.LENGTH_SHORT).show();
                    locationManager.removeUpdates(GoogleMapFragment.this);
                }


            }
        });

    }

    public void getLocationUpdates(){
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 5000, 0, this);

        if (location != null){
            onLocationChanged(location);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        mapView.onPause();
        googleMap.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onLocationChanged(Location location) {
        googleMap.clear();
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng jwalakhelChowk = new LatLng(latitude, longitude);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(jwalakhelChowk));
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(jwalakhelChowk)
                .zoom(18)
                .tilt(50)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.addMarker(new MarkerOptions().position(jwalakhelChowk).title("You are,")
                .snippet("Here")).showInfoWindow();

        Toast.makeText(getActivity().getApplicationContext(), "Latitude: "+ latitude+
                "\nLongitude: "+longitude + "\nProvider: "+bestProvider, Toast.LENGTH_SHORT).show();

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

    //spinner clicklistener
    public void spinnerClickListener(){

        mapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView text = (TextView)view;
                String textView = text.getText().toString();
                switch (textView){

                    case "Normal":
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;

                    case "Hybrid":
                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;

                    case "Satellite":
                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;

                    case "Terrain":
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;

                    default:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}