package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;

/*
 * This is a SpotMe Algorithm Example Class
 * We generate grids of a certain specified dimensions and report user location in
 * each cell based on a defined probability
 * 
 * This has been implemented based on the paper by D. Quercia, I. Leontiadis, 
 * L. McNamara, C. Mascolo and J. Crowcroft, "SpotME If You Can: Randomized Responses
 * for Location Obfuscation on Mobile Phones," 2011 31st International Conference on
 * Distributed Computing Systems, Minneapolis, MN, 2011, pp. 363-372.
 */

import java.util.Map;

import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;

public class SpotMeAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		//specify the probability for reporting true
		double probability = 0.01;

		//		//this is the current user location
		//		LatLng current_loc = new LatLng(35.3123,-80.7432);

		//Specify the topleft and the bottomright locations for the grid 
		LatLng topleft = new LatLng(35.312266, -80.743184);
		LatLng bottomright = new LatLng(35.2944838,-80.71985850859298);

		SpotMeAlgorithm algorithm = new SpotMeAlgorithm(topleft, bottomright, probability);

		//change this variable to pick 1000, 5000, 10000 dummy points
		int data = 10000;

		ArrayList<LatLng> locations = DataHandler.readData("data/"+data+".txt");

		long startTime = System.currentTimeMillis();

		for(int i=0;i<locations.size();i++) {

			Map<Integer, Boolean> generated_location = algorithm.generate(locations.get(i));

			//	System.out.println("Generated locations: "+ generated_location);
		}

		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("run time : "+totalTime);
	}
}
