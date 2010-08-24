package de.htwmaps.algorithm.tests;



import java.sql.SQLException;
import java.util.Arrays;

//import junit.framework.TestCase;
import de.htwmaps.algorithm.AStar;
import de.htwmaps.algorithm.AStarBidirectionalStarter;
import de.htwmaps.algorithm.GraphData;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.algorithm.ShortestPathAlgorithm;
import de.htwmaps.algorithm.util.RouteToText;
import de.htwmaps.algorithm.util.TextInfos;
import de.htwmaps.database.DBAdapterParabel;
import de.htwmaps.database.DBAdapterRectangle;
//import de.htwmaps.trash.AlteRouteToText;

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

		//Riegelsberg - B268 Losheim
//		int startNodeID = 270165797;
//		int goalNodeID = 685103967;
		
		//Marpingen - Saarbruecken;
//		int startNodeID = 385944205;
//		int goalNodeID = 29780642;
		
		//Freisen - Saarbrücken
		int startNodeID = 358929754; //Freisen
		int goalNodeID = 29780642; //SB
//		int startNodeID = 29780642;
//		int goalNodeID = 358929754;
		
		//nur ein Strasse
//		int startNodeID = 270165797;
//		int goalNodeID = 270166141;
		
		GraphData gd = new GraphData();
		ShortestPathAlgorithm as = new AStar(gd);
		float a = 0.8f;
		float h = 0.01f;
		int motorwaySpeed = as.getMotorwaySpeed(), primarySpeed = as.getPrimarySpeed(), residentialSpeed = as.getResidentialSpeed();
		DBAdapterParabel dbar;
		dbar = new DBAdapterParabel(gd);
		
		while(true) {
			dbar.fillGraphData(startNodeID, goalNodeID, a, h);
			try {
				Node[] result = as.findFastestPath(startNodeID, goalNodeID, motorwaySpeed, primarySpeed, residentialSpeed);
//				System.out.println(((AStarBidirectionalStarter)as).generateTrack(result));
				
				System.out.println("Start RTT");
				time = System.currentTimeMillis();
				RouteToText rtt = new RouteToText(result);
				System.out.println("RTT " + (System.currentTimeMillis() - time) + " ms");
//				System.out.println(rtt.toString());
				for (String s : rtt.buildRouteInfo())
					System.out.println(s);
				
				System.out.println("\nAutobahn: " + rtt.getAutobahnString() + " Dauer: " + rtt.getAutobahnTime());
				System.out.println("Landstraße: " + rtt.getLandstrasseString() + " Dauer: " + rtt.getLandstrasseTime());
				System.out.println("Innerorts: " + rtt.getInnerOrtsString() + " Dauer: " + rtt.getInnerOrtstime());
				System.out.println("\nGesamtstrecke: " + rtt.getTotallengthString() + " Gesamtdauer: " + rtt.getTotaltime());
				
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
	}
		
}
