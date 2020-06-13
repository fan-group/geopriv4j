/*
 * This is OPT Geo Ind Example class
 */

package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;
import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;
import geopriv4j.utils.Mapper;
import geopriv4j.utils.OpenStreetMapFileReader;

public class OPTGeoIndAlgorithmExample {
	public static void main(String[] args) throws ClassNotFoundException, IOException {

		// ∊ value for ∊-differential privacy
		double epsilon = 1f;
		double delta = 1.f;

		// Specify the topleft and the bottomright locations for the grid
		LatLng topleft = new LatLng(35.3123, -80.7432);
		LatLng bottomright = new LatLng(35.2944838, -80.71985850859298);

		OPTGeoIndAlgorithm algorithm = new OPTGeoIndAlgorithm(topleft, bottomright, epsilon, delta);

		// change this variable to pick 1000, 5000, 10000 synthetic points
		int data = 10000;

		ArrayList<LatLng> locations = DataHandler.readData("data/" + data + ".txt");
		
		String file = "data/maploc.txt";
		ArrayList<Mapper> mappers = OpenStreetMapFileReader.readFile(file);
		
		float[] prior = OPTGeoIndAlgorithm.getProbabilities(mappers);
		
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
