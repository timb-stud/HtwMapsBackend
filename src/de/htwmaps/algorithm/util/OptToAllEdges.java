package de.htwmaps.algorithm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import de.htwmaps.algorithm.Coordinate;
import de.htwmaps.algorithm.Edge;
import de.htwmaps.algorithm.Node;
import de.htwmaps.database.DBConnector;

public class OptToAllEdges {
	
	String sql1 = "SELECT node1lat, node1lon, node2lat, node2lon, ID FROM edges_all WHERE partOfEdgesOptID = ? ORDER BY 5";
	String sql2 = "SELECT node1lat, node1lon, node2lat, node2lon, ID FROM edges_all WHERE partOfEdgesOptID = ? ORDER BY 5 DESC";
	String sql3 = "SELECT COUNT(*) FROM edges_opt WHERE node1ID = ? AND node2ID = ?";
	
	PreparedStatement ps1, ps2, ps3;
	LinkedList<Coordinate> coordList;
	Connection con = DBConnector.getConnection();
	Coordinate c;
	
	int myEdgeID;
	int rsCounter = 0;
	
	long time, timesum, timesum2;
	// Variable die speichert ob Strasse vorwaerts oder rueckewaerts durchfahren wird
	boolean inOrder = true;


	public OptToAllEdges(Node[] route) {
		try {
			parseEdges(route);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private LinkedList<Coordinate> parseEdges(Node[] route) throws SQLException {
		
		ps1 		= con.prepareStatement(sql1);
		ps2 		= con.prepareStatement(sql2);
		ps3 		= con.prepareStatement(sql3);
		coordList 	= new LinkedList<Coordinate>();

		ResultSet rs1 = null;
		ResultSet rs2 = null;
		System.out.println("Size Opt: " + route.length);
		for(int i=0; i <= route.length-2; i++){ 
			ps3.setInt(1, route[i].getId());
			ps3.setInt(2, route[i+1].getId());
			time = System.currentTimeMillis();
			rs2 = ps3.executeQuery();
			timesum2 = timesum2 + (System.currentTimeMillis() - time);
			while (rs2.next()) {
				if (rs2.getInt(1) == 1) {
					inOrder = true;
				} else {
					inOrder = false;
				}
			}
			for(Edge e : route[i].getEdgeList()){
			    if((e.getSuccessor()).equals(route[i+1]) || (e.getPredecessor()).equals(route[i+1])){
					myEdgeID = e.getID();
			    }
			}
			ps1.setInt(1, myEdgeID);
			ps2.setInt(1, myEdgeID);
			time = System.currentTimeMillis();
			if (inOrder) {
				rs1 = ps1.executeQuery();
			} else {
				rs1 = ps2.executeQuery();
			}
			timesum = timesum + (System.currentTimeMillis() - time);
			rsCounter = 0;
			while (rs1.next()) {
				c = new Coordinate(rs1.getFloat(1), rs1.getFloat(2));
				coordList.add(c);
				if (rs1.isLast() && rsCounter >= 1) {
					c = new Coordinate(rs1.getFloat(3), rs1.getFloat(4));
					coordList.add(c);
				}
				rsCounter++;
			}
			
		}
		System.out.println("DB-Abfragen1 " + timesum2 + "ms");
		System.out.println("DB-Abfragen2 " + timesum + "ms");
		System.out.println("Size All: " + coordList.size());

	con.close();
	return coordList;
	}
}
