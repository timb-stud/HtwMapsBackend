package de.htwmaps.algorithm;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.htwmaps.database.DBConnector;




/**
 * @author Stanislaw Tartakowski
 * 
 * This class build a graph, calls the search algorithms, awaits their end and builds result
 */
public class AStarBidirectionalStarter implements ShortestPathAlgorithm {
	/**
	 * knotenobjekte miteinander referenzieren
	 * @param edgeLengths 
	 * @param highwayTypes 
	 * @param edgeIDs 
	 */
	private void generateReferences(HashMap<Integer, AStarBidirectionalNode> Q, int[] edgeStartNodeIDs, int[] edgeEndNodeIDs, boolean[] oneways, double[] edgeLengths, int[] highwayTypes, int searchOption, int[] wayIDs) {
		switch (searchOption) {
		case FASTEST_ROUTE:
			for (int i = 0 ; i < edgeStartNodeIDs.length; i++) {
				AStarBidirectionalNode fromNode = Q.get(edgeStartNodeIDs[i]), toNode = Q.get(edgeEndNodeIDs[i]);
				Edge edge = new Edge(toNode, edgeLengths[i], highwayTypes[i], wayIDs[i]);
				edge.setPredecessor(fromNode);
				fromNode.addEdge(edge);
				toNode.addEdge(edge);
				edge.setOneway(true);
				if (oneways[i] == false) {
					edge.setOneway(false);
				}
			}
			break;
		case SHORTEST_ROUTE:
			for (int i = 0 ; i < edgeStartNodeIDs.length; i++) {
				AStarBidirectionalNode fromNode = Q.get(edgeStartNodeIDs[i]), toNode = Q.get(edgeEndNodeIDs[i]);
				Edge edge = new Edge(toNode, fromNode.getDistanceTo(toNode), 1, wayIDs[i]);
				edge.setPredecessor(fromNode);
				fromNode.addEdge(edge);
				toNode.addEdge(edge);
				edge.setOneway(true);
				if (oneways[i] == false) {
					edge.setOneway(false);
				}
			}
			break;
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
		ArrayList<AStarBidirectionalNode> nodesContainer = new ArrayList<AStarBidirectionalNode>(150);
		while (tmp != null) {
			nodesContainer.add(tmp);
			tmp = tmp.getPredecessor();
		}
		return nodesContainer.toArray(new Node[0]);
	}

	public String generateXML(Node[] result) {
		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<osm version=\"0.6\" generator=\"De gute gute Generator 1.0\">\n");
		StringBuilder way = new StringBuilder(" <way id=\"1\">\n");
		for (Node tmp: result) {
			str.append(" <node id=\"").append(tmp.getId()).append("\" lat=\"").append(tmp.getLat()).append("\" lon=\"").append(tmp.getLon()).append("\"/>\n");
			way.append("  <nd ref=\"").append(tmp.getId()).append("\"/>\n");
		}
		str.append(way).append("  <tag k=\"highway\" v=\"secondary\"/>\n </way>\n <relation id=\"1\">\n  <member type=\"way\" ref=\"1\" role=\"\"/>\n  <tag k=\"route\" v=\"bicycle\"/>\n  <tag k=\"type\" v=\"route\"/>\n </relation>\n").append("</osm>");
		return str.toString();
	}
	
	public String generatePOI(Node[] result) {
		StringBuilder str = new StringBuilder("lat\tlon\ttitle\tdescription\ticon\ticonoffset\n");
		for (Node tmp: result) {
			str.append(tmp.getLat()).append("\t").append(tmp.getLon()).append("\t").append("title\t").append("descr\t").append("rosa_punkt.png\t").append("8,8\t").append("0,0\n");
		}
		return str.toString();
	}
	
	public String generateTrack(Node[] result) {	
		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<gpx>\n<trk>\n <trkseg>\n");
		for (Node tmp : result) {
			str.append("  <trkpt lat=\"").append(tmp.getLat()).append("\" lon=\"").append(tmp.getLon()).append("\">\n").append("  </trkpt>\n");
		}
		str.append(" </trkseg>\n</trk>\n</gpx>");
		return str.toString();
	}
	
