package de.htwmaps.algorithm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.algorithm.Node;
import de.htwmaps.database.DBConnector;

public class OptToAllEdges {
	
	String sql1 = "SELECT ID FROM edges_opt WHERE (node1ID = ? AND node2ID = ?) OR (node1ID = ? AND node2ID = ?)";
	String sql2 = "SELECT node1ID, node1lat, node1lon, node2ID, node2lat, node2lon FROM edges_all WHERE partOfEdgesOptID = ?";
	
	PreparedStatement ps1, ps2;
	Node[] completeRoute;
	Connection con = DBConnector.getConnection();

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
		ps2 	= con.prepareStatement(sql2);

		ResultSet rs1 = null;
		ResultSet rs2 = null;
		// TODO Auto-generated method stub
		System.out.println("First: " + route[0].getId() + " :: Last: " + route[route.length-1]);
		for(int i=1; i <= route.length-1; i++){ 
//			System.out.println(route[i-1].getId());
			ps1.setInt(1, route[i].getId());
			ps1.setInt(2, route[i-1].getId());
			ps1.setInt(3, route[i-1].getId());
			ps1.setInt(4, route[i].getId());
			System.out.println(ps1.toString());
			rs1 = ps1.executeQuery();
			rs1.next();
			ps2.setInt(1, rs1.getInt(1));
			System.out.println(ps2.toString());
			rs2 = ps2.executeQuery();
//			while (rs2.next()) {
//				System.out.println("Fuege Node[] completeRoute Node mit ID " + rs2.getInt(1) + " lat/lon " + rs2.getFloat(2) + "/" + rs2.getFloat(3));
////				int n1ID 	= rs2.getInt(1);
////				float n1lat = rs2.getFloat(2);
////				float n1lon = rs2.getFloat(3);
//				if (rs2.last()) {
////					int n2ID 	= rs2.getInt(4);
////					float n2lat = rs2.getFloat(5);
////					float n2lon = rs2.getFloat(6);
//					System.out.println("Last! Fuege Node[] completeRoute Node mit ID " + rs2.getInt(4) + " lat/lon " + rs2.getFloat(5) + "/" + rs2.getFloat(6)); 
//				}
////				System.out.println("Opt: " + rs1.getInt(1) + " All: " + rs2.toString());
//			}
			
//			System.out.println(eID + "    " + ps1);
		}
//		for (Node n : route) {
//			System.out.println(n);
//		}
	}

}
