package de.htwmaps.algorithm;

public abstract class Node {
	private double x, y;
	private int id;
	
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
}
