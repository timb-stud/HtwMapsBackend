package de.htwmaps.algorithm.tests;



import java.sql.SQLException;

import junit.framework.TestCase;
import de.htwmaps.algorithm.AStar;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.database.DBAdapterRectangle;

public class AStarTest extends TestCase {

	public void testFindShortestPath1() {
		Node[] result;
		int[] expectedResult = {7,5,3,1};
		AStar as = new AStar();
		//
		int[] allNodeIDs = {1,2,3,4,5,6,7};
		float[] x = {0,6,6,16,16,22,28};
		float[] y = {10,16,6,10,6,10,6};
		int startNodeID = 1;
		int goalNodeID = 7;
		int[] fromNodeIDs = {1,1,2,3,4,4,5,6};
		int[] toNodeIDs =   {2,3,4,5,5,6,7,7};
		double[] fromToDistances = {8.49, 7.21, 11.66, 10, 12, 6, 4, 7.21};
		boolean[] oneways = {false, false, false, false, false, false, false, false};
		int[] highwayTypes = {0,0,0,0,0,0,0,0};
		
		try{
			long time = System.currentTimeMillis();
			result = as.findShortestPath(allNodeIDs, x, y, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, fromToDistances, oneways, highwayTypes);
			System.out.println(System.currentTimeMillis() - time + " ms");
			for(int i = 0; i < result.length; i++){
				assertEquals(expectedResult[i], result[i].getId());
			}
		}catch(PathNotFoundException e){
			fail("Path not found");
		}
	}
	
	/**
	 * Illingen Schillerstraßße nach  Schiffweiler Schillerstraße
	 * @throws SQLException
	 * @throws PathNotFoundException
	 */
	public void testFindShortestPath2() throws SQLException, PathNotFoundException{
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
		
		for(Node n : result){
			System.out.println(n);
		}
	}
	
	public void testFindShortestPath3() throws SQLException, PathNotFoundException{
		AStar as = new AStar();
		int startNodeID = 334539268;
		int goalNodeID = 307999903;
		float startNodeLon = 7.0004f;
		float startNodeLat = 49.3496f;
		float endNodeLon = 7.0998f;
		float endNodeLat = 49.4037f;
		
		
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
		
		for(Node n : result){
			System.out.println(n);
		}
	}
	
//	public void testFindShortestPath4() throws SQLException{
//		System.out.println("A* test");
//		ResultSetToArray_ohne_fehler r = new ResultSetToArray_ohne_fehler();
//		Node[] result;
//		int[] expectedResult = {6,4,2,1};
//		AStar as = new AStar();
//		//
//		int[] allNodeIDs = r.getNodeID();
//		float[] x = r.getLon();
//		float[] y = r.getLat();
//		int startNodeID = 587836344;
//		int goalNodeID = 274026832;
//		int[] fromNodeIDs = r.getFromNodeID();
//		int[] toNodeIDs =   r.getToNodeID();
//		double[] fromToDistances = r.getLength1();
//		boolean[] oneways = r.getOneWay();
//		int[] highwayTypes = null;
//		
//		try{
//			long time = System.currentTimeMillis();
//			result = as.findShortestPath(allNodeIDs, x, y, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, fromToDistances, oneways, highwayTypes);
//			System.out.println(System.currentTimeMillis() - time + " ms");
//			System.out.println(Arrays.toString(result));
//			for(int i = 0; i < result.length; i++){
//				assertEquals(expectedResult[i], result[i].getId());
//			}
//		}catch(PathNotFoundException e){
//			fail("Path not found");
//		}
//	}

}
