/*
 * This is Adaptive Cloaking Algorithm example class.
 */

package geopriv4j;

import java.io.IOException;
import java.util.ArrayList;
import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;
import geopriv4j.utils.Node;

public class AdaptiveCloakingAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException, CloneNotSupportedException {

		// Specify the topleft and the bottomright locations for the grid
		LatLng topleft = new LatLng(35.3123, -80.7432);
		LatLng bottomright = new LatLng(35.2944838, -80.71985850859298);

		double theta = 0.1;

		int alpha_max = 3;

		AdaptiveCloakingAlgorithm algorithm = new AdaptiveCloakingAlgorithm(theta, alpha_max, topleft, bottomright);

		// change this variable to pick 1000, 5000, 10000 synthetic points
		int data = 1000;

		ArrayList<LatLng> locations = DataHandler.readData("data/" + data + ".txt");

		long startTime = System.currentTimeMillis();
		ArrayList<Node> graph = new ArrayList<Node>();
		
		// speicify the size of each time slot for the graph 
		int time_slots = 25;
		
		for (int i = 0; i < locations.size(); i++) { 
			System.out.println("timestamp: " + i);
			int timestamp = i % time_slots;
			if (timestamp == 0) {
				graph = new ArrayList<Node>();
			}
			graph = algorithm.generate(locations.get(i), timestamp, graph);
			// System.out.println("generated location: " + graph);

			System.out.println("\n graph : \n");
			AdaptiveCloakingAlgorithm.display(graph);
		}
		// ArrayList<Node> graph = algorithm.generate(locations);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		System.out.println("run time : " + totalTime);

	}
}
