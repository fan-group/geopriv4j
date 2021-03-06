package geopriv4j;

/*
 * We implement noise by simply adding 2D, Gaussian noise to each measured 
 * latitude and longitude coordinate. For each point, we generate a noise vector with a 
 * random uniform direction over [0,2π ) and a Gaussian-distributed magnitude from N(0,σ)^2.
 * A negative magnitude reverses the direction of the noise vector. 
 * 
 * This method has been implemented from the paper by Krumm, John. 
 * "Inference attacks on location tracks." International Conference on Pervasive Computing. 
 * Springer, Berlin, Heidelberg, 2007.
 */

import java.util.Random;

import geopriv4j.utils.Constants;
import geopriv4j.utils.LatLng;

public class NoiseAlgorithm {

	public double variance;

	public NoiseAlgorithm(double variance) {

		this.variance = variance;

	}

	public LatLng generate(LatLng location) {

		Random rand = new Random();

		// Generate a random Gaussian noise with the variance provided
		double noise_lat = rand.nextGaussian() * Math.sqrt(this.variance);
		double noise_lng = rand.nextGaussian() * Math.sqrt(this.variance);

		// offsets in meters
		double random_lat = noise_lat;
		double random_lng = noise_lng;

		// Coordinate offsets in radians
		double lat_in_degrees = random_lat / Constants.earth_radius;
		double lng_in_degrees = random_lng / (Constants.earth_radius * Math.cos(Math.PI * location.latitude / 180));

		// OffsetPosition, decimal degrees
		double lat = location.latitude + lat_in_degrees * 180 / Math.PI;
		double lng = location.longitude + lng_in_degrees * 180 / Math.PI;

		LatLng generated_noise = new LatLng(lat, lng);

		return generated_noise;
	}

}
