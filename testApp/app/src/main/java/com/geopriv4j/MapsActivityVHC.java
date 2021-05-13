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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import geopriv4j.utils.Mapper;

import geopriv4j.*;
import geopriv4j.VHCAlgorithm;


public class MapsActivityVHC extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;

    //this is used to get the current latitude and longitude
    private double currentLatitude = 0.0, currentLongitude = 0.0;
    //if this is set to false then there won't be any location updates
    private boolean requestingLocationUpdates = true;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    //specify the sample rate to generate locations
    public static int SAMPLERATE = 10;

    public VHCAlgorithm algorithm;

    public Mapper topleft;
    public Mapper topright;
    public Mapper bottomright;
    public Mapper bottomleft;

    public File osm_dataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // request for permission
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
                        mapFragment.getMapAsync(MapsActivityVHC.this);

                    }

                }
            };
        }

        //Declare path to dataset
        osm_dataset = new File(getFilesDir() + "/osm_dataset.txt");

        //Declare sigma value for algorithm
        int sigma = 5000;

        //Declare boundaries for the city, these bounds can be no larger than the bounds of your OSM node dataset.
        topleft = new Mapper("topleft", new geopriv4j.utils.LatLng(35.3630, -80.9845));
        topright = new Mapper("topright", new geopriv4j.utils.LatLng(35.3630, -80.6134));
        bottomright = new Mapper("bottomright", new geopriv4j.utils.LatLng(35.1427, -80.6134));
        bottomleft = new Mapper("bottomleft", new geopriv4j.utils.LatLng(35.1427, -80.9845));

        //Set thread policy to allow network call in main thread for OpenStreetMap API call
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*
        Application will check if an existing OpenStreetMap node dataset is present. On first run, this will create the needed dataset
        and format it as expected by the algorithm. This will take a while depending on the size and density of the test location.
         */

        if (!osm_dataset.exists()) {
            try {
                System.out.println("--------RUNNING INITIAL DATASET CREATION--------");
                generateDataset(topleft, bottomright);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        //Initiate the algorithm
        algorithm = new VHCAlgorithm(sigma, topleft, topright, bottomright, bottomleft, osm_dataset.getPath());
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
        VHC Algorithm Example
        See onCreate() method for initialization
    */
        //Code for visualizing the boundaries you set
        LatLng topleft_LatLng = new LatLng(topleft.loc.latitude, topleft.loc.longitude);
        LatLng bottomright_LatLng = new LatLng(bottomright.loc.latitude, bottomright.loc.longitude);
        LatLng topright_LatLng = new LatLng(topright.loc.latitude, topright.loc.longitude);
        LatLng bottomleft_LatLng = new LatLng(bottomleft.loc.latitude, bottomleft.loc.longitude);
        googleMap.addMarker(new MarkerOptions().position(topleft_LatLng).title("Top Left").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
        googleMap.addMarker(new MarkerOptions().position(bottomright_LatLng).title("Bottom Right").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
        googleMap.addMarker(new MarkerOptions().position(topright_LatLng).title("Top Right").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
        googleMap.addMarker(new MarkerOptions().position(bottomleft_LatLng).title("Bottom Left").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));

        //Declare mapper at current location
        Mapper current_mapper = new Mapper("currentLoc", location);

        //Generate dummy location
        geopriv4j.utils.LatLng generated_location = algorithm.generate(current_mapper);

        //Convert to google LatLng object
        LatLng converted_location = new LatLng(generated_location.latitude, generated_location.longitude);

        //Plot generated location on map
        googleMap.addMarker(new MarkerOptions().position(converted_location).title("VHC Algorithm Output").icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(converted_location, 14));
    }

    //Method for generating dataset of nodes from OpenStreetMap for use by VHCAlgorithm
    public void generateDataset(Mapper topleft, Mapper bottomright) throws IOException, ParserConfigurationException, SAXException {
        File file = new File(getFilesDir() + "/osm_response.xml");

        //Manipulate topLeft and bottomRight LatLng into OSM Bounding Box for Query
        String min_lat = Double.toString(Math.min(topleft.loc.latitude, bottomright.loc.latitude));
        String min_long = Double.toString(Math.min(topleft.loc.longitude, bottomright.loc.longitude));
        String max_lat = Double.toString(Math.max(topleft.loc.latitude, bottomright.loc.latitude));
        String max_long = Double.toString(Math.max(topleft.loc.longitude, bottomright.loc.longitude));

        //Declare bounding box string using previously calculated min and max
        String bbox = min_long + "," + min_lat + "," + max_long + "," + max_lat;

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

            System.out.println("--------------RESPONSE FILE CREATED--------------");
            System.out.println("Length of file before trimming: " + file.length());
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
        try {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            osm_dataset.createNewFile();
            FileWriter myWriter = new FileWriter(osm_dataset.getPath());
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
        System.out.println("---------------FORMATTING DATASET---------------");
        System.out.println("Length of file after trimming: " + osm_dataset.length());
    }

    //Method for formatting OpenStreetMap response xml into dataset usable by VHCAlgorithm
    private static void printNote(NodeList nodeList, FileWriter myWriter) throws IOException {

        for (int count = 0; count < nodeList.getLength(); count++) {

            Node tempNode = nodeList.item(count);

            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                // get node name and value
                if (tempNode.getNodeName().equals("node")) {
                    if (tempNode.hasAttributes()) {

                        // get attributes names and values
                        NamedNodeMap nodeMap = tempNode.getAttributes();

                        for (int i = 0; i < nodeMap.getLength(); i++) {

                            Node node = nodeMap.item(i);

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