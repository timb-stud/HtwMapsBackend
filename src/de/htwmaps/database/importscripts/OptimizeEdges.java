/**
 * 
 */
package de.htwmaps.database.importscripts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.database.DBConnector;

/**
 * @author Tobias Lana, Yassir Klos
 *
 */
public class OptimizeEdges {
	
	
	public OptimizeEdges() {
		start();
	}
	
	private void start() {
		
		boolean toOptimize = true;
		
		//String sql1 = "SELECT ways.ID, startNodeID, endNodeID FROM ways, nodes WHERE nodes.partOfHighway = 1 AND nodes.ID = ways.startNodeID AND ways.ID = 27057142";
		String sql1 = "SELECT ways.ID, startNodeID, endNodeID FROM ways, nodes WHERE nodes.partOfHighway = 1 AND nodes.ID = ways.startNodeID";
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
			
			ResultSet rs1 = ps1.executeQuery();
			while (rs1.next()) {
				int wayID = rs1.getInt(1);
//				int wayID = 27057142;
				int startNodeID = rs1.getInt(2);
//				int startNodeID = 270697578;
				int endNodeID = rs1.getInt(3);
//				int endNodeID = 270697627;
//				System.out.println("way: " + wayID + " Start: " + startNodeID + " End: " + endNodeID);
				runWay(wayID, startNodeID, startNodeID, endNodeID, 0);
				System.out.println("Fertig mit Way " + wayID);
				}
				
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
	
	private void runWay(int wayID, int startNodeID, int currentNodeID, int endNodeID, float sumLength) {
		//suche startkante und starte optimierung
		if (currentNodeID != endNodeID && currentNodeID != 0) {
			int nextNodeID = getNextNode(currentNodeID, wayID);
			int crossCount = getNodeCrossings(nextNodeID, wayID);
			float edgeLength = getEdgeLength(wayID, currentNodeID);
			sumLength = sumLength + edgeLength;
			//System.out.println("Start: " + startNodeID + " Current: " + currentNodeID + " Next: " + nextNodeID + " Kreuzungen: " + crossCount + " Laenge: " + sumLength);

			if (crossCount > 0) {
				//System.out.println("Neue Edge mit Start: " + startNodeID + " Ende: " + nextNodeID + " Laenge:" + sumLength);
				insertNewEdge(wayID, startNodeID, nextNodeID, sumLength);
				startNodeID = nextNodeID;
				sumLength = 0;
			}
			
			runWay(wayID, startNodeID, nextNodeID, endNodeID, sumLength);
		}
	}

	private void insertNewEdge(int wayID, int startNodeID, int nextNodeID,
			float sumLength) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO edges3 (wayID, node1ID, node2ID, length) VALUES (?, ?, ?, ?)";
		PreparedStatement ps;
		try {
			ps = DBConnector.getConnection().prepareStatement(sql);
			ps.setInt(1, wayID);
			ps.setInt(2, startNodeID);
			ps.setInt(3, nextNodeID);
			ps.setFloat(4, sumLength);
//			System.out.println(ps.toString());
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private float getEdgeLength(int wayID, int currentNodeID) {
		// TODO Auto-generated method stub
		String sql = "SELECT length FROM edges2 WHERE node1ID = ? AND wayID = ?";
		PreparedStatement ps;
		float edgeLength = 0;
		try {
			ps = DBConnector.getConnection().prepareStatement(sql);
			ps.setInt(1, currentNodeID);
			ps.setInt(2, wayID);
			ResultSet rs = ps.executeQuery();
			rs.next();
			edgeLength = rs.getFloat(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return edgeLength;
	}

	private int getNodeCrossings(int currentNodeID, int wayID) {
		// TODO Auto-generated method stub
		String sql = "SELECT COUNT(*) FROM edges2 WHERE (node1ID = ? OR node2ID = ?) AND wayID <> ?";
		PreparedStatement ps;
		int crossCount = 0;
		try {
			ps = DBConnector.getConnection().prepareStatement(sql);
			ps.setInt(1, currentNodeID);
			ps.setInt(2, currentNodeID);
			ps.setInt(3, wayID);
			ResultSet rs = ps.executeQuery();
			rs.next();
			crossCount = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crossCount;
	}

	private int getNextNode(int currentNodeID, int wayID) {
		// TODO Auto-generated method stub
		String sql = "SELECT node2ID, length FROM `edges2` WHERE `wayID` = ? AND `node1ID` = ?";
		PreparedStatement ps;
		int nextNodeID = 0;
		float length = 0;
		try {
			ps = DBConnector.getConnection().prepareStatement(sql);
			ps.setInt(1, wayID);
			ps.setInt(2, currentNodeID);
			ResultSet rs = ps.executeQuery();
			rs.next();
			nextNodeID = rs.getInt(1);
			length = rs.getFloat(2);
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
