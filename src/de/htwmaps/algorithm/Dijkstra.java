package de.htwmaps.algorithm;

import java.util.LinkedList;

import de.htwmaps.util.FibonacciHeap;


/*
 * @author Stanislaw Tartakowski
 * @version 1.0
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
		while (Q.size() > 0 && !finnished) { 									
			DijkstraNode currentNode = (DijkstraNode)Q.popMin();				
			if (currentNode == null 											
					|| currentNode.getDist() == Double.MAX_VALUE 				
					|| (currentNode == endNode && endNode.getPredecessor() != null || endNode.getPredecessor2() != null)) {								
				finnished = true; 												
				if (!thread1) {
					DijkstraNode tmp;
					while ((tmp = currentNode.getPredecessor2()) != null) {
						currentNode.setPredecessor(tmp);
						currentNode = tmp;
					}
				}
				reactivateCaller();										
				break;
			}
			currentNode.setRemovedFromQ(true);									
			LinkedList<Edge> edges = currentNode.getEdgeList();	
			for (Edge edge : edges) { 						
				if (!((DijkstraNode)edge.getSuccessor()).isRemovedFromQ()) {
					updateSuccessorDistance(currentNode, (DijkstraNode)edge.getSuccessor());
					Q.decreaseKey((DijkstraNode)edge.getSuccessor(), ((DijkstraNode)edge.getSuccessor()).getDist());				
				}
				if (checkThreads(currentNode, (DijkstraNode)edge.getSuccessor())) {					
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
	 * Überprüfung des Knotens Successor, ob dieser von dem anderen Thread analysiert wurde.
	 * Wenn ja, dann endet die Wegsuche an dieser Stelle.
	 */
	private synchronized boolean checkThreads(DijkstraNode currentNode, DijkstraNode successor) {
		if (!finnished) {
			if (thread1 && successor.getPredecessor2() != null) {
				finnished = true;   							//nächster Thread wird beim Eintritt in die Schleife abgebrochen
				while (successor != null) {						//konkatenieren des ergebnisses von thread1 and das des knotens successor
					successor.setPredecessor(currentNode);
					currentNode = successor;
					successor = successor.getPredecessor2();
				}
				reactivateCaller();
				return true;
			}
			if (!thread1 && successor.getPredecessor() != null) {
				finnished = true;								//nächster Thread wird beim Eintritt in die Schleife abgebrochen
				while (currentNode != null) {					//konkatenieren des ergebnisses von thread2 and das des knotens currentNode
					currentNode.setPredecessor(successor);
					successor = currentNode;
					currentNode = currentNode.getPredecessor2();
				}
				reactivateCaller();
				return true;
			}
		}
		return false;
	}

	/*
	 * set new distance(priority) to successor
	 */
	private void updateSuccessorDistance(DijkstraNode currentNode, DijkstraNode successor) {
		//Knoten mit Distanz zur Luftlinie zwischen Start und Endknoten versehen. Dient als Prioritätsschlüßel
		double alternative = currentNode.getDist() + getDistBetweenNodes(currentNode, successor) - getDistBetweenNodes(currentNode, endNode) + getDistBetweenNodes(successor, endNode);
		if (alternative < successor.getDist()) {
			successor.setDist(alternative);
			if (thread1) {
				successor.setPredecessor(currentNode);
			} else {
				successor.setPredecessor2(currentNode);
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