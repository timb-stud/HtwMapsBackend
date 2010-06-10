package de.htwmaps.algorithm;

public class Edge {
	private Node successor;
	private double distance;
	
	public Edge(Node successor, double distance) {
		this.successor = successor;
		this.distance = distance;
	}

	public Node getSuccessor() {
		return successor;
	}

	public double getDistance() {
		return distance;
	}
	
	
}
