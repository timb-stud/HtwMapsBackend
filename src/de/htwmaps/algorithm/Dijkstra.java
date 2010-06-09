package de.htwmaps.algorithm;

import java.util.LinkedList;


/*
 * @author Stanislaw Tartakowski
 * @version 1.0
 * 
 * Dies ist eine konkurrierende Implementierung eines Suchalgorithmus basierend auf Dijkstras Suchalgorithmus.
 * Abweichend von der klassischen Implementierung besitzt diese Klasse eine zielorientierte Heuristik, ähnlich
 * der von A* und ist auf schnellstmögliche Laufzeit optimiert. Dabei garantiert diese Klasse nicht die beste
 * sondern nur eine relativ gute Lösung. Diese Klasse kann nur sinnvoll eingesetzt werden, wenn die aufrufende Klasse
 * solange wartet, bis der Algorithmus sie aufweckt.
 */

public class Dijkstra extends Thread {
	
	public volatile static boolean finnished; 							//Dient als Hinweis für den jeweiligen Thread, ob der andere Thread bereits fertig ist.

	
	private boolean thread1;
	private DijkstraNode startNode, endNode;
	private FibonacciHeap Q;
	private Object caller;
	private String name;
	
	/*
	 * @param caller ist das Objekt, dass diese Klasse aufruft
	 * @param thread1 ist ein Flag an das Threadobjekt das die beiden laufenden Threads unterscheidet.
	 * @param Q ist als eine Prioritätsliste zu verstehen
	 */
	public Dijkstra(FibonacciHeap Q, DijkstraNode startNode, DijkstraNode endNode, boolean thread1, Object caller, String name) {
		super(name);
		this.thread1 = thread1;
		this.endNode = endNode;
		this.Q = Q;
		this.startNode = startNode;
		this.caller = caller;
		this.name = name;												//für toString
	}

	/*
	 * Threadeinstieg
	 */
	@Override
	public void run() {
		dijkstra();
	}
	
	/*
	 * Main Loop
	 * Searching shortest path from startNode to endNode running at O(n*(log2 n) + |Edges|)
	 */
	public void dijkstra() {	
		startNode.setDist(0.0);
		Q.decreaseKey(startNode, 0.0);
		while (Q.size() > 0 && !finnished) { 							//while: O(n)
			DijkstraNode currentNode = (DijkstraNode)Q.popMin();								//O(log2 n)
			if (currentNode == null 									//wenn null werte in der fib. heap gelagert werden.
					|| currentNode.getDist() == Double.MAX_VALUE 		//sind alle nachbarn unendlich weit weg ? also gibt es keinen weg nach ziel ? Ziel == Quelle
					|| currentNode == endNode) {						//schon am ziel ?
				finnished = true; 										//Der nächste Thread wird beim Eintritt in die Schleife abgebrochen
				reactivateCaller();										
				break;
			}
			currentNode.setRemovedFromQ(true);							//removed Flag aufgrund von Effizienz (erspart ein .contains von Q)
			LinkedList<DijkstraNode> neighborList = currentNode.getNeighbors();	//alle nachbarn des aktuellen knotens als liste
			for (DijkstraNode neighbor : neighborList) { 						//|Edges|
				if (!neighbor.isRemovedFromQ()) {
					updateNeighborDistance(currentNode, neighbor);
					Q.decreaseKey(neighbor, neighbor.getDist());		//O(1)
				}
				if (checkThreads(currentNode, neighbor)) {				//Gemeinsamer Knoten(neighbor) der beiden Threads? Wenn ja, dann Weg gefunden
					break;
				}
			}
		}
	}
	
	/*
	 * Wecken der aufrufenden Klasse
	 */
	private void reactivateCaller() {
		synchronized(caller.getClass()) {								//weckruf muss auf das aufrufende runtime objekt synchronisiert sein
			caller.getClass().notifyAll();
		}
	}

	/*
	 * Überprüfung des Knotens neighbor, ob dieser von dem anderen Thread analysiert wurde.
	 * Wenn ja, dann endet die Wegsuche an dieser Stelle.
	 */
	private synchronized boolean checkThreads(DijkstraNode currentNode, DijkstraNode neighbor) {
		if (thread1 && neighbor.getPredecessor2() != null) {
			finnished = true;   							//nächster Thread wird beim Eintritt in die Schleife abgebrochen
			while (neighbor != null) {						//konkatenieren des ergebnisses von thread1 and das des knotens neighbor
				neighbor.setPredecessor(currentNode);
				currentNode = neighbor;
				neighbor = neighbor.getPredecessor2();
			}
			reactivateCaller();
			return true;
		}
		if (!thread1 && neighbor.getPredecessor() != null) {
			finnished = true;								//nächster Thread wird beim Eintritt in die Schleife abgebrochen
			while (currentNode != null) {					//konkatenieren des ergebnisses von thread2 and das des knotens currentNode
				currentNode.setPredecessor(neighbor);
				neighbor = currentNode;
				currentNode = currentNode.getPredecessor2();
			}
			reactivateCaller();
			return true;
		}
		return false;
	}

	/*
	 * set new distance(priority) to neighbor
	 */
	private void updateNeighborDistance(DijkstraNode currentNode, DijkstraNode neighbor) {
		//Knoten mit Distanz zur Luftlinie zwischen Start und Endknoten versehen. Dient als Prioritätsschlüßel
		double alternative = currentNode.getDist() + getDistBetweenNodes(currentNode, neighbor) - getDistBetweenNodes(currentNode, endNode) + getDistBetweenNodes(neighbor, endNode);
		if (alternative < neighbor.getDist()) {
			neighbor.setDist(alternative);
			if (thread1) {
				neighbor.setPredecessor(currentNode);
			} else {
				neighbor.setPredecessor2(currentNode);
			}
		}
	}
	
	
	/*
	 * Pythagoras function to evaluate the distance between 2 nodes
	 */
	private double getDistBetweenNodes(DijkstraNode u, DijkstraNode v) {
		double dX = Math.abs(u.getX() - v.getX());
		double dY = Math.abs(u.getY() - v.getY());
		return Math.sqrt(dX*dX + dY*dY);
	}

	@Override
	public String toString() {
		return name;
	}
}