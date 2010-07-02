package de.htwmaps.algorithm;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import de.htwmaps.util.FibonacciHeap;
import de.htwmaps.util.InitLogger;

/**
 * @author Stanislaw Tartakowski
 * 
 * This is a concurrent implementation of an graph search algorithm based on Dijkstra's.
 * Depart from classic implementations this algorithm has a goal oriented heuristic similar to A*'s 
 * and is optimized for maximal speed performance. Though, this algorithm doesn't guarantee
 * best possible solution, but a relatively good one. This class can only be reasonably used if the caller of this class
 * remains sleeping until this class awakens him when the work is done.
 */
public class Dijkstra extends Thread {
	private volatile static boolean finished;								
	private volatile static AtomicInteger count = new AtomicInteger();		
	private volatile Dijkstra dij;
	private boolean thread;
	private DijkstraNode startNode, endNode;
	private Object caller;
	private int nodesVisited;
	
	/**
	 * 
	 * @param Q the container of all nodes of the graph
	 * @param startNode destination
	 * @param endNode goal
	 * @param thread flags the threads to run from destination to goal (true) or reverse (false)
	 * @param caller the object who started this class and waits for its completion 
	 */
	public Dijkstra(DijkstraNode startNode, DijkstraNode endNode, boolean thread, Object caller) {
		this.startNode = startNode;
		this.endNode = endNode;
		this.thread = thread;
		this.caller = caller;
	}
	
	/**
	 * thread entrance
	 */
	@Override
	public void run() {
		FibonacciHeap Q = new FibonacciHeap();
		startNode.setDist(0.0);
		touch(startNode);
		Q.add(startNode, 0.0);
		nodesVisited++;
		while (Q.size() > 0) {
			if (finished) {
				return;
			}
			DijkstraNode currentNode = (DijkstraNode) Q.popMin();
			if (currentNode == endNode) {
				reactivateCaller();
				return;					
			}
			currentNode.setRemovedFromQ(true);
			LinkedList<Edge> edges = currentNode.getEdgeList();
			for (Edge edge : edges) {
				DijkstraNode successor = edge.getSuccessor() != currentNode ? (DijkstraNode)edge.getSuccessor() : (DijkstraNode)edge.getPredecessor();
				if (thread || (!thread && edge.getPredecessor() != null)) {
					if (!thread && successor.isTouchedByTh1() || thread && successor.isTouchedByTh2() || !successor.isRemovedFromQ()) {
						synchronized(getClass()) {
							if (checkForCommonNode(currentNode, successor)) {
								return;
							}
							if (!successor.isRemovedFromQ()) {
								updateSuccDist(Q, currentNode, successor);
							}
						}
					}
				}
			}
		}
		InitLogger.INSTANCE.getLogger().warn(this + ": no path found. Terminates");
		if (count.incrementAndGet() == 2) {
			reactivateCaller();
			return;
		}
	}

	private void updateSuccDist(FibonacciHeap Q, DijkstraNode currentNode, DijkstraNode successor) {
		double alternative = currentNode.getDist() + currentNode.getDistanceTo(successor);

		if (alternative < successor.getDist()) {
			successor.setDist(alternative);
			successor.setPredecessor(currentNode);
			touch(successor);
			nodesVisited++;
			if (!Q.contains(successor)) { 
				Q.add(successor, alternative + successor.getDistanceTo(endNode) - successor.getDistanceTo(startNode));
			}
		}
	}

	/**
	 * Sets a touched mark on the current node as a hint for the threads whether the node has been analyzed before
	 */
	private void touch(DijkstraNode node) {
		if (thread) {
			node.setTouchedByTh1(true);
		} else {
			node.setTouchedByTh2(true);
		}
	}

	/**
	 * Checks whether the current thread may build the result and finish the algorithm
	 */
	private boolean checkForCommonNode(DijkstraNode currentNode, DijkstraNode successor) {
		if (!thread && successor.isTouchedByTh1() || thread && successor.isTouchedByTh2()) {
			if (dij.getNodesVisited() > nodesVisited) {
				concantenate(successor, currentNode);
			} else {
				concantenate(currentNode, successor);
			}
			return true;
		}
		return false;
	}

	/**
	 * Builds the result. Can be entered only once.
	 */
	private void concantenate(DijkstraNode currentNode, DijkstraNode successor) {
		DijkstraNode tmp;
		if (!finished) {
			while (successor != null) {
				tmp = successor.getPredecessor();
				successor.setPredecessor(currentNode);
				currentNode = successor;
				successor = tmp;
			}
			reactivateCaller();
		}
	}
	
	/**
	 * wakes up every thread thread, waiting on caller's class
	 */
	private void reactivateCaller() {
		synchronized(caller.getClass()) {	
			finished = true;
			caller.getClass().notifyAll();
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * 
	 * @return indicator that says whether the algorithm has finished
	 */
	public static boolean isFinished() {
		return finished;
	}

	public void setDijkstra(Dijkstra dij) {
		this.setDij(dij);
	}

	public int getNodesVisited() {
		return nodesVisited;
	}

	public void setDij(Dijkstra dij) {
		this.dij = dij;
	}

	public Dijkstra getDij() {
		return dij;
	}
}