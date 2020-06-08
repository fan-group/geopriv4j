package geopriv4j.utils;

import java.util.ArrayList;
import java.util.LinkedList;

public class SpannerGraph {
	public static int gridSize = 5;
	// No of vertices
	public static int v = gridSize * gridSize;

	// Driver Program
	public static void main(String args[]) {

		ArrayList<ArrayList<Integer>> adj = initialize();
		int source = 0, dest = 7;
		int dist = getShortestDistance(adj, source, dest, v);
		// Print distance
		System.out.println("Shortest path length is: " + dist);
	}

	public static ArrayList<ArrayList<Integer>> initialize() {
		// Adjacency list for storing which vertices are connected
		ArrayList<ArrayList<Integer>> adj = new ArrayList<ArrayList<Integer>>(v);
		for (int i = 0; i < v; i++) {
			adj.add(new ArrayList<Integer>());
		}

		// Creating graph given in the above diagram.
		// add_edge function takes adjacency list, source
		// and destination vertex as argument and forms
		// an edge between them.

		for (int i = 0; i < v; i++) {
			ArrayList<Integer> possibileNeighbors = neighbors(i, gridSize);
			for (int pn : possibileNeighbors) {
				addEdge(adj, i, pn);
			}
		}
		return adj;
	}

	// get all possible location for each node
	public static ArrayList<Integer> neighbors(int cell, int gridSize) {

		ArrayList<Integer> possibileNeighbors = new ArrayList<Integer>();

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

		if (left != -1) {
			possibileNeighbors.add(left);
		}

		if (right != -1) {
			possibileNeighbors.add(right);
		}

		if (top != -1) {
			possibileNeighbors.add(top);
		}

		if (bottom != -1) {
			possibileNeighbors.add(bottom);
		}

		if (topLeft != -1) {
			possibileNeighbors.add(topLeft);
		}

		if (topRight != -1) {
			possibileNeighbors.add(topRight);
		}

		if (bottomLeft != -1) {
			possibileNeighbors.add(bottomLeft);
		}

		if (bottomRight != -1) {
			possibileNeighbors.add(bottomRight);
		}

		return possibileNeighbors;

	}

	// function to form edge between two vertices
	// source and dest
	private static void addEdge(ArrayList<ArrayList<Integer>> adj, int i, int j) {
		adj.get(i).add(j);
		adj.get(j).add(i);
	}

	// function to print the shortest distance and path
	// between source vertex and destination vertex
	public static int getShortestDistance(ArrayList<ArrayList<Integer>> adj, int s, int dest, int v) {
		// predecessor[i] array stores predecessor of
		// i and distance array stores distance of i
		// from s
		int pred[] = new int[v];
		int dist[] = new int[v];

		if (BFS(adj, s, dest, v, pred, dist) == false) {
			// System.out.println("Given source and destination" + "are not connected");
			return 0;
		}

		// LinkedList to store path
		LinkedList<Integer> path = new LinkedList<Integer>();
		int crawl = dest;
		path.add(crawl);
		while (pred[crawl] != -1) {
			path.add(pred[crawl]);
			crawl = pred[crawl];
		}

		// Print path
		// System.out.println("Path is ::");
		// for (int i = path.size() - 1; i >= 0; i--) {
		// System.out.print(path.get(i) + " ");
		// }
		return dist[dest];
	}

	// a modified version of BFS that stores predecessor
	// of each vertex in array pred
	// and its distance from source in array dist
	private static boolean BFS(ArrayList<ArrayList<Integer>> adj, int src, int dest, int v, int pred[], int dist[]) {
		// a queue to maintain queue of vertices whose
		// adjacency list is to be scanned as per normal
		// BFS algorithm using LinkedList of Integer type
		LinkedList<Integer> queue = new LinkedList<Integer>();

		// boolean array visited[] which stores the
		// information whether ith vertex is reached
		// at least once in the Breadth first search
		boolean visited[] = new boolean[v];

		// initially all vertices are unvisited
		// so v[i] for all i is false
		// and as no path is yet constructed
		// dist[i] for all i set to infinity
		for (int i = 0; i < v; i++) {
			visited[i] = false;
			dist[i] = Integer.MAX_VALUE;
			pred[i] = -1;
		}

		// now source is first to be visited and
		// distance from source to itself should be 0
		visited[src] = true;
		dist[src] = 0;
		queue.add(src);

		// bfs Algorithm
		while (!queue.isEmpty()) {
			int u = queue.remove();
			for (int i = 0; i < adj.get(u).size(); i++) {
				if (visited[adj.get(u).get(i)] == false) {
					visited[adj.get(u).get(i)] = true;
					dist[adj.get(u).get(i)] = dist[u] + 1;
					pred[adj.get(u).get(i)] = u;
					queue.add(adj.get(u).get(i));

					// stopping condition (when we find
					// our destination)
					if (adj.get(u).get(i) == dest)
						return true;
				}
			}
		}
		return false;
	}
}