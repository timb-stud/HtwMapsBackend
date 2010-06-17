package de.htwmaps.algorithm;

public class Edge {
	private Node successor, predecessor;
	private double distance;

	public Edge(Node successor, double distance) {
		this.successor = successor;
		this.distance = distance;
	}
	
	public Node getPredecessor() {
		return predecessor;
	}
	
	public void setPredecessor(Node predecessor) {
		this.predecessor = predecessor;
	}
	
	public Node getSuccessor() {
		return successor;
	}

	public double getDistance() {
		return distance;
	}

	@Override
	public String toString() {
		return "[" + successor + "; " + distance + "]";
	}
}
