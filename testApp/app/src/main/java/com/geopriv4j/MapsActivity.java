package com.geopriv4j;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.net.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import geopriv4j.utils.Mapper;

//import geopriv4j.*;
//import geopriv4j.VHCAlgorithm;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;

    //this is used to get the current latitude and longitude
    private double currentLatitude = 0.0, currentLongitude = 0.0;
    //if this is set to false then there won't be any location updates
    private boolean requestingLocationUpdates = true;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    //specify the sample rate to generate locations
    public static int SAMPLERATE = 10;

    public Boolean initialized;


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
                        mapFragment.getMapAsync(MapsActivity.this);

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
//        /*
//         * Rounding Algorithm example
//         */
//
//        //specify the offset in meters
//        double s = 500;
//
//        //generating the noise using the RoundingAlgorithm from geopriv4j package
//        geopriv4j.utils.LatLng rounded = new RoundingAlgorithm(s).generate(location);
//
//
//        //converting geopriv4j.utils.LatLng object to com.google.android.gms.maps.model.LatLng obejct
//        LatLng roundedLatLng = new LatLng(rounded.latitude, rounded.longitude);
//
//        //displaying the location on the map
//        googleMap.addMarker(new MarkerOptions().position(roundedLatLng).title("rounded location").icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_dot)));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(roundedLatLng, 14));

//        /*
//         * Laplace Algorithm example
//         */
//
//        //specify the offset in meters
//        double e = 0.001;
//
//        //generating the noise using the LaplaceAlgorithm from geopriv4j package
//        geopriv4j.utils.LatLng laPlace = new LaplaceAlgorithm(e).generate(location);
//
//
//        //converting geopriv4j.utils.LatLng object to com.google.android.gms.maps.model.LatLng obejct
//        LatLng laPlaceLatLng = new LatLng(laPlace.latitude, laPlace.longitude);
//
//        //displaying the location on the map
//        googleMap.addMarker(new MarkerOptions().position(laPlaceLatLng).title("Laplace location").icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_dot)));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(laPlaceLatLng, 14));

//        /*
//         * Noise Algorithm example
//         */
//
//        double variance = 5000;
//
//        //generating the noise using the NoiseAlgorithm from geopriv4j package
//        geopriv4j.utils.LatLng noise = new NoiseAlgorithm(variance).generate(location);
//
//
//        //converting geopriv4j.utils.LatLng object to com.google.android.gms.maps.model.LatLng obejct
//        LatLng noiseLatLng = new LatLng(noise.latitude, noise.longitude);
//
//        //displaying the location on the map
//        googleMap.addMarker(new MarkerOptions().position(noiseLatLng).title("Noise Algorithm location").icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_dot)));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(noiseLatLng, 14));


//        /*
//         * MovingInTheNeighbourhood Algorithm example
//         */
//
//        LatLng topLeft = new LatLng((loc.latitude + 0.004), (loc.longitude - 0.004));
//        LatLng bottomRight = new LatLng((loc.latitude - 0.004), (loc.longitude + 0.004));
//
//
//        googleMap.addMarker(new MarkerOptions().position(topLeft).title("Top Left").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
//        googleMap.addMarker(new MarkerOptions().position(bottomRight).title("Bottom Right").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
//
//        //Number of dummies to be generated
//        int n = 5;
//
//        //distance between generated locations
//        double offset = 0.001;
//        geopriv4j.utils.LatLng topleft = new geopriv4j.utils.LatLng((loc.latitude + 0.004), (loc.longitude - 0.004));
//        geopriv4j.utils.LatLng bottomright = new geopriv4j.utils.LatLng((loc.latitude - 0.004), (loc.longitude + 0.004));
//
//        //generating the noise using the SpotMeAlgorithm from geopriv4j package
//        MovingInTheNeighbourhoodAlgorithm moving = new MovingInTheNeighbourhoodAlgorithm(topleft, bottomright);
//
//        ArrayList<geopriv4j.utils.LatLng> generated_locations = moving.generate(offset, n, location);
//        for (int i = 0; i < generated_locations.size(); i++){
//            //converting geopriv4j.utils.LatLng object to com.google.android.gms.maps.model.LatLng obejct
//            LatLng movingLatLng = new LatLng(generated_locations.get(i).latitude, generated_locations.get(i).longitude);
//
//            //displaying the location on the map
//            googleMap.addMarker(new MarkerOptions().position(movingLatLng).title("MitN Algorithm location").icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_dot)));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(movingLatLng, 14));
//        }

