/**
 * 
 */
package de.htwmaps.database.importscripts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.database.DBConnector;

public class EdgeOptimizer {
	
	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID IN (27057142);";
//	String sql1 = "SELECT ID, startEdge, endEdge FROM ways WHERE ID IN (27057142, 24343561);";
	String sql2 = "SELECT ID, node1ID, node2ID, length FROM edges_all WHERE wayID = ?;";
	String sql3 = "SELECT COUNT(*) FROM edges_all WHERE (node1ID = ? OR node2ID = ?) AND wayID <> ?";
	
	PreparedStatement ps1;
	PreparedStatement ps2;
	PreparedStatement ps3;
	
	Connection con = DBConnector.getConnection();
	
	
	public EdgeOptimizer() {
		start();
	}

	private void start() {
		System.out.println("Start");
		
		try {
			ps1 = con.prepareStatement(sql1);
			ps2 = con.prepareStatement(sql2);
			ps3 = con.prepareStatement(sql3);
			
			ResultSet rs1 = ps1.executeQuery();
			while (rs1.next()) {
				int wayID = rs1.getInt(1);
				int startEdgeID = rs1.getInt(2);
				int endEdgeID = rs1.getInt(3);
				runWay(wayID, startEdgeID, endEdgeID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

	private void runWay(int wayID, int startEdgeID, int endEdgeID) {
		try {
			if (startEdgeID != endEdgeID) {
				float sumLength = 0;
				int newEdgeStartID = startEdgeID;
				ps2.setInt(1, wayID);
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					int edgeID = rs2.getInt(1);
					int node1ID = rs2.getInt(2);
					int node2ID = rs2.getInt(3);
					float length = rs2.getFloat(4);
					int nodeCrossings = getNodeCrossings(node2ID, wayID);
					sumLength = sumLength + length;
					System.out.println("Node " + node2ID + " : " + nodeCrossings + " crossings : " + sumLength + "m");
					if (nodeCrossings == 0) {
						System.out.println("no crossing.");
					}
					else {
//						System.out.println("crossing. add edge (newStartEdgeID to node2ID). set node2 as newEdgeStartID. reset sumLength");
						addEdge(newEdgeStartID, node2ID, sumLength);
						newEdgeStartID = node2ID;
						sumLength = 0;
					}
					
				}
			}
			else {
				System.out.println("Way hat nur eine Edge. Direkt kopieren");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addEdge(int newEdgeStartID, int node2ID, float sumLength) {
		System.out.println("NEW " + newEdgeStartID + "|" + node2ID + "|" + sumLength);
	}

	private int getNodeCrossings(int node2id, int wayID) {
		int crossings = 0;
		try {
			ps3.setInt(1, node2id);
			ps3.setInt(2, node2id);
			ps3.setInt(3, wayID);
			ResultSet rs3 = ps3.executeQuery(); 
			while (rs3.next()) {
				crossings = rs3.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return crossings;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EdgeOptimizer();

	}

}
