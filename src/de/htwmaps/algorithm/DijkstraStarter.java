package de.htwmaps.algorithm;


import java.util.ArrayList;

import de.htwmaps.util.FibonacciHeap;



/**
 * @author Stanislaw Tartakowski
 * 
 * This class build a graph, calls the search algorithms, awaits their end and builds result
 */
public class DijkstraStarter implements ShortestPathAlgorithm {
	/**
	 * knotenobjekte miteinander referenzieren
	 */
	private void generateReferences(FibonacciHeap Q, int[] fromNodeIDs, int[] toNodeIDs, boolean[] oneways, double[] fromToDistances) {
		for (int i = 0 ; i < fromNodeIDs.length; i++) {
			DijkstraNode fromNode = Q.getDijkstraNode(fromNodeIDs[i]), toNode = Q.getDijkstraNode(toNodeIDs[i]);
			Edge onewayEdge = new Edge(toNode, fromToDistances[i]);
			fromNode.addEdge(onewayEdge);
			if(!oneways[i]) {
				onewayEdge.setPredecessor(fromNode);
				toNode.addEdge(onewayEdge);
			}
		}
	}
	
	/**
	 * Knoten erstellen
	 */
	private void generateNodes(FibonacciHeap QTh1, FibonacciHeap QTh2, int[] allNodesIDs, float[] x, float[] y) {
		DijkstraNode node;
		for (int i = 0; i < allNodesIDs.length; i++) {
			node = new DijkstraNode(x[i], y[i], allNodesIDs[i]);
			QTh1.add(node, Double.MAX_VALUE);
			QTh2.add(node, Double.MAX_VALUE);
		}
	}
	
	/**
	 * node list -> Node[] array
	 */
	public Node[] nodeToArray(DijkstraNode startNode, DijkstraNode endNode) {
		DijkstraNode tmp = startNode.getPredecessor() != null ? startNode : endNode;
		ArrayList<DijkstraNode> nodesContainer = new ArrayList<DijkstraNode>();
		while (tmp != null) {
			nodesContainer.add(tmp);
			tmp = tmp.getPredecessor();
		}
		return nodesContainer.toArray(new Node[0]);
	}

	/**
	 * This method is the interface to this class
	 * @return result node array
	 */
	@Override
	public Node[] findShortestPath(int[] allNodesIDs, float[] x, float[] y, int startNodeID, int goalNodeID, int[] fromNodeIDs,
			int[] toNodeIDs,
			double[] fromToDistances, 
			boolean[] oneways,
			int[] highwayTypes) throws PathNotFoundException {
		
		FibonacciHeap QTh1 = new FibonacciHeap();
		FibonacciHeap QTh2 = new FibonacciHeap();

		generateNodes(QTh1, QTh2, allNodesIDs, x, y);
		generateReferences(QTh1, fromNodeIDs, toNodeIDs, oneways, fromToDistances);
		
		DijkstraNode startNode = QTh1.getDijkstraNode(startNodeID); 
		DijkstraNode endNode = QTh1.getDijkstraNode(goalNodeID);
		
		Dijkstra d0 = new Dijkstra(QTh1, startNode, endNode, true, this);
		Dijkstra d1 = new Dijkstra(QTh2, endNode, startNode, false, this);
		d0.start();
		d1.start();
		synchronized(getClass()) {
			try {
				while (!Dijkstra.isFinished()) {
					this.getClass().wait();
				}
			} catch (InterruptedException e) {}
		}
		Node[] result = nodeToArray(startNode, endNode);
		if (result.length == 1) {
			throw new PathNotFoundException();
		}
		return result;
	}
}
