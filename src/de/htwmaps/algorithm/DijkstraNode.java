package de.htwmaps.algorithm;

import java.util.LinkedList;

/*
 * Node representing location on earth
 */
class DijkstraNode extends Node {

	private double dist;

	private volatile DijkstraNode predecessor, predecessor2;						//gehören den Knoten von Thread1 bzw Thread2
    private volatile boolean removed = false;
    
    /*
     * @param id unique node id on earth
     * @param key distance relative to start node
     * 
     */
    public DijkstraNode(float x, float y, int id) {
    	super(x, y, id);
    	setDist(Double.MAX_VALUE);
    	edgeList = new LinkedList<Edge>();
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
	
	public void setPredecessor2(DijkstraNode predecessor2) {
		this.predecessor2 = predecessor2;
	}

	public DijkstraNode getPredecessor2() {
		return predecessor2;
	}
}