//        /*
//         * Spatial Cloaking Algorithm example
//         */
//
//        // change this variable to choose the amount of synthetic points
//        int data = 5;
//
//        // specify the sensitive location
//        geopriv4j.utils.LatLng sensitive = new geopriv4j.utils.LatLng(location.latitude, location.longitude);
//
//        // specify the radius r in meters
//        int radius = 500;
//
//        // specify the radius R in meters
//        int Radius = 1000;
//
//        // instantiating the algorithm
//        SpatialCloakingAlgorithm algorithm = null;
//        try {
//            algorithm = new SpatialCloakingAlgorithm(sensitive, radius, Radius);
//        } catch (RadiusException e) {
//            e.printStackTrace();
//        }
//
//        ArrayList<geopriv4j.utils.LatLng> locations = new ArrayList<geopriv4j.utils.LatLng>();
//        for(int i = 0; i < data; i++){
//            geopriv4j.utils.LatLng generated_point = new geopriv4j.utils.LatLng(sensitive.latitude + ((Math.random() * (0.07 + 0.07)) - 0.07), sensitive.longitude + ((Math.random() * (0.07 + 0.07)) - 0.07));
//            locations.add(i, generated_point);
//        }
//
//        for (int i = 0; i < locations.size(); i++) {
//            geopriv4j.utils.LatLng generated_location = algorithm.generate(locations.get(i));
//            LatLng spatialLatLng = new LatLng(generated_location.latitude, generated_location.longitude);
//            googleMap.addMarker(new MarkerOptions().position(spatialLatLng).title("Spatial Cloaking Algorithm location").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spatialLatLng, 14));
//        }


//        /*
//        SpotMe Algorithm Example
//         */
//
//        // Specify the topleft and the bottomright locations for the grid
//        //For this example, we are adding onto the current coordinates to expand by 0.1 in each direction
//        geopriv4j.utils.LatLng topleft = new geopriv4j.utils.LatLng((location.latitude + 0.1), (location.longitude - 0.1));
//        geopriv4j.utils.LatLng bottomright = new geopriv4j.utils.LatLng((location.latitude - 0.1), (location.longitude + 0.1));
//
//        // specify the probability for reporting true
//        double probability = 0.01;
//
//        //Initiate the algorithm
//        SpotMeAlgorithm algorithm = new SpotMeAlgorithm(topleft, bottomright, probability);
//
//        Map<Integer, Boolean> generated_map = new HashMap<>(algorithm.generate(location));
//
//        Map<Integer, ArrayList<geopriv4j.utils.LatLng>> grid_map = new HashMap<>(algorithm.grids);
//
//
//        for (int i = 0; i < 625; i++) {
//            geopriv4j.utils.LatLng cellTopLeft = grid_map.get(i).get(0);
//            geopriv4j.utils.LatLng cellBottomRight = grid_map.get(i).get(1);
//
//            double lat_diff = cellTopLeft.latitude - cellBottomRight.latitude;
//            double long_diff = cellBottomRight.longitude - cellTopLeft.longitude;
//            double center_lat = cellTopLeft.latitude - (lat_diff / 2);
//            double center_long = cellTopLeft.longitude + (long_diff / 2);
//
//            LatLng cellCenter = new LatLng(center_lat, center_long);
//
//            if(generated_map.get(i)){
//                googleMap.addMarker(new MarkerOptions().position(cellCenter).title("True").icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
//            }else{
//                googleMap.addMarker(new MarkerOptions().position(cellCenter).title("False").icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
//            }
//        }
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 11));
//        generated_map.clear();
//        grid_map.clear();
//        algorithm = null;



    /*
        VHC Algorithm Example
    */

        //specify the offset size here
        int sigma = 1000;

        //specify all four locations to be considered in this algorithm
        Mapper topleft = new Mapper("topleft", new geopriv4j.utils.LatLng(35.3630, -80.9845));
        Mapper topright = new Mapper("topright", new geopriv4j.utils.LatLng(35.3630, -80.6134));
        Mapper bottomright = new Mapper("bottomright", new geopriv4j.utils.LatLng(35.1427, -80.6134));
        Mapper bottomleft = new Mapper("bottomleft", new geopriv4j.utils.LatLng(35.1427, -80.9845));

        //Code for visualizing the boundaries you set
        LatLng topleft_LatLng = new LatLng(topleft.loc.latitude, topleft.loc.longitude);
        LatLng bottomright_LatLng = new LatLng(bottomright.loc.latitude, bottomright.loc.longitude);
        LatLng topright_LatLng = new LatLng(topright.loc.latitude, topright.loc.longitude);
        LatLng bottomleft_LatLng = new LatLng(bottomleft.loc.latitude, bottomleft.loc.longitude);
        googleMap.addMarker(new MarkerOptions().position(topleft_LatLng).title("Top Left").icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_dot)));
        googleMap.addMarker(new MarkerOptions().position(bottomright_LatLng).title("Bottom Right").icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_dot)));
        googleMap.addMarker(new MarkerOptions().position(topright_LatLng).title("Top Right").icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_dot)));
        googleMap.addMarker(new MarkerOptions().position(bottomleft_LatLng).title("Bottom Left").icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_dot)));

        //Manipulate topLeft and bottomRight LatLng into OSM Bounding Box for Query
        String min_lat = Double.toString(Math.min(topleft.loc.latitude, bottomright.loc.latitude));
        String min_long = Double.toString(Math.min(topleft.loc.longitude, bottomright.loc.longitude));
        String max_lat = Double.toString(Math.max(topleft.loc.latitude, bottomright.loc.latitude));
        String max_long = Double.toString(Math.max(topleft.loc.longitude, bottomright.loc.longitude));



        //Set thread policy to allow network call in main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        File file = new File(getFilesDir() + "/clt.xml");
        File trimmed_file = new File( getFilesDir() + "/clt_trimmed.txt");

        //Declare bounding box string using previously calculated min and max
        String bbox = min_long + "," + min_lat + "," + max_long + "," + max_lat;

        if (!trimmed_file.exists()) {
            System.out.println("Trimmed Dataset not present. Running API call and file creation.");
            //Pull node data from API
            try {
                String data = "[bbox];way[highway];node(w);out;&bbox=" + bbox;
                URL url = new URL("https://overpass-api.de/api/interpreter?data=" + data);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //Write to xml file on internal storage
                System.out.println("File Path: " + file.getPath());
                System.out.println("Response Length: " + response.length());

                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file, false);


                byte[] bytesArray = response.toString().getBytes();

                fos.write(bytesArray);
                fos.flush();
                fos.close();

                System.out.println("Length of file before trimming: " + file.length());
            } catch (IOException err) {
                System.out.println(err.getMessage());
            }

            try {
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                FileWriter myWriter = new FileWriter(trimmed_file.getPath());
                if (doc.getDocumentElement().hasChildNodes()) {
                    printNote(doc.getDocumentElement().getChildNodes(), myWriter);
                }
                myWriter.close();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }


        }
//        else{
//            System.out.println("Trimmed Dataset present.");
//        }

//        final VHCAlgorithm algorithm = new VHCAlgorithm(sigma, topleft, topright, bottomright, bottomleft, trimmed_file.getPath());
//        Mapper current_mapper = new Mapper("currentLoc", location);
//        geopriv4j.utils.LatLng generated_location = algorithm.generate(current_mapper);
//
//        //Convert to google LatLng object
//        System.out.println("Current Location: " + location);
//        System.out.println("Generated Location: " + generated_location);
//        LatLng converted_location = new LatLng(generated_location.latitude, generated_location.longitude);
//
//        googleMap.addMarker(new MarkerOptions().position(converted_location).title("VHC Algorithm Output").icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(converted_location, 14));
    }

    private static void printNote(NodeList nodeList, FileWriter myWriter) throws IOException {

        for (int count = 0; count < nodeList.getLength(); count++) {
//            System.out.println(count + " out of " + nodeList.getLength());

            Node tempNode = nodeList.item(count);

            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                // get node name and value
//        System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
//        System.out.println("Node Value =" + tempNode.getTextContent());
                if (tempNode.getNodeName().equals("node")) {
                    if (tempNode.hasAttributes()) {

                        // get attributes names and values
                        NamedNodeMap nodeMap = tempNode.getAttributes();

                        for (int i = 0; i < nodeMap.getLength(); i++) {

                            Node node = nodeMap.item(i);
//                System.out.println("attr name : " + node.getNodeName());
//                System.out.println("attr value : " + node.getNodeValue());
                            if (node.getNodeName().equals("id"))
                                myWriter.write(node.getNodeValue() + "\n");
                            if (node.getNodeName().equals("lat"))
                                myWriter.write(node.getNodeValue() + "\n");
                            if (node.getNodeName().equals("lon"))
                                myWriter.write(node.getNodeValue() + "\n");


                        }
                        myWriter.write("\n");
                    }
                }
            }
        }
    }
}