package com.geopriv4j;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import geopriv4j.SpotMeAlgorithm;
import geopriv4j.utils.Mapper;

//import geopriv4j.*;
//import geopriv4j.VHCAlgorithm;


public class MapsActivitySpotMe extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;

    //this is used to get the current latitude and longitude
    private double currentLatitude = 0.0, currentLongitude = 0.0;
    //if this is set to false then there won't be any location updates
    private boolean requestingLocationUpdates = true;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    //specify the sample rate to generate locations
    public static int SAMPLERATE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // reuqest for permission
            int locationRequestCode = 1000;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = LocationRequest.create();

            //set to low power mode
            locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

            //this delays the location sampling
            locationRequest.setInterval(SAMPLERATE * 1000);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Log.d("geopriv4j", "onLocationResult: ");
                    if (locationResult == null) {
                        return;
                    }
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();

                        Log.d("geopriv4j", String.format(Locale.US, "%s -- %s", currentLatitude, currentLongitude));

                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);

                        assert mapFragment != null;
                        mapFragment.getMapAsync(MapsActivitySpotMe.this);

                    }

                }
            };
        }
    }


    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {

            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
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

        //displaying actual location on the map
        LatLng loc = new LatLng(currentLatitude, currentLongitude);
        googleMap.addMarker(new MarkerOptions().position(loc).title("actual location").icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));

        //converting com.google.android.gms.maps.model.LatLng object to geopriv4j.utils.LatLng object
        geopriv4j.utils.LatLng location = new geopriv4j.utils.LatLng(loc.latitude, loc.longitude);

        /*
        SpotMe Algorithm Example
         */

        // Specify the topleft and the bottomright locations for the grid
        //For this example, we are adding onto the current coordinates to expand by 0.1 in each direction
        geopriv4j.utils.LatLng topleft = new geopriv4j.utils.LatLng((location.latitude + 0.1), (location.longitude - 0.1));
        geopriv4j.utils.LatLng bottomright = new geopriv4j.utils.LatLng((location.latitude - 0.1), (location.longitude + 0.1));

        // specify the probability for reporting true
        double probability = 0.01;
        int gridsize = 25;

        //Initiate the algorithm
        SpotMeAlgorithm algorithm = new SpotMeAlgorithm(topleft, bottomright, probability, gridsize);

        Map<Integer, Boolean> generated_map = new HashMap<>(algorithm.generate(location));

        Map<Integer, ArrayList<geopriv4j.utils.LatLng>> grid_map = new HashMap<>(algorithm.grids);


        for (int i = 0; i < 625; i++) {
            geopriv4j.utils.LatLng cellTopLeft = grid_map.get(i).get(0);
            geopriv4j.utils.LatLng cellBottomRight = grid_map.get(i).get(1);

            double lat_diff = cellTopLeft.latitude - cellBottomRight.latitude;
            double long_diff = cellBottomRight.longitude - cellTopLeft.longitude;
            double center_lat = cellTopLeft.latitude - (lat_diff / 2);
            double center_long = cellTopLeft.longitude + (long_diff / 2);

            LatLng cellCenter = new LatLng(center_lat, center_long);

            if (generated_map.get(i)) {
                googleMap.addMarker(new MarkerOptions().position(cellCenter).title("True").icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
            } else {
                googleMap.addMarker(new MarkerOptions().position(cellCenter).title("False").icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
            }
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 11));
        generated_map.clear();
        grid_map.clear();
    }

}