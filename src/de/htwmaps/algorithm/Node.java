package de.htwmaps.algorithm;

import java.util.LinkedList;

/**
 * 
 * @author Stanislaw Tartakowski, Tim Bartsch
 *	Allgemeine Knotendefinition
 */
public abstract class Node {
	float x, y;
	int id;
	LinkedList<Edge> edgeList;
	
	Node(float x, float y, int id) {
		edgeList = new LinkedList<Edge>();
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	/**
	 * 
	 * @param e Kante die auf den Knoten gesetzt wird
	 * @return false: fehler beim setzen
	 */
	public boolean addEdge(Edge e){
		return edgeList.add(e);
	}
	
	/**
	 * @return liefert alle Kanten die von diesem Knoten ausgehen
	 */
	public LinkedList<Edge> getEdgeList(){
		return edgeList;
	}
	
	/**
	 * 
	 * @return id des Knotens
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return Longitude
	 */
	public float getX() {
		return x;
	}

	/**
	 * 
	 * @return Latitude
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * 
	 * @param n Knoten zu dem die Laenge ermittelt werden soll
	 * @return Laenge in km
	 */
	public double getDistanceTo(Node n) {
		double lat = (this.y + n.y) / 2 * 0.01745;
		double dx = 111.3 * Math.cos(lat) * (this.x - n.x);
		double dy = 111.3 * (this.y - n.y);
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * prueft die Knoten auf Gleichheit in Bezug auf deren ID
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node n = (Node) o;
			if (this.id == n.id)
				return true;
		}
		return false;
	}
	
	/**
	 * dient beispielsweise einer HashMap
	 */
	@Override
	public int hashCode(){
		return id;
	}
	
	@Override
	public String toString() {
		return id + "";
	}
}
