package geopriv4j;

/*
 * We generate grids of a certain specified dimensions and report user location in
 * each cell based on a defined probability
 * 
 * This has been implemented based on the paper by D. Quercia, I. Leontiadis, 
 * L. McNamara, C. Mascolo and J. Crowcroft, "SpotME If You Can: Randomized Responses
 * for Location Obfuscation on Mobile Phones," 2011 31st International Conference on
 * Distributed Computing Systems, Minneapolis, MN, 2011, pp. 363-372.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import geopriv4j.utils.Constants;
import geopriv4j.utils.LatLng;

public class SpotMeAlgorithm {

	// specify the grid size
	public int gridSize;

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	// This contains the grids that is generated
	public static Map<Integer, ArrayList<LatLng>> grids = new HashMap<>();

	public LatLng topleft;
	public LatLng bottomright;
	public double probability;

	public SpotMeAlgorithm(LatLng topleft, LatLng bottomright, double probability) {
		this.topleft = topleft;
		this.bottomright = bottomright;
		this.probability = probability;
		this.initiateSpotMe(this.topleft, this.bottomright);
	}

	// created the grid by calculating the cell size and bearing
	private void initiateSpotMe(LatLng topleft, LatLng bottomright) {

		// calculate the cell size
		double difflat = Math.abs(Math.toRadians(topleft.latitude) - Math.toRadians(bottomright.latitude));
		double difflng = Math.abs(Math.toRadians(topleft.longitude) - Math.toRadians(bottomright.longitude));
		double result = Math.pow(Math.sin(difflat / 2), 2) + Math.cos(Math.toRadians(topleft.latitude))
				* Math.cos(Math.toRadians(bottomright.latitude)) * Math.pow(Math.sin(difflng / 2), 2);
		result = 2 * Math.asin(Math.sqrt(result));
		double offset = result * Constants.earth_radius / gridSize;

		// calculate bearing
		double y = Math.sin(Math.toRadians(bottomright.longitude) - Math.toRadians(topleft.longitude))
				* Math.cos(Math.toRadians(bottomright.latitude));
		double x = Math.cos(Math.toRadians(topleft.latitude)) * Math.sin(Math.toRadians(bottomright.latitude))
				- Math.sin(Math.toRadians(topleft.latitude)) * Math.cos(Math.toRadians(bottomright.latitude))
						* Math.cos(Math.toRadians(bottomright.longitude) - Math.toRadians(topleft.longitude));
		double brng = Math.atan2(y, x);

		generateGrids(offset, topleft, brng);

	}

	// generates the grid based on the specified gridsize and the topleft location
	public void generateGrids(double offset, LatLng topleft, double brng) {

		double meters = offset;

		for (int i = 0; i < this.gridSize; i++) {
			for (int j = 0; j < this.gridSize; j++) {

				ArrayList<LatLng> arrayList = new ArrayList<LatLng>();

				// calculate the top left location for each cell

				// convert to radians
				double lat_t1 = Math.toRadians(topleft.latitude);
				double lon_t1 = Math.toRadians(topleft.longitude);

				// calculate the new lcations
				double lat_t2 = Math.asin(Math.sin(lat_t1) * Math.cos((meters * (i)) / Constants.earth_radius)
						+ Math.cos(lat_t1) * Math.sin((meters * (i)) / Constants.earth_radius) * Math.cos(brng));

				double lon_t2 = lon_t1 + Math.atan2(
						Math.sin(brng) * Math.sin((meters * (j)) / Constants.earth_radius) * Math.cos(lat_t1),
						Math.cos((meters * (j)) / Constants.earth_radius) - Math.sin(lat_t1) * Math.sin(lat_t2));

				// convert back to degrees
				double new_tlat = Math.toDegrees(lat_t2);
				double new_tlong = Math.toDegrees(lon_t2);

				arrayList.add(new LatLng(new_tlat, new_tlong));

				// calculate the bottom right location for each cell

				// calculate new location
				double lat_b2 = Math.asin(Math.sin(lat_t1) * Math.cos((meters * (i + 1)) / Constants.earth_radius)
						+ Math.cos(lat_t1) * Math.sin((meters * (i + 1)) / Constants.earth_radius) * Math.cos(brng));

				double lon_b2 = lon_t1 + Math.atan2(
						Math.sin(brng) * Math.sin((meters * (j + 1)) / Constants.earth_radius) * Math.cos(lat_t1),
						Math.cos((meters * (j + 1)) / Constants.earth_radius) - Math.sin(lat_t1) * Math.sin(lat_b2));

				// convert back to degrees
				double new_blat = Math.toDegrees(lat_b2);
				double new_blong = Math.toDegrees(lon_b2);

				arrayList.add(new LatLng(new_blat, new_blong));

				// adding both topleft and bottom right to specified cell in the grid
				grids.put(grids.size(), arrayList);
			}

		}
	}

	/*
	 * In this algorithm we do not report anything if the initial location passed is
	 * out of bounds
	 */

	// this generates new locations based on the current location and specified
	// probability
	public Map<Integer, Boolean> generate(LatLng current) {

		Map<Integer, Boolean> result = new HashMap<>();
		Random random = new Random();

		int count = 0;

		for (Integer r = 0; r < grids.size(); r++) {
			ArrayList<LatLng> locs = grids.get(r);

			// check if the probability is greater then report true
			if (random.nextDouble() < this.probability) {
				result.put(r, true);
				count++;

			} else {
				// check if current user location is in the cell then report true
				if (locs.get(0).latitude >= current.latitude && locs.get(1).latitude <= current.latitude
						&& locs.get(0).longitude <= current.longitude && locs.get(1).longitude >= current.longitude) {
					result.put(r, true);
					count++;
				}
				// report false if not
				else {
					result.put(r, false);
				}
			}
		}
		// System.out.println("reported count "+count);

		return result;
	}

}
