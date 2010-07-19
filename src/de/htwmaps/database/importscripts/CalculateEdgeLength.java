/**
 * 
 */
package de.htwmaps.database.importscripts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.database.DBConnector;

/**
 * @author tobiaslana
 *
 */



public class CalculateEdgeLength {

	int node1ID = 0;
	int node2ID = 0;
	int edgeID 	= 0;
	int restEdge = 0;
	float node1lat, node1lon, node2lat, node2lon;
	float length;

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
    	// es gibt Edges mit einer Laenge von 0, diese werden zur uebersicht mit -1 gefuellt
//    	if (length == 0.0) {
//        	length = -1.0;
//        }
    	return length;
    }

	private void start() {
		boolean error = false;
		ResultSet allEdges 	= null;
		ResultSet restEdges = null;

		try {
	        PreparedStatement psLength 	= DBConnector.getConnection().prepareStatement("UPDATE `edges_all` SET `length` = ? WHERE `ID`= ?");
        	//System.out.println("Lade Edges");
        	while (!error) {
		        allEdges = DBConnector.getConnection().createStatement().executeQuery("SELECT ID, node1ID, node2ID, node1lat, node1lon, node2lat, node2lon FROM edgeview WHERE length IS NULL LIMIT 0, 100000");
	        	System.out.println("100.000 Edges geladen");
		        //System.out.println("Setze Edgecount");
	//	        restEdges = DBConnector.getConnection().createStatement().executeQuery("SELECT COUNT(*) FROM edges_all WHERE length IS NULL");
	//        	restEdges.next();
	//        	restEdge = restEdges.getInt(1);
	//	        System.out.println("noch " + restEdge + " Edges");
				while (allEdges.next()){
					//System.out.println("Inner");
					edgeID	= allEdges.getInt(1);
					node1ID = allEdges.getInt(2);
					node2ID = allEdges.getInt(3);
					node1lat = allEdges.getFloat(4);
					node1lon = allEdges.getFloat(5);
					node2lat = allEdges.getFloat(6);
					node2lon = allEdges.getFloat(7);
					length = (float) betterDistance(node1lat, node1lon, node2lat, node2lon);
					psLength.setFloat(1, length);
					psLength.setInt(2, edgeID);
					//System.out.println("SQL: " + psLength.toString());
					psLength.execute();
				}
        	}
			System.out.println("Fertig");
	        

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			error = true;
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CalculateEdgeLength();

	}
}