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
	
	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways;";
//	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID IN (27057142, 24343561);";
	String sql2 = "SELECT ID, node1ID, node2ID, length FROM edges_all WHERE wayID = ?;";
	String sql3 = "SELECT COUNT(*) FROM edges_all WHERE (node1ID = ? OR node2ID = ?) AND wayID <> ?;";
	String sql4 = "INSERT INTO edges_opt (ID, node1ID, node2ID, wayID, length) VALUES (?, ?, ?, ?, ?);";
	String sql5 = "INSERT INTO edges_opt (ID, node1ID, node2ID, wayID, length) SELECT ID, node1ID, node2ID, wayID, length FROM edges_all WHERE ID = ?;";
	
	PreparedStatement ps1, ps2, ps3, ps4, ps5;

	
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
			ps4 = con.prepareStatement(sql4);
			ps5 = con.prepareStatement(sql5);
			
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
//		System.out.println("Start with way " + wayID);
		try {
			if (startEdgeID != endEdgeID) {
				float sumLength = 0;
				int newStartEdgeNodeID = 0;
				int newStartEdgeID = 0;
				ps2.setInt(1, wayID);
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					int edgeID = rs2.getInt(1);
					if (newStartEdgeID == 0) {
						newStartEdgeID = edgeID;
					}
					int node1ID = rs2.getInt(2);
					if (newStartEdgeNodeID == 0) {
						newStartEdgeNodeID = node1ID;
					}
					int node2ID = rs2.getInt(3);
					float length = rs2.getFloat(4);
					int nodeCrossings = getNodeCrossings(node2ID, wayID);
					sumLength = sumLength + length;
//					System.out.println("Node " + node2ID + " : " + nodeCrossings + " crossings : " + sumLength + "m");
					if (nodeCrossings > 0) {
//						System.out.println("crossing. add edge (newStartEdgeID to node2ID). set node2 as newEdgeStartID. reset sumLength");
						addEdge(newStartEdgeID, newStartEdgeNodeID, node2ID, wayID, sumLength);
						newStartEdgeNodeID = node2ID;
						newStartEdgeID = edgeID;
						sumLength = 0;
					}					
				}
			}
			else {
//				System.out.println("Way hat nur eine Edge. Direkt kopieren");	
				copyEdge(startEdgeID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addEdge(int newStartEdgeID, int newStartEdgeNodeID, int node2ID, int wayID, float sumLength) {
		boolean success = false;
		while (!success) {
			try {
				ps4.setInt(1, newStartEdgeID);
				ps4.setInt(2, newStartEdgeNodeID);
				ps4.setInt(3, node2ID);
				ps4.setInt(4, wayID);
				ps4.setFloat(5, sumLength);
				ps4.execute();
//				System.out.println("TRY " + newStartEdgeID + "|" + newStartEdgeNodeID + "|" + node2ID + "|" + wayID + "|" + sumLength);
				success = true;
			} catch (SQLException e) {
				newStartEdgeID = newStartEdgeID + 1;	
//				e.printStackTrace();
			}
		}
		
	}
	
	private void copyEdge(int startEdgeID) {
		try {
			ps5.setInt(1, startEdgeID);
			ps5.execute();
//			System.out.println("COPY " + startEdgeID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		new CalculateEdgeLength();
		new EdgeOptimizer();

	}

}
