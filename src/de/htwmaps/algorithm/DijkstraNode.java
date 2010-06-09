package de.htwmaps.algorithm;

import java.util.LinkedList;

/*
 * Node representing location on earth
 */
class DijkstraNode extends Node implements Comparable<DijkstraNode> {

	private double dist;

	private volatile DijkstraNode predecessor, predecessor2;						//gehören den Knoten von Thread1 bzw Thread2
	private LinkedList<DijkstraNode> next;
    private volatile boolean removed = false;
    
    /*
     * @param id unique node id on earth
     * @param key distance relative to start node
     * 
     */
    public DijkstraNode(double x, double y, int id) {
    	super(x, y, id);
    	setDist(Double.MAX_VALUE);
    	next = new LinkedList<DijkstraNode>();
    }
    
    /*
     * adding new neighbor
     */
    public boolean addNextNeighbor(DijkstraNode node) {
    	return next.add(node);
    }
    

	@Override
	public int compareTo(DijkstraNode o) {
		if (id > o.id) {
			return 1;
		} else {
			if (id == o.id) {
				return 0;
			} else {
				return -1;
			}
		}
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DijkstraNode)) 
			return false;
		if (((DijkstraNode)other).getId() == this.getId()) {
			return true;
		}
		return false;
	}
	
	
	//------Getters 'n Setters-------
    
    public LinkedList<DijkstraNode> getNeighbors() {
    	return next;
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
	public String toString() {
		return id + ""; 
	}
	
	public void setPredecessor(DijkstraNode predecessor) {
		this.predecessor = predecessor;
	}
	
	public Node getPredecessor() {
		return predecessor;
	}
	
	public void setPredecessor2(DijkstraNode predecessor2) {
		this.predecessor2 = predecessor2;
	}

	public DijkstraNode getPredecessor2() {
		return predecessor2;
	}
}
