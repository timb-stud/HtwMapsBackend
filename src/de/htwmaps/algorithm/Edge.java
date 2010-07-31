package de.htwmaps.algorithm;

/**
 * 
 * @author Stanislaw Tartakowski, Tim Bartsch
 *	Diese Klasse ist eine Kante zwischen 2 Knoten im Graphen
 */
public class Edge {
	private Node successor, predecessor;
	private double lenght;
	private int highwayType;
	private boolean isOneway;
	private int id;

	/**
	 * 
	 * @param successor der Knoten auf den die Kante gerichtet ist
	 * @param length laenge der Kante
	 */
	public Edge(Node successor, double length, int highwayType, int id) {
		this.successor = successor;
		this.lenght = length;
		this.highwayType = highwayType;
		this.id = id;
	}
	
	public int getID() {
		return id;
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
		return lenght;
	}

	@Override
	public String toString() {
		return "[" + successor + "; " + lenght + "]";
	}

	public double getPrioLength() {
		return highwayType * lenght;
	}

	public boolean isOneway() {
		return isOneway;
	}
}
