package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DBAdapterRectangle {
	private DBConnector dbConnector;
	private float rectangleStartNodeLon;
	private float rectangleStartNodeLat;
	private float rectangleEndNodeLon;
	private float rectangleEndNodeLat;
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
	
	public DBAdapterRectangle(float startNodeLon, float startNodeLat, float endNodeLon, float endNodeLat) {
		dbConnector = DBConnector.getInstance();
		setRectangle(startNodeLon, startNodeLat, endNodeLon, endNodeLat);
	}

	private void initNodes() throws SQLException{
		int tableLength;
		PreparedStatement pStmt = dbConnector.con.prepareStatement("select ID, lon, lat from nodes where partofhighway = 1");
		ResultSet resultSet = pStmt.executeQuery();
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
		PreparedStatement pStmt = dbConnector.con.prepareStatement("select fromNodeID, toNodeID, length1, oneway, k_highwayspeedID from edges");
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
			distances[i] = resultSet.getFloat(3);
			oneways[i] = resultSet.getBoolean(4);
			highwayTypes[i] = resultSet.getInt(5);
		}
	}
	
	private void setRectangle(float startNodeLon, float startNodeLat, float endNodeLon, float endNodeLat) {
		if((startNodeLon > endNodeLon && startNodeLat > endNodeLat) || (startNodeLon > endNodeLon && startNodeLat < endNodeLat) ){
			float buffer = startNodeLon;
			startNodeLon = endNodeLon;
			endNodeLon = buffer;
			buffer = startNodeLat;
			startNodeLat = endNodeLat;
			endNodeLat = buffer;
		}
		
		if(startNodeLon < endNodeLon && startNodeLat > endNodeLat){
			float buffer = startNodeLat;
			startNodeLat = endNodeLat;
			endNodeLat = buffer;
		}
		
		this.rectangleStartNodeLon = startNodeLon;
		this.rectangleStartNodeLat = startNodeLat;
		this.rectangleEndNodeLon = endNodeLon;
		this.rectangleEndNodeLat = endNodeLat;
	}
	
	
}
