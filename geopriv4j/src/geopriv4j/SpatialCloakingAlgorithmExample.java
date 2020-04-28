package geopriv4j;

/*
 * This is a Spatial Cloaking Algorithm Example Class
 * In this method we are generating new location by choosing a random distance within radius r 
 * and then moving the user location in that direction.
 * 
 * This method has been implemented from the paper by Krumm, John. 
 * "Inference attacks on location tracks." International Conference on Pervasive Computing. 
 * Springer, Berlin, Heidelberg, 2007.
 */

import geopriv4j.utils.LatLng;

public class SpatialCloakingAlgorithmExample {

	public static void main(String[] args) {
		
		//this is the current user location
		LatLng current_loc = new LatLng(35.3123,-80.7432);
		
		//specify the radius in meters
		int radius = 500;
		
		LatLng generated_spatialCloaked_location = SpatialCloakingAlgorithm.generate(current_loc, radius); 
		
		System.out.println("genereated noise: "+ generated_spatialCloaked_location);
	}
}
