package de.htwmaps.algorithm;

/**
 * 
 * @author Stanislaw Tartakowski, Tim Bartsch
 *	Diese Klasse ist eine Kante zwischen 2 Knoten im Graphen
 */
public class Edge {
	private Node successor, predecessor;
	private double distance;
	private int type;
	private boolean oneway;

	/**
	 * 
	 * @param successor der Knoten auf den die Kante gerichtet ist
	 * @param distance laenge der Kante
	 */
	public Edge(Node successor, double distance, int type) {
		this.successor = successor;
		this.distance = distance;
		setType(type);
	}
	
	public void setOneway(boolean oneway) {
		this.oneway = oneway;
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
	public double getDistance() {
		return distance;
	}

	@Override
	public String toString() {
		return "[" + successor + "; " + distance + "]";
	}

	public void setType(int type) {
		if (type <= 0) {
			throw new RuntimeException("Straßentyp darf nicht 0 oder kleiner sein");
		}
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public double getPrioDist() {
		return type * distance;
	}

	public boolean isOneway() {
		return oneway;
	}
}
