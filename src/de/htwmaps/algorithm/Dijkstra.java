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
	private static boolean finished;								//TODO should be volatile
	private static AtomicInteger count = new AtomicInteger();		//TODO should be volatile
	private boolean thread;
	private FibonacciHeap Q;
	private DijkstraNode startNode, endNode;
	private Object caller;
	
	/**
	 * 
	 * @param Q the container of all nodes of the graph
	 * @param startNode destination
	 * @param endNode goal
	 * @param thread flags the threads to run from destination to goal (true) or reverse (false)
	 * @param caller the object who started this class and waits for its completion 
	 */
	public Dijkstra(FibonacciHeap Q, DijkstraNode startNode, DijkstraNode endNode, boolean thread, Object caller) {
		this.Q = Q;
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
		dijkstra();
	}
	
	/**
	 * Main loop
	 */
	private void dijkstra() {
		startNode.setDist(0.0);
		touch(startNode);
		Q.decreaseKey(startNode, 0.0);
		mainloop:while (Q.size() > 0) {
			if (finished) {
				return;
			}
			DijkstraNode currentNode = (DijkstraNode) Q.popMin();
			if (thread && currentNode.isTouchedByTh2() || !thread && currentNode.isTouchedByTh1() || currentNode.getDist() == Double.MAX_VALUE || currentNode == endNode ) {
				if (currentNode == endNode && currentNode.getPredecessor() != null) {
					finished = true;
					break;					
				} else {
					InitLogger.INSTANCE.getLogger().warn(this + ": no path found. Terminates");
					if (count.incrementAndGet() == 2) {
						finished = true;
						break;
					}
					return;
				}
			}
			currentNode.setRemovedFromQ(true);
			LinkedList<Edge> edges = currentNode.getEdgeList();
			for (Edge edge : edges) {
				DijkstraNode successor = edge.getSuccessor() != currentNode ? (DijkstraNode)edge.getSuccessor() : (DijkstraNode)edge.getPredecessor();
				if (thread || (!thread && edge.getPredecessor() != null)) {
					if (!thread && successor.isTouchedByTh1() || thread && successor.isTouchedByTh2() || !successor.isRemovedFromQ()) {
						synchronized(getClass()) {
							if (checkForCommonNode(currentNode, successor)) {
								break mainloop;
							}
							if (!successor.isRemovedFromQ()) {
								updateSuccessorDistance(currentNode, successor);
								Q.decreaseKey(successor, successor.getDist());
							}
						}
					}
				}
			}
		}
		reactivateCaller();
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
			concantenate(currentNode, successor);
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
			finished = true;
			while (successor != null) {
				tmp = successor.getPredecessor();
				successor.setPredecessor(currentNode);
				currentNode = successor;
				successor = tmp;
			}
		}
	}

	/**
	 * Updates the distance to a successor node
	 */
	private void updateSuccessorDistance(DijkstraNode currentNode, DijkstraNode successor) {
		double alternative = currentNode.getDist() + currentNode.getDistanceTo(successor) - currentNode.getDistanceTo(endNode) + successor.getDistanceTo(endNode);
		if (alternative < successor.getDist()) {
			successor.setDist(alternative);
			successor.setPredecessor(currentNode);
			touch(successor);
		}
	}
	
	/**
	 * wakes up every thread thread, waiting on caller's class
	 */
	private void reactivateCaller() {
		synchronized(caller.getClass()) {	
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
}