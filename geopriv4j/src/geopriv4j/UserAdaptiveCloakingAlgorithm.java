package geopriv4j;

/*
 * In this algorithm we want to let people have the privacy protection level they desire. 
 * In order to provide this, they define θ, the personal privacy threshold, 
 * which expresses the desired level (i.e., the lower bound) of expected distortion 
 * (i.e., distance) from the actual user location. This privacy threshold depends 
 * on the user’s sensitivity about their privacy at a particular location, and it 
 * can be chosen by a user-specific function of the desired absolute distance from 
 * the sensitive location.
 * 
 * We implemented this algorithm based on the paper by Agir, Berker, et al. "User-side 
 * adaptive protection of location privacy in participatory sensing." GeoInformatica 
 * 18.1 (2014): 165-191.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import geopriv4j.utils.LatLng;
import geopriv4j.utils.Node;

public class UserAdaptiveCloakingAlgorithm {

	static Random random = new Random();

	// Earth’s radius, sphere
	final public static int earth_radius = 6378137;
	// specify the grid size
	public static int gridSize = 5;

	// This contains the grids that is generated
	public static Map<Integer, ArrayList<LatLng>> grids = new HashMap<>();

	// speicfy the topleft and the bottomright locations for the grid
	public LatLng topleft;
	public LatLng bottomright;

	static ArrayList<Node> spannerGraph = new ArrayList<Node>();
	public static int lambda = 2;
	public double theta;
	public int alpha_max;

	public UserAdaptiveCloakingAlgorithm(double theta, int alpha_max, LatLng topleft, LatLng bottomright) {

		this.theta = theta;
		this.alpha_max = alpha_max;
		this.topleft = topleft;
		this.bottomright = bottomright;

		initiate(this.topleft, this.bottomright);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Node> generate(ArrayList<LatLng> trajectory) {

		int counter = 0;
		ArrayList<Node> prevSpannerGraph = new ArrayList<Node>();

		for (int timestamp = 0; timestamp < trajectory.size(); timestamp++) {
			// re-initlize lambda for every iteration
			lambda = 2;
			prevSpannerGraph = (ArrayList<Node>) spannerGraph.clone();
			Node actual = new Node();
			// get current cell and add it to the node
			actual.cell = getCurrentCell(trajectory.get(timestamp));
			do {
				counter++;
				// System.out.println("lambda: "+ lambda);
				spannerGraph = (ArrayList<Node>) prevSpannerGraph.clone();

				ArrayList<Node> new_graph = new ArrayList<Node>();

				ArrayList<Integer> alpha = getAlpha(lambda, trajectory.get(timestamp));

				System.out.println("timestamp: " + timestamp + " alpha: " + alpha);

				int k = alpha.size();

				for (int cell : alpha) {
					Node node = new Node();
					node.cell = cell;
					//build graph by adding the node
					Node new_node = buildGraph(k, node, alpha, timestamp);
					if (new_node != null) {
						new_graph.add(new_node);
					}
				}

				for (Node g : new_graph) {
					if (spannerGraph.contains(g)) {
						int index = spannerGraph.indexOf(g);
						spannerGraph.get(index).child = g.child;
						spannerGraph.get(index).parent = g.parent;
						spannerGraph.get(index).probability = g.probability;
					} else {
						spannerGraph.add(g);
					}
				}
				// check if alpha_max has reached
				if (this.alpha_max == counter) {
					lambda++;
					counter = 0;
				} else if (lambda == gridSize) {
					break;
				} else {
					counter++;
				}
				// check if the expected distortion is greater than theta and end it
			} while (expectedDistortion(actual) < this.theta);
		}
		return spannerGraph;

	}

	// calculate the expected distortion
	public static double expectedDistortion(Node actual) {

		double distortion = 0.;
		ArrayList<Node> leafNodes = getLeafNodes();
		for (int i = 0; i < leafNodes.size(); i++) {
			distortion += getCoordinatesDistance(getCoordinates(actual.cell), getCoordinates(leafNodes.get(i).cell))
					* leafNodes.get(i).probability;
		}
		System.out.println("distortion: " + distortion);
		return distortion;
	}

	// get the euclidean coordinate distance
	public static double getCoordinatesDistance(double[] n1, double[] n2) {
		return Math.sqrt((n1[0] - n2[0]) * (n1[0] - n2[0]) + (n1[1] - n2[1]) * (n1[1] - n2[1]));
	}

	//// get the coordinates for each cell
	public static double[] getCoordinates(int cell) {
		return new double[] { cell / gridSize, cell % gridSize };
	}

	// get leaf nodes of the graph
	public static ArrayList<Node> getLeafNodes() {
		ArrayList<Node> leafNodes = new ArrayList<Node>();

		for (Node g : spannerGraph) {
			if (g.child.size() == 0) {
				leafNodes.add(g);
			}
		}

		return leafNodes;
	}

	// build the spanner graph for each time stamp
	public static Node buildGraph(int k, Node node, List<Integer> alpha, int timestamp) {
		if (timestamp == 0) {
			node.probability = (double) 1 / k;
		} else {
			ArrayList<Node> spanner = getLeafNodes();
			ArrayList<Integer> graphNodes = new ArrayList<Integer>();
			for (Node g : spanner) {
				graphNodes.add(g.cell);
			}
			ArrayList<Integer> parents = possibleLocations(node.cell, gridSize, graphNodes);
			if (parents.size() > 0) {
				Iterator<Node> iterator = spanner.iterator(); // change it to leaf nodes
				while (iterator.hasNext()) {
					Node graphNode = iterator.next();
					if (parents.contains(graphNode.cell)) {
						if (node.parent.contains(graphNode)) {
							continue;
						}
						node.parent.add(graphNode);
						if (graphNode.child.contains(node)) {
							continue;
						}
						graphNode.child.add(node);
					}
				}
				if (node.parent.size() > 0) {
					node.probability = getProbability(node);
				} else {
					return null;
				}
			}
		}

		return node;
	}

	// check if node exists
	public static boolean nodeExists(int cell) {
		for (Node n : spannerGraph) {
			if (cell == n.cell) {
				return true;
			}
		}
		return false;
	}

	// diplay the graph
	public void display(ArrayList<Node> graph) {
		for (Node node : graph) {
			System.out.println("node.cell: " + node.cell);
			if (node.parent.size() > 0) {
				System.out.println("parents: ");
				for (Node p : node.parent) {
					System.out.println(p.cell);
				}
			}

			if (node.child.size() > 0) {
				System.out.println("children: ");
				for (Node c : node.child) {
					System.out.println(c.cell);
				}
			}

			System.out.println("node.probability : " + node.probability);
			System.out.println();
		}
	}

	// calculate probability of the node
	public static Double getProbability(Node node) {
		double probability = 0.;
		for (Node parent : node.parent) {
			probability += parent.probability * 1 / (parent.child.size());
		}
		return probability;
	}

	// get all possible location for each node
	public static ArrayList<Integer> possibleLocations(int cell, int gridSize, List<Integer> alpha) {

		ArrayList<Integer> possibilites = new ArrayList<Integer>();

		int left = cell % gridSize == 0 ? -1 : cell - 1;
		int right = (cell + 1) % gridSize == 0 ? -1 : cell + 1;
		int top = (cell - gridSize) < 0 ? -1 : cell - gridSize;
		int bottom = (cell + gridSize) > (gridSize * gridSize - 1) ? -1 : cell + gridSize;
		int topLeft = (cell - gridSize) < 0 | cell % gridSize == 0 ? -1 : cell - gridSize - 1;
		int topRight = (cell - gridSize) < 0 | (cell + 1 % gridSize) == 0 ? -1 : cell - gridSize + 1;
		int bottomLeft = (cell + gridSize) > (gridSize * gridSize - 1) | cell % gridSize == 0 ? -1
				: cell + gridSize - 1;
		int bottomRight = (cell + gridSize) > (gridSize * gridSize - 1) | ((cell + 1) % gridSize) == 0 ? -1
				: cell + gridSize + 1;

		if (left != -1 && alpha.contains(left)) {
			possibilites.add(left);
		}

		if (right != -1 && alpha.contains(right)) {
			possibilites.add(right);
		}

		if (top != -1 && alpha.contains(top)) {
			possibilites.add(top);
		}

		if (bottom != -1 && alpha.contains(bottom)) {
			possibilites.add(bottom);
		}

		if (topLeft != -1 && alpha.contains(topLeft)) {
			possibilites.add(topLeft);
		}

		if (topRight != -1 && alpha.contains(topRight)) {
			possibilites.add(topRight);
		}

		if (bottomLeft != -1 && alpha.contains(bottomLeft)) {
			possibilites.add(bottomLeft);
		}

		if (bottomRight != -1 && alpha.contains(bottomRight)) {
			possibilites.add(bottomRight);
		}

		return possibilites;

	}

	// get alpha value for the current lambda
	public static ArrayList<Integer> getAlpha(int lambda, LatLng l) {

		int sx = (int) (1 + Math.ceil(lambda / 2));
		int sy = (int) (1 + Math.floor(lambda / 2));

		ArrayList<Integer> area = new ArrayList<Integer>();
		area.add(sx);
		area.add(sy);

		return getAlphaPosition(area, l);
	}

	// Calculate the alpha position for the sx and sy values
	public static ArrayList<Integer> getAlphaPosition(ArrayList<Integer> area, LatLng l) {

		int x_l = random.nextInt(area.get(0));
		int x_r = area.get(0) - x_l - 1;

		int y_l = random.nextInt(area.get(1));
		int y_r = area.get(1) - y_l - 1;

		while ((getCurrentCell(l) - x_l) % gridSize > x_l && x_l != 0) {
			x_l = random.nextInt(area.get(0));
			x_r = area.get(0) - x_l - 1;
		}

		while ((getCurrentCell(l) - y_l * gridSize) < 0) {
			y_l = random.nextInt(area.get(1));
			y_r = area.get(1) - y_l - 1;
		}

		int topLeft = getCurrentCell(l) - x_l - y_l * gridSize;
		if (topLeft < 0) {
			topLeft = 0;
		}

		int bottomRight = getCurrentCell(l) + x_r + y_r * gridSize;

		if (bottomRight >= gridSize * gridSize) {
			bottomRight = gridSize * gridSize - 1;
		}

		return generateObfuscationArea(topLeft, bottomRight, area.get(0), area.get(1));
	}

	// generate the obfuscation area or the alpha
	public static ArrayList<Integer> generateObfuscationArea(int topLeft, int bottomRight, int sx, int sy) {
		ArrayList<Integer> alpha = new ArrayList<Integer>();
		for (int i = topLeft; i < bottomRight; i = i + gridSize) {
			for (int j = i; j < i + sx; j++) {
				if (i % gridSize <= j % gridSize) {
					alpha.add(j);
				}
			}
		}
		return alpha;
	}

	// created the grid by calculating the cell size and bearing
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

}
