package de.htwmaps.algorithm;

import java.util.LinkedList;

import de.htwmaps.util.FibonacciHeap;

public class Dijkstra extends Thread {
	private static boolean finnished;
	private boolean thread1;
	private FibonacciHeap Q;
	private DijkstraNode startNode, endNode;
	private Object caller;
	private String name;
	
	public Dijkstra(FibonacciHeap Q, DijkstraNode startNode, DijkstraNode endNode, boolean thread1, Object caller, String name) {
		this.Q = Q;
		this.startNode = startNode;
		this.endNode = endNode;
		this.thread1 = thread1;
		this.caller = caller;
		this.name = name;
	}
	
	@Override
	public void run() {
		try {
			dijkstra();
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			return;
		}
	}
	
	public void dijkstra() throws InterruptedException {
		startNode.setDist(0.0);
		touch(startNode);
		Q.decreaseKey(startNode, 0.0);
		mainloop:while (Q.size() > 0) {
			if (finnished) {
				throw new InterruptedException(this + " has been finnished");
			}
			DijkstraNode currentNode = (DijkstraNode) Q.popMin();
			if (currentNode == null || currentNode.getDist() == Double.MAX_VALUE || currentNode == endNode ) {
				if (endNode.getPredecessor() == null) {
					throw new InterruptedException(this + " oneway");
				}
				finnished = true;
				break;
			}
			currentNode.setRemovedFromQ(true);
			LinkedList<Edge> edges = currentNode.getEdgeList();
			for (Edge edge : edges) {
				DijkstraNode successor = (DijkstraNode) edge.getSuccessor();
				if (checkForCommonNode(currentNode, successor)) {
					break mainloop;
				}
				if (!successor.isRemovedFromQ()) {
					updateSuccessorDistance(currentNode, edge);
					Q.decreaseKey(successor, successor.getDist());
				}
			}
		}
		reactivateCaller();
	}

	private void touch(DijkstraNode node) {
		if (thread1) {
			node.setTouchedByTh1(true);
		} else {
			node.setTouchedByTh2(true);
		}
	}

	private boolean checkForCommonNode(DijkstraNode currentNode, DijkstraNode successor) {
		if (!thread1 && successor.isTouchedByTh1() || thread1 && successor.isTouchedByTh2()) {
			concantenate(currentNode, successor);
			return true;
		}
		return false;
	}

	private void concantenate(DijkstraNode currentNode, DijkstraNode successor) {
		synchronized (this.getClass()) {
			DijkstraNode tmp;
			if (!finnished) {
				finnished = true;
				while (successor != null) {
					tmp = successor.getPredecessor();
					successor.setPredecessor(currentNode);
					currentNode = successor;
					successor = tmp;
				}
			}
		}
	}

	private void updateSuccessorDistance(DijkstraNode currentNode, Edge edge) {
		DijkstraNode successor = (DijkstraNode)edge.getSuccessor();
		double alternative = currentNode.getDist() + edge.getDistance() - getDistBetweenNodes(currentNode, endNode) + getDistBetweenNodes(successor, endNode);
		if (alternative < successor.getDist()) {
			successor.setDist(alternative);
			successor.setPredecessor(currentNode);
			touch(successor);
		}
	}

	
	private double getDistBetweenNodes(DijkstraNode u, DijkstraNode v) {
		double dX = Math.abs(u.getX() - v.getX());
		double dY = Math.abs(u.getY() - v.getY());
		return Math.sqrt(dX * dX + dY * dY);
	}
	
	private void reactivateCaller() {
		synchronized(caller.getClass()) {								//weckruf muss auf das aufrufende runtime objekt synchronisiert sein
			caller.getClass().notifyAll();
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}