package de.htwmaps.algorithm.tests;

import java.sql.SQLException;


import de.htwmaps.algorithm.AStar;
import de.htwmaps.algorithm.AStarBidirectionalStarter;
import de.htwmaps.algorithm.GraphData;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.algorithm.ShortestPathAlgorithm;
import de.htwmaps.database.DBAdapterParabel;

public class DijkstraStarterTest {

	public static void main(String[] args) throws SQLException, PathNotFoundException  {


//		int startNodeID = 403500108;
//		int goalNodeID =  262529904;
		
		int startNodeID = 29221535;
		int goalNodeID = 587836344;
		
//		int startNodeID = 270697603;
//		int goalNodeID = 314037031;
		
//		int startNodeID = 270165797;
//		int goalNodeID = 685103967;
		
//		int startNodeID = 245901690; //köln
//		int goalNodeID = 587836344;
		
//		int startNodeID = 245901690; //köln
//		int goalNodeID = 269319503; //riegelsberg
//		
//		int startNodeID = 248556824; //Kassel
//		int goalNodeID = 587836344;
		
//		int startNodeID = 580665431;
//		int goalNodeID = 279565843; //Ormesheim
		
//		float startNodeLon = 7.3093605f;
//		float startNodeLat = 49.1737379f;
//		float endNodeLon = 6.3849224f;
//		float endNodeLat = 49.5321632f;
////		
//		int startNodeID = 321992397; //mannheim
//		int goalNodeID =  587836344;
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
		

//		int startNodeID = 587836344;
//		int goalNodeID =  317518403;
		
//		int startNodeID = 587836344;
//		int goalNodeID = 492621932;
//		float startNodeLon = 7.1199603f;
//		float startNodeLat = 49.3599494f;
//		float endNodeLon = 6.95033f;
//		float endNodeLat = 49.4124f;

		
		
		
		GraphData gd = new GraphData();
		ShortestPathAlgorithm as = new AStarBidirectionalStarter(gd);
		float a = 0.8f;
		float h = 0.01f;
		int searchOption = ShortestPathAlgorithm.ROUTE_OPTION_FASTEST;
		int motorwaySpeed = 130,  primarySpeed = 70, residentialSpeed = 35;
		DBAdapterParabel dbar;
		dbar = new DBAdapterParabel(gd);
		while(true) {
			dbar.fillGraphData(startNodeID, goalNodeID, a, h);
			try {
				Node[] result = as.findPath(startNodeID, goalNodeID, searchOption, motorwaySpeed, primarySpeed, residentialSpeed);
				System.out.println(new AStarBidirectionalStarter(null).generateTrack(result));
				break;
			} catch (PathNotFoundException e) {
				a *= 0.5f;
				h += 0.001f;
				System.out.println(a);
				if (a <= 0.01) {
					throw new PathNotFoundException("Weg nicht gefunden");
				}
			}
		}
		
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