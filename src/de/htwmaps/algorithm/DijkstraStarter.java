package de.htwmaps.algorithm;


import java.util.ArrayList;
import java.util.HashMap;

import de.htwmaps.util.FibonacciHeap;



/*
 * Dieses Programm ruft den Suchalgorithmus auf
 */
public class DijkstraStarter implements ShortestPathAlgorithm {
	/*
	 * knotenobjekte miteinander referenzieren
	 */
	public void generateReferences(HashMap<Integer, DijkstraNode> Q, int[] fromNodeIDs, int[] toNodeIDs, boolean[] oneways, double[] fromToDistances) {
		for (int i = 0 ; i < fromNodeIDs.length; i++) {
			int fromID = fromNodeIDs[i], toID = toNodeIDs[i];
			Q.get(fromID).addEdge(new Edge(Q.get(toID), fromToDistances[i]));
			if(!oneways[i])
				Q.get(toID).addEdge(new Edge(Q.get(fromID), fromToDistances[i]));
		}
	}
	
	private HashMap<Integer, DijkstraNode> generateNodesQ(int[] allNodesIDs, float[] x, float[] y) {
		HashMap<Integer, DijkstraNode> Q = new HashMap<Integer, DijkstraNode> (allNodesIDs.length);
		for (int i = 0; i < allNodesIDs.length; i++) {
			Q.put(allNodesIDs[i], new DijkstraNode(x[i], y[i], allNodesIDs[i]));
		}
		return Q;
	}
	
	/*
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


	@Override
	public Node[] findShortestPath(int[] allNodesIDs, float[] x, float[] y, int startNodeID, int goalNodeID, int[] fromNodeIDs,
			int[] toNodeIDs,
			double[] fromToDistances, 
			boolean[] oneways,
			int[] highwayTypes) throws PathNotFoundException {
		
		HashMap<Integer, DijkstraNode> Q = generateNodesQ(allNodesIDs, x, y);
		generateReferences(Q, fromNodeIDs, toNodeIDs, oneways, fromToDistances);
		

		FibonacciHeap fh = new FibonacciHeap();
		FibonacciHeap fh2 = new FibonacciHeap();
		for (DijkstraNode n : Q.values()) {
			fh.add(n, n.getDist());
			fh2.add(n, n.getDist());
		}
		
		DijkstraNode startNode = Q.get(startNodeID); 
		DijkstraNode endNode = Q.get(goalNodeID);
		Dijkstra d0 = new Dijkstra(fh, startNode, endNode, true, this, "Thread1");
		Dijkstra d1 = new Dijkstra(fh2, endNode, startNode, false, this, "Thread2");
		d0.start();
		d1.start();
		synchronized(this.getClass()) {
			try {
				this.getClass().wait();
			} catch (InterruptedException e) {}
		}
		Node[] result = nodeToArray(startNode, endNode);
		if (result.length == 1) {
			throw new PathNotFoundException();
		}
		return result;
	}

}
