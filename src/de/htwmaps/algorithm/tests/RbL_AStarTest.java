package de.htwmaps.algorithm.tests;



import java.sql.SQLException;
import java.util.Arrays;

import junit.framework.TestCase;
import de.htwmaps.algorithm.AStar;
import de.htwmaps.algorithm.AStarBidirectionalStarter;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.algorithm.ShortestPathAlgorithm;
import de.htwmaps.algorithm.util.RouteToTextNew;
import de.htwmaps.database.DBAdapterParabel;
import de.htwmaps.database.DBAdapterRectangle;
import de.htwmaps.trash.AlteRouteToText;

public class RbL_AStarTest{

	/**
	 * Illingen Schillerstrasse nach  Schiffweiler Schillerstrasse
	 * @throws SQLException
	 * @throws PathNotFoundException
	 */
	public static void main(String[] args) throws SQLException, PathNotFoundException  {
		System.out.println("Start");

		long time = System.currentTimeMillis();
		
		//Schiffweiler - Berlin
//		int startNodeID = 29221535;
//		int goalNodeID = 587836344;
		
		//Riegelsberg - B268 Losheim ---> geht nicht
//		int startNodeID = 270697603;
//		int goalNodeID = 314037031;
		
		//Riegelsberg - B268 Losheim
		int startNodeID = 270165797;
		int goalNodeID = 685103967;
		
		//nur ein Strasse
//		int startNodeID = 270165797;
//		int goalNodeID = 270166141;
		
		AStarBidirectionalStarter as = new AStarBidirectionalStarter();
		float a = 0.8f;
		float h = 0.01f;
		int searchOption = ShortestPathAlgorithm.SHORTEST_ROUTE;
		DBAdapterParabel dbar;
		dbar = new DBAdapterParabel();
		while(true) {
			dbar.prepareGraph(startNodeID, goalNodeID, a, h);
			int[] allNodeIDs = dbar.getNodeIDs();
			float[] nodeLons = dbar.getNodeLons(); //x
			float[] nodeLats = dbar.getNodeLats(); //y
			
			int[] edgeStartNodeIDs = dbar.getEdgeStartNodeIDs();
			int[] edgeEndNodeIDs = dbar.getEdgeEndNodeIDs();
			double[] lengths = dbar.getEdgeLengths();
			boolean[] oneways = dbar.getOneways();
			int[] highwayTypes = dbar.getHighwayTypes();
			int[] wayIDs = dbar.getWayIDs();
			try {
				Node[] result = as.findShortestPath(allNodeIDs, nodeLons, nodeLats, startNodeID, goalNodeID, wayIDs, edgeStartNodeIDs, edgeEndNodeIDs, lengths, oneways, highwayTypes, searchOption);
				System.out.println(System.currentTimeMillis() - time);
//				System.out.println(new AStarBidirectionalStarter().generateTrack(result));
				
				System.out.println("Start RTT");
				time = System.currentTimeMillis();
				RouteToTextNew rtt = new RouteToTextNew(result);
				System.out.println("RTT " + (System.currentTimeMillis() - time) + " ms");
				System.out.println(rtt.toString());
				
				break;
			} catch (PathNotFoundException e) {
				a *= 0.5f;
				h += 0.01f;
				System.out.println(a);
				if (a <= 0.01) {
					throw new PathNotFoundException("Weg nicht gefunden");
				}
			}
		}
	}
		
}
