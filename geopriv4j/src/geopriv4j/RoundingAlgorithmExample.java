package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;

import geopriv4j.utils.DataHandler;

/*
 * This is a Rounding Algorithm Example Class
 * We snap each latitude and longitude to the nearest point on a square 
 * grid with spacing ∆ in meters. 
 * 
 * This method has been implemented from the paper by Krumm, John. 
 * "Inference attacks on location tracks." International Conference on Pervasive Computing. 
 * Springer, Berlin, Heidelberg, 2007. and Micinski, Kristopher, Philip Phelps, and 
 * Jeffrey S. Foster. "An empirical study of location truncation on android." 
 * Weather 2 (2013): 21.
 * 
 */

import geopriv4j.utils.LatLng;

public class RoundingAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		// //this is the current user location
		// LatLng current_loc = new LatLng(35.3123,-80.741);

		// specify the offset in meters
		double s = 500;

		RoundingAlgorithm algorithm = new RoundingAlgorithm(s);

		// change this variable to pick 1000, 5000, 10000 dummy points
		int data = 10000;

		ArrayList<LatLng> locations = DataHandler.readData("data/" + data + ".txt");

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < locations.size(); i++) {

			LatLng generated_location = algorithm.generate(locations.get(i));

			// System.out.println("Genereated location: "+ generated_location);
		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("run time : " + totalTime);
	}
}
