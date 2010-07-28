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
		start.setG(0);
		start.setF(start.getDistanceTo(goal));
		openSet.add(start, start.getF());

		while (openSet.size() > 0) {
			current = (AStarNode) openSet.popMin();
			if (current == goal) {
				return reconstructPath(goal);
			}
			closedSet.put(current.getId(), current);
			for (Edge edge : current.getEdgeList()) {
				AStarNode successor = (AStarNode)edge.getSuccessor();
				if (closedSet.containsKey(successor.id))
					continue;
				double tentativeG = current.getG() + edge.getPrioDist();
				
				if (!openSet.contains(successor)) {
					successor.setPredeccessor(current);
					successor.setG(tentativeG);
					successor.setF(successor.getG() + successor.getDistanceTo(goal));
					openSet.add(successor, successor.getF());
				} else {
					if (tentativeG < successor.getG()) {
						successor.setPredeccessor(current);
						successor.setG(tentativeG);
						successor.setF(successor.getG() + successor.getDistanceTo(goal));
						openSet.decreaseKey(successor, successor.getF());
					}
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
	 * @param allNodesIDs
	 *            HashMap containing all Nodes.
	 * @param edgeStartNodeIDs
	 *            First nodeID that will be connected by the edge.
	 * @param edgeEndNodeIDs
	 *            Second nodeID that will be connected by the edge.
	 * @param edgeLengths
	 *            Distance between first and second Node.
	 * @param oneways
	 *            Is this edge a oneway?
	 * @param highwayTypes
	 *            HighwayType of this edge.
	 * @return a HashTable containing all inserted nodes.
	 */
	private void buildEdges(HashMap<Integer, AStarNode> allNodesIDs,
			int[] edgeStartNodeIDs, int[] edgeEndNodeIDs, double[] edgeLengths,
			boolean[] oneways, int[] highwayTypes) {
		
		for (int i = 0; i < edgeStartNodeIDs.length; i++) {
			AStarNode fromNode = allNodesIDs.get(edgeStartNodeIDs[i]);
			AStarNode toNode = allNodesIDs.get(edgeEndNodeIDs[i]);
			fromNode.addEdge(new Edge(toNode, fromNode.getDistanceTo(toNode), highwayTypes[i]));
			if(!oneways[i]){
				toNode.addEdge(new Edge(fromNode, fromNode.getDistanceTo(toNode), highwayTypes[i]));
			}
		}
	}
	
	private HashMap<Integer, AStarNode> buildNodes(int[] allNodeIDs, float[] lon, float[] lat){
		HashMap<Integer, AStarNode> allNodes = new HashMap<Integer, AStarNode>(allNodeIDs.length, 1.0f);
		for (int i = 0; i < allNodeIDs.length; i++) {
			allNodes.put(allNodeIDs[i], new AStarNode(allNodeIDs[i], lon[i], lat[i]));
		}
		return allNodes;
	}

	//TODO Java Doc
	/**
	 * 
	 */
	@Override
	public Node[] findShortestPath(int[] allNodeIDs, float[] lon, float[] lat,
			int startNodeID, int goalNodeID, int[] edgeIDs,
			int[] edgeStartNodeIDs, int[] edgeEndNodeIDs, double[] edgeLengths,
			boolean[] oneways, int[] highwayTypes, int searchOption) throws PathNotFoundException {
		long time = System.currentTimeMillis();
		HashMap<Integer, AStarNode> allNodes = buildNodes(allNodeIDs, lon, lat);
		System.out.println("HashMap bauen:" + (System.currentTimeMillis() - time) + "ms");
		time = System.currentTimeMillis();
		buildEdges(allNodes, edgeStartNodeIDs, edgeEndNodeIDs, edgeLengths, oneways, highwayTypes);
		System.out.println("Edges bauen: " + (System.currentTimeMillis() - time) + "ms");
		time = System.currentTimeMillis();
		Node[] result = aStar(allNodes, startNodeID, goalNodeID).toArray(new Node[0]);
		System.out.println("Algo: " + (System.currentTimeMillis() - time) + "ms");
		return result;
	}

}
