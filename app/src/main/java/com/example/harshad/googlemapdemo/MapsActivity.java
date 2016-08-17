package com.example.harshad.googlemapdemo;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private TextView locationText;
    private TextView addressText;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker marker;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationText = (TextView) findViewById(R.id.location);
        addressText = (TextView) findViewById(R.id.address);

        //replace GOOGLE MAP fragment in this Activity
        replaceMapFragment();
    }

    private void replaceMapFragment() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        // Enable Zoom
        map.getUiSettings().setZoomGesturesEnabled(true);

        //set Map TYPE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //enable Current location Button
        map.setMyLocationEnabled(true);

        buildGoogleApiClient();
    }


    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    public void callBackDataFromAsyncTask(String address) {
        addressText.setText(address);
    }


    public void onConnected(Bundle bundle) {

//        add client request using fused api
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(90000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        // check if lat log in there so focus in current location and add marker
        if (mLastLocation != null) {

            LatLng loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            double longitude = mLastLocation.getLongitude();
            double latitude = mLastLocation.getLatitude();

            marker = map.addMarker(new MarkerOptions().position(loc));
            //for zooming current location.
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            locationText.setText("You are at [" + longitude + " ; " + latitude + " ]");

            //get current address by invoke an AsyncTask object
            new GetAddressTask(MapsActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null){

            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

//            if marker not set (because gps is not on) in onConnect method the add here
            if (marker==null){
                marker = map.addMarker(new MarkerOptions().position(loc));
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            locationText.setText("You are at [" + longitude + " ; " + latitude + " ]");

            //get current address by invoke an AsyncTask object
            new GetAddressTask(MapsActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));

        }
        else{
            Log.d("", "onLocationChanged: Location not update");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
