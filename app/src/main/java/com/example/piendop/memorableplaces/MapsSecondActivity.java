package com.example.piendop.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationProvider;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsSecondActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    //global variables
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;


    //if we have an allow from user to get the location we do a request on permission's result
    //method for zooming user location
    public void centerUserLocation(Location location, String address){
        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

        if(address!="Your location"){
            //add marker for memorable places at the first time
            mMap.addMarker(new MarkerOptions().position(userLocation).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
        }else{//your location case
            //clear markers
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
        }
    }

    private String getAddress(LatLng latLng) {

        //create a geocoder to get the address of clicked location
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        //return address
        String addressInfo="Could not find the address";
        //create a list address to store addresses of clicked location
        try {//use try catch to check if its created or not
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            //check if we have address or not ==> check null and empty size
            if(addressList!=null && addressList.size()>0){
                Address address = addressList.get(0);
                addressInfo = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressInfo;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //we check self-permission again to make sure
            startListening();
        }
    }
    //check self permission to update user location if yes center location
    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //request location update will call onClickListener in onMapReady method
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_second);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //set on map click at on Map ready
        mMap.setOnMapLongClickListener(MapsSecondActivity.this);
        Intent intent = getIntent();
        //if we change the location it will update for us
        if(intent.getIntExtra("placeNumber",0)==0){

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //zoom the user location
                    centerUserLocation(location,"Your location");
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            /*THE FIRST TIME TIME TIME OR THE NEXT TIME OPEN THE APP*/
            //so for the first time open the app we don't click anything so it must show marker in the previous location
            //ask user to allow get location
            //version api<23
            if(Build.VERSION.SDK_INT<23){
                //startClick();
                //request location update if version < 23
                startListening();
            }else {
                //ask for permission at the first time
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    //1 here is that we want to ask 1 time for the first time
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }else{
                    //request location update if user allow u to access location
                    startListening();
                    //we r allowed to get access location when we open the app second time
                    //startClick();
                }
            }

        }else{
            //if in list view we click on memorable places we must center to this marker
            //first get location of memorable places
            Location memorableLocation = new Location(locationManager.GPS_PROVIDER);
            memorableLocation.setLatitude(MainActivity.locations.
                    get(intent.getIntExtra("placeNumber",0)).latitude);
            memorableLocation.setLongitude(MainActivity.locations.
                    get(intent.getIntExtra("placeNumber",0)).longitude);
            centerUserLocation(memorableLocation,MainActivity.places.
                    get(intent.getIntExtra("placeNumber",0)));

        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //remove location listener so that it cannot update
        locationManager.removeUpdates(locationListener);
        //get the address from click buttons
        String address = getAddress(latLng);
        //add marker of this clicked location
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        //update places and locations in memorable places list
        MainActivity.places.add(address);
        MainActivity.locations.add(latLng);

        /**************notifyDataSetChanged()********/
        //Notifies the attached observers that the underlying data has been changed and any
        // View reflecting the data set should refresh itself.
        MainActivity.arrayAdapter.notifyDataSetChanged();

        /*****************store data to permanent storage***********************/
        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("com.example.piendop.memorableplaces",Context.MODE_PRIVATE);
        //store places
        try {
            ArrayList<String> latitudes= new ArrayList<>();
            ArrayList<String> longitudes= new ArrayList<>();

            for(LatLng coordinates: MainActivity.locations){
                latitudes.add(Double.toString(coordinates.latitude));
                longitudes.add(Double.toString(coordinates.longitude));
            }
            sharedPreferences.edit().putString
                    ("places",ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString
                    ("latitudes",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString
                    ("longitudes",ObjectSerializer.serialize(longitudes)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /****log to check if we have stored it or not*******/

        /*try {
            ArrayList<String> places;
            ArrayList<String> _latitudes;
            ArrayList<String> _longitudes;
            places =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.
                    getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            _latitudes =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.
                    getString("latitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            _longitudes =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.
                    getString("longitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("places",places.toString());
            Log.i("latitudes",_latitudes.toString());
            Log.i("longitudes",_longitudes.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /**********************************************************************************/
        //notify location is saved
        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
    }
}
