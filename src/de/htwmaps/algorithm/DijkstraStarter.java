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
			DijkstraNode fromNode = Q.get(fromID), toNode = Q.get(toID);
			Edge onewayEdge = new Edge(toNode, fromToDistances[i]);
			fromNode.addEdge(onewayEdge);
			if(!oneways[i]) {
				onewayEdge.setPredecessor(fromNode);
				toNode.addEdge(onewayEdge);
			}
		}
	}
	
	private void generateNodesQ(FibonacciHeap fh, FibonacciHeap fh2, HashMap<Integer, DijkstraNode> Q, int[] allNodesIDs, float[] x, float[] y) {
		DijkstraNode node;
		for (int i = 0; i < allNodesIDs.length; i++) {
			node = new DijkstraNode(x[i], y[i], allNodesIDs[i]);
			Q.put(allNodesIDs[i], node);
			fh.add(node, node.getDist());
			fh2.add(node, node.getDist());
		}
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
		
		FibonacciHeap fh = new FibonacciHeap();
		FibonacciHeap fh2 = new FibonacciHeap();
		HashMap<Integer, DijkstraNode> Q = new HashMap<Integer, DijkstraNode> (allNodesIDs.length);
		
		generateNodesQ(fh, fh2, Q, allNodesIDs, x, y);
		generateReferences(Q, fromNodeIDs, toNodeIDs, oneways, fromToDistances);
		

		
		DijkstraNode startNode = Q.get(startNodeID); 
		DijkstraNode endNode = Q.get(goalNodeID);
		Dijkstra d0 = new Dijkstra(fh, startNode, endNode, true, this);
		Dijkstra d1 = new Dijkstra(fh2, endNode, startNode, false, this);
		d0.start();
		d1.start();
		synchronized(getClass()) {
			try {
				while (!Dijkstra.finished) {
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
