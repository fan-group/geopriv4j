package geopriv4j.utils;

/*
 * This class is used by UserAdaptiveCloakingAlgorithm 
 */

import java.util.ArrayList;

public class Node {
	public ArrayList<Node> parent = new ArrayList<Node>();
	public ArrayList<Node> child = new ArrayList<Node>();
	public int cell;
	public double probability;

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (cell != other.cell)
			return false;
		return true;
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}