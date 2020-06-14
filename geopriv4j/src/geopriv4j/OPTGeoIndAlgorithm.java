package geopriv4j;

/*
 * In this algorithm we show that, given a desired degree of geo-indistinguishability, 
 * it is possible to construct a mechanism that minimizes the service quality loss, 
 * using linear programming techniques. 
 * 
 * We implemented this algorithm based on the paper by Bordenabe, Nicolás E.,
 * Konstantinos Chatzikokolakis, and Catuscia Palamidessi. "Optimal geo-indistinguishable
 * mechanisms for location privacy." Proceedings of the 2014 ACM SIGSAC conference
 * on computer and communications security. 2014.
 */

//import gurobi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import geopriv4j.utils.DataHandler;
import geopriv4j.utils.LatLng;
import geopriv4j.utils.Mapper;
import geopriv4j.utils.SpannerGraph;

public class OPTGeoIndAlgorithm {

	public static int gridSize = 8;
	// Earth’s radius, sphere
	final public static int earth_radius = 6378137;

	// This contains the grids that is generated
	public static Map<Integer, ArrayList<LatLng>> grids = new HashMap<>();

	// speicfy the topleft and the bottomright locations for the grid
	public LatLng topleft;
	public LatLng bottomright;

	public float[] prior;
	public double epsilon;
	public double delta;

	public static Map<Integer, ArrayList<Double>> K = new HashMap<Integer, ArrayList<Double>>();

	public OPTGeoIndAlgorithm(LatLng topleft, LatLng bottomright, double epsilon, double delta) {

		this.topleft = topleft;
		this.bottomright = bottomright;
		this.epsilon = epsilon;
		this.delta = delta;

		initiate(this.topleft, this.bottomright);

	}

	// initialize K according to the paper
	// public void initializeK(float[] prior) {
	//
	// try {
	// // pi sums to 1
	// // prior = normalizePrior(this.prior);
	//
	// ArrayList<ArrayList<Integer>> adj = SpannerGraph.initialize();
	//
	// // Create empty environment, set options, and start
	// GRBEnv env = new GRBEnv(true);
	// env.set("logFile", "AdaptiveCloakingAlgorithm.log");
	// env.start();
	//
	// // Create empty model
	// GRBModel model = new GRBModel(env);
	// GRBVar[][] k = new GRBVar[gridSize * gridSize][gridSize * gridSize];
	// GRBLinExpr expr = new GRBLinExpr();
	//
	// Map<Integer, ArrayList<Integer>> spanner = getSpanner(delta);
	//
	// // construct optimization function
	//
	// for (int x = 0; x < gridSize * gridSize; x++) {
	// for (int z = 0; z < gridSize * gridSize; z++) {
	// k[x][z] = model.addVar(0.0, 1.0, 1.0, GRB.CONTINUOUS, "K" + x + z);
	// expr.addTerm(prior[x] * getLatLngDistance(getLatLng(x), getLatLng(z)),
	// k[x][z]);
	// }
	// }
	//
	// // set it to minimization
	// model.setObjective(expr, GRB.MINIMIZE);
	//
	// // add constraints
	// for (int z = 0; z < gridSize * gridSize; z++) {
	// for (int x = 0; x < gridSize * gridSize; x++) {
	// ArrayList<Integer> edges = spanner.get(x);
	// for (int x_prime : edges) {
	// GRBLinExpr expr1 = new GRBLinExpr();
	// expr1.addTerm(
	// Math.exp((epsilon / delta)
	// * SpannerGraph.getShortestDistance(adj, x, x_prime, gridSize * gridSize)),
	// k[x_prime][z]);
	// model.addConstr(expr1, GRB.GREATER_EQUAL, k[x][z], "c" + x + z);
	// }
	// }
	// }
	//
	// // add constraints
	// for (int x = 0; x < gridSize * gridSize; x++) {
	// GRBLinExpr expr2 = new GRBLinExpr();
	// for (int z = 0; z < gridSize * gridSize; z++) {
	// expr2.addTerm(1.0, k[x][z]);
	// }
	// model.addConstr(expr2, GRB.EQUAL, 1.0, "c" + x);
	// }
	//
	// model.optimize();
	//
	// for (int i = 0, counter = 0; i < gridSize * gridSize; i++) {
	// ArrayList<Double> list = new ArrayList<Double>();
	// for (int j = 0; j < gridSize * gridSize; j++) {
	// double x = model.getVars()[counter].get(GRB.DoubleAttr.X);
	// list.add(x);
	// counter++;
	// }
	// K.put(i, list);
	// }
	//
	// System.out.println(K);
	// model.dispose();
	// env.dispose();
	// } catch (GRBException e) {
	// System.out.println(" Error code : " + e.getErrorCode() + ". " +
	// e.getMessage());
	// }
	// }

	// generate new locations
	public LatLng generate(LatLng current) {
		int cell = getCurrentCell(current);
		if (cell == -1) {
			return current;
		}
		int reportedcell = getMaxIndex(cell);

		return getLatLng(reportedcell);
	}

	// get the max probability of for the given cell
	public static int getMaxIndex(int cell) {
		ArrayList<Double> list = K.get(cell);
		return list.indexOf(Collections.max(list));
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

	// snap the lat lng to the center of the cell
	public static LatLng getLatLng(int cell) {
		return new LatLng((grids.get(cell).get(0).latitude + grids.get(cell).get(1).latitude) / 2,
				(grids.get(cell).get(0).longitude + grids.get(cell).get(1).longitude) / 2);
	}

	// get the euclidian lat lng distance
	public static double getLatLngDistance(LatLng l1, LatLng l2) {
		return Math.sqrt((l1.latitude - l2.latitude) * (l1.latitude - l2.latitude)
				+ (l1.longitude - l2.longitude) * (l1.longitude - l2.longitude));
	}

	// generate the spanner-graph
	public static Map<Integer, ArrayList<Integer>> getSpanner(double delta) {
		ArrayList<ArrayList<Integer>> adj = SpannerGraph.initialize();
		Map<Integer, ArrayList<Integer>> edges = new HashMap<Integer, ArrayList<Integer>>();
		for (int x = 0; x < gridSize * gridSize; x++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int x_prime = 0; x_prime < gridSize * gridSize; x_prime++) {
				if (SpannerGraph.getShortestDistance(adj, x, x_prime, gridSize * gridSize) > delta
						* getLatLngDistance(getLatLng(x), getLatLng(x_prime))) {
					list.add(x_prime);
				}
			}
			edges.put(x, list);
		}
		return edges;
	}

	private static void initiate(LatLng topleft, LatLng bottomright) {

		// calculate the cell size
		double difflat = Math.abs(Math.toRadians(topleft.latitude) - Math.toRadians(bottomright.latitude));
		double difflng = Math.abs(Math.toRadians(topleft.longitude) - Math.toRadians(bottomright.longitude));
		double result = Math.pow(Math.sin(difflat / 2), 2) + Math.cos(Math.toRadians(topleft.latitude))
				* Math.cos(Math.toRadians(bottomright.latitude)) * Math.pow(Math.sin(difflng / 2), 2);
		result = 2 * Math.asin(Math.sqrt(result));
		double offset = result * earth_radius / gridSize;

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
	public static void generateGrids(double offset, LatLng topleft, double brng) {

		double meters = offset;

		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {

				ArrayList<LatLng> arrayList = new ArrayList<LatLng>();

				// calculate the top left location for each cell

				// convert to radians
				double lat_t1 = Math.toRadians(topleft.latitude);
				double lon_t1 = Math.toRadians(topleft.longitude);

				// calculate the new lcations
				double lat_t2 = Math.asin(Math.sin(lat_t1) * Math.cos((meters * (i)) / earth_radius)
						+ Math.cos(lat_t1) * Math.sin((meters * (i)) / earth_radius) * Math.cos(brng));

				double lon_t2 = lon_t1
						+ Math.atan2(Math.sin(brng) * Math.sin((meters * (j)) / earth_radius) * Math.cos(lat_t1),
								Math.cos((meters * (j)) / earth_radius) - Math.sin(lat_t1) * Math.sin(lat_t2));

				// convert back to degrees
				double new_tlat = Math.toDegrees(lat_t2);
				double new_tlong = Math.toDegrees(lon_t2);

				arrayList.add(new LatLng(new_tlat, new_tlong));

				// calculate the bottom right location for each cell

				// calculate new location
				double lat_b2 = Math.asin(Math.sin(lat_t1) * Math.cos((meters * (i + 1)) / earth_radius)
						+ Math.cos(lat_t1) * Math.sin((meters * (i + 1)) / earth_radius) * Math.cos(brng));

				double lon_b2 = lon_t1
						+ Math.atan2(Math.sin(brng) * Math.sin((meters * (j + 1)) / earth_radius) * Math.cos(lat_t1),
								Math.cos((meters * (j + 1)) / earth_radius) - Math.sin(lat_t1) * Math.sin(lat_b2));

				// convert back to degrees
				double new_blat = Math.toDegrees(lat_b2);
				double new_blong = Math.toDegrees(lon_b2);

				arrayList.add(new LatLng(new_blat, new_blong));

				// adding both topleft and bottom right to specified cell in the grid
				grids.put(grids.size(), arrayList);
			}

		}
	}

	// get the current cell for the lat lng location
	public static int getCurrentCell(LatLng current) {
		if (current.latitude != 0 && current.latitude != -1) {
			for (Integer r = 0; r < grids.size(); r++) {
				ArrayList<LatLng> locs = grids.get(r);
				// check if current user location is in the cell then report true
				if (locs.get(0).latitude >= current.latitude && locs.get(1).latitude <= current.latitude
						&& locs.get(0).longitude <= current.longitude && locs.get(1).longitude >= current.longitude) {
					return r;
				}
			}
			return -1;
		} else {
			return -1;
		}
	}

	// generate prior probabilites
	public static float[] getProbabilities(ArrayList<Mapper> mappers) {
		Map<Integer, Integer> probability = new HashMap<Integer, Integer>();
		for (int i = 0; i < gridSize * gridSize; i++) {
			probability.put(i, 0);
		}

		for (Mapper l : mappers) {
			int cell = getCurrentCell(l.loc);
			if (cell == -1) {
				continue;
			}
			probability.put(cell, probability.get(cell) + 1);
		}

		float[] prior = new float[gridSize * gridSize];
		for (int i = 0; i < gridSize * gridSize; i++) {
			prior[i] = probability.get(i);
		}
		prior = normalizePrior(prior);
		return prior;
	}
}
