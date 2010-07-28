package de.htwmaps.algorithm.tests;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import de.htwmaps.algorithm.AStar;
import de.htwmaps.algorithm.AStarBidirectionalStarter;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.algorithm.ShortestPathAlgorithm;
import de.htwmaps.algorithm.util.RouteToText;
import de.htwmaps.database.DBAdapterParabel;
import de.htwmaps.database.DBAdapterRectangle;
import de.htwmaps.database.DBConnector;

public class CreateViewTest {



	public static void main(String[] args) throws SQLException, PathNotFoundException  {
		long time;

		int startNodeID = 29221535;
		int goalNodeID = 587836344;
	
		
		AStarBidirectionalStarter ds = new AStarBidirectionalStarter();
		float a = 0.8f;
		float h = 0.01f;
		int searchOption = ShortestPathAlgorithm.FASTEST_ROUTE;
		DBAdapterParabel dbar;
		dbar = new DBAdapterParabel();
		while(true) {
			dbar.prepareGraph(startNodeID, goalNodeID, a, h);
			int[] allNodeIDs = dbar.getNodeIDs();
			float[] nodeLons = dbar.getNodeLons(); //x
			float[] nodeLats = dbar.getNodeLats(); //y
			
			int[] edgeStartNodeIDs = dbar.getEdgeStartNodeIDs();
			int[] edgeEndNodeIDs = dbar.getEdgeEndNodeIDs();
			double[] distances = dbar.getEdgeLengths();
			boolean[] oneways = dbar.getOneways();
			int[] highwayTypes = dbar.getHighwayTypes();
			int[] edgeIDs = dbar.getEdgesIDs();
			try {
				Node[] result = ds.findShortestPath(allNodeIDs, nodeLons, nodeLats, startNodeID, goalNodeID, edgeIDs, edgeStartNodeIDs, edgeEndNodeIDs, distances, oneways, highwayTypes, searchOption);
//				System.out.println(new AStarBidirectionalStarter().generateTrack(result));
				System.out.println("Start einzelTest\n");
				time = System.currentTimeMillis();
				einzelTest(result);
				System.out.println("\n einzelTest: " + (System.currentTimeMillis() - time) + "ms");
				System.out.println("Start viewTest \n");
				time = System.currentTimeMillis();
				viewTest(result);
				System.out.println("\n viewTest: " + (System.currentTimeMillis() - time) + "ms");
				break;
			} catch (PathNotFoundException e) {
				a *= 0.5f;
				h += 0.01f;
				System.out.println(a);
				if (a <= 0.001) {
					throw new PathNotFoundException("Weg nicht gefunden");
				}
			}
		}
		
	}

	private static void viewTest(Node[] route) {
		PreparedStatement pStmt;
		
		//Sicherstellen, dass kein View bereits existiert
		String dropView = "drop view if exists nodesView;";
		
		String createView = buildViewStatement(route);
//		System.out.println(createView);
		
		//Join Query mit View um benoetigte Daten zu bekommen
		String execQuery = buildStreetsStatement();
		
		try {
			pStmt = DBConnector.getConnection().prepareStatement(dropView);
			pStmt.executeUpdate();
			pStmt = null;
			pStmt = DBConnector.getConnection().prepareStatement(createView);
			pStmt.executeUpdate();
			pStmt = null;
			pStmt = DBConnector.getConnection().prepareStatement(execQuery);
			ResultSet resultSet =  pStmt.executeQuery();
			pStmt = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String buildStreetsStatement() {
		StringBuilder sb = new StringBuilder();
		sb.append("select * " +
				"from nodesView inner join edges_opt on " +
				"((nodesView.node1ID = edges_opt.node1ID " +
				"and " +
				"nodesView.node2ID = edges_opt.node2ID) " +
				"or " +
				"(nodesView.node2ID = edges_opt.node1ID " +
				"and " +
				"nodesView.node1ID = edges_opt.node2ID)) " +
				"order by routeIndex;");
		
		return sb.toString();
	}
	
	private static String buildViewStatement(Node[] route) {
		int i= 1;
		StringBuilder sb = new StringBuilder();
		
		sb.append("create view nodesView as ");
		sb.append("select " + i + " as routeIndex , " + route[i - 1] + " as node1ID, " + route[i] + " as node2ID");
		for (i = 2; i < route.length; i++){
			sb.append(" union select " + i + ", " + route[i - 1] + ", " + route[i]);
		}
		sb.append("; ");
		
		return sb.toString();
	}

	private static void einzelTest(Node[] route) {
		PreparedStatement pStmt;
		String query;
		
		for (int i = 0; i < route.length -1 ; i++){
			query = "select * " +
					"from edges_opt, nodesView " +
					"where " +
					"((edges_opt.node1ID = " + route[i] +
					"and " +
					"edges_opt.node2ID = " + route[i+1] + ")" +
					"or " +
					"(edges_opt.node2ID = " + route[i+1] +
					"and " +
					"edges_opt.node1ID = " + route[i] + "))";
			
			try {
				pStmt = DBConnector.getConnection().prepareStatement(query);
				pStmt.executeUpdate();
				pStmt = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
