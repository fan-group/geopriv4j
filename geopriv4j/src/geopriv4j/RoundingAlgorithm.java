package geopriv4j;


/*
 * We snap each latitude and longitude to the nearest point on a square 
 * grid with spacing ∆ in meters. 
 * 
 * Micinski, Kristopher, Philip Phelps, and Jeffrey S. Foster. 
 * "An empirical study of location truncation on android." 
 * Weather 2 (2013): 21.
 * 
 */


import geopriv4j.utils.Constants;
import geopriv4j.utils.LatLng;

public class RoundingAlgorithm {

	static double slat;
	static double slong;
	double e = 0.0167; 
	
	public RoundingAlgorithm(double s) {
		//initializing the grid spacing based on the offset specified
		slat = s / 111500;
		slong = s * (180 * Math.sqrt(1 - Math.pow(e,2) * Math.pow(Math.sin(slat),2)) / (Math.PI * Constants.earth_radius * Math.cos(slat)));
		
	}
	
	public LatLng generate(LatLng location) {
		
		double latitude = slat * Math.ceil(location.latitude / slat);
		double longitude = slong * Math.ceil(location.longitude / slong);
		LatLng rounded = new LatLng(latitude, longitude);
		return rounded;
		
	}

}
