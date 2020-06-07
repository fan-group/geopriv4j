package geopriv4j;

/*
 * In this method we will be generating new location z with a probability p that 
 * reduces as the distance increase within a certain radius r.
 * 
 * We implemented this algorithm based on the paper by Andrés, Miguel E., et al. 
 * "Geo-indistinguishability: Differential privacy for location-based systems." 
 * Proceedings of the 2013 ACM SIGSAC conference on Computer & communications 
 * security. 2013.
 */

import geopriv4j.utils.Cartesian;
import geopriv4j.utils.Constants;
import geopriv4j.utils.LatLng;

public class LaplaceAlgorithm {

	public double epsilon;

	public LaplaceAlgorithm(double epsilon) {

		this.epsilon = epsilon;

	}

	public static double rad_of_deg(double ang) {
		return ang * Math.PI / 180;
	}

	public static double deg_of_rad(double ang) {
		return ang * 180 / Math.PI;
	}

	public static LatLng getLatLon(Cartesian cart) {
		double rLon = cart.x / Constants.earth_radius;
		double rLat = 2 * (Math.atan(Math.exp(cart.y / Constants.earth_radius))) - Math.PI / 2;
		// convert to degrees
		LatLng latlng = new LatLng(deg_of_rad(rLat), deg_of_rad(rLon));
		return latlng;

	}

	public static Cartesian getCartesian(LatLng location) {
		// latitude and longitude are converted in radiants
		Cartesian cartesian = new Cartesian();

		cartesian.x = Constants.earth_radius * rad_of_deg(location.longitude);
		cartesian.y = Constants.earth_radius * Math.log(Math.tan(Math.PI / 4 + rad_of_deg(location.latitude) / 2));
		return cartesian;
	}

	public static double LambertW(double x) {
		// min_diff decides when the while loop should stop
		double min_diff = 1e-10;
		if (x == -1 / Math.E) {
			return -1;
		}

		else if (x < 0 && x > -1 / Math.E) {
			double q = Math.log(-x);
			double p = 1;
			while (Math.abs(p - q) > min_diff) {
				p = (q * q + x / Math.exp(q)) / (q + 1);
				q = (p * p + x / Math.exp(p)) / (p + 1);
			}
			// This line decides the precision of the float number that would be returned
			return (Math.round(1000000 * q) / 1000000);
		} else if (x == 0) {
			return 0;
		} else {
			return 0;
		}
	}

	public static double inverseCumulativeGamma(double epsilon, double z) {
		double x = (z - 1) / Math.E;
		return -(LambertW(x) + 1) / epsilon;
	}

	public static double alphaDeltaAccuracy(double epsilon, double delta) {
		return inverseCumulativeGamma(epsilon, delta);
	}

	public static double expectedError(double epsilon) {
		return 2 / epsilon;
	}

	public static LatLng addPolarNoise(double epsilon, LatLng pos) {
		// random number in [0, 2*PI)
		double theta = Math.random() * Math.PI * 2;
		// random variable in [0,1)
		double z = Math.random();
		double r = inverseCumulativeGamma(epsilon, z);

		return addVectorToPos(pos, r, theta);
	}

	public static LatLng addPolarNoiseCartesian(double epsilon, LatLng pos) {

		Cartesian cartpos = getCartesian(pos);

		// random number in [0, 2*PI)
		double theta = Math.random() * Math.PI * 2;
		// random variable in [0,1)
		double z = Math.random();
		double r = inverseCumulativeGamma(epsilon, z);

		Cartesian cartesian = new Cartesian();

		cartesian.x = cartpos.x + r * Math.cos(theta);
		cartesian.y = cartpos.y + r * Math.sin(theta);
		return getLatLon(cartesian);
	}

	public static LatLng addVectorToPos(LatLng pos, double distance, double angle) {
		double ang_distance = distance / Constants.earth_radius;
		double lat1 = rad_of_deg(pos.latitude);
		double lon1 = rad_of_deg(pos.longitude);

		double lat2 = Math.asin(
				Math.sin(lat1) * Math.cos(ang_distance) + Math.cos(lat1) * Math.sin(ang_distance) * Math.cos(angle));
		double lon2 = lon1 + Math.atan2(Math.sin(angle) * Math.sin(ang_distance) * Math.cos(lat1),
				Math.cos(ang_distance) - Math.sin(lat1) * Math.sin(lat2));
		lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI; // normalise to -180..+180
		LatLng latLng = new LatLng(deg_of_rad(lat2), deg_of_rad(lon2));

		return latLng;
	}

	public LatLng generate(LatLng location) {
		return addPolarNoise(this.epsilon, location);
	}

}
