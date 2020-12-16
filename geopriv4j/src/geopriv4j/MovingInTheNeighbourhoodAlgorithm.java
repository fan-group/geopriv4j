package geopriv4j;

import java.util.ArrayList;
import java.util.Collections;

/*
 * Moving in a Neighborhood (MN) In this algorithm, the next position of the dummy 
 * is decided in a neighborhood of the current position of the dummy.
 * 
 * This method is implemented based on the paper by H. Kido, Y. Yanagisawa and T. Satoh, 
 * "An anonymous communication technique using dummies for location-based services," 
 * ICPS '05. Proceedings. International Conference on Pervasive Services, 2005., 
 * Santorini, Greece, 2005, pp. 88-97.
 * 
 */

import java.util.Random;

import geopriv4j.utils.LatLng;

public class MovingInTheNeighbourhoodAlgorithm {

	// Specify the topleft and the bottomright locations for the grid
	public LatLng topleft;
	public LatLng bottomright;
	public double offset;
	public int n;
	public int max_trial=20;
	public ArrayList<LatLng> previous = new ArrayList<>();

	public MovingInTheNeighbourhoodAlgorithm(LatLng topleft, LatLng bottomright, double offset, int n) {
		this.topleft = topleft;
		this.bottomright = bottomright;
		this.offset = offset;
		this.n = n;
	}
	


	/*
	 * In this algorithm we do not report anything if the initial location passed is
	 * out of bounds
	 */

	// This method generates new dummy locations based on the previous location and
	// offset specified	
	public ArrayList<LatLng> generate(LatLng current_loc) {

		ArrayList<LatLng> dummies = new ArrayList<>();

		// check if the location is within bounds
		if (!this.checkBounds(current_loc))
			return null;

		if (this.previous.size() == 0) {
			dummies.add(current_loc);
			for (int i = 0; i < this.n - 1; i++) {
				dummies.add(randLoc());
			}
		}
		
		else {

			dummies.add(current_loc);

			for (int i = 1; i < this.n; i++) {
				//			int prevIndex = dummies.size() - 1;
				LatLng previous_location = this.previous.get(i);
				LatLng generated_location = MN(this.offset, previous_location);
				
				int counter = 0;
				while (!this.checkBounds(generated_location) && counter <= this.max_trial) {
					counter++;
					generated_location = MN(this.offset, previous_location);
				}
				
				dummies.add(generated_location);
			}
		}

		// TO-DO shuffle the dummies
//		Collections.shuffle(dummies, new Random(this.n));
		this.previous = dummies;
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
		return new LatLng(random_lat, random_lng);
	}

	public boolean checkBounds(LatLng location) {
		if (this.topleft.latitude >= location.latitude && this.bottomright.latitude <= location.latitude
				&& this.topleft.longitude <= location.longitude && this.bottomright.longitude >= location.longitude) {
			return true;
		}

		return false;
	}
	
	public LatLng randLoc() {
		double lat1 = topleft.latitude;
		double lat2 = bottomright.latitude;
		
		double long1 = topleft.longitude;
		double long2 = bottomright.longitude;
		
		Random random = new Random();
		double r = random.nextDouble();
		double new_lat = r*lat1 + (1-r)*lat2;
		double new_long = r*long1 + (1-r)*long2;
		
		return new LatLng(new_lat, new_long);
	}
}