package geopriv4j;

/* 
 * This is a VHC Algorithm Example class
 * In this we use Various-size-grid Hilbert Curve (VHC)-mapping to project user location 
 * based on the population density in each cell.
 * 
 * This has been implemented based on the paper by Pingley, Aniket, et al. 
 * "Cap: A context-aware privacy protection system for location-based services." 
 * 2009 29th IEEE International Conference on Distributed Computing Systems. IEEE, 2009.
 */

import geopriv4j.utils.LatLng;
import geopriv4j.utils.Mapper;

public class VHCAlgorithmExample {

	public static void main(String[] args) {

		//this is the current user location
		LatLng current_loc = new LatLng(35.3123,-80.7432);

		Mapper current_mapper = new Mapper("currentLoc", current_loc);

		//specify the offset size here
		int sigma = 5;


		//specify all four locations to be considered in this algorithm
		Mapper topleft = new Mapper("topleft",new LatLng(35.3123,-80.7432));
		Mapper topright = new Mapper("topright",new LatLng(35.3123,-80.7199));
		Mapper bottomright = new Mapper("bottomright",new LatLng(35.2945,-80.7199));
		Mapper bottomleft = new Mapper("bottomleft",new LatLng(35.2945,-80.7432));

		//Specify the path to the open Street dataset
		String file = "data/maploc.txt";


		VHCAlgorithm algorithm = new VHCAlgorithm( topleft, topright, bottomright, bottomleft, file);

		LatLng generated_location = algorithm.generate(current_mapper, sigma);

		System.out.println("generated location: "+ generated_location);

	}
}
