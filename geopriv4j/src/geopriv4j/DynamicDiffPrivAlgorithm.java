package geopriv4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import geopriv4j.utils.LatLng;
import geopriv4j.utils.Mapper;
import geopriv4j.utils.OpenStreetMapFileReader;

public class DynamicDiffPrivAlgorithm {

	public static Map<Integer, ArrayList<Mapper>> map = new HashMap<>();

	// Specify the threshold for each cell in VHC algorithm
	public static int gridSize = 4;

	public double epsilon;
	public double Em;

	public DynamicDiffPrivAlgorithm(double epsilon, double Em, Mapper topleft, Mapper topright, Mapper bottomright,
			Mapper bottomleft, String file) {

		int counter = 0;
		this.Em = Em;
		this.epsilon = epsilon;

		// read data obtained from openStreetMap
		ArrayList<Mapper> mappers = OpenStreetMapFileReader.readFile(file);
		ArrayList<Mapper> coordinates = new ArrayList<>();
		coordinates.add(topleft);
		coordinates.add(topright);
		coordinates.add(bottomright);
		coordinates.add(bottomleft);
		initiatehc(mappers, coordinates, counter);

	}

	// create the HC based on the coordinates specified
	public int initiatehc(ArrayList<Mapper> mappers, ArrayList<Mapper> coordinates, int counter) {

		Mapper topleftMap = coordinates.get(0);
		Mapper toprightMap = coordinates.get(1);
		Mapper bottomrightMap = coordinates.get(2);
		Mapper bottomleftMap = coordinates.get(3);

		int count = 0;

		// Count total nodes in each cell
		for (int i = 0; i < mappers.size(); i++) {
			if (topleftMap.loc.latitude > mappers.get(i).loc.latitude
					&& Math.abs(topleftMap.loc.longitude) > Math.abs(mappers.get(i).loc.longitude)) {
				if (bottomrightMap.loc.latitude < mappers.get(i).loc.latitude
						&& Math.abs(bottomrightMap.loc.longitude) < Math.abs(mappers.get(i).loc.longitude)) {
					count++;
				}
			}
		}

		// check if the count is below threshold
		if (counter < gridSize / 2) {
			double lat = topleftMap.loc.latitude
					- (Math.abs(topleftMap.loc.latitude) - Math.abs(bottomrightMap.loc.latitude)) / 2;
			double lng = topleftMap.loc.longitude
					+ (Math.abs(topleftMap.loc.longitude) - Math.abs(bottomrightMap.loc.longitude)) / 2;

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
			initiatehc(mappers, topLeftSquare_coordinates, counter + 1);

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
			initiatehc(mappers, topRightSquare_coordinates, counter + 1);

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
			initiatehc(mappers, bottomRightSquare_coordinates, counter + 1);

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
			initiatehc(mappers, bottomLeftSquare_coordinates, counter + 1);
			return 0;

		} else {
			map.put(map.size(), coordinates);
			return 0;
		}

	}

	/*
	 * In this algorithm we do not report anything if the initial location passed is
	 * out of bounds
	 */

	// generate new location based on the HC
	public LatLng generate(LatLng current, int range) {

		// fetch the open street map locations to pre-compute the prior probabilities
		String file = "data/maploc.txt";
		ArrayList<Mapper> mappers = OpenStreetMapFileReader.readFile(file);

		float[] prior = getProbabilities(mappers);

		int x = getPosition(current);

		ArrayList<Integer> s = new ArrayList<Integer>();

		ArrayList<Map<Integer, LatLng>> L = new ArrayList<Map<Integer, LatLng>>();
		ArrayList<Double> diameter = new ArrayList<Double>();
		if (x != -1) {
			int l = (x - range) >= 0 ? (x - range) : 0;
			int k = (x + range) < gridSize * gridSize ? (x + range) : (gridSize * gridSize) - 1;
			for (int m = l; m <= k; m++) {
				s.add(m);
			}
			Map<Integer, LatLng> phi = new HashMap<Integer, LatLng>();
			for (int i = l; i <= x; i++) {
				for (int j = x; j <= k; j++) {
					for (int r = i; r <= j; r++) {
						phi.put(r, getLocation(r));
					}
					// System.out.println(phi);
					if (phi.size() > 0) {
						Double e = expectedError(phi, prior, i, j);
						if (e >= Math.exp(this.epsilon) * this.Em) {
							// System.out.println("success");
							L.add(phi);
							diameter.add(getDiameter(phi));
							break;
						}
					}
				}
			}
			if (L.size() > 0) {
				int minIndex = getMinDiameter(diameter);
				Map<Integer, LatLng> cloaked = L.get(minIndex);
				double wx = getWx(diameter.get(minIndex), current);

				Random random = new Random();
				double probaility = random.nextDouble();

				LatLng xprime = getXprime(wx, probaility, diameter.get(minIndex), current);

				ArrayList<LatLng> cloakedRegion = new ArrayList<LatLng>();
				for (int index : cloaked.keySet()) {
					cloakedRegion.add(cloaked.get(index));
				}

				return xprime;
			}
		} else {
			System.out.println("out of bounds");
		}
		return null;
	}

