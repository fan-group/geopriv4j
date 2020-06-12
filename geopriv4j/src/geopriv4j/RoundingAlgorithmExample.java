/*
 * This is a Rounding Algorithm Example Class
 */

package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;
import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;

public class RoundingAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		// specify the offset in meters
		double s = 500;

		RoundingAlgorithm algorithm = new RoundingAlgorithm(s);

		// change this variable to pick 1000, 5000, 10000 synthetic points
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
