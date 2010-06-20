package de.htwmaps.algorithm.tests;


import java.sql.SQLException;
import java.util.Arrays;

import de.htwmaps.algorithm.AStar;
import de.htwmaps.algorithm.DijkstraStarter;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.database.DBAdapterRectangle;
import de.htwmaps.util.InitLogger;

public class DijkstraStarterTest {

	public static void main(String[] args) throws SQLException, PathNotFoundException {
		InitLogger.INSTANCE.initLogger();
		DijkstraStarter ds = new DijkstraStarter();
		int startNodeID = 274026832;
		int goalNodeID = 587836344;
		float startNodeLon = 7.0213f;
		float startNodeLat = 49.3431f;
		float endNodeLon = 7.1549f;
		float endNodeLat = 49.391f;
		
		
		DBAdapterRectangle dbar;
		dbar = new DBAdapterRectangle(startNodeLon, startNodeLat, endNodeLon, endNodeLat);
		int[] nodeIDs = dbar.getNodeIDs();
		float[] nodeLons = dbar.getNodeLons(); //x
		float[] nodeLats = dbar.getNodeLats(); //y
		
		int[] fromNodeIDs = dbar.getFromNodeIDs();
		int[] toNodeIDs = dbar.getToNodeIDs();
		double[] distances = dbar.getDistances();
		boolean[] oneways = dbar.getOneways();
		int[] highwayTypes = dbar.getHighwayTypes();
		long time = System.currentTimeMillis();
		Node[] result = ds.findShortestPath(nodeIDs, nodeLons, nodeLats, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, distances, oneways, highwayTypes);
		System.out.println(System.currentTimeMillis() - time + " ms algo + bauen");
		System.out.println(Arrays.toString(result));
		System.out.println(generateXML(result));
	}
	
	public static String generateXML(Node[] result) {
		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<osm version=\"0.6\" generator=\"De gute gute Generator 1.0\">\n");
		StringBuilder way = new StringBuilder(" <way id=\"1\">\n");
		for (Node tmp: result) {
			str.append(" <node id=\"").append(tmp.getId()).append("\" lat=\"").append(tmp.getY()).append("\" lon=\"").append(tmp.getX()).append("\"/>\n");
			way.append("  <nd ref=\"").append(tmp.getId()).append("\"/>\n");
		}
		str.append(way).append("  <tag k=\"highway\" v=\"secondary\"/>\n </way>\n <relation id=\"1\">\n  <member type=\"way\" ref=\"1\" role=\"\"/>\n  <tag k=\"route\" v=\"bicycle\"/>\n  <tag k=\"type\" v=\"route\"/>\n </relation>\n").append("</osm>");
		return str.toString();
	}

}