package geopriv4j;

/*
 * This is a Rounding Algorithm Example Class
 * We snap each latitude and longitude to the nearest point on a square 
 * grid with spacing ∆ in meters. 
 * 
 * This method has been implemented from the paper by Krumm, John. 
 * "Inference attacks on location tracks." International Conference on Pervasive Computing. 
 * Springer, Berlin, Heidelberg, 2007.
 */

import geopriv4j.utils.LatLng;

public class RoundingAlgorithmExample {
	
	public static void main(String[] args) {
		
		//this is the current user location
		LatLng current_loc = new LatLng(35.3123,-80.7432);
		
		//specify the offset in meters
		double delta = 500;
		
		LatLng generated_rounded_location = RoundingAlgorithm.generate(current_loc, delta); 
		
		System.out.println("genereated noise: "+ generated_rounded_location);
	}
}
