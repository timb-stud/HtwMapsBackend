package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.algorithm.Node;


public class DBAdapterParabel{
	//Nodes
	private int[] nodeIDs;
	private float[] nodeLons; //x
	private float[] nodeLats; //y
	//Edges
	private int[] fromNodeIDs;
	private int[] toNodeIDs;
	private double[] distances;
	private boolean[] oneways;
	private int[] highwayTypes;
	
	private String NODE_SELECT;
	private String EDGE_SELECT;
		
	public DBAdapterParabel(int startID, int endID, float startNodeLon, float startNodeLat, float endNodeLon, float endNodeLat) throws SQLException {	
		setRectangle(startID, endID, startNodeLon, startNodeLat, endNodeLon, endNodeLat);
		initNodes();
		initEdges();
	}
	

	private void initNodes() throws SQLException{
		int tableLength;
		PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(NODE_SELECT);
		ResultSet resultSet = pStmt.executeQuery();
//		while (resultSet.next()) {
//			System.out.println(resultSet.getFloat(3)+"\t"+resultSet.getFloat(2)+"\t"+"title\t"+"descr\t"+"rosa_punkt.png\t"+"8,8\t"+"0,0");
//		}
		resultSet.last();
		tableLength = resultSet.getRow();
		resultSet.beforeFirst();
		nodeIDs = new int[tableLength];
		nodeLons = new float[tableLength];
		nodeLats = new float[tableLength];
		
		for (int i = 0; resultSet.next(); i++){
			nodeIDs[i] = resultSet.getInt(1);
			nodeLons[i] = resultSet.getFloat(2);
			nodeLats[i] = resultSet.getFloat(3);
		}
	}

	private void initEdges() throws SQLException{
		int tableLength;
		PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(EDGE_SELECT);
		ResultSet resultSet = pStmt.executeQuery();
		pStmt = null;
		resultSet.last();
		tableLength = resultSet.getRow();
		resultSet.beforeFirst();
		fromNodeIDs = new int[tableLength];
		toNodeIDs = new int[tableLength];
		distances = new double[tableLength];
		oneways = new boolean[tableLength];
		highwayTypes = new int[tableLength];
		
		for (int i = 0; resultSet.next(); i++){
			fromNodeIDs[i] = resultSet.getInt(1);
			toNodeIDs[i] = resultSet.getInt(2);
			distances[i] = 0.0;
			oneways[i] = resultSet.getBoolean(3);
			highwayTypes[i] = resultSet.getInt(4);
		}
	}