	public String writeRoute(Node[] result) throws Exception {
		DecimalFormat df = new DecimalFormat("0.00");
		String street = "";
		String streetBefore = "";
		double dist = 0.0;
		ArrayList<String> streets = new ArrayList<String>();
		Statement wayStatement = DBConnector.getConnection().createStatement();
		
		for (int i = 0; i < result.length; i++) {
			ResultSet currentNodeRS = wayStatement.executeQuery("select value from edges, r_way_tag, k_tags where r_way_tag.tagID = k_tags.ID and edges.wayID = r_way_tag.wayID and (edges.fromNodeID = " + result[i].getId() + " or edges.toNodeID = " + result[i].getId() + ") and k_tags.key = 'name'");
			if (currentNodeRS.next()) {
				street = currentNodeRS.getString(1);
				if (i > 0) {
					dist += result[i].getDistanceTo(result[i - 1]);
				}
				if (!streets.isEmpty() && !streetBefore.equals(street)) {
					if (i < result.length - 1) {
						ResultSet nextNodeRS = wayStatement.executeQuery("select value from edges, r_way_tag, k_tags where r_way_tag.tagID = k_tags.ID and edges.wayID = r_way_tag.wayID and (edges.fromNodeID = " + result[i + 1].getId() + " or edges.toNodeID = " + result[i + 1].getId() + ") and k_tags.key = 'name'");
						if (nextNodeRS.next() && street.equals(nextNodeRS.getString(1))) {
							streetBefore = street;
							streets.add(df.format(dist) + " km " + giveDirection(result[i], result[i + 1]) + street);
							dist = 0.0;
						}
					}
				} else {
					if (streets.isEmpty()) {
						streetBefore = street;
						streets.add(street);
					}
				}
			}
			
			
		}
		StringBuilder res = new StringBuilder("fahren sie auf die erste straÃŸe: " + streets.get(0) + "\n");
		for (int i = 1; i < streets.size(); i++) {
			res.append(streets.get(i) + "\n");
		}
		return res.toString();
	}

	private String giveDirection(Node from, Node to) {
		if (from.getLon() < to.getLon()) {
			return "rechts halten : ";
		}
		return "links halten : ";
	}

	@Override
	public Node[] findShortestPath(int[] allNodeIDs, float[] lon, float[] lat,
			int startNodeID, int goalNodeID, int[] wayIDs,
			int[] edgeStartNodeIDs, int[] edgeEndNodeIDs, double[] edgeLengths,
			boolean[] oneways, int[] highwayTypes, int searchOption) throws PathNotFoundException {
		HashMap<Integer, AStarBidirectionalNode> Q = new HashMap<Integer, AStarBidirectionalNode>(allNodeIDs.length);

		long time = System.currentTimeMillis();
		generateNodes(Q, allNodeIDs, lon, lat);
		System.out.println(System.currentTimeMillis() - time + "ms knoten");
		time = System.currentTimeMillis();
		generateReferences(Q, edgeStartNodeIDs, edgeEndNodeIDs, oneways, edgeLengths, highwayTypes, searchOption, wayIDs);
		System.out.println(System.currentTimeMillis() - time + "ms kanten");

		
		AStarBidirectionalNode start = Q.get(startNodeID); 
		AStarBidirectionalNode goal = Q.get(goalNodeID);
		
		AStarBidirectional d0 = new AStarBidirectional(start, goal, true, this);
		AStarBidirectional d1 = new AStarBidirectional(goal, start, false, this);
		
		d0.setDijkstra(d1);
		d1.setDijkstra(d0);
		time = System.currentTimeMillis();
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
		System.out.println(System.currentTimeMillis() - time + " reiner algo");
		Node[] result = nodeToArray(start, goal);
		AStarBidirectional.count.set(0);
		AStarBidirectional.finished = false;
		if (result.length == 1) {
			throw new PathNotFoundException("Weg nicht gefunden.");
		}
		return result;

	}
}
