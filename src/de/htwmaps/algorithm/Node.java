package de.htwmaps.algorithm;

import java.util.LinkedList;

/**
 * 
 * @author Stanislaw Tartakowski, Tim Bartsch
 *
 */
public class Node {
	double x, y;
	int id;
	LinkedList<Edge> edgeList;
	
	Node(double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public boolean addEdge(Edge e){
		return edgeList.add(e);
	}
	
	public LinkedList<Edge> getEdgeList(){
		return edgeList;
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
	
	public double getDistanceTo(Node n) {
		return Math.sqrt((this.x - n.x) * (this.x - n.x)
				+ (this.y - n.y) * (this.y - n.y));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node n = (Node) o;
			if (this.id == n.id)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return id + "";
	}
}