	// calculate the normalization factor wx
	public double getWx(double diameter, LatLng x) {

		double sum = 0;
		for (int i = 0; i < gridSize * gridSize; i++) {
			sum += Math.exp((-this.epsilon * getLatLngDistance(x, getLocation(i))) / (2 * diameter));
		}
		double wx = 1 / sum;
		return wx;
	}

	// make fixed buckets with cumulative probability based on sample random values.
	public LatLng getXprime(double wx, double probability, double diameter, LatLng x) {
		double sum = 0;
		Map<LatLng, Double> probabilities = new HashMap<LatLng, Double>();
		for (int i = 0; i < gridSize * gridSize; i++) {
			LatLng xprime = getLocation(i);
			double p = wx * Math.exp((-this.epsilon * getLatLngDistance(x, xprime)) / (2 * diameter));
			sum += p;
			probabilities.put(xprime, sum);
		}
		// System.out.println("sum " + sum);
		ArrayList<Double> values = new ArrayList<Double>(probabilities.values());
		ArrayList<LatLng> keys = new ArrayList<LatLng>(probabilities.keySet());
		for (int i = 0; i < gridSize * gridSize; i++) {
			if (probability < values.get(i)) {
				return keys.get(i);
			}
		}

		return null;
	}

	// get the minimum diameter
	public static Integer getMinDiameter(ArrayList<Double> diameter) {

		double min = 0.f;
		int minIndex = -1;
		for (int i = 0; i < diameter.size(); i++) {
			if (min < diameter.get(i)) {
				min = diameter.get(i);
				minIndex = i;
			}
		}
		return minIndex;
	}

	// get the diameter from phi
	public static double getDiameter(Map<Integer, LatLng> phi) {

		double diameter = 0.f;

		for (int i : phi.keySet()) {
			for (int j : phi.keySet()) {
				double distance = getLatLngDistance(phi.get(i), phi.get(j));
				if (diameter < distance) {
					diameter = distance;
				}
			}
		}

		return diameter;
	}

	// calculates expected error
	public static Double expectedError(Map<Integer, LatLng> phi, float[] prior, int min, int max) {
		ArrayList<Double> error = new ArrayList<Double>();
		for (int xprime = min; xprime <= max; xprime++) {
			double e = 0;
			for (int x = min; x <= max; x++) {
				float pi_y = 0;
				for (int y = min; y <= max; y++) {
					pi_y += prior[y];
				}
				if (xprime != x) {
					e += (prior[x] / pi_y) * getLatLngDistance(phi.get(xprime), phi.get(x));
				}
			}
			error.add(e);
		}
		Collections.sort(error);
		return error.get(0);
	}

	// get the euclidian lat lng distance
	public static double getLatLngDistance(LatLng l1, LatLng l2) {
		return Math.sqrt((l1.latitude - l2.latitude) * (l1.latitude - l2.latitude)
				+ (l1.longitude - l2.longitude) * (l1.longitude - l2.longitude));
	}

	// get current cell for the latlng co-ordinates
	public static int getPosition(LatLng current) {
		for (int i = 0; i < map.size(); i++) {
			ArrayList<Mapper> coordinates = map.get(i);
			Mapper topleftMap = coordinates.get(0);
			Mapper bottomrightMap = coordinates.get(2);
			if (topleftMap.loc.latitude >= current.latitude
					&& Math.abs(topleftMap.loc.longitude) >= Math.abs(current.longitude)) {
				if (bottomrightMap.loc.latitude <= current.latitude
						&& Math.abs(bottomrightMap.loc.longitude) <= Math.abs(current.longitude)) {
					return i;
				}
			}
		}
		return -1;
	}

	// get latlng co-ordiantes for the given cell
	public static LatLng getLocation(int k) {
		ArrayList<Mapper> coordinates = map.get(k);
		Mapper topleftMap = coordinates.get(0);
		Mapper bottomrightMap = coordinates.get(2);
		return new LatLng((topleftMap.loc.latitude + bottomrightMap.loc.latitude) / 2,
				(topleftMap.loc.longitude + bottomrightMap.loc.longitude) / 2);
	}

	// generate prior probabilites
	public static float[] getProbabilities(ArrayList<Mapper> mappers) {
		Map<Integer, Integer> probability = new HashMap<Integer, Integer>();
		for (int i = 0; i < gridSize * gridSize; i++) {
			probability.put(i, 0);
		}

		for (Mapper l : mappers) {
			int cell = getPosition(l.loc);
			if (cell == -1) {
				continue;
			}
			// System.out.println(cell);
			probability.put(cell, probability.get(cell) + 1);
		}

		float[] prior = new float[gridSize * gridSize];
		for (int i = 0; i < gridSize * gridSize; i++) {
			prior[i] = probability.get(i);
		}
		prior = normalizePrior(prior);
		return prior;
	}

	// normalize priors to add up to 1
	public static float[] normalizePrior(float pi[]) {
		float sum = 0.f;
		for (int i = 0; i < pi.length; i++) {
			sum += pi[i];
		}
		for (int i = 0; i < pi.length; i++) {
			pi[i] = pi[i] / sum;
		}
		return pi;
	}
}
