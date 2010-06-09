package de.htwmaps.algorithm;

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
}
