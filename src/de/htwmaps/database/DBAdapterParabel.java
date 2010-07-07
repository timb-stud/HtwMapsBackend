package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBAdapterRectangle {
	private final static  float h = 0.01f;
	private float rectStartNodeLon;
	private float rectStartNodeLat;
	private float rectEndNodeLon;
	private float rectEndNodeLat;
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
	
	private final static String COORD_SELECT = "SELECT lat, lon FROM nodes WHERE partofhighway = 1";
	private final static String NODE_SELECT = "SELECT id, lon, lat FROM nodes WHERE partofhighway = 1";
	private final static String EDGE_SELECT = "SELECT node1ID, node2ID, oneway, speedID, node1lon, node1lat, node2lon, node2lat FROM edges2";
	
	public DBAdapterRectangle(float startNodeLon, float startNodeLat, float endNodeLon, float endNodeLat) throws SQLException {
		setRectangle(startNodeLon, startNodeLat, endNodeLon, endNodeLat);
		expandRectangle(h);
		initNodes();
		initEdges();
	}
	
	public DBAdapterRectangle(int node1Id, int node2Id) throws SQLException{
		setRectangle(node1Id, node2Id);
		expandRectangle(h);
		initNodes();
		initEdges();
	}
	
	
	private void expandRectangle(float value){
		this.rectStartNodeLat -= value;
		this.rectStartNodeLon -= value;
		this.rectEndNodeLat += value;
		this.rectEndNodeLon += value;
	}
	
	private String buildCoordSelectStatement(int node1Id, int node2Id){
		StringBuilder sb = new StringBuilder(COORD_SELECT);
		sb.append(" AND (id = ").append(node1Id)
		.append(" OR id = ").append(node2Id).append(")");
		
		return sb.toString();
	}

	private String buildNodeSelectStatement(){
		StringBuilder sb = new StringBuilder(NODE_SELECT);
		sb.append(" AND lon >= ").append(rectStartNodeLon)
			.append(" AND lat >= ").append(rectStartNodeLat)
			.append(" AND lon  <= ").append(rectEndNodeLon)
			.append(" AND lat <= ").append(rectEndNodeLat);
		
		return sb.toString();
	}
	
	private String buildEdgeSelectStatement(){
		StringBuilder sb = new StringBuilder(EDGE_SELECT);
		sb.append(" WHERE node1lon >= ").append(rectStartNodeLon)
		.append(" AND node1lat >= ").append(rectStartNodeLat)
		.append(" AND node1lon  <= ").append(rectEndNodeLon)
		.append(" AND node1lat <= ").append(rectEndNodeLat)
		.append(" AND node2lon >= ").append(rectStartNodeLon)
		.append(" AND node2lat >= ").append(rectStartNodeLat)
		.append(" AND node2lon  <= ").append(rectEndNodeLon)
		.append(" AND node2lat <= ").append(rectEndNodeLat);
		
		return sb.toString();
	}

	private void initNodes() throws SQLException{
		int tableLength;
		PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(buildNodeSelectStatement());
		ResultSet resultSet = pStmt.executeQuery();
		resultSet.last();
		tableLength = resultSet.getRow();
		System.out.println("Nodes: " + tableLength);
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
		PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(buildEdgeSelectStatement());
		ResultSet resultSet = pStmt.executeQuery();
		pStmt = null;
		resultSet.last();
		tableLength = resultSet.getRow();
		System.out.println("Edges: " + tableLength);
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
	
	private void setRectangle(int node1Id, int node2Id) throws SQLException{
		Statement select = DBConnector.getConnection().createStatement();
		ResultSet resultSet = select.executeQuery(buildCoordSelectStatement(node1Id, node2Id));
		resultSet.next();
		float n1Lat = resultSet.getFloat(1);
		float n1Lon = resultSet.getFloat(2);
		resultSet.next();
		float n2Lat = resultSet.getFloat(1);
		float n2Lon = resultSet.getFloat(2);
		setRectangle(n1Lon, n1Lat, n2Lon, n2Lat);
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
		
		this.rectStartNodeLon = startNodeLon;
		this.rectStartNodeLat = startNodeLat;
		this.rectEndNodeLon = endNodeLon;
		this.rectEndNodeLat = endNodeLat;
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
