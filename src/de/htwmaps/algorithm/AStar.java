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
public class AStar extends ShortestPathAlgorithm {
	HashMap<Integer, AStarNode> allNodes;
	
	
	public AStar(GraphData gd) {
		super(gd);
	}

	/**
	 * Implementation of the A Star algorithm. It uses Nodes connected with
	 * references.
	 * 
	 * @param allNodes
	 *            a HashTable containing all Nodes or at least start and goal
	 *            Node.
	 * @param startNodeID
	 *            id of the start node.
	 * @param goalNodeID
	 *            id of the goal node.
	 * @return ArrayList containing Nodes representing the way found from goal
	 *         to start.
	 * @throws PathNotFoundException
	 *             if no way from start to goal is found. This exception will be
	 *             thrown.
	 */
	private ArrayList<AStarNode> aStar(int startNodeID, int goalNodeID, int maxSpeed) throws PathNotFoundException {
		AStarNode start = allNodes.get(startNodeID);
		AStarNode goal = allNodes.get(goalNodeID);
		HashMap<Integer, AStarNode> closedSet = new HashMap<Integer, AStarNode>(
				allNodes.size());
		FibonacciHeap openSet = new FibonacciHeap();
		AStarNode current;
		start.setG(0);
		start.setF(start.getDistanceTo(goal) / maxSpeed);
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
				double tentativeG = current.getG() + edge.getPrioLength();
				
				if (!openSet.contains(successor)) {
					successor.setPredeccessor(current);
					successor.setG(tentativeG);
					successor.setF(successor.getG() + (successor.getDistanceTo(goal) / maxSpeed));
					openSet.add(successor, successor.getF());
				} else {
					if (tentativeG < successor.getG()) {
						successor.setPredeccessor(current);
						successor.setG(tentativeG);
						successor.setF(successor.getG() + (successor.getDistanceTo(goal) / maxSpeed));
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
 * 
 * @return
 */
	private void buildNodes(){
		int[] allNodeIDs = graphData.getAllNodeIDs();
		float[] allNodeLats = graphData.getAllNodeLats();
		float[] allNodeLons = graphData.getAllNodeLons();
		this.allNodes = new HashMap<Integer, AStarNode>(allNodeIDs.length, 1.0f);
		for (int i = 0; i < allNodeIDs.length; i++) {
			allNodes.put(allNodeIDs[i], new AStarNode(allNodeIDs[i], allNodeLons[i], allNodeLats[i]));
		}
	}
	

	/**
	 * 
	 */
	private void buildEdges(int motorwaySpeed, int primarySpeed, int secondarySpeed, int residentialSpeed, int roadSpeed, int livingStreetSpeed){
		int[] edgeStartNodeIDs = graphData.getEdgeStartNodeIDs();
		int[] edgeEndNodeIDs = graphData.getEdgeEndNodeIDs();
		double[] edgeLenghts = graphData.getEdgeLengths();
		int[] highwayTypes = graphData.getHighwayTypes();
		int[] wayIDs = graphData.getWayIDs();
		boolean[] oneways = graphData.getOneways();
		
		for (int i = 0; i < edgeStartNodeIDs.length; i++) {
			AStarNode fromNode = allNodes.get(edgeStartNodeIDs[i]);
			AStarNode toNode = allNodes.get(edgeEndNodeIDs[i]);
			
			switch (highwayTypes[i]) {
			case ShortestPathAlgorithm.MOTORWAY:
				fromNode.addEdge(new Edge(toNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], motorwaySpeed));
				if(!oneways[i])
					toNode.addEdge(new Edge(fromNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], motorwaySpeed));
				break;
			case ShortestPathAlgorithm.PRIMARY:
				fromNode.addEdge(new Edge(toNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], primarySpeed));
				if(!oneways[i])
					toNode.addEdge(new Edge(fromNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], primarySpeed));
				break;
			case ShortestPathAlgorithm.SECONDARY:
				fromNode.addEdge(new Edge(toNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], secondarySpeed));
				if(!oneways[i])
					toNode.addEdge(new Edge(fromNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], secondarySpeed));
				break;
			case ShortestPathAlgorithm.RESIDENTIAL:
				fromNode.addEdge(new Edge(toNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], residentialSpeed));
				if(!oneways[i])
					toNode.addEdge(new Edge(fromNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], residentialSpeed));
				break;
			case ShortestPathAlgorithm.ROAD:
				fromNode.addEdge(new Edge(toNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], roadSpeed));
				if(!oneways[i])
					toNode.addEdge(new Edge(fromNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], roadSpeed));
				break;
			case ShortestPathAlgorithm.LIVING_STREET:
				fromNode.addEdge(new Edge(toNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], livingStreetSpeed));
				if(!oneways[i])
					toNode.addEdge(new Edge(fromNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], livingStreetSpeed));
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
	}
	
	/**
	 * 
	 */
	private void buildEdges() {
		int[] edgeStartNodeIDs = graphData.getEdgeStartNodeIDs();
		int[] edgeEndNodeIDs = graphData.getEdgeEndNodeIDs();
		double[] edgeLenghts = graphData.getEdgeLengths();
		int[] highwayTypes = graphData.getHighwayTypes();
		int[] wayIDs = graphData.getWayIDs();
		boolean[] oneways = graphData.getOneways();
		for (int i = 0; i < edgeStartNodeIDs.length; i++) {
			AStarNode fromNode = allNodes.get(edgeStartNodeIDs[i]);
			AStarNode toNode = allNodes.get(edgeEndNodeIDs[i]);
			fromNode.addEdge(new Edge(toNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], 1));
				if(!oneways[i])
					toNode.addEdge(new Edge(fromNode, edgeLenghts[i], highwayTypes[i], wayIDs[i], 1));
		}
	}
	
	/**
	 * 
	 */
	@Override
	public Node[] findFastestPath(int startNodeID, int goalNodeID,
			int motorwaySpeed, int primarySpeed, int secondarySpeed,
			int residentialSpeed, int roadSpeed, int livingStreetSpeed)
			throws PathNotFoundException {
		
		int maxSpeed;
		int[] speeds= new int[6];
		speeds[0] = motorwaySpeed;
		speeds[1] = primarySpeed;
		speeds[2] = secondarySpeed;
		speeds[3] = residentialSpeed;
		speeds[4] = roadSpeed;
		speeds[5] = livingStreetSpeed;
		maxSpeed = getMax(speeds);
		buildNodes();
		buildEdges(motorwaySpeed, primarySpeed, secondarySpeed, residentialSpeed, roadSpeed, livingStreetSpeed);
		return aStar(startNodeID, goalNodeID, maxSpeed).toArray(new Node[0]);
	}

	/**
	 * 
	 */
	@Override
	public Node[] findFastestPath(int startNodeID, int goalNodeID,
			int motorwaySpeed, int primarySpeed, int residentialSpeed)
			throws PathNotFoundException {
		
		return findFastestPath(startNodeID,
					goalNodeID,
					motorwaySpeed,
					primarySpeed,
					this.getSecondarySpeed(),
					residentialSpeed,
					this.getRoadSpeed(),
					this.getLivingStreetSpeed());
	}

	private int getMax(int[] tab){
		int max = 0;
		for(int i:tab){
			if(max < i)
				max = i;
		}
		return max;
	}

	@Override
	public Node[] findShortestPath(int startNodeID, int goalNodeID)
			throws PathNotFoundException {
		buildNodes();
		buildEdges();
		int maxSpeed = 1;
		return aStar(startNodeID, goalNodeID, maxSpeed).toArray(new Node[0]);
	}
	
	
}
