package de.htwmaps.algorithm;

/**
 * 
 * @author Stanislaw Tartakowski, Tim Bartsch
 *
 */
public abstract class Node {
	double x, y;
	int id;
	
	Node(double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	

	@Override
	public boolean equals(Object o) {
		if (o instanceof AStarNode) {
			AStarNode n = (AStarNode) o;
			if (this.id == n.id)
				return true;
		} else {
			if (o instanceof DijkstraNode) {
				DijkstraNode n = (DijkstraNode) o;
				if (this.id == n.id)
					return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + id + ")";
	}
}
