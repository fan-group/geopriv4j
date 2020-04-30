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

import exception.RadiusException;
import geopriv4j.utils.Constants;
import geopriv4j.utils.LatLng;

public class SpatialCloakingAlgorithm {


	public static LatLng generate(LatLng location, int r, int R) throws RadiusException {


		try {
			
			if(r>=R){
				throw new RadiusException("Please specify r < R");
			}
	
			Random rand = new Random();

			// generating new lat and lng location within the specified radius r
			double random_lat_r = rand.nextInt(r);
			double random_lng_r = rand.nextInt(r);


			//Coordinate offsets in radians
			double lat_in_degrees = random_lat_r/Constants.earth_radius;
			double lng_in_degrees = random_lng_r/(Constants.earth_radius * Math.cos(Math.PI*location.latitude/180));

			if(rand.nextGaussian()<0.5) {
				lat_in_degrees *=-1;
			}

			if(rand.nextGaussian()<0.5) {
				lng_in_degrees *=-1;
			}


			double lat = location.latitude + lat_in_degrees *180/Math.PI;
			double lng = location.longitude + lng_in_degrees *180/Math.PI;

			//new center within the radius r
			LatLng center = new LatLng(lat,lng);


			// generating new lat and lng location within the specified radius R excluding r
			double random_lat_R = rand.nextInt(R-r)+r;
			double random_lng_R = rand.nextInt(R-r)+r;


			//Coordinate offsets in radians
			double lat_R_in_degrees = random_lat_R/Constants.earth_radius;
			double lng_R_in_degrees = random_lng_R/(Constants.earth_radius * Math.cos(Math.PI*center.latitude/180));

			if(rand.nextGaussian()<0.5) {
				lat_R_in_degrees *=-1;
			}

			if(rand.nextGaussian()<0.5) {
				lng_R_in_degrees *=-1;
			}


			double lat_R = center.latitude + lat_R_in_degrees *180/Math.PI;
			double lng_R = center.longitude + lng_R_in_degrees *180/Math.PI;

			//new location within the radius R-r
			LatLng spatialCloaked = new LatLng(lat_R,lng_R);

			return spatialCloaked;
		}
		catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}



}
