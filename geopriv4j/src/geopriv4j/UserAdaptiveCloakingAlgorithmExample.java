package geopriv4j;

import java.io.IOException;

/*
 * In this algorithm we want to let people have the privacy protection level they desire. 
 * In order to provide this, they define θ, the personal privacy threshold, 
 * which expresses the desired level (i.e., the lower bound) of expected distortion 
 * (i.e., distance) from the actual user location. This privacy threshold depends 
 * on the user’s sensitivity about their privacy at a particular location, and it 
 * can be chosen by a user-specific function of the desired absolute distance from 
 * the sensitive location.
 * 
 * We implemented this algorithm based on the paper by Agir, Berker, et al. "User-side 
 * adaptive protection of location privacy in participatory sensing." GeoInformatica 
 * 18.1 (2014): 165-191.
 */

import java.util.ArrayList;

import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;
import geopriv4j.utils.Node;

public class UserAdaptiveCloakingAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException, CloneNotSupportedException {

		// Specify the topleft and the bottomright locations for the grid
		LatLng topleft = new LatLng(35.3123, -80.7432);
		LatLng bottomright = new LatLng(35.2944838, -80.71985850859298);

		double theta = 0.1;

		int alpha_max = 3;

		UserAdaptiveCloakingAlgorithm algorithm = new UserAdaptiveCloakingAlgorithm(theta, alpha_max, topleft,
				bottomright);

		// change this variable to pick 1000, 5000, 10000 dummy points
		int data = 1000;

		ArrayList<LatLng> locations = DataHandler.readData("data/" + data + ".txt");

		long startTime = System.currentTimeMillis();

		ArrayList<Node> graph = algorithm.generate(locations);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		System.out.println("\n graph : \n");
		UserAdaptiveCloakingAlgorithm.display(graph);

		System.out.println("run time : " + totalTime);

	}
}
