package de.htwmaps.algorithm.tests;



import java.sql.SQLException;
import java.util.Arrays;

import junit.framework.TestCase;
import de.htwmaps.algorithm.AStar;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.algorithm.util.RouteToText;
import de.htwmaps.database.DBAdapterRectangle;

public class RbL_AStarTest extends TestCase {

	/**
	 * Illingen Schillerstrasse nach  Schiffweiler Schillerstrasse
	 * @throws SQLException
	 * @throws PathNotFoundException
	 */
	public void testFindShortestPath3() throws SQLException, PathNotFoundException{
		AStar as = new AStar();
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
		Node[] result = as.findShortestPath(nodeIDs, nodeLons, nodeLats, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, distances, oneways, highwayTypes);
		
		RouteToText rbl = new RouteToText(result);
	}
	
	
}
