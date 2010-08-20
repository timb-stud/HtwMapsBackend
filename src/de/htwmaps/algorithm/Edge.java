package de.htwmaps.algorithm;

/**
 * 
 * @author Stanislaw Tartakowski, Tim Bartsch
 *	Diese Klasse ist eine Kante zwischen 2 Knoten im Graphen
 */
public class Edge {
	private Node successor, predecessor;
	private double length;
	private int highwayType;
	private boolean isOneway;
	private int wayID;
	private int speed;
	private int id;

	/**
	 * 
	 * @param successor der Knoten auf den die Kante gerichtet ist
	 * @param length laenge der Kante
	 * @param speed 
	 */
	public Edge(Node successor, double length, int highwayType, int wayID, int speed, int id) {
		this.successor = successor;
		this.length = length;
		this.highwayType = highwayType;
		this.wayID = wayID;
		this.speed = speed;
		this.id = id;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getWayID() {
		return wayID;
	}
	
	public int getHighwayType() {
		return highwayType;
	}
	
	public void setOneway(boolean oneway) {
		this.isOneway = oneway;
	}

	/**
	 * @return der Knoten, vom dem aus die Kante ausgeht. Ein Indiz fuer beidseitiges Betreten der Kante
	 */
	public Node getPredecessor() {
		return predecessor;
	}
	
	/**
	 * 
	 * @param predecessor der Knoten, vom dem aus die Kante ausgeht. Ein Indiz fuer beidseitiges Betreten der Kante
	 */
	public void setPredecessor(Node predecessor) {
		this.predecessor = predecessor;
	}
	
	/**
	 * @return der Knoten, auf den die Kante zeigt
	 */
	public Node getSuccessor() {
		return successor;
	}

	/**
	 * 
	 * @return Laenge der Kante
	 */
	public double getLenght() {
		return length;
	}

	@Override
	public String toString() {
		return "[" + successor + "; " + length + "]";
	}

	public double getPrioLength() {
		return length / speed;
	}

	public boolean isOneway() {
		return isOneway;
	}
	
	public int getID() {
		return id;
	}
}
