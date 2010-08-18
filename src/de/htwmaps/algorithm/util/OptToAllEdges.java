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
	
	String sql1 = "SELECT node1lat, node1lon, node2lat, node2lon FROM edges_all WHERE partOfEdgesOptID = ?";
	
	PreparedStatement ps1;
	LinkedList<Coordinate> coordList;
	Connection con = DBConnector.getConnection();
	Coordinate c;
	
	int myEdgeID;
	
	long time, timesum;


	public OptToAllEdges(Node[] route) {
		try {
			parseEdges(route);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private LinkedList<Coordinate> parseEdges(Node[] route) throws SQLException {
		
		ps1 		= con.prepareStatement(sql1);
		coordList 	= new LinkedList<Coordinate>();

		ResultSet rs1 = null;
		System.out.println("Size Opt: " + route.length);
		for(int i=0; i <= route.length-2; i++){ 
			for(Edge e : route[i].getEdgeList()){
			    if((e.getSuccessor()).equals(route[i+1]) || (e.getPredecessor()).equals(route[i+1])){
					myEdgeID = e.getID();
			    }
			}
			ps1.setInt(1, myEdgeID);
			time = System.currentTimeMillis();
			rs1 = ps1.executeQuery();
			timesum = timesum + (System.currentTimeMillis() - time);
			while (rs1.next()) {
				c = new Coordinate(rs1.getFloat(1), rs1.getFloat(2));
				coordList.add(c);
				if (rs1.isLast()) {
					c = new Coordinate(rs1.getFloat(3), rs1.getFloat(4));
					coordList.add(c);
				}
			}
			
		}
		System.out.println("DB-Abfragen " + timesum + "ms");
		System.out.println("Size All: " + coordList.size());
		con.close();
		generateTrack(coordList);
		return coordList;
	}
	
	public String generateTrack(LinkedList<Coordinate> result) {	
		StringBuilder str = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<gpx>\n<trk>\n <trkseg>\n");
		for (Coordinate tmp : result) {
			str.append("  <trkpt lat=\"").append(tmp.getLat()).append("\" lon=\"").append(tmp.getLon()).append("\">\n").append("  </trkpt>\n");
		}
		str.append(" </trkseg>\n</trk>\n</gpx>");
		System.out.println(str.toString());
		return str.toString();
	}
}
