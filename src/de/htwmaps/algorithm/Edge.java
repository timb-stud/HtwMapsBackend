package de.htwmaps.algorithm;

/**
 * 
 * @author Stanislaw Tartakowski, Tim Bartsch
 *	Diese Klasse ist eine Kante zwischen 2 Knoten im Graphen
 */
public class Edge {
	private Node successor, predecessor;
	private double distance;

	/**
	 * 
	 * @param successor der Knoten auf den die Kante gerichtet ist
	 * @param distance laenge der Kante
	 */
	public Edge(Node successor, double distance) {
		this.successor = successor;
		this.distance = distance;
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
}
