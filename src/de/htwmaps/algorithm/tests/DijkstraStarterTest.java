package de.htwmaps.algorithm.tests;


import java.sql.SQLException;

import de.htwmaps.algorithm.DijkstraStarter;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.database.DBAdapterRectangle;
import de.htwmaps.util.InitLogger;

public class DijkstraStarterTest {

	public static void main(String[] args) throws SQLException, PathNotFoundException {
		InitLogger.INSTANCE.initLogger();
		DijkstraStarter ds = new DijkstraStarter();
		 int startNodeID = 318113531;
		 int goalNodeID = 587836344;
		 float startNodeLon = 7.0886f;
		 float startNodeLat = 49.388f;
		 float endNodeLon = 7.2021f;
		 float endNodeLat = 49.3268f;
		
		
		
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
		System.out.println(ds.generateTrack(result));
	}
}