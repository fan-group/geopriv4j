package geopriv4j;

/*
 * Moving in a Neighborhood (MN) In this algorithm, the next position of the dummy 
 * is decided in a neighborhood of the current position of the dummy.
 * 
 * This method is implemented based on the paper by H. Kido, Y. Yanagisawa and T. Satoh, 
 * "An anonymous communication technique using dummies for location-based services," 
 * ICPS '05. Proceedings. International Conference on Pervasive Services, 2005., 
 * Santorini, Greece, 2005, pp. 88-97.
 */


import java.util.Random;

import geopriv4j.utils.LatLng;

public class MovingInTheNeighbourhoodAlgorithm {
	
	//This method generates new dummy location based on the previous location and offset specified
    public static LatLng generate(double offset, LatLng prev){
        Random random = new Random();
        double l1 = prev.latitude - offset;
        double l2 = prev.latitude + offset;
        double random_lat = l1 + (l2 - l1) * random.nextDouble();
        double ln1 = prev.longitude - offset;
        double ln2 = prev.longitude + offset;
        double random_lng = ln1 + (ln2 - ln1) * random.nextDouble();
        return new LatLng(random_lat,random_lng);
    }
    
}