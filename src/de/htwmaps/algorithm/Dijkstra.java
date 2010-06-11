package de.htwmaps.algorithm;

import java.util.LinkedList;

import de.htwmaps.util.FibonacciHeap;

public class Dijkstra {

	public void dijkstra(FibonacciHeap Q, DijkstraNode startNode, DijkstraNode endNode) {
		startNode.setDist(0.0);
		Q.decreaseKey(startNode, 0.0);
		while (Q.size() > 0) {
			DijkstraNode currentNode = (DijkstraNode) Q.popMin();
			if (currentNode == null || currentNode.getDist() == Double.MAX_VALUE || currentNode == endNode ) {
				break;
			}
			currentNode.setRemovedFromQ(true);
			LinkedList<Edge> edges = currentNode.getEdgeList();
			for (Edge edge : edges) {
				DijkstraNode successor = (DijkstraNode) edge.getSuccessor();
				if (!successor.isRemovedFromQ()) {
					updateSuccessorDistance(currentNode, successor, endNode);
					Q.decreaseKey(successor, successor.getDist());
				}
			}
		}
	}

	private void updateSuccessorDistance(DijkstraNode currentNode, DijkstraNode successor, DijkstraNode endNode) {
		double alternative = currentNode.getDist() + getDistBetweenNodes(currentNode, successor) - getDistBetweenNodes(currentNode, endNode) + getDistBetweenNodes(successor, endNode);
		if (alternative < successor.getDist()) {
			successor.setDist(alternative);
			successor.setPredecessor(currentNode);
		}
	}

	
	private double getDistBetweenNodes(DijkstraNode u, DijkstraNode v) {
		double dX = Math.abs(u.getX() - v.getX());
		double dY = Math.abs(u.getY() - v.getY());
		return Math.sqrt(dX * dX + dY * dY);
	}
}