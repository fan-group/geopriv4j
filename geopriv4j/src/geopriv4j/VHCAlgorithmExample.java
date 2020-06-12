/* 
 * This is a VHC Algorithm Example class
 */

package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;
import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;
import geopriv4j.utils.Mapper;

public class VHCAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		// specify the offset size here
		int sigma = 5;

		// specify all four locations to be considered in this algorithm
		Mapper topleft = new Mapper("topleft", new LatLng(35.3123, -80.7432));
		Mapper topright = new Mapper("topright", new LatLng(35.3123, -80.7199));
		Mapper bottomright = new Mapper("bottomright", new LatLng(35.2945, -80.7199));
		Mapper bottomleft = new Mapper("bottomleft", new LatLng(35.2945, -80.7432));

		// Specify the path to the open Street dataset
		String file = "data/maploc.txt";

		VHCAlgorithm algorithm = new VHCAlgorithm(sigma, topleft, topright, bottomright, bottomleft, file);

		// change this variable to pick 1000, 5000, 10000 synthetic points
		int data = 5000;

		ArrayList<LatLng> locations = DataHandler.readData("data/" + data + ".txt");

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < locations.size(); i++) {

			Mapper current_mapper = new Mapper("currentLoc", locations.get(i));

			LatLng generated_location = algorithm.generate(current_mapper);

			// System.out.println("Generated location: "+ generated_location);
		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("run time : " + totalTime);
	}
}
