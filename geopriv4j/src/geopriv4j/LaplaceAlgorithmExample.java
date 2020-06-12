/* 
 * This is a Laplace Algorithm Example class.
 */

package geopriv4j;

<<<<<<< HEAD
import java.io.IOException;
=======
import java.io.IOException; 
>>>>>>> 19cc36cf79cb74fecace42cec1bb07417c4c0867
import java.util.ArrayList;
import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;


public class LaplaceAlgorithmExample {

	public static void main(String[] args) throws ClassNotFoundException, IOException {

<<<<<<< HEAD
=======

>>>>>>> 19cc36cf79cb74fecace42cec1bb07417c4c0867
		// ∊ value for ∊-differential privacy
		double epsilon = 0.001;

		LaplaceAlgorithm algorithm = new LaplaceAlgorithm(epsilon);

		// change this variable to pick 1000, 5000, 10000 synthetic points
		int data = 5000;

		ArrayList<LatLng> locations = DataHandler.readData("data/" + data + ".txt");

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < locations.size(); i++) {
<<<<<<< HEAD

=======
			
>>>>>>> 19cc36cf79cb74fecace42cec1bb07417c4c0867
			LatLng generated_location = algorithm.generate(locations.get(i));

			// System.out.println("Genereated location: "+ generated_location);
		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("run time : " + totalTime);
	}

}
