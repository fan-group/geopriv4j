package geopriv4j;

import java.io.IOException;

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

import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;

public class MovingInTheNeighbourhoodAlgorithmExample {
	//Moving in the neighborhood
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		//Number of dummies to be generated 
		int n = 5;

		//distance between generated locations
		double offset = 0.001;

		//		//this is the current user location
		//		LatLng initial_location = new LatLng(35.3123, -80.7432);

		LatLng topleft = new LatLng(35.312266, -80.743184);
		LatLng bottomright = new LatLng(35.2944838,-80.71985850859298);

		//change this variable to pick 1000, 5000, 10000 dummy points
		int data = 10000;

		ArrayList<LatLng> locations = DataHandler.readData("data/"+data+"_dummies.txt");
		MovingInTheNeighbourhoodAlgorithm algorithm = new MovingInTheNeighbourhoodAlgorithm(topleft, bottomright);

		for(int i=0;i<locations.size();i++) {

			ArrayList<LatLng> dummies = algorithm.generate(offset, n, locations.get(i));

			System.out.println("generated location: "+dummies);
		}

	}
}
