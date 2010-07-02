/**
 * 
 */
package de.htwmaps.database.importscripts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.htwmaps.database.DBConnector;

/**
 * @author tobiaslana
 *
 */



public class CalculateEdgeLength {
	
	int node1ID = 0;
	int node2ID = 0;
	int edgeID 	= 0;
	float node1lat, node1lon, node2lat, node2lon;
	float length;
	
	ResultSet rsNode1 = null;
	ResultSet rsNode2 = null;
	
	public CalculateEdgeLength() {
		start();
	}
    
    /**
     * Berechnet die exakte Entfernung anhand der Breiten und Laengengrade in Metern
     * 
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    private double betterDistance(float lat1, float lon1, float lat2, float lon2) {
    	float lat 	= (float) ((lat1 + lat2) / 2.0 * 0.01745);
    	float dx 	= (float) (111.3 * Math.cos(lat) * (lon1 - lon2));
    	float dy 	= (float) 111.3 * (lat1 - lat2);
    	double length = Math.sqrt(dx * dx + dy * dy);
    	length = length * 1000;
    	// es gibt Edges mit einer Länge von 0, diese werden zur Übersicht mit -1 gefüllt
    	if (length == 0.0) {
        	length = -1.0;
        }
    	return length;
    }
	
	private void start() {
		boolean moreResults = true;
		ResultSet allEdges = null;
		try {
	        PreparedStatement psNode1 	= DBConnector.getConnection().prepareStatement("SELECT lat, lon FROM `nodes` WHERE `ID` = ?");
	        PreparedStatement psNode2 	= DBConnector.getConnection().prepareStatement("SELECT lat, lon FROM `nodes` WHERE `ID` = ?");
	        PreparedStatement psLength 	= DBConnector.getConnection().prepareStatement("UPDATE `edges` SET `length` = ? WHERE `ID`= ?");
	        while (moreResults) {
	        	allEdges = DBConnector.getConnection().createStatement().executeQuery("SELECT ID, fromNodeID, toNodeID FROM edges WHERE length = 0 LIMIT 0, 100000");
		        System.out.println("neue 1000000 geladen");
	        	if (!allEdges.next()) {
		        	moreResults = false;
		        	System.out.println("nichts mehr da");
		        }
	        	System.out.println("Edges geladen");
				while (allEdges.next()){
					edgeID	= allEdges.getInt(1);
					node1ID = allEdges.getInt(2);
					node2ID = allEdges.getInt(3);
					psNode1.setInt(1, node1ID);
					rsNode1 = psNode1.executeQuery();
					//System.out.println("Node1 Daten geladen");
					if (rsNode1.next()) {
						node1lat = rsNode1.getFloat(1);
						node1lon = rsNode1.getFloat(2);
					}
					psNode2.setInt(1, node2ID);
					rsNode2 = psNode2.executeQuery();
					//System.out.println("Node2 Daten geladen");
		
					if (rsNode2.next()) {
						node2lat = rsNode2.getFloat(1);
						node2lon = rsNode2.getFloat(2);
					}
					length = (float) betterDistance(node1lat, node1lon, node2lat, node2lon);
					psLength.setFloat(1, length);
					psLength.setInt(2, edgeID);
					//System.out.println("SQL: " + psLength.toString());
					psLength.execute();
				}
	        }
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
