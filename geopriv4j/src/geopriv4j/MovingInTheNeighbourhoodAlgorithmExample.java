/*
 * This is an example class for Moving in a Neighborhood (MN).
 */

package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;
import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;

public class MovingInTheNeighbourhoodAlgorithmExample {
	// Moving in the neighborhood
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		// Number of dummies to be generated
		int n = 5;

		// distance between generated locations
		double offset = 0.001;

		LatLng topleft = new LatLng(35.312266, -80.743184);
		LatLng bottomright = new LatLng(35.2944838, -80.71985850859298);

		// change this variable to pick 1000, 5000, 10000 synthetic points
		int data = 10000;

		ArrayList<LatLng> locations = DataHandler.readData("data/" + data + ".txt");
		MovingInTheNeighbourhoodAlgorithm algorithm = new MovingInTheNeighbourhoodAlgorithm(topleft, bottomright,
				offset, n);

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < locations.size(); i++) {

			ArrayList<LatLng> generated_location = algorithm.generate(locations.get(i));

			// System.out.println("Generated location: "+generated_location);
		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("run time : " + totalTime);

	}
}
