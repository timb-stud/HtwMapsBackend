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
	 * @param allNodes
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
	private ArrayList<AStarNode> aStar(HashMap<Integer, AStarNode> allNodes,
			int startID, int goalID) throws PathNotFoundException {
		AStarNode start = allNodes.get(startID);
		AStarNode goal = allNodes.get(goalID);
		HashMap<Integer, AStarNode> closedSet = new HashMap<Integer, AStarNode>(
				allNodes.size());
		FibonacciHeap openSet = new FibonacciHeap();
		AStarNode current;
		openSet.add(start, start.getF());
		start.setG(0);
		start.setF(start.getDistanceTo(goal));

		while (!(openSet.size() < 1)) {
			current = (AStarNode) openSet.popMin();
			if (current == goal) {
				return reconstructPath(goal);
			}
			closedSet.put(current.getId(), current);
			for (Edge edge : current.getEdgeList()) {
				boolean useTentativeG;
				AStarNode successor = (AStarNode)edge.getSuccessor();
				if (closedSet.containsKey(successor.id)) // TODO contains key or value !?!??!
					continue;
				double tentativeG = current.getG() + edge.getDistance();
				
				if (!openSet.contains(successor)) {
					openSet.add(successor, successor.getF());
					useTentativeG = true;
				} else {
					if (tentativeG < successor.getG()) {
						useTentativeG = true;
					} else {
						useTentativeG = false;
					}
				}
				if (useTentativeG) {
					successor.setPredeccessor(current);
					successor.setG(tentativeG);
					successor.setF(successor.getG() + successor.getDistanceTo(goal));
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
	//TODO multiplicate highwaytypes with distance
	private void buildEdges(HashMap<Integer, AStarNode> allNodes,
			int[] fromNodeIDs, int[] toNodeIDs, double[] fromToDistances,
			boolean[] oneways, int[] highwayTypes) {
		
		for (int i = 0; i < fromNodeIDs.length; i++) {
			AStarNode fromNode = allNodes.get(fromNodeIDs[i]);
			AStarNode toNode = allNodes.get(toNodeIDs[i]);
			fromNode.addEdge(new Edge(toNode, fromToDistances[i]));
			if(!oneways[i]){
				toNode.addEdge(new Edge(fromNode, fromToDistances[i]));
			}
		}
	}
	
	private HashMap<Integer, AStarNode> buildNodes(int[] allNodeIDs, float[] x, float[] y){
		HashMap<Integer, AStarNode> allNodes = new HashMap<Integer, AStarNode>(allNodeIDs.length, 1.0f);
		for (int i = 0; i < allNodeIDs.length; i++) {
			allNodes.put(allNodeIDs[i], new AStarNode(allNodeIDs[i], x[i], y[i]));
		}
		return allNodes;
	}

	//TODO Java Doc
	/**
	 * 
	 */
	@Override
	public Node[] findShortestPath(int[] allNodeIDs, float[] x, float[] y,
			int startNodeID, int goalNodeID, int[] fromNodeIDs,
			int[] toNodeIDs, double[] fromToDistances, boolean[] oneways,
			int[] highwayTypes) throws PathNotFoundException {
		
		HashMap<Integer, AStarNode> allNodes = buildNodes(allNodeIDs, x, y);
		buildEdges(allNodes, fromNodeIDs, toNodeIDs, fromToDistances, oneways, highwayTypes);
		return aStar(allNodes, startNodeID, goalNodeID).toArray(new Node[0]);
	}

}
