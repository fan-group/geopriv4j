package geopriv4j;

/*
 * In this algorithm we show that, given a desired degree of geo-indistinguishability, 
 * it is possible to construct a mechanism that minimizes the service quality loss, 
 * using linear programming techniques. 
 * 
 * We implemented this algorithm based on the paper by Bordenabe, Nicolás E.,
 * Konstantinos Chatzikokolakis, and Catuscia Palamidessi. "Optimal geo-indistinguishable
 * mechanisms for location privacy." Proceedings of the 2014 ACM SIGSAC conference
 * on computer and communications security. 2014.
 */

import geopriv4j.utils.LatLng;

public class OPTGeoIndistinguishabilityAlgorithmExample {
	public static void main(String[] args) {
		// this is the current user location
		LatLng current_loc = new LatLng(40.026, 116.1983);

		float prior[] = new float[] { 0.1f, 0.8f, 0.3f, 0.8f, 0.1f, 0.5f, 0.8f, 0.3f, 0.4f, 0.8f, 0.5f, 0.2f, 0.8f,
				0.4f, 0.1f, 0.5f, 0.8f, 0.3f, 0.8f, 0.1f, 0.5f, 0.8f, 0.3f, 0.4f, 0.8f };

		// ∊ value for ∊-differential privacy
		double epsilon = 1f;
		double delta = 1.f;

		// speicfy the topleft and the bottomright locations for the grid
		LatLng topleft = new LatLng(40.0266, 116.1983);
		LatLng bottomright = new LatLng(39.7563, 116.5478);

		OPTGeoIndistinguishabilityAlgorithm algorithm = new OPTGeoIndistinguishabilityAlgorithm(topleft, bottomright,
				prior, epsilon, delta);

		LatLng generated_location = algorithm.generate(current_loc);
		System.out.println("generated location: " + generated_location);
	}
}
