package de.htwmaps.algorithm;

import java.util.LinkedList;

/**
 * 
 * @author Stanislaw Tartakowski, Tim Bartsch
 *
 */
public abstract class Node {
	float x, y;
	int id;
	LinkedList<Edge> edgeList;
	
	Node(float x, float y, int id) {
		edgeList = new LinkedList<Edge>();
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

	public float getX() {
		return x;
	}

	public float getY() {
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
	public int hashCode(){
		return id;
	}
	
	@Override
	public String toString() {
		return id + "";
	}
}
