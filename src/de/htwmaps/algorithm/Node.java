package de.htwmaps.algorithm;

public abstract class Node {
	double x, y;
	int id;
	
	Node(double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	int getId() {
		return id;
	}

	double getX() {
		return x;
	}

	double getY() {
		return y;
	}
}
