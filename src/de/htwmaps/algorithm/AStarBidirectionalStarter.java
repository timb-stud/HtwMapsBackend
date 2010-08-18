package de.htwmaps.algorithm;
import java.util.ArrayList;
import java.util.HashMap;





/**
 * @author Stanislaw Tartakowski
 * 
 * This class build a graph, calls the search algorithms, awaits their end and builds result
 */
public class AStarBidirectionalStarter extends ShortestPathAlgorithm {
	public AStarBidirectionalStarter(GraphData gd) {
		super(gd);
	}

	/**
	 * knotenobjekte miteinander referenzieren
	 * @param edgeLengths 
	 * @param highwayTypes 
	 * @param edgeIDs 
	 */
	private void generateReferences(HashMap<Integer, AStarBidirectionalNode> Q, int[] edgeStartNodeIDs, int[] edgeEndNodeIDs, boolean[] oneways, double[] edgeLengths, int[] highwayTypes, int[] wayIDs, int[] edgeIDs) {
		for (int i = 0 ; i < edgeStartNodeIDs.length; i++) {
			AStarBidirectionalNode fromNode = Q.get(edgeStartNodeIDs[i]), toNode = Q.get(edgeEndNodeIDs[i]);
			Edge edge = null;
			switch (highwayTypes[i]) {
			case MOTORWAY: edge = new Edge(toNode, edgeLengths[i], MOTORWAY, wayIDs[i], getMotorwaySpeed(), edgeIDs[i]); break;
			case PRIMARY: edge = new Edge(toNode, edgeLengths[i], PRIMARY, wayIDs[i], getPrimarySpeed(), edgeIDs[i]); break;
			case SECONDARY: edge = new Edge(toNode, edgeLengths[i], SECONDARY, wayIDs[i], getSecondarySpeed(), edgeIDs[i]); break;
			case ROAD: edge = new Edge(toNode, edgeLengths[i], ROAD, wayIDs[i], getRoadSpeed(), edgeIDs[i]); break;
			case RESIDENTIAL: edge = new Edge(toNode, edgeLengths[i], RESIDENTIAL, wayIDs[i], getResidentialSpeed(), edgeIDs[i]); break;
			case LIVING_STREET: edge = new Edge(toNode, edgeLengths[i], LIVING_STREET, wayIDs[i], getLivingStreetSpeed(), edgeIDs[i]); break;
			default: throw new IllegalArgumentException();
			}
			edge.setPredecessor(fromNode);
			fromNode.addEdge(edge);
			toNode.addEdge(edge);
			edge.setOneway(true);
			if (oneways[i] == false) {
				edge.setOneway(false);
			}
		}
	}
	
	/**
	 * Knoten erstellen
	 */
	private void generateNodes(HashMap<Integer, AStarBidirectionalNode> Q, int[] allNodesIDs, float[] lon, float[] lat) {
		for (int i = 0; i < allNodesIDs.length; i++) {
			Q.put(allNodesIDs[i], new AStarBidirectionalNode(lon[i], lat[i], allNodesIDs[i]));
		}
	}
	
	/**
	 * node list -> Node[] array
	 */
	public Node[] nodeToArray(AStarBidirectionalNode start, AStarBidirectionalNode goal) {
		AStarBidirectionalNode tmp = start.getPredecessor() != null ? start : goal;
		ArrayList<AStarBidirectionalNode> nodesContainer = new ArrayList<AStarBidirectionalNode>(1000);
		while (tmp != null) {
			nodesContainer.add(tmp);
			tmp = tmp.getPredecessor();
		}
		return nodesContainer.toArray(new Node[0]);
	}
	

	public Node[] aStar(int startNodeID, int goalNodeID) throws PathNotFoundException {
		HashMap<Integer, AStarBidirectionalNode> Q = new HashMap<Integer, AStarBidirectionalNode>(graphData.getAllNodeIDs().length);

		generateNodes(Q, graphData.getAllNodeIDs(), graphData.getAllNodeLons(), graphData.getAllNodeLats());
		generateReferences(Q, graphData.getEdgeStartNodeIDs(), graphData.getEdgeEndNodeIDs(), graphData.getOneways(), graphData.getEdgeLengths(), graphData.getHighwayTypes(), graphData.getWayIDs(), graphData.getEdgeIDs());

		
		AStarBidirectionalNode start = Q.get(startNodeID); 
		AStarBidirectionalNode goal = Q.get(goalNodeID);
		
		AStarBidirectional d0 = new AStarBidirectional(start, goal, true, this);
		AStarBidirectional d1 = new AStarBidirectional(goal, start, false, this);
		
		d0.setDijkstra(d1);
		d1.setDijkstra(d0);
		long time = System.currentTimeMillis();
		d0.start();
		d1.start();
		
		synchronized(getClass()) {
			try {
				while (!AStarBidirectional.finished) {
					this.getClass().wait();
				}
			} catch (InterruptedException e) {
				System.out.println("fatal error.");
			}
		}
		d0.interrupt();
		d1.interrupt();
		System.out.println(System.currentTimeMillis() - time + " ms");
		Node[] result = nodeToArray(start, goal);
		AStarBidirectional.count.set(0);
		AStarBidirectional.finished = false;
		if (result.length == 1) {
			throw new PathNotFoundException("Weg nicht gefunden.");
		}
		return result;
	}
	
	@Override
	public Node[] findShortestPath(int startNodeID, int goalNodeID)
			throws PathNotFoundException {
		setMotorwaySpeed(1);
		setPrimarySpeed(1);
		setSecondarySpeed(1);
		setResidentialSpeed(1);
		setRoadSpeed(1);
		setLivingStreetSpeed(1);
		return aStar(startNodeID, goalNodeID);
	}

	@Override
	public Node[] findFastestPath(int startNodeID, int goalNodeID,
			int motorwaySpeed, int primarySpeed, int secondarySpeed,
			int residentialSpeed, int roadSpeed, int livingStreetSpeed)
			throws PathNotFoundException {
		setMotorwaySpeed(motorwaySpeed);
		setPrimarySpeed(primarySpeed);
		setSecondarySpeed(secondarySpeed);
		setResidentialSpeed(residentialSpeed);
		setRoadSpeed(roadSpeed);
		setLivingStreetSpeed(livingStreetSpeed);
		return aStar(startNodeID, goalNodeID);
	}

	@Override
	public Node[] findFastestPath(int startNodeID, int goalNodeID,
			int motorwaySpeed, int primarySpeed, int residentialSpeed)
			throws PathNotFoundException {
		setMotorwaySpeed(motorwaySpeed);
		setPrimarySpeed(primarySpeed);
		setResidentialSpeed(residentialSpeed);
		return aStar(startNodeID, goalNodeID);
	}
	
	public String generateTrack(Node[] result) {	
		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<gpx>\n<trk>\n <trkseg>\n");
		for (Node tmp : result) {
			str.append("  <trkpt lat=\"").append(tmp.getLat()).append("\" lon=\"").append(tmp.getLon()).append("\">\n").append("  </trkpt>\n");
		}
		str.append(" </trkseg>\n</trk>\n</gpx>");
		return str.toString();
	}
}
