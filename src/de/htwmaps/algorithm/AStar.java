package de.htwmaps.algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import de.htwmaps.util.FibonacciHeap;

/**
 * A Star implementation from en.wikipedia.org
 * 
 * @author Tim Bartsch
 * 
 */
public class AStar implements ShortestPathAlgorithm {

	/**
	 * Implementation of the A Star algorithm. It uses Nodes connected with
	 * references.
	 * 
	 * @param nodeTable
	 *            a HashTable containing all Nodes or at least start and goal
	 *            Node.
	 * @param startID
	 *            id of the start node.
	 * @param goalID
	 *            id of the goal node.
	 * @return ArrayList containing Nodes representing the way found from goal
	 *         to start.
	 * @throws PathNotFoundException
	 *             if no way from start to goal is found. This exception will be
	 *             thrown.
	 */
	public ArrayList<AStarNode> aStar(HashMap<Integer, AStarNode> nodeTable,
			int startID, int goalID) throws PathNotFoundException {
		AStarNode start = nodeTable.get(startID);
		AStarNode goal = nodeTable.get(goalID);
		HashMap<Integer, AStarNode> closedSet = new HashMap<Integer, AStarNode>(
				nodeTable.size());
		FibonacciHeap openSet = new FibonacciHeap();
		AStarNode current;
		openSet.add(start, start.getF());
		start.setG(0);
		start.setF(start.getH(goal));

		while (!openSet.isEmpty()) {
			current = (AStarNode) openSet.popMin();
			if (current == goal) {
				return reconstructPath(goal);
			}
			closedSet.put(current.getId(), current);
			int i = 0;
			for (AStarNode succ : current.getSuccessors()) {
				boolean useTentativeG;
				if (closedSet.containsKey(succ.getId())) // TODO contains key or
															// value !?!??!
					continue;
				double tentativeG = current.getG() + current.getDistToSucc(i++);

				if (!openSet.contains(succ)) {
					openSet.add(succ, succ.getF());
					useTentativeG = true;
				} else {
					if (tentativeG < succ.getG()) {
						useTentativeG = true;
					} else {
						useTentativeG = false;
					}
				}
				if (useTentativeG) {
					succ.setPredeccessor(current);
					succ.setG(tentativeG);
					succ.setF(succ.getG() + succ.getH(goal));
				}
			}
		}
		throw new PathNotFoundException();
	}

	/**
	 * Writes the Nodes on the way between goal and start in an ArrayList
	 * 
	 * @param goal
	 *            the goal Node of the searched way.
	 */
	private ArrayList<AStarNode> reconstructPath(AStarNode goal) {
		ArrayList<AStarNode> path = new ArrayList<AStarNode>();
		while (goal != null) {
			path.add(goal);
			goal = goal.getPredeccessor();
		}
		return path;
	}

	/**
	 * Creates a referenced graph with the given parameters. <br>
	 * The parameters fromNodeIDs, toNodeIDs, fromToDistances, oneways and
	 * highwayTypes must have the same order.
	 * 
	 * @param allNodes
	 *            HashMap containing all Nodes.
	 * @param fromNodeIDs
	 *            First nodeID that will be connected by the edge.
	 * @param toNodeIDs
	 *            Second nodeID that will be connected by the edge.
	 * @param fromToDistances
	 *            Distance between first and second Node.
	 * @param oneways
	 *            Is this edge a oneway?
	 * @param highwayTypes
	 *            HighwayType of this edge.
	 * @return a HashTable containing all inserted nodes.
	 */
	public void buildEdges(HashMap<Integer, AStarNode> allNodes,
			int[] fromNodeIDs, int[] toNodeIDs, double[] fromToDistances,
			boolean[] oneways, int[] highwayTypes) {

		for (int i = 0; i < fromNodeIDs.length; i++) {
			AStarNode n = allNodes.get(fromNodeIDs[i]);
			AStarNode succ = allNodes.get(toNodeIDs[i]);
			n.addSuccessor(succ, fromToDistances[i], oneways[i],
					highwayTypes[i]);
		}
	}

	//TODO Java Doc
	/**
	 * 
	 */
	@Override
	public AStarNode[] findShortestPath(HashMap<Integer, AStarNode> allNodes,
			int startNodeID, int goalNodeID, int[] fromNodeIDs,
			int[] toNodeIDs, double[] fromToDistances, boolean[] oneways,
			int[] highwayTypes) throws PathNotFoundException {

		buildEdges(allNodes, fromNodeIDs, toNodeIDs, fromToDistances, oneways,
				highwayTypes);
		return (AStarNode[]) (aStar(allNodes, startNodeID, goalNodeID)
				.toArray());
	}

}
