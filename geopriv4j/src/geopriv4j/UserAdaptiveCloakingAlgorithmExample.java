package geopriv4j;

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

import geopriv4j.utils.LatLng;
import geopriv4j.utils.Node;

public class UserAdaptiveCloakingAlgorithmExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<LatLng> trajectory = new ArrayList<LatLng>();
		trajectory.add(new LatLng(39.9996120356879, 116.37365828472014));
		trajectory.add(new LatLng(39.94561501577017, 116.44361393370866));
		trajectory.add(new LatLng(39.89157588683963, 116.37338076992393));
		trajectory.add(new LatLng(39.837494795795294, 116.30325936160527));

		// speicfy the topleft and the bottomright locations for the grid
		LatLng topleft = new LatLng(40.0266, 116.1983);
		LatLng bottomright = new LatLng(39.7563, 116.5478);

		double theta = 0.9;

		int alpha_max = 3;

		UserAdaptiveCloakingAlgorithm algorithm = new UserAdaptiveCloakingAlgorithm(theta, alpha_max,
				topleft, bottomright);

		ArrayList<Node> graph = algorithm.generate(trajectory);

		System.out.println("\ngraph : \n");
		algorithm.display(graph);
	}
}
