



import java.sql.SQLException;
import java.util.Arrays;

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
			result = as.findShortestPath(allNodeIDs, x, y, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, fromToDistances, oneways, highwayTypes);
			for(int i = 0; i < result.length; i++){
				assertEquals(expectedResult[i], result[i].getId());
			}
		}catch(PathNotFoundException e){
			fail("Path not found");
		}
	}
	
	public void testFindShortestPath2() {
		Node[] result;
		int[] expectedResult = {2,12,10,7,14};
		AStar as = new AStar();
		//
		int startNodeID = 14;
		int goalNodeID = 2;
		int[] allNodeIDs = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		float[] x = {1.74f,2.76f,4.36f,8f,10.54f,11.52f,10.18f,4.39f,2.33f,7.3f,6.03f,4.39f,9.2f,12.66f,6.62f};
		float[] y = {3.73f,5.82f,7.81f,8f,6.83f,4.28f,2.52f,1.57f,2.91f,2.71f,5.17f,4.41f,5.43f,2.75f,0.98f};
		int[] fromNodeIDs = {1,14,1,15,2,6,3,4,5,6,5,4,11,11,2,9,8,10,10,7,7,8};
		int[] toNodeIDs =   {9,7,2,7,3,14,4,5,6,13,13,11,13,12,12,12,12,12,11,10,13,15};
		double[] fromToDistances = {1.01,2.5,2.33,3.88,2.56,1.92,3.65,2.79,2.73,2.59,1.94,3.45,3.18,1.8,2.16,2.55,2.84,3.37,2.76,2.88,3.07,2.3};
		boolean[] oneways = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,false,false,false,false,false,false};
		int[] highwayTypes = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		
		try{
			result = as.findShortestPath(allNodeIDs, x, y, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, fromToDistances, oneways, highwayTypes);
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
	public void testFindShortestPath3() throws SQLException, PathNotFoundException{
		AStar as = new AStar();
//		float h = 0.001f;
		 int startNodeID = 274026832;
		 int goalNodeID = 587836344;
//		 float startNodeLon = 7.0478f - h;
//		 float startNodeLat = 49.3745f + h;
//		 float endNodeLon = 7.11996f + h;
//		 float endNodeLat = 49.36f - h;
		 float startNodeLon = 7.0213f;
		 float startNodeLat = 49.3431f;
		 float endNodeLon = 7.1549f;
		 float endNodeLat = 49.391f;
		
		
		DBAdapterRectangle dbar;
		//dbar = new DBAdapterRectangle(startNodeLon, startNodeLat, endNodeLon, endNodeLat);
		dbar = new DBAdapterRectangle(startNodeID, goalNodeID);
		int[] nodeIDs = dbar.getNodeIDs();
		float[] nodeLons = dbar.getNodeLons(); //x
		float[] nodeLats = dbar.getNodeLats(); //y
		
		int[] fromNodeIDs = dbar.getFromNodeIDs();
		int[] toNodeIDs = dbar.getToNodeIDs();
		double[] distances = dbar.getDistances();
		boolean[] oneways = dbar.getOneways();
		int[] highwayTypes = dbar.getHighwayTypes();
		long time = System.currentTimeMillis();
		Node[] result = as.findShortestPath(nodeIDs, nodeLons, nodeLats, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, distances, oneways, highwayTypes);
		System.out.println("insgesamt: " + (System.currentTimeMillis() - time));
		System.out.println(Arrays.toString(result));
	}
	
	/**
	 * Irgend ein Weg stas fragen
	 * @throws SQLException
	 * @throws PathNotFoundException
	 */
	public void testFindShortestPath4() throws SQLException, PathNotFoundException{
		AStar as = new AStar();
		 int startNodeID = 262529904;
		 int goalNodeID = 403500108;
		 float startNodeLon = 6.326f;
		 float startNodeLat = 49.657f;
		 float endNodeLon = 7.4f;
		 float endNodeLat = 49.121f;
		
		
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
		Node[] result = as.findShortestPath(nodeIDs, nodeLons, nodeLats, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, distances, oneways, highwayTypes);
		System.out.println("insgesamt: " + (System.currentTimeMillis() - time));
		System.out.println(Arrays.toString(result));
	}

	
//	public void testFindShortestPath3() throws SQLException, PathNotFoundException{
//		AStar as = new AStar();
//		int startNodeID = 334539268;
//		int goalNodeID = 307999903;
//		float startNodeLon = 7.0004f;
//		float startNodeLat = 49.3496f;
//		float endNodeLon = 7.0998f;
//		float endNodeLat = 49.4037f;
//		
//		DBAdapterRectangle dbar;
//		
//		long startTime = System.currentTimeMillis();
//		dbar = new DBAdapterRectangle(startNodeLon, startNodeLat, endNodeLon, endNodeLat);
//		int[] nodeIDs = dbar.getNodeIDs();
//		float[] nodeLons = dbar.getNodeLons(); //x
//		float[] nodeLats = dbar.getNodeLats(); //y
//		
//		int[] fromNodeIDs = dbar.getFromNodeIDs();
//		int[] toNodeIDs = dbar.getToNodeIDs();
//		double[] distances = dbar.getDistances();
//		boolean[] oneways = dbar.getOneways();
//		int[] highwayTypes = dbar.getHighwayTypes();
//		System.out.println("Laden: " + (System.currentTimeMillis() - startTime) + "ms");
//		startTime = System.currentTimeMillis();
//		Node[] result = as.findShortestPath(nodeIDs, nodeLons, nodeLats, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, distances, oneways, highwayTypes);
//		System.out.println("Weg finden: " + (System.currentTimeMillis() - startTime) + "ms");
//	}
	
}
