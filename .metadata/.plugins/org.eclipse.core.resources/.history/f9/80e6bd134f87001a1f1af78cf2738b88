package geopriv4j.utils;

/*
 * this is used to read the OpenStreetMap dataset
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class VhcFileReader {
	
	
	public static ArrayList<Mapper> readFile(){
		
		ArrayList<Mapper> mappers = new ArrayList<Mapper>();
	    BufferedReader read = null;
        String text=null;

	    
	    try {
	        read = new BufferedReader(
	                new InputStreamReader(new FileInputStream(Constants.FILE)));
	        
	        while((text = read.readLine())!=null) {
	        	
	            if(!text.equals("\n")){
	                String id = text;
	                String lat = read.readLine();
	                String lon = read.readLine();
	                try {
	                    read.readLine();
	                }catch(Exception e) {
	                	e.printStackTrace();
	                }
	
	                Mapper m = new Mapper();
	                m.id = id;
	                m.loc = new LatLng(Double.valueOf(lat),Double.valueOf(lon));
	                mappers.add(m);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return mappers;
	}	
}
