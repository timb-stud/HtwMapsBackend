package de.htwmaps.algorithm.tests;

import java.sql.SQLException;

import de.htwmaps.algorithm.AStarBidirectionalStarter;
import de.htwmaps.algorithm.GraphData;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.algorithm.ShortestPathAlgorithm;
import de.htwmaps.algorithm.util.OptToAllEdges;
import de.htwmaps.database.DBAdapterParabel;


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
		
//		//Riegelsberg - B268 Losheim
//		int startNodeID = 270165797;
//		int goalNodeID = 685103967;
		
		//nur ein Strasse
//		int startNodeID = 270165797;
//		int goalNodeID = 270166141;
		
		GraphData gd = new GraphData();
		ShortestPathAlgorithm as = new AStarBidirectionalStarter(gd);
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
				
				System.out.println("Algo " 		+ (System.currentTimeMillis() - time) + " ms");
				System.out.println("Start Opt -> All Edges");
				time = System.currentTimeMillis();
				new OptToAllEdges(result);
				System.out.println("Opt->All " 	+ (System.currentTimeMillis() - time) + " ms");
				//System.out.println(rtt.toString());

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
