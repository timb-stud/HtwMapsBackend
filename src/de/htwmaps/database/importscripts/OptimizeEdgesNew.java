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
 * Klasse entfernt die, fuer den Algo, unnoetigen Edges aus edges_all und schreibt die zusammengefassten in edges_opt 
 *
 */

public class OptimizeEdgesNew {
	
//	String sql1 = "SELECT ways.ID, startEdgeID, endEdgeID FROM ways, waysview WHERE ways.ID = waysview.wayID "; 
	String sql1 = "SELECT ways.ID, startEdgeID, endEdgeID FROM ways WHERE ID = 4118350";
	//String sql1 = "SELECT ways.ID, startEdgeID, endEdgeID FROM ways WHERE ID = 27057142";
	//String sql1 = "SELECT ways.ID, startEdgeID, endEdgeID FROM ways WHERE ID = 14334208";
	String sql3 = "SELECT COUNT(*) FROM edges_all WHERE (node1ID = ? OR node2ID = ?) AND wayID <> ?";
	String sql4 = "SELECT node2ID FROM edges_all WHERE `wayID` = ? AND `node1ID` = ?";
//	String sql5 = "SELECT length FROM edges_all WHERE node1ID = ? AND wayID = ?";
	String sql7 = "SELECT node1ID, node2ID, length FROM edges_all WHERE ID = ? AND visited = 0";
	String sql8 = "SELECT node1ID, node2ID, length FROM edges_all WHERE wayID = ? AND node1ID = ? AND visited = 0";
	
	String sql2 = "INSERT INTO edges_opt (wayID, node1ID, node2ID, length) VALUES (?, ?, ?, ?)";
	String sql6 = "UPDATE edges_all SET visited = 1 WHERE wayID = ? AND node1ID = ? AND node2ID = ?";

	PreparedStatement ps2;
	PreparedStatement ps3;
	PreparedStatement ps4;
//	PreparedStatement ps5;
	PreparedStatement ps6;
	PreparedStatement ps7;
	PreparedStatement ps8;
	int selectcounter = 0;
	int waycounter = 0;


	

	public OptimizeEdgesNew() {
		start();
	}
	
	private void start() {
		
		System.out.println("Start");
			
        try {
        	PreparedStatement ps1 = DBConnector.getConnection().prepareStatement(sql1);
			ps2 = DBConnector.getConnection().prepareStatement(sql2);
			ps3 = DBConnector.getConnection().prepareStatement(sql3);
			ps4 = DBConnector.getConnection().prepareStatement(sql4);
//        	ps5 = DBConnector.getConnection().prepareStatement(sql5);
        	ps6 = DBConnector.getConnection().prepareStatement(sql6);
        	ps7 = DBConnector.getConnection().prepareStatement(sql7);
        	ps8 = DBConnector.getConnection().prepareStatement(sql8);

        	// rs1 enthaelt die ID, startEdge und EndEdge aller Ways mit mehr als einer Edge
        	ResultSet rs1 = ps1.executeQuery();
			selectcounter++;
			// durchlaufe alle selektierten Ways
			while (rs1.next()) {
				int wayID = rs1.getInt(1);
				int startEdgeID = rs1.getInt(2);
				int endEdgeID = rs1.getInt(3);
				ps7.setInt(1, startEdgeID);
				// rs7 enthaelt node1 und node2 sowie die Laenge der startEdge
				ResultSet rs7 = ps7.executeQuery();
				selectcounter++;
				try {
					rs7.next();
				} catch (SQLException e) {
					System.out.println("Fehler bei" + ps7.toString());
				}
				// benoetigte Daten aus rs7 werden in drei Variablen geschrieben
				int node1ID = rs7.getInt(1);
				int node2ID = rs7.getInt(2);
				float edgeLength = rs7.getFloat(3);
				// nun wird rs7 mit den Daten der endEdge gefŸllt 
				ps7.setInt(1, endEdgeID);
				rs7 = ps7.executeQuery();
				selectcounter++;
				try {
					rs7.next();
				} catch (SQLException e) {
					System.out.println("Fehler bei" + ps7.toString());
				}
				// endNodeID ist Abbruchbedingung und wir hier einmalig pro Way gesetzt
				int endNodeID = rs7.getInt(2);
				waycounter++;
				System.out.println("Starte mit Way #" + waycounter + "(" + wayID + "). Aktuell " + selectcounter + " SQL Abfragen");
				// zum rekursiven Durchlaufen des Ways wird die Methode runWay aufgerufen
				runWay(wayID, node1ID, node1ID, node2ID, edgeLength, 0, endNodeID);
				}
				
//			}
			

        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	}
	
	private void runWay(int wayID, int startNodeID, int currentNodeID, int toNodeID, float edgeLength, float sumLength, int endWayNodeID) {
		System.out.println(startNodeID + "|" + currentNodeID + "|" + toNodeID + "|" + edgeLength + "|" + sumLength + "|" + endWayNodeID);
		if (currentNodeID != toNodeID && currentNodeID != 0) {
			int crossCount = getNodeCrossings(toNodeID, wayID);
			sumLength = sumLength + edgeLength;
//			System.out.println("Start: " + startNodeID + " Current: " + currentNodeID + " Next: " + toNodeID + " Kreuzungen: " + crossCount + " Laenge: " + sumLength);
			setEdgeVisited(wayID, currentNodeID, toNodeID);
			int nextNodeID = 0;
			float nextEdgeLength = 0;
			try {
				ps8.setInt(1, wayID);
				ps8.setInt(2, toNodeID);
				ResultSet rs8 = ps8.executeQuery();
				selectcounter++;
				rs8.next();
				nextNodeID = rs8.getInt(2);
				nextEdgeLength = rs8.getFloat(3);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (crossCount > 0) {
				System.out.println("Neue Edge mit Start: " + startNodeID + " Ende: " + toNodeID + " Laenge:" + sumLength);
				insertNewEdge(wayID, startNodeID, nextNodeID, sumLength);				
				startNodeID = toNodeID;
				sumLength 	= 0;
			}
//			System.out.println("next:" + nextNodeID + " endNode: " + endWayNodeID);
			if (nextNodeID != endWayNodeID) {
				runWay(wayID, startNodeID, toNodeID, getNextNode(nextNodeID, wayID), nextEdgeLength, sumLength, endWayNodeID);
			}
		}
	}

	private void insertNewEdge(int wayID, int startNodeID, int nextNodeID, float sumLength) {
		try {
			ps2.setInt(1, wayID);
			ps2.setInt(2, startNodeID);
			ps2.setInt(3, nextNodeID);
			ps2.setFloat(4, sumLength);
			ps2.execute();
			selectcounter++;
		} catch (SQLException e) {
			System.out.println(ps2.toString());
			e.printStackTrace();
		}
		
		
	}

	private int getNodeCrossings(int currentNodeID, int wayID) {
		// TODO Auto-generated method stub
		int crossCount = 0;
		try {
			//ps3 = null;
			ps3.setInt(1, currentNodeID);
			ps3.setInt(2, currentNodeID);
			ps3.setInt(3, wayID);
			ResultSet rs = ps3.executeQuery();
			selectcounter++;
			rs.next();
			crossCount = rs.getInt(1);
		} catch (SQLException e) {
			System.out.println(ps3.toString());
			e.printStackTrace();
		}
		return crossCount;
	}

	private int getNextNode(int toNodeID, int wayID) {
		int nextNodeID = 0;
		try {
			ps8.setInt(1, wayID);
			ps8.setInt(2, toNodeID);
			ResultSet rs8 = ps8.executeQuery();
			selectcounter++;
			rs8.next();
			nextNodeID = rs8.getInt(1);
		} catch (SQLException e) {
			System.out.println(ps8.toString());
			e.printStackTrace();
		}
		return nextNodeID;
	}
	
	private void setEdgeVisited(int wayID, int currentNodeID, int nextNodeID) {		// TODO Auto-generated method stub
		try {
			ps6.setInt(1, wayID);
			ps6.setInt(2, currentNodeID);
			ps6.setInt(3, nextNodeID);
			ps6.execute();
			selectcounter++;
		} catch (SQLException e) {
			System.out.println(ps6.toString());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new OptimizeEdges();
	}

}