	private void setRectangle(int startID, int endID, float startNodeLon, float startNodeLat, float endNodeLon, float endNodeLat) {
		float h = 0.9f;
		float k = 0.02f;
		if(startNodeLon < endNodeLon && startNodeLat < endNodeLat || startNodeLon > endNodeLon && startNodeLat < endNodeLat){
			//ps(x) = h (ey - sy) / (ex - sx)Â² (x - sx)Â² + sy - k
			//pe(x) = h (sy - ey) / (sx - ex)Â² (x - ex)Â² + ey + k
			NODE_SELECT = "select varNodes.id, varNodes.lon, varNodes.lat from saarland.nodes startNodes, saarland.nodes endNodes, saarland.nodes varNodes "
				+ " where startNodes.id = " + startID + "  and endNodes.id = " + endID + " "
				+ " and "
				+ " " + h + " *((endNodes.lat - startNodes.lat)/POW((endNodes.lon - startNodes.lon),2))*POW((varNodes.lon - startNodes.lon),2) + startNodes.lat  - " + k + " <= varNodes.lat "
				+ " and "
				+ " " + h + " *((startNodes.lat - endNodes.lat)/POW((startNodes.lon - endNodes.lon),2))*POW((varNodes.lon - endNodes.lon),2) + endNodes.lat + " + k + " >= varNodes.lat "
				+ " and varNodes.partofhighway = 1";
			EDGE_SELECT = "select fromNodeID, toNodeID, oneway, k_highwayspeedID, n1.lon, n1.lat, n2.lon, n2.lat from saarland.edges, saarland.nodes n1, saarland.nodes n2 "
				+ " where edges.fromNodeID = n1.ID AND edges.toNodeID = n2.ID " 
				+ " and  fromNodeID in ("
				+ " select varNodes.id from saarland.nodes startNodes, saarland.nodes endNodes, saarland.nodes varNodes "
				+ " where startNodes.id = " + startID + "  and endNodes.id = " + endID + " "
				+ " and "
				+ " " + h + "*((endNodes.lat - startNodes.lat)/POW((endNodes.lon - startNodes.lon),2))*POW((varNodes.lon - startNodes.lon),2) + startNodes.lat  - " + k + " <= varNodes.lat "
				+ " and "
				+ " " + h + "*((startNodes.lat - endNodes.lat)/POW((startNodes.lon - endNodes.lon),2))*POW((varNodes.lon - endNodes.lon),2) + endNodes.lat + " + k + " >= varNodes.lat "
				+ " and varNodes.partofhighway = 1"
				+ ")"
				+ " and toNodeID in ("
				+ " select varNodes.id from saarland.nodes startNodes, saarland.nodes endNodes, saarland.nodes varNodes "
				+ " where startNodes.id = " + startID + "  and endNodes.id = " + endID + " "
				+ " and "
				+ " " + h + " *((endNodes.lat - startNodes.lat)/POW((endNodes.lon - startNodes.lon),2))*POW((varNodes.lon - startNodes.lon),2) + startNodes.lat  - " + k + " <= varNodes.lat "
				+ " and "
				+ " " + h + " *((startNodes.lat - endNodes.lat)/POW((startNodes.lon - endNodes.lon),2))*POW((varNodes.lon - endNodes.lon),2) + endNodes.lat + " + k + " >= varNodes.lat "
				+ " and varNodes.partofhighway = 1"
				+ ")";
		} else {
			//ps(x) = h (ey - sy) / (ex - sx)Â² (x - sx)Â² + sy + k
			//pe(x) = h (sy - ey) / (sx - ex)Â² (x - ex)Â² + ey - k
			NODE_SELECT = "select varNodes.id, varNodes.lon, varNodes.lat from saarland.nodes startNodes, saarland.nodes endNodes, saarland.nodes varNodes "
				+ " where startNodes.id = " + startID + "  and endNodes.id = " + endID + " "
				+ " and "
				+ " " + h + " *((endNodes.lat - startNodes.lat)/POW((endNodes.lon - startNodes.lon),2))*POW((varNodes.lon - startNodes.lon),2) + startNodes.lat  + " + k + " >= varNodes.lat "
				+ " and "
				+ " " + h + " *((startNodes.lat - endNodes.lat)/POW((startNodes.lon - endNodes.lon),2))*POW((varNodes.lon - endNodes.lon),2) + endNodes.lat - " + k + " <= varNodes.lat "
				+ " and varNodes.partofhighway = 1";
			EDGE_SELECT = "select fromNodeID, toNodeID, oneway, k_highwayspeedID, n1.lon, n1.lat, n2.lon, n2.lat from saarland.edges, saarland.nodes n1, saarland.nodes n2 "
				+ " where edges.fromNodeID = n1.ID AND edges.toNodeID = n2.ID " 
				+ " and  fromNodeID in ("
				+ " select varNodes.id from saarland.nodes startNodes, saarland.nodes endNodes, saarland.nodes varNodes "
				+ " where startNodes.id = " + startID + "  and endNodes.id = " + endID + " "
				+ " and "
				+ " " + h + "*((endNodes.lat - startNodes.lat)/POW((endNodes.lon - startNodes.lon),2))*POW((varNodes.lon - startNodes.lon),2) + startNodes.lat  + " + k + " >= varNodes.lat "
				+ " and "
				+ " " + h + "*((startNodes.lat - endNodes.lat)/POW((startNodes.lon - endNodes.lon),2))*POW((varNodes.lon - endNodes.lon),2) + endNodes.lat - " + k + " <= varNodes.lat "
				+ " and varNodes.partofhighway = 1"
				+ ")"
				+ " and toNodeID in ("
				+ " select varNodes.id from saarland.nodes startNodes, saarland.nodes endNodes, saarland.nodes varNodes "
				+ " where startNodes.id = " + startID + "  and endNodes.id = " + endID + " "
				+ " and "
				+ " " + h + " *((endNodes.lat - startNodes.lat)/POW((endNodes.lon - startNodes.lon),2))*POW((varNodes.lon - startNodes.lon),2) + startNodes.lat  + " + k + " >= varNodes.lat "
				+ " and "
				+ " " + h + " *((startNodes.lat - endNodes.lat)/POW((startNodes.lon - endNodes.lon),2))*POW((varNodes.lon - endNodes.lon),2) + endNodes.lat - " + k + " <= varNodes.lat "
				+ " and varNodes.partofhighway = 1"
				+ ")";
		}
	}

	public int[] getNodeIDs() {
		return nodeIDs;
	}

	public float[] getNodeLons() {
		return nodeLons;
	}

	public float[] getNodeLats() {
		return nodeLats;
	}

	public int[] getFromNodeIDs() {
		return fromNodeIDs;
	}

	public int[] getToNodeIDs() {
		return toNodeIDs;
	}

	public double[] getDistances() {
		return distances;
	}

	public boolean[] getOneways() {
		return oneways;
	}

	public int[] getHighwayTypes() {
		return highwayTypes;
	}
	
	
	
}
