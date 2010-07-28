package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author Stanislaw Tartakowski
 * 
 * Diese Klasse stellt dem Suchalgorithmus Knoten aus der Datenbank bereit, 
 * die in einer von 2 Parabeln begrenzter Fläche liegen. Die Form aehnelt einer Ellipse, die Implementierung
 * ist jedoch performanter 
 *
 */
public class DBAdapterParabel{
	private float startNodeLon, startNodeLat, endNodeLon, endNodeLat;
	private float h;
	private float a; 
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
	private int[] edgeIDs;
	
	private String NODE_SELECT;
	private String EDGE_SELECT;

	private final static String COORD_SELECT = "SELECT lat, lon FROM nodes_opt WHERE ";
	
	public void prepareGraph(int node1Id, int node2Id, float a, float h) throws SQLException{
		this.a = a;
		this.h = h;
		ResultSet resultSet = DBConnector.getConnection().createStatement().executeQuery(buildCoordSelectStatement(node1Id, node2Id));
		resultSet.next();
		startNodeLat = resultSet.getFloat(1);
		startNodeLon = resultSet.getFloat(2);
		resultSet.next();
		endNodeLat = resultSet.getFloat(1);
		endNodeLon = resultSet.getFloat(2);

		setRectangle();
		initNodes();
		initEdges();
	}

	private String buildCoordSelectStatement(int node1Id, int node2Id) {
		StringBuilder sb = new StringBuilder(COORD_SELECT);
		sb.append(" (id = ").append(node1Id)
		.append(" OR id = ").append(node2Id).append(")");
		
		return sb.toString();
	}

	private void initNodes() throws SQLException{
		int tableLength;
		PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(NODE_SELECT);
		pStmt.setFloat(1, a);
		pStmt.setFloat(2, (endNodeLat - startNodeLat));
		pStmt.setFloat(3, endNodeLon - startNodeLon);
		pStmt.setFloat(4, startNodeLon);
		pStmt.setFloat(5, startNodeLat);
		pStmt.setFloat(6, h);
		pStmt.setFloat(7, a);
		pStmt.setFloat(8, (startNodeLat - endNodeLat));
		pStmt.setFloat(9, startNodeLon - endNodeLon);
		pStmt.setFloat(10, endNodeLon);
		pStmt.setFloat(11, endNodeLat);
		pStmt.setFloat(12, h);
		
		
		
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
		pStmt.setFloat(1, a);
		pStmt.setFloat(2, (endNodeLat - startNodeLat));
		pStmt.setFloat(3, endNodeLon - startNodeLon);
		pStmt.setFloat(4, startNodeLon);
		pStmt.setFloat(5, startNodeLat);
		pStmt.setFloat(6, h);
		pStmt.setFloat(7, a);
		pStmt.setFloat(8, (startNodeLat - endNodeLat));
		pStmt.setFloat(9, startNodeLon - endNodeLon);
		pStmt.setFloat(10, endNodeLon);
		pStmt.setFloat(11, endNodeLat);
		pStmt.setFloat(12, h);
		pStmt.setFloat(13, a);
		pStmt.setFloat(14, (endNodeLat - startNodeLat));
		pStmt.setFloat(15, endNodeLon - startNodeLon);
		pStmt.setFloat(16, startNodeLon);
		pStmt.setFloat(17, startNodeLat);
		pStmt.setFloat(18, h);
		pStmt.setFloat(19, a);
		pStmt.setFloat(20, (startNodeLat - endNodeLat));
		pStmt.setFloat(21, startNodeLon - endNodeLon);
		pStmt.setFloat(22, endNodeLon);
		pStmt.setFloat(23, endNodeLat);
		pStmt.setFloat(24, h);
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
			oneways[i] = resultSet.getBoolean(3);
			highwayTypes[i] = resultSet.getInt(4);
			distances[i] = resultSet.getInt(5);
			edgeIDs[i] = 0;						//TODO
		}
	}

	private void setRectangle() {
		if(startNodeLat < endNodeLat){
			//ps(x) = a (ey - sy) / (ex - sx)² (x - sx)² + sy - h
			//pe(x) = a (sy - ey) / (sx - ex)² (x - ex)² + ey + h
			NODE_SELECT = "select varNodes.id, varNodes.lon, varNodes.lat from nodes_opt varNodes "
				+ " where "
				+ " ? *(?/POW((?),2))*POW((varNodes.lon - ?),2) + ?  - ? <= varNodes.lat "
				+ " and "
				+ " ? *(?/POW((?),2))*POW((varNodes.lon - ?),2) + ? + ? >= varNodes.lat ";
			EDGE_SELECT = "select node1ID, node2ID, isoneway, speedID, length from edges_opt"
				+ " where" 
				+ " ?*((?)/POW((?),2))*POW((node1lon - ?),2) + ?  - ? <= node1lat"
				+ " and"
				+ " ?*((?)/POW((?),2))*POW((node1lon - ?),2) + ? + ? >= node1lat"
				+ " and"
				+ " ?*((?)/POW((?),2))*POW((node2lon - ?),2) + ?  - ? <= node2lat"
				+ " and"
				+ " ?*((?)/POW((?),2))*POW((node2lon - ?),2) + ? + ? >= node2lat";
		} else {
			//ps(x) = a (ey - sy) / (ex - sx)² (x - sx)² + sy + h
			//pe(x) = a (sy - ey) / (sx - ex)² (x - ex)² + ey - h
			NODE_SELECT = "select varNodes.id, varNodes.lon, varNodes.lat from nodes_opt varNodes "
				+ " where "
				+ " ? *(?/POW((?),2))*POW((varNodes.lon - ?),2) + ?  + ? >= varNodes.lat "
				+ " and "
				+ " ? *(?/POW((?),2))*POW((varNodes.lon - ?),2) + ? - ? <= varNodes.lat ";
			EDGE_SELECT = "select node1ID, node2ID, isoneway, speedID, length from edges_opt"
				+ " where" 
				+ " ?*((?)/POW((?),2))*POW((node1lon - ?),2) + ?  + ? >= node1lat"
				+ " and"
				+ " ?*((?)/POW((?),2))*POW((node1lon - ?),2) + ? - ? <= node1lat"
				+ " and"
				+ " ?*((?)/POW((?),2))*POW((node2lon - ?),2) + ?  + ? >= node2lat"
				+ " and"
				+ " ?*((?)/POW((?),2))*POW((node2lon - ?),2) + ? - ? <= node2lat";
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
	
	public int[] getEdgesIDs() {
		return edgeIDs;
	}
	
	
}
