package de.htwmaps.algorithm;

public class Edge {
	private Node successor;
	private double distance;
	private boolean oneway;

	public Edge(Node successor, double distance) {
		this.successor = successor;
		this.distance = distance;
		setOneway(true);
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

	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	public boolean isOneway() {
		return oneway;
	}
}
