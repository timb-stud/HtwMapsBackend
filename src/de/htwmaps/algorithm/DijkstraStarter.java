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
	public void generateReferences(HashMap<Integer, Node> Q, int[] fromNodeIDs, int[] toNodeIDs, boolean[] oneways) {
		for (int i = 0 ; i < fromNodeIDs.length; i++) {
			int fromID = fromNodeIDs[i], toID = toNodeIDs[i];
			((DijkstraNode)Q.get(fromID)).addNextNeighbor(((DijkstraNode)Q.get(toID)));
			if(!oneways[i]) //nicht oneway
				((DijkstraNode)Q.get(toID)).addNextNeighbor(((DijkstraNode)Q.get(fromID)));
		}
	}
	
	/*
	 * node list -> Node[] array
	 */
	public Node[] nodeToArray(Node endNode, Node startNode) {
		Node tmp = ((DijkstraNode)startNode).getPredecessor() != null ? startNode : endNode;
		ArrayList<Node> nodesContainer = new ArrayList<Node>();
		while (tmp != null) {
			nodesContainer.add(tmp);
			tmp = ((DijkstraNode)tmp).getPredecessor();
		}
		return nodesContainer.toArray(new Node[0]);
	}

	//TODO PathNotFoundException
	@Override
	public Node[] findShortestPath(int[] allNodesIDs, int startNodeID, int goalNodeID, int[] fromNodeIDs, int[] toNodeIDs, double[] fromToDistances, boolean[] oneways, int[] highwayTypes) {
		
		generateReferences(allNodes, fromNodeIDs, toNodeIDs, oneways);
		
		/*
		 * hashmap nach fibonacci heap umwandeln. 360ms
		 */
		FibonacciHeap fh = new FibonacciHeap();
		FibonacciHeap fh2 = new FibonacciHeap();
		for (Node n : allNodes.values()) {
			fh.add(n, ((DijkstraNode)n).getDist());
			fh2.add(n, ((DijkstraNode)n).getDist());
		}
		
		DijkstraNode startNode = (DijkstraNode)allNodes.get(startNodeID); 
		DijkstraNode endNode = (DijkstraNode)allNodes.get(goalNodeID);

		
		Dijkstra d0 = new Dijkstra(fh, startNode, endNode, true, this, "Tread1");
		Dijkstra d1 = new Dijkstra(fh2, endNode, startNode, false, this, "Thread2");
		d0.start();
		d1.start();
		
		synchronized(this.getClass()) {
			try{
				while (!Dijkstra.finnished)
					this.getClass().wait();
			} catch(InterruptedException iexc) {
			}
		}
		return nodeToArray(endNode, startNode);
	}
}
