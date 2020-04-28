package geopriv4j;

/* 
 * This is a Planar Laplace Algorithm Example class.
 * In this method we will be generating new location z with a probability p that 
 * reduces as the distance increase within a certain radius r.
 * 
 * We implemented this algorithm based on the paper by Andrés, Miguel E., et al. 
 * "Geo-indistinguishability: Differential privacy for location-based systems." 
 * Proceedings of the 2013 ACM SIGSAC conference on Computer & communications 
 * security. 2013.
 */

import geopriv4j.utils.LatLng;

public class PlanarLaplaceAlgorithmExample {
	
	public static void main(String[] args) {
		
		//this is the current user location
		LatLng current_loc = new LatLng(35.3123,-80.7432);
		
		// ∊ value for ∊-differential privacy
		double epsilon = 0.001;
    	
		LatLng generated_noise = PlanarLaplaceAlgorithm.generate(epsilon, current_loc);
		
		System.out.println("Genereated noise: "+ generated_noise);
		
	}
	
}
