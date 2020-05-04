package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;

import geopriv4j.utils.DataHandler;

/* 
 * This is a Laplace Algorithm Example class.
 * In this method we will be generating new location z with a probability p that 
 * reduces as the distance increase within a certain radius r.
 * 
 * We implemented this algorithm based on the paper by Andrés, Miguel E., et al. 
 * "Geo-indistinguishability: Differential privacy for location-based systems." 
 * Proceedings of the 2013 ACM SIGSAC conference on Computer & communications 
 * security. 2013.
 */

import geopriv4j.utils.LatLng;

public class LaplaceAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException {


		//		//this is the current user location
		//		LatLng current_loc = new LatLng(35.3123,-80.7432);

		// ∊ value for ∊-differential privacy
		double epsilon = 0.001;

		LaplaceAlgorithm algorithm = new LaplaceAlgorithm(epsilon);

		int data = 10000;

		ArrayList<LatLng> locations = DataHandler.readData("data/"+data+"_dummies.txt");
		
		for(int i=0;i<locations.size();i++) {
			LatLng generated_noise = algorithm.generate(locations.get(i));

			System.out.println("Genereated noise: "+ generated_noise);
		}

	}

}
