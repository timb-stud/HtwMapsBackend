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
	
//	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID BETWEEN 0 AND 1000000;";
//	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID BETWEEN 1000001 AND 2000000;";
//	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID BETWEEN 2000001 AND 3000000;";
//	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID BETWEEN 3000001 AND 4000000;";
//	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID BETWEEN 4000001 AND 5000000;";
//	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID > 5000000;";
//	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways WHERE ID = 27057142;";
	String sql1 = "SELECT ID, startEdgeID, endEdgeID FROM ways;";
	String sql2 = "SELECT ID, node1ID, node2ID, length FROM edges_all WHERE wayID = ?;";
	String sql30 = "SELECT COUNT(*) FROM edges_all WHERE node1ID = ? AND wayID <> ?;";
	String sql31 = "SELECT COUNT(*) FROM edges_all WHERE node1ID = ? AND wayID <> ?;";
	String sql4 = "INSERT INTO edges_opt (ID, node1ID, node2ID, wayID, length) VALUES (?, ?, ?, ?, ?);";
	String sql5 = "INSERT INTO edges_opt (ID, node1ID, node2ID, wayID, length) SELECT ID, node1ID, node2ID, wayID, length FROM edges_all WHERE ID = ?;";
	
	PreparedStatement ps1, ps2, ps30, ps31, ps4, ps5;
	
	boolean inserted = false;

	int counter;
	
	Connection con = DBConnector.getConnection();
	
	
	public EdgeOptimizer() {
		start();
	}

	private void start() {
		System.out.println("Start EdgeOptimzer");
		
		try {
			ps1 = con.prepareStatement(sql1);
			ps2 = con.prepareStatement(sql2);
			ps30 = con.prepareStatement(sql30);
			ps31 = con.prepareStatement(sql31);
			ps4 = con.prepareStatement(sql4);
			ps5 = con.prepareStatement(sql5);
			
			ResultSet rs1 = ps1.executeQuery();
			while (rs1.next()) {
				counter++;
				int wayID = rs1.getInt(1);
				if (!inserted) {
					System.out.println("Fehler vor Way" + wayID);
				}
				int startEdgeID = rs1.getInt(2);
				int endEdgeID = rs1.getInt(3);
				runWay(wayID, startEdgeID, endEdgeID);
				if (counter % 1000 == 0) {
					System.out.println(counter);
				}
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
//					System.out.println("Start CrossCount");
					int nodeCrossings = getNodeCrossings(node2ID, wayID);
//					System.out.println("End CrossCount");
					sumLength = sumLength + length;
//					System.out.println("Node " + node2ID + " : " + nodeCrossings + " crossings : " + sumLength + "m");
					if (nodeCrossings > 0) {
//						System.out.println("crossing. add edge (newStartEdgeID to node2ID). set node2 as newEdgeStartID. reset sumLength");
//						System.out.println("Start AddEdge");
						addEdge(newStartEdgeID, newStartEdgeNodeID, node2ID, wayID, sumLength);
//						System.out.println("End AddEdge");
						newStartEdgeNodeID = node2ID;
						newStartEdgeID = edgeID;
						sumLength = 0;
					}					
				}
			}
			else {
//				System.out.println("Way hat nur eine Edge. Direkt kopieren");	
//				System.out.println("Start CopyEdge");
				copyEdge(startEdgeID);
//				System.out.println("End CopyEdge");
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
				inserted = true;
			} catch (SQLException e) {
				newStartEdgeID = newStartEdgeID + 100000000;	
//				e.printStackTrace();
			}
		}
		
	}
	
	private void copyEdge(int startEdgeID) {
		try {
			ps5.setInt(1, startEdgeID);
			ps5.execute();
//			System.out.println("COPY " + startEdgeID);
			inserted = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private int getNodeCrossings(int node2id, int wayID) {
		int crossings = 0;
		try {
			ps30.setInt(1, node2id);
			ps30.setInt(2, wayID);
//			System.out.println(ps30.toString());
			ResultSet rs30 = ps30.executeQuery(); 
			while (rs30.next()) {
				crossings = crossings + rs30.getInt(1);
			}
			ps31.setInt(1, node2id);
			ps31.setInt(2, wayID);
//			System.out.println(ps31.toString());
			ResultSet rs31 = ps31.executeQuery(); 
			while (rs31.next()) {
				crossings = crossings + rs31.getInt(1);
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
//		new CalculateEdgeLength();
		new EdgeOptimizer();

	}

}
