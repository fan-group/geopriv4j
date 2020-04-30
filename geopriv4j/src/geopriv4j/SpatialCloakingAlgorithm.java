package geopriv4j;

/*
 * In this method we are generating new location by choosing a random distance within radius r 
 * and then moving the user location in that direction.
 * 
 * This method has been implemented from the paper by Krumm, John. 
 * "Inference attacks on location tracks." International Conference on Pervasive Computing. 
 * Springer, Berlin, Heidelberg, 2007.
 */

import java.util.Random;

import geopriv4j.utils.Constants;
import geopriv4j.utils.LatLng;

public class SpatialCloakingAlgorithm {


	public static LatLng generate(LatLng location, int delta) {

		Random rand = new Random();

		// generating new lat and lng location within the specified radius
		double random_lat = rand.nextInt(delta);
		double random_lng = rand.nextInt(delta);


		//Coordinate offsets in radians
		double lat_in_degrees = random_lat/Constants.earth_radius;
		double lng_in_degrees = random_lng/(Constants.earth_radius * Math.cos(Math.PI*location.latitude/180));

		if(rand.nextGaussian()<0.5) {
			lat_in_degrees *=-1;
		}

		if(rand.nextGaussian()<0.5) {
			lng_in_degrees *=-1;
		}

		double lat = location.latitude + lat_in_degrees *180/Math.PI;
		double lng = location.longitude + lng_in_degrees *180/Math.PI;

		LatLng spatialCloaked = new LatLng(lat,lng);

		return spatialCloaked;
	}


}