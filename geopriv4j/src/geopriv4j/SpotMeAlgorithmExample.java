package geopriv4j;

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

import geopriv4j.utils.LatLng;

public class SpotMeAlgorithmExample {

	public static void main(String[] args) {

		//specify the probability for reporting true
		double probability = 0.01;

		//this is the current user location
		LatLng current_loc = new LatLng(35.3123,-80.7432);

		SpotMeAlgorithm algorithm = new SpotMeAlgorithm();
		Map<Integer, Boolean> reported_locations = algorithm.generate(current_loc, probability);

		System.out.println("reported locations: "+ reported_locations);

	}
}