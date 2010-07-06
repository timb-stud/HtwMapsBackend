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
public class OptimizeEdges {
	
	
	public OptimizeEdges() {
		start();
	}
	
	private void start() {
		
		boolean toOptimize = true;
		
		String sql1 = "SELECT wayID, startNodeID, endNodeID FROM ways WHERE partOfHighway = 1";
		String sql2 = "SELECT ID, node1ID, node2ID, wayID, length FROM `edges2` WHERE `wayID` = ? AND `node1ID` = ?";
		String sql3 = "SELECT COUNT(*) FROM edges2 WHERE (node1ID = ? OR node2ID = ?) AND wayID <> ?";
		// TODO Auto-generated method stub
        try {
        	// nehme alle wayIDs mit partOfHighway = 1: SELECT wayID, startNodeID FROM ways WHERE partOfHighway = 1
			
        	PreparedStatement ps1 = DBConnector.getConnection().prepareStatement(sql1);
			// selektiere erste edge des Ways: 
			PreparedStatement ps2 = DBConnector.getConnection().prepareStatement(sql2);
			// count aller ankommenden/abgehenden edges fuer den node2 die nicht zu dem aktuellen way gehoeren
			PreparedStatement ps3 = DBConnector.getConnection().prepareStatement(sql3);
			
//			ResultSet rs1 = ps1.executeQuery();
//			while (rs1.next()) {
				//int wayID = rs1.getInt(1);
				int wayID = 27057142;
				//int startNodeID = rs1.getInt(2)
				int startNodeID = 270697578;
				//int endNodeID = rs1.getInt(2)
				int endNodeID = 270697627;
				runWay(wayID, startNodeID, endNodeID);
				/*ps2.setInt(1, wayID);
				ps2.setInt(2, startNodeID);
				ResultSet rs2 = ps2.executeQuery();
				float optLength = 0;
				while (toOptimize) {
					int edgeID 		= rs2.getInt(1);
					int node1ID 	= rs2.getInt(2);
					int node2ID 	= rs2.getInt(3);
					float edgeLength = rs2.getFloat(4);
					ps3.setInt(1, node2ID);
					ps3.setInt(2, node2ID);
					ps3.setInt(3, wayID);
					ResultSet rs3 = ps3.executeQuery();
					int edgeCount = rs3.getInt(1);
					if (edgeCount == 0) {
						optimize(node1ID, node2ID, edgeLength, optLength);
						// keine anderen ways benutzen den Knoten
						// >> mit optimierung fortfahren
					}
					else {
						// andere ways benutzen Knoten 
						// >> optimierung beenden
					}
				}*/
				
//			}
			

        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	}

	private void optimize(int node1id, int node2id, float edgeLength,
			float optLength) {
		// TODO Auto-generated method stub
		
	}
	
	private void runWay(int wayID, int currentNodeID, int endNodeID) {
		//suche startkante und starte optimierung
		System.out.println("Current: " + currentNodeID);
		if (currentNodeID != endNodeID && currentNodeID != 0) {
			int nextNodeID = getNextNode(currentNodeID, wayID);
			System.out.println("Next: " + nextNodeID);
			runWay(wayID, nextNodeID, endNodeID);
		}
	}

	private int getNextNode(int currentNodeID, int wayID) {
		// TODO Auto-generated method stub
		String sql = "SELECT node2ID FROM `edges2` WHERE `wayID` = ? AND `node1ID` = ?";
		PreparedStatement ps;
		int nextNodeID = 0;
		try {
			ps = DBConnector.getConnection().prepareStatement(sql);
			ps.setInt(1, wayID);
			ps.setInt(2, currentNodeID);
			ResultSet rs = ps.executeQuery();
			rs.next();
			nextNodeID = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nextNodeID;
	}

	public static void main(String[] args) {
		new OptimizeEdges();
	}

}
