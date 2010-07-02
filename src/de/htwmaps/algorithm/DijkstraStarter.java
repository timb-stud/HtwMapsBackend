package de.htwmaps.algorithm;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

import de.htwmaps.algorithm.util.RouteByLetter;
import de.htwmaps.database.DBConnector;
import de.htwmaps.util.FibonacciHeap;
import java.util.HashMap;




/**
 * @author Stanislaw Tartakowski
 * 
 * This class build a graph, calls the search algorithms, awaits their end and builds result
 */
public class DijkstraStarter implements ShortestPathAlgorithm {
	/**
	 * knotenobjekte miteinander referenzieren
	 */
	private void generateReferences(HashMap<Integer, DijkstraNode> Q, int[] fromNodeIDs, int[] toNodeIDs, boolean[] oneways) {
		for (int i = 0 ; i < fromNodeIDs.length; i++) {
			DijkstraNode fromNode = Q.get(fromNodeIDs[i]), toNode = Q.get(toNodeIDs[i]);
			Edge onewayEdge = new Edge(toNode, 0.0);
			fromNode.addEdge(onewayEdge);
			if(!oneways[i]) {
				onewayEdge.setPredecessor(fromNode);
				toNode.addEdge(onewayEdge);
			}
		}
	}
	
	/**
	 * Knoten erstellen
	 */
	private void generateNodes(HashMap<Integer, DijkstraNode> Q, int[] allNodesIDs, float[] x, float[] y) {
		for (int i = 0; i < allNodesIDs.length; i++) {
			Q.put(allNodesIDs[i], new DijkstraNode(x[i], y[i], allNodesIDs[i]));
		}
	}
	
	/**
	 * node list -> Node[] array
	 */
	public Node[] nodeToArray(DijkstraNode startNode, DijkstraNode endNode) {
		DijkstraNode tmp = startNode.getPredecessor() != null ? startNode : endNode;
		ArrayList<DijkstraNode> nodesContainer = new ArrayList<DijkstraNode>();
		while (tmp != null) {
			nodesContainer.add(tmp);
			tmp = tmp.getPredecessor();
		}
		return nodesContainer.toArray(new Node[0]);
	}

	/**
	 * This method is the interface to this class
	 * @return result node array
	 */
	@Override
	public Node[] findShortestPath(int[] allNodesIDs, float[] x, float[] y, int startNodeID, int goalNodeID, int[] fromNodeIDs,
			int[] toNodeIDs,
			double[] fromToDistances,
			boolean[] oneways,
			int[] highwayTypes) throws PathNotFoundException {
		
		HashMap<Integer, DijkstraNode> Q = new HashMap<Integer, DijkstraNode>(allNodesIDs.length);

		generateNodes(Q, allNodesIDs, x, y);
		generateReferences(Q, fromNodeIDs, toNodeIDs, oneways);

		
		DijkstraNode startNode = Q.get(startNodeID); 
		DijkstraNode endNode = Q.get(goalNodeID);
		
		Dijkstra d0 = new Dijkstra(startNode, endNode, true, this);
		Dijkstra d1 = new Dijkstra(endNode, startNode, false, this);
		
		d0.setDijkstra(d1);
		d1.setDijkstra(d0);
		
		d0.start();
		d1.start();
		
		synchronized(getClass()) {
			try {
				while (!Dijkstra.isFinished()) {
					this.getClass().wait();
				}
			} catch (InterruptedException e) {}
		}
		Node[] result = nodeToArray(startNode, endNode);
		if (result.length == 1) {
			throw new PathNotFoundException();
		}
		return result;
	}
	
	public String generateXML(Node[] result) {
		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<osm version=\"0.6\" generator=\"De gute gute Generator 1.0\">\n");
		StringBuilder way = new StringBuilder(" <way id=\"1\">\n");
		for (Node tmp: result) {
			str.append(" <node id=\"").append(tmp.getId()).append("\" lat=\"").append(tmp.getY()).append("\" lon=\"").append(tmp.getX()).append("\"/>\n");
			way.append("  <nd ref=\"").append(tmp.getId()).append("\"/>\n");
		}
		str.append(way).append("  <tag k=\"highway\" v=\"secondary\"/>\n </way>\n <relation id=\"1\">\n  <member type=\"way\" ref=\"1\" role=\"\"/>\n  <tag k=\"route\" v=\"bicycle\"/>\n  <tag k=\"type\" v=\"route\"/>\n </relation>\n").append("</osm>");
		return str.toString();
	}
	
	public String generatePOI(Node[] result) {
		StringBuilder str = new StringBuilder("lat\tlon\ttitle\tdescription\ticon\ticonoffset\n");
		for (Node tmp: result) {
			str.append(tmp.getY()).append("\t").append(tmp.getX()).append("\t").append("title\t").append("descr\t").append("rosa_punkt.png\t").append("8,8\t").append("0,0\n");
		}
		return str.toString();
	}
	
	public String generateTrack(Node[] result) {	
		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<gpx>\n<trk>\n <trkseg>\n");
		for (Node tmp : result) {
			str.append("  <trkpt lat=\"").append(tmp.getY()).append("\" lon=\"").append(tmp.getX()).append("\">\n").append("  </trkpt>\n");
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
		StringBuilder res = new StringBuilder("fahren sie auf die erste straße: " + streets.get(0) + "\n");
		for (int i = 1; i < streets.size(); i++) {
			res.append(streets.get(i) + "\n");
		}
		return res.toString();
	}

	private String giveDirection(Node from, Node to) {
		if (from.getX() < to.getX()) {
			return "rechts halten : ";
		}
		return "links halten : ";
	}
}
