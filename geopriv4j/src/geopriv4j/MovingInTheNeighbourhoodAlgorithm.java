package geopriv4j;

import java.util.ArrayList;

/*
 * Moving in a Neighborhood (MN) In this algorithm, the next position of the dummy 
 * is decided in a neighborhood of the current position of the dummy.
 * 
 * This method is implemented based on the paper by H. Kido, Y. Yanagisawa and T. Satoh, 
 * "An anonymous communication technique using dummies for location-based services," 
 * ICPS '05. Proceedings. International Conference on Pervasive Services, 2005., 
 * Santorini, Greece, 2005, pp. 88-97.
 */


import java.util.Random;

import geopriv4j.utils.LatLng;

public class MovingInTheNeighbourhoodAlgorithm {

	//speicfy the topleft and the bottomright locations for the grid 
	public LatLng topleft ;
	public LatLng bottomright ;

	public MovingInTheNeighbourhoodAlgorithm(LatLng topleft, LatLng bottomright) {
		this.topleft = topleft;
		this.bottomright = bottomright;
	}

	//This method generates new dummy locations based on the previous location and offset specified
	public  ArrayList<LatLng> generate(double offset, int n, LatLng current_loc){

		ArrayList<LatLng> dummies = new ArrayList<>();

		dummies.add(current_loc);
		for(int i =0;i<n;i++) {
			int prevIndex = dummies.size() - 1;
			LatLng previous_loaction = dummies.get(prevIndex);
			LatLng generated_location = MN(offset, previous_loaction);
			while(!this.checkBounds(generated_location)) {
				generated_location = MN(offset, previous_loaction);
			}
			dummies.add(generated_location);
		}
		return dummies;
	}


	public static LatLng MN(double offset, LatLng prev) {

		Random random = new Random();
		double l1 = prev.latitude - offset;
		double l2 = prev.latitude + offset;
		double random_lat = l1 + (l2 - l1) * random.nextDouble();
		double ln1 = prev.longitude - offset;
		double ln2 = prev.longitude + offset;
		double random_lng = ln1 + (ln2 - ln1) * random.nextDouble();
		return new LatLng(random_lat,random_lng);

	}


	public boolean checkBounds(LatLng location) {
		if (this.topleft.latitude >= location.latitude && this.bottomright.latitude <= location.latitude
				&& this.topleft.longitude <= location.longitude && this.bottomright.longitude >= location.longitude) {
			return true;
		}

		return false;
	}

}