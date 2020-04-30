package geopriv4j;

/*
 * This is an example class for Moving in a Neighborhood (MN). In this algorithm, 
 * the next position of the dummy is decided in a neighborhood of the current 
 * position of the dummy.
 * 
 * This method is implemented based on the paper by H. Kido, Y. Yanagisawa and T. Satoh, 
 * "An anonymous communication technique using dummies for location-based services," 
 * ICPS '05. Proceedings. International Conference on Pervasive Services, 2005., 
 * Santorini, Greece, 2005, pp. 88-97.
 */

import java.util.ArrayList;

import geopriv4j.utils.LatLng;

public class MovingInTheNeighbourhoodAlgorithmExample {
	//Moving in the neighborhood
    public static void main(String[] args) {
    	
    	//Number of dummies to be generated 
    	int n = 5;
    	
    	//distance between generated locations
    	double offset = 0.01;
    	
		//this is the current user location
		LatLng current_loc = new LatLng(35.3123,-80.7432);
    	
		ArrayList<LatLng> dummies = MovingInTheNeighbourhoodAlgorithm.generate(offset,n, current_loc);
		
		System.out.println("generated location: "+dummies);
		
	}
}
