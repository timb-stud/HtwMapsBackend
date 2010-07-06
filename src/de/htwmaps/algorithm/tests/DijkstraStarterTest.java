package de.htwmaps.algorithm.tests;


import java.sql.SQLException;
import java.util.Arrays;

import de.htwmaps.algorithm.AStar;
import de.htwmaps.algorithm.DijkstraStarter;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.database.DBAdapterParabel;
import de.htwmaps.database.DBAdapterRectangle;
import de.htwmaps.util.InitLogger;

public class DijkstraStarterTest {

	public static void main(String[] args) throws Exception {
		InitLogger.INSTANCE.initLogger();
		DijkstraStarter ds = new DijkstraStarter();

		int startNodeID = 403500108;
		int goalNodeID =  262529904;
		float startNodeLon = 7.3093605f;
		float startNodeLat = 49.1737379f;
		float endNodeLon = 6.3849224f;
		float endNodeLat = 49.5321632f;
		
//		int startNodeID = 587836344;
//		int goalNodeID =  272349340;
//		float startNodeLon = 7.1199603f;
//		float startNodeLat = 49.3599494f;
//		float endNodeLon = 7.0751071f;
//		float endNodeLat = 49.432203f;
		
//		int startNodeID = 335981487; 
//		int goalNodeID =  587836344; 
//		float startNodeLon = 7.1910695f;
//		float startNodeLat = 49.3699591f;
//		float endNodeLon = 7.1199603f; 
//		float endNodeLat = 49.3599494f;

		
		
		
		
		DBAdapterParabel dbar;
		dbar = new DBAdapterParabel(startNodeID, goalNodeID, startNodeLon, startNodeLat, endNodeLon, endNodeLat);

		
		
		
		
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
		System.out.println("insgesamt: " + (System.currentTimeMillis() - time));
		//System.out.println(ds.generateTrack(result));
		System.out.println(ds.writeRoute(result));
		
		
		
//		Node[] result;
//		DijkstraStarter as = new DijkstraStarter();
//		//
//		int[] allNodeIDs = {1,2,3,4,5,6,7};
//		float[] x = {0,6,6,16,16,22,28};
//		float[] y = {10,16,6,10,6,10,6};
//		int startNodeID = 7;
//		int goalNodeID = 1;
//		int[] fromNodeIDs = {1,1,2,3,4,4,5,6};
//		int[] toNodeIDs =   {2,3,4,5,5,6,7,7};
//		double[] fromToDistances = {8.49, 7.21, 11.66, 10, 12, 6, 4, 7.21};
//		boolean[] oneways = {false, false, false, false, false, false, false, false};
//		int[] highwayTypes = {0,0,0,0,0,0,0,0};
//		
//		try{
//			result = as.findShortestPath(allNodeIDs, x, y, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, fromToDistances, oneways, highwayTypes);
//			System.out.println(Arrays.toString(result));
//		}catch(PathNotFoundException e){
//			System.out.println("Path not found");
//		}
	}
}