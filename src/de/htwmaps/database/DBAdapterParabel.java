package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.algorithm.Node;


public class DBAdapterParabel{
	private int startID, endID;
	private float startNodeLon, startNodeLat, endNodeLon, endNodeLat;
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
		this.startNodeLat = startNodeLat;
		this.startNodeLon = startNodeLon;
		this.endNodeLat = endNodeLat;
		this.endNodeLon = endNodeLon;
		this.startID = startID;
		this.endID = endID;
		setRectangle();
		initNodes();
		initEdges();
	}
	

	private void initNodes() throws SQLException{
		int tableLength;
		PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(NODE_SELECT);
		pStmt.setFloat(1, (endNodeLat - startNodeLat));
		pStmt.setFloat(2, endNodeLon - startNodeLon);
		pStmt.setFloat(3, startNodeLon);
		pStmt.setFloat(4, startNodeLat);
		pStmt.setFloat(5, (startNodeLat - endNodeLat));
		pStmt.setFloat(6, startNodeLon - endNodeLon);
		pStmt.setFloat(7, endNodeLon);
		pStmt.setFloat(8, endNodeLat);
		
		
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
		System.out.println(tableLength);
		
		for (int i = 0; resultSet.next(); i++){
			nodeIDs[i] = resultSet.getInt(1);
			nodeLons[i] = resultSet.getFloat(2);
			nodeLats[i] = resultSet.getFloat(3);
		}
	}

	private void initEdges() throws SQLException{
		int tableLength;
		PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(EDGE_SELECT);
		pStmt.setFloat(1, (endNodeLat - startNodeLat));
		pStmt.setFloat(2, endNodeLon - startNodeLon);
		pStmt.setFloat(3, startNodeLon);
		pStmt.setFloat(4, startNodeLat);
		pStmt.setFloat(5, (startNodeLat - endNodeLat));
		pStmt.setFloat(6, startNodeLon - endNodeLon);
		pStmt.setFloat(7, endNodeLon);
		pStmt.setFloat(8, endNodeLat);
		pStmt.setFloat(9, (endNodeLat - startNodeLat));
		pStmt.setFloat(10, endNodeLon - startNodeLon);
		pStmt.setFloat(11, startNodeLon);
		pStmt.setFloat(12, startNodeLat);
		pStmt.setFloat(13, (startNodeLat - endNodeLat));
		pStmt.setFloat(14, startNodeLon - endNodeLon);
		pStmt.setFloat(15, endNodeLon);
		pStmt.setFloat(16, endNodeLat);
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
		System.out.println(tableLength);
		
		for (int i = 0; resultSet.next(); i++){
			fromNodeIDs[i] = resultSet.getInt(1);
			toNodeIDs[i] = resultSet.getInt(2);
			distances[i] = 0.0;
			oneways[i] = resultSet.getBoolean(3);
			highwayTypes[i] = resultSet.getInt(4);
		}
	}

	private void setRectangle() {
		float h = 0.9f;
		float k = 0.02f;
		if(startNodeLon < endNodeLon && startNodeLat < endNodeLat || startNodeLon > endNodeLon && startNodeLat < endNodeLat){
			//ps(x) = h (ey - sy) / (ex - sx)Â² (x - sx)Â² + sy - k
			//pe(x) = h (sy - ey) / (sx - ex)Â² (x - ex)Â² + ey + k
			NODE_SELECT = "select varNodes.id, varNodes.lon, varNodes.lat from saarland.nodes varNodes "
				+ " where "
				+ " 0.9 *(?/POW((?),2))*POW((varNodes.lon - ?),2) + ?  - 0.009 <= varNodes.lat "
				+ " and "
				+ " 0.9 *(?/POW((?),2))*POW((varNodes.lon - ?),2) + ? + 0.009 >= varNodes.lat "
				+ " and varNodes.partofhighway = 1";
			EDGE_SELECT = "select node1ID, node2ID, oneway, speedID from saarland.edges2"
			+ " where" 
			+ " 0.9*((?)/POW((?),2))*POW((node1lon - ?),2) + ?  - 0.009 <= node1lat"
			+ " and"
			+ " 0.9*((?)/POW((?),2))*POW((node1lon - ?),2) + ? + 0.009 >= node1lat"
			+ " and"
			+ " 0.9*((?)/POW((?),2))*POW((node2lon - ?),2) + ?  - 0.009 <= node2lat"
			+ " and"
			+ " 0.9*((?)/POW((?),2))*POW((node2lon - ?),2) + ? + 0.009 >= node2lat";
		} else {
			//ps(x) = h (ey - sy) / (ex - sx)Â² (x - sx)Â² + sy + k
			//pe(x) = h (sy - ey) / (sx - ex)Â² (x - ex)Â² + ey - k
			NODE_SELECT = "select varNodes.id, varNodes.lon, varNodes.lat from saarland.nodes varNodes "
				+ " where "
				+ " 0.9 *(?/POW((?),2))*POW((varNodes.lon - ?),2) + ?  + 0.009 >= varNodes.lat "
				+ " and "
				+ " 0.9 *(?/POW((?),2))*POW((varNodes.lon - ?),2) + ? - 0.009 <= varNodes.lat "
				+ " and varNodes.partofhighway = 1";
			EDGE_SELECT = "select node1ID, node2ID, oneway, speedID from saarland.edges2"
			+ " where" 
			+ " 0.9*((?)/POW((?),2))*POW((node1lon - ?),2) + ?  + 0.009 >= node1lat"
			+ " and"
			+ " 0.9*((?)/POW((?),2))*POW((node1lon - ?),2) + ? - 0.009 <= node1lat"
			+ " and"
			+ " 0.9*((?)/POW((?),2))*POW((node2lon - ?),2) + ?  + 0.009 >= node2lat"
			+ " and"
			+ " 0.9*((?)/POW((?),2))*POW((node2lon - ?),2) + ? - 0.009 <= node2lat";
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
