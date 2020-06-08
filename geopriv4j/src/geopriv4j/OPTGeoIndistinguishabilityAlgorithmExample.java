package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;

import geopriv4j.utils.DataHandler;

/*
 * In this algorithm we show that, given a desired degree of geo-indistinguishability, 
 * it is possible to construct a mechanism that minimizes the service quality loss, 
 * using linear programming techniques. 
 * 
 * We implemented this algorithm based on the paper by Bordenabe, Nicolás E.,
 * Konstantinos Chatzikokolakis, and Catuscia Palamidessi. "Optimal geo-indistinguishable
 * mechanisms for location privacy." Proceedings of the 2014 ACM SIGSAC conference
 * on computer and communications security. 2014.
 */

import geopriv4j.utils.LatLng;

public class OPTGeoIndistinguishabilityAlgorithmExample {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// this is the current user location
		// LatLng current_loc = new LatLng(35.3122, -80.72985850);

		// ∊ value for ∊-differential privacy
		double epsilon = 1f;
		double delta = 1.f;

		// Specify the topleft and the bottomright locations for the grid
		LatLng topleft = new LatLng(35.3123, -80.7432);
		LatLng bottomright = new LatLng(35.2944838, -80.71985850859298);

		OPTGeoIndistinguishabilityAlgorithm algorithm = new OPTGeoIndistinguishabilityAlgorithm(topleft, bottomright,
				epsilon, delta);

		// change this variable to pick 1000, 5000, 10000 dummy points
		int data = 10000;

		ArrayList<LatLng> locations = DataHandler.readData("data/" + data + ".txt");

		float[] prior = OPTGeoIndistinguishabilityAlgorithm.getProbabilities(locations);
		algorithm.initializeK(prior);

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < locations.size(); i++) {
			LatLng generated_location = algorithm.generate(locations.get(i));
			System.out.println("generated location: " + generated_location);
		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		System.out.println("run time : " + totalTime);

	}
}
