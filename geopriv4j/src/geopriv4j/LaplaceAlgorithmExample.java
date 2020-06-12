/* 
 * This is a Laplace Algorithm Example class.
 */

package geopriv4j;

import java.io.IOException; 
import java.util.ArrayList;
import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;


public class LaplaceAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException {


		// ∊ value for ∊-differential privacy
		double epsilon = 0.001;

		LaplaceAlgorithm algorithm = new LaplaceAlgorithm(epsilon);

		// change this variable to pick 1000, 5000, 10000 synthetic points
		int data = 5000;

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
