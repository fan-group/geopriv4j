package geopriv4j;

/*
 * In this we use Various-size-grid Hilbert Curve (VHC)-mapping to project user location 
 * based on the population density in each cell.
 * 
 * This has been implemented based on the paper by Pingley, Aniket, et al. 
 * "Cap: A context-aware privacy protection system for location-based services." 
 * 2009 29th IEEE International Conference on Distributed Computing Systems. IEEE, 2009.
 * 
 * 
 * In the original paper the authors partition a cell into 4 equal-size 
 * subcells iff the total road length (in the original space) 
 * covered by the cell is at least µ times the edge length of 
 * the cell, where µ > 1 is a pre-determined granularity ratio.
 * 
 * In this implementation we divide the cell into 4 equal-size subcells iff
 * the total nodes present is greater than a threshold set by VHC_LIMIT. 
 * We have captured the nodes within the boundary from OpenStreetMaps and data 
 * is present in maploc.txt.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import geopriv4j.utils.Constants;
import geopriv4j.utils.LatLng;
import geopriv4j.utils.Mapper;
import geopriv4j.utils.OpenStreetMapFileReader;

public class VHCUpdatedAlgorithm {

	public static Map<Integer, ArrayList<Mapper>> vhcmap = new HashMap<>();
	public static Map<Integer, ArrayList<Double>> ranges = new HashMap<>();

	// Specify the threshold for each cell in VHC algorithm
	public static int VHC_LIMIT = 500;

	public double sigma;

	public VHCUpdatedAlgorithm(double sigma, Mapper topleft, Mapper topright, Mapper bottomright, Mapper bottomleft,
			String file) {

		this.sigma = sigma;

		// read data obtained from openStreetMap
		ArrayList<Mapper> mappers = OpenStreetMapFileReader.readFile(file);
		ArrayList<Mapper> coordinates = new ArrayList<>();
		coordinates.add(topleft);
		coordinates.add(topright);
		coordinates.add(bottomright);
		coordinates.add(bottomleft);
		initiateVhc(mappers, coordinates);
		initiateRanges();

	}

	// create the VHC based on the coordinates specified
	public int initiateVhc(ArrayList<Mapper> mappers, ArrayList<Mapper> coordinates) {

		Mapper topleftMap = coordinates.get(0);
		Mapper toprightMap = coordinates.get(1);
		Mapper bottomrightMap = coordinates.get(2);
		Mapper bottomleftMap = coordinates.get(3);

		// double diff_in_lat = Math.abs(topleftMap.loc.latitude -
		// bottomrightMap.loc.latitude);
		// double diff_in_lng = Math.abs(topleftMap.loc.longitude -
		// bottomrightMap.loc.longitude);

		// double latmeters = difflat/0.0000089;
		// double lngmeters = difflng * Math.cos(m3.loc.latitude * 0.018)/0.0000089;

		int count = 0;

		// Count total nodes in each cell
		for (int i = 0; i < mappers.size(); i++) {
			if (topleftMap.loc.latitude > mappers.get(i).loc.latitude
					&& topleftMap.loc.longitude < mappers.get(i).loc.longitude) {// &&
				// m4.loc.longitude
				// <
				// m.get(i).loc.longitude
				// &&
				// m4.loc.latitude
				// <
				// m.get(i).loc.latitude
				if (bottomrightMap.loc.latitude < mappers.get(i).loc.latitude
						&& bottomrightMap.loc.longitude > mappers.get(i).loc.longitude) {// &&
					// m3.loc.longitude
					// >m.get(i).loc.longitude
					// &&
					// m2.loc.latitude
					// >
					// m.get(i).loc.latitude
					count++;
				}
			}
		}

		// check if the count is below threshold
		if (count > VHC_LIMIT) {// MU * latmeters && count > MU * lngmeters
			double lat = topleftMap.loc.latitude
					- (Math.abs(topleftMap.loc.latitude - bottomrightMap.loc.latitude)) / 2;
			double lng = topleftMap.loc.longitude
					+ (Math.abs(topleftMap.loc.longitude - bottomrightMap.loc.longitude)) / 2;

			ArrayList<Mapper> topLeftSquare_coordinates = new ArrayList<Mapper>();

			// divide the map into four quadrants

			// topleft quadrant
			// Calculate new topright, bottomright and bottomleft location
			Mapper new_TLS_toprightMap = new Mapper("TLS_toprightMap", new LatLng(topleftMap.loc.latitude, lng));
			Mapper new_TLS_bottomrightMap = new Mapper("TLS_bottomrightMap", new LatLng(lat, lng));
			Mapper new_TLS_bottomleftMap = new Mapper("TLS_bottomleftMap", new LatLng(lat, topleftMap.loc.longitude));
			topLeftSquare_coordinates.add(topleftMap);
			topLeftSquare_coordinates.add(new_TLS_toprightMap);
			topLeftSquare_coordinates.add(new_TLS_bottomrightMap);
			topLeftSquare_coordinates.add(new_TLS_bottomleftMap);

			// recursive call with new coordinates
			initiateVhc(mappers, topLeftSquare_coordinates);

			// topright quadrant
			// Calculate new topleft, bottomright and bottomleft location
			ArrayList<Mapper> topRightSquare_coordinates = new ArrayList<Mapper>();
			Mapper new_TRS_topleftMap = new Mapper("TRS_topleftMap", new LatLng(topleftMap.loc.latitude, lng));
			Mapper new_TRS_bottomrightMap = new Mapper("TRS_bottomrightMap",
					new LatLng(lat, toprightMap.loc.longitude));
			Mapper new_TRS_bottomleftMap = new Mapper("TRS_bottomleftMap", new LatLng(lat, lng));
			topRightSquare_coordinates.add(new_TRS_topleftMap);
			topRightSquare_coordinates.add(toprightMap);
			topRightSquare_coordinates.add(new_TRS_bottomrightMap);
			topRightSquare_coordinates.add(new_TRS_bottomleftMap);

			// recursive call with new coordinates
			initiateVhc(mappers, topRightSquare_coordinates);

			// bottomright quadrant
			// Calculate new topleft, topleft and bottomleft location
			ArrayList<Mapper> bottomRightSquare_coordinates = new ArrayList<Mapper>();
			Mapper new_BRS_topleftMap = new Mapper("BRS_topleftMap", new LatLng(lat, lng));
			Mapper new_BRS_toprightMap = new Mapper("BRS_toprightMap", new LatLng(lat, bottomrightMap.loc.longitude));
			Mapper new_BRS_bottomleftMap = new Mapper("BRS_bottomleftMap",
					new LatLng(bottomrightMap.loc.latitude, lng));
			bottomRightSquare_coordinates.add(new_BRS_topleftMap);
			bottomRightSquare_coordinates.add(new_BRS_toprightMap);
			bottomRightSquare_coordinates.add(bottomrightMap);
			bottomRightSquare_coordinates.add(new_BRS_bottomleftMap);

			// recursive call with new coordinates
			initiateVhc(mappers, bottomRightSquare_coordinates);

			// bottomleft quadrant
			// Calculate new topleft, topright and bottomright location
			ArrayList<Mapper> bottomLeftSquare_coordinates = new ArrayList<Mapper>();
			Mapper new_BLS_topleftMap = new Mapper("BLS_topleftMap", new LatLng(lat, bottomleftMap.loc.longitude));
			Mapper new_BLS_toprightMap = new Mapper("BLS_toprightMap", new LatLng(lat, lng));
			Mapper new_BLS_bottomrightMap = new Mapper("BLS_bottomrightMap",
					new LatLng(bottomleftMap.loc.latitude, lng));
			bottomLeftSquare_coordinates.add(new_BLS_topleftMap);
			bottomLeftSquare_coordinates.add(new_BLS_toprightMap);
			bottomLeftSquare_coordinates.add(new_BLS_bottomrightMap);
			bottomLeftSquare_coordinates.add(bottomleftMap);

			// recursive call with new coordinates
			initiateVhc(mappers, bottomLeftSquare_coordinates);

		} else {
			vhcmap.put(vhcmap.size(), coordinates);
			return 0;
		}
		return 0;
	}

	public void initiateRanges() {

		for (int i = 0; i < vhcmap.size(); i++) {
			ArrayList<Mapper> coordinates = vhcmap.get(i);
			Mapper topleftMap = coordinates.get(0);
			Mapper bottomrightMap = coordinates.get(2);

			double offset = Math.abs(topleftMap.loc.longitude - bottomrightMap.loc.longitude);

			if (ranges.containsKey(i - 1)) {
				ArrayList<Double> previous = ranges.get(i - 1);
				ArrayList<Double> newrange = new ArrayList<Double>();
				newrange.add(previous.get(1));
				newrange.add(previous.get(1) + offset);
				ranges.put(i, newrange);
			} else {
				ArrayList<Double> newrange = new ArrayList<Double>();
				newrange.add(0.);
				newrange.add(offset);
				ranges.put(i, newrange);
			}
		}
	}

	/*
	 * In this algorithm we do not report anything if the initial location passed is
	 * out of bounds
	 */

	// generate new location based on the VHC
	public LatLng generate(Mapper mapper) {
		Random random = new Random();
		int result = -1;
		double f_x = 0.;
		int sign = 1;

		if (random.nextGaussian() < 0.5) {
			sign *= -1;
		}

		for (int i = 0; i < vhcmap.size(); i++) {
			ArrayList<Mapper> coordinates = vhcmap.get(i);
			Mapper topleftMap = coordinates.get(0);
			Mapper bottomrightMap = coordinates.get(2);
			if (topleftMap.loc.latitude > mapper.loc.latitude && topleftMap.loc.longitude < mapper.loc.longitude) {
				if (bottomrightMap.loc.latitude < mapper.loc.latitude
						&& bottomrightMap.loc.longitude > mapper.loc.longitude) {

					// getting the range
					ArrayList<Double> range = ranges.get(i);
					double dx = Math.abs(topleftMap.loc.longitude - mapper.loc.longitude);
					// calculating F(x)
					f_x = range.get(0) + dx;
				}
			}
		}

		// adding noise to F(x)
		double randomValue = -this.sigma + 2 * this.sigma * random.nextDouble();

		System.out.println(randomValue);

		f_x += randomValue;

		for (int i = 0; i < ranges.size(); i++) {
			ArrayList<Double> range = ranges.get(i);
			if (f_x >= range.get(0) && f_x < range.get(1)) {
				result = i;
			}
		}

		if (result == -1)
			return null;

		// check if the generated cell is within the grid
		if (result >= vhcmap.size()) {
			ArrayList<Mapper> coordinates = vhcmap.get(vhcmap.size() - 1);
			Mapper topleftMap = coordinates.get(0);
			Mapper bottomrightMap = coordinates.get(2);

			double lat = topleftMap.loc.latitude
					- (Math.abs(topleftMap.loc.latitude - bottomrightMap.loc.latitude)) / 2;
			double lng = topleftMap.loc.longitude
					+ (Math.abs(topleftMap.loc.longitude - bottomrightMap.loc.longitude)) / 2;

			return new LatLng(lat, lng);
		}

		// check if the generated cell is within the grid
		if (result <= 0) {
			ArrayList<Mapper> coordinates = vhcmap.get(0);
			Mapper topleftMap = coordinates.get(0);
			Mapper bottomrightMap = coordinates.get(2);
			double lat = topleftMap.loc.latitude
					- (Math.abs(topleftMap.loc.latitude - bottomrightMap.loc.latitude)) / 2;
			double lng = topleftMap.loc.longitude
					+ (Math.abs(topleftMap.loc.longitude - bottomrightMap.loc.longitude)) / 2;
			return new LatLng(lat, lng);
		}

		else {
			ArrayList<Mapper> coordinates = vhcmap.get(result);
			Mapper topleftMap = coordinates.get(0);
			Mapper bottomrightMap = coordinates.get(2);
			double lat = topleftMap.loc.latitude
					- (Math.abs(topleftMap.loc.latitude - bottomrightMap.loc.latitude)) / 2;
			double lng = topleftMap.loc.longitude
					+ (Math.abs(topleftMap.loc.longitude - bottomrightMap.loc.longitude)) / 2;
			return new LatLng(lat, lng);
		}
	}

}
