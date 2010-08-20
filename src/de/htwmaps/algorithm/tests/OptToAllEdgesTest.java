package de.htwmaps.algorithm.tests;

import java.sql.SQLException;

import de.htwmaps.algorithm.AStarBidirectionalStarter;
import de.htwmaps.algorithm.GraphData;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.algorithm.ShortestPathAlgorithm;
import de.htwmaps.algorithm.util.OptToAllEdges;
import de.htwmaps.database.DBAdapterParabel;
import de.htwmaps.database.DBConnector;


public class OptToAllEdgesTest {



	/**
	 * Illingen Schillerstrasse nach  Schiffweiler Schillerstrasse
	 * @throws SQLException
	 * @throws PathNotFoundException
	 */
	public static void main(String[] args) throws SQLException, PathNotFoundException  {
		System.out.println("Start");

		long time = System.currentTimeMillis();
		
		//Schiffweiler - Berlin
		int startNodeID = 29221535;
		int goalNodeID = 587836344;
		
		//Schiffweiler - Kšln
//		int startNodeID = 245901690;
//		int goalNodeID = 587836344;
		
//		//Riegelsberg - B268 Losheim
//		int startNodeID = 685103967;
//		int goalNodeID = 270165797;
		
		//nur ein Strasse
//		int startNodeID = 270165797;
//		int goalNodeID = 269358642;
		
		GraphData gd = new GraphData();
		ShortestPathAlgorithm as = new AStarBidirectionalStarter(gd);
		float a = 0.9f;
		float h = 0.01f;
		int motorwaySpeed = as.getMotorwaySpeed(), primarySpeed = as.getPrimarySpeed(), residentialSpeed = as.getResidentialSpeed();
		DBAdapterParabel dbar = new DBAdapterParabel(gd);
		//dbar.printNodes();
		while(true) {
			time = System.currentTimeMillis();
			dbar.fillGraphData(startNodeID, goalNodeID, a, h);
			System.out.println("dbar.fillGraphData " 	+ (System.currentTimeMillis() - time) + " ms");
			try {
				time = System.currentTimeMillis();
				Node[] result = as.findFastestPath(startNodeID, goalNodeID, motorwaySpeed, primarySpeed, residentialSpeed);
				System.out.println("findFastestPath " 	+ (System.currentTimeMillis() - time) + " ms");
				System.out.println("Start Opt -> All Edges");
				time = System.currentTimeMillis();
				new OptToAllEdges(result);
				System.out.println("Opt->All " 	+ (System.currentTimeMillis() - time) + " ms");
				break;
			} catch (PathNotFoundException e) {
				a *= 0.5f;
				h += 0.01f;
				System.out.println(a + " " + h);
				if (a <= 0.01f) {
					throw new PathNotFoundException("Weg nicht gefunden");
				}
			}
		}
		DBConnector.disconnect();
	}
		

	
}
