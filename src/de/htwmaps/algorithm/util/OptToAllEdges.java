package de.htwmaps.algorithm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.algorithm.Edge;
import de.htwmaps.algorithm.Node;
import de.htwmaps.database.DBConnector;

public class OptToAllEdges {
	
	String sql1 = "SELECT node1ID, node1lat, node1lon, node2ID, node2lat, node2lon, ID FROM edges_all WHERE partOfEdgesOptID = ?";
	
	PreparedStatement ps1;
	Node[] completeRoute;
	Connection con = DBConnector.getConnection();
	
	int myEdgeID;

	public OptToAllEdges(Node[] route) {
		try {
			parseEdges(route);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	private void parseEdges(Node[] route) throws SQLException {
		
		ps1 	= con.prepareStatement(sql1);

		ResultSet rs1 = null;
		// TODO Auto-generated method stub
		System.out.println("First: " + route[0].getId() + " :: Last: " + route[route.length-1]);
		for(int i=0; i <= route.length-2; i++){ 
			for(Edge e : route[i].getEdgeList()){
			    if((e.getSuccessor()).equals(route[i+1]) || (e.getPredecessor()).equals(route[i+1])){
					myEdgeID = e.getID();
			    	System.out.println(e.getID());

			    }
			}
			ps1.setInt(1, myEdgeID);
			System.out.println(ps1.toString());
			rs1 = ps1.executeQuery();
			while (rs1.next()) {
				System.out.println("EgdeOpt: " + myEdgeID + " EdgeAll: " + rs1.getInt(7));
			}
			
		}
	}

}
