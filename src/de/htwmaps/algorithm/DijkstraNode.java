package de.htwmaps.algorithm;

/*
 * Node representing location on earth
 */
class DijkstraNode extends Node {

	private double dist;
	private DijkstraNode predecessor; 
	private volatile boolean removed, touchedByTh1, touchedByTh2;

	/*
	 * @param id unique node id on earth
	 * 
	 * @param key distance relative to start node
	 */
	public DijkstraNode(float x, float y, int id) {
		super(x, y, id);
		setDist(Double.MAX_VALUE);
	}

	public boolean isRemovedFromQ() {
		return removed;
	}

	public void setRemovedFromQ(boolean removed) {
		this.removed = removed;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public void setPredecessor(DijkstraNode predecessor) {
		this.predecessor = predecessor;
	}

	public DijkstraNode getPredecessor() {
		return predecessor;
	}

	public void setTouchedByTh1(boolean touchedByTh1) {
		this.touchedByTh1 = touchedByTh1;
	}

	public boolean isTouchedByTh1() {
		return touchedByTh1;
	}

	public void setTouchedByTh2(boolean touchedByTh2) {
		this.touchedByTh2 = touchedByTh2;
	}

	public boolean isTouchedByTh2() {
		return touchedByTh2;
	}
}
