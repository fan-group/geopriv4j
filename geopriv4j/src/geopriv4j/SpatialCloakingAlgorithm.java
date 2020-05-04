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

	public static LatLng center;
	
	public static int r;
	public static int R;
	
	public SpatialCloakingAlgorithm(LatLng sensitive, int r, int R) throws RadiusException {
			
		try {
			
			if(r>=R){
				throw new RadiusException("Please specify r < R");
			}
			SpatialCloakingAlgorithm.r = r;
			SpatialCloakingAlgorithm.R = R;
			
			Random rand = new Random();

			// generating new lat and lng location within the specified radius r
			double random_lat_r =  rand.nextInt(SpatialCloakingAlgorithm.r);
			double random_lng_r =  rand.nextInt(SpatialCloakingAlgorithm.r);


			//Coordinate offsets in radians
			double lat_in_degrees = random_lat_r/Constants.earth_radius;
			double lng_in_degrees = random_lng_r/(Constants.earth_radius * Math.cos(Math.PI*sensitive.latitude/180));

			if(rand.nextGaussian()<0.5) {
				lat_in_degrees *=-1;
			}

			if(rand.nextGaussian()<0.5) {
				lng_in_degrees *=-1;
			}


			double lat = sensitive.latitude + lat_in_degrees *180/Math.PI;
			double lng = sensitive.longitude + lng_in_degrees *180/Math.PI;

			//new center within the radius r
			 center = new LatLng(lat,lng);

		}
		catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
		
	}
	
	
	public  LatLng generate(LatLng location) {
		
		double difflat = Math.abs(Math.toRadians(center.latitude) - Math.toRadians(location.latitude));
		double difflng = Math.abs(Math.toRadians(center.longitude) - Math.toRadians(location.longitude));
		double result = Math.pow(Math.sin(difflat / 2), 2) + Math.cos(Math.toRadians(center.latitude))
		* Math.cos(Math.toRadians(location.latitude)) * Math.pow(Math.sin(difflng / 2), 2);
		result = 2 * Math.asin(Math.sqrt(result));
		double distance = result * Constants.earth_radius;
		
		if(R > distance) {
			return null;
		}
		
		return location;
	}

}
