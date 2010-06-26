package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.algorithm.Node;

public class DBAdapterRouteByLetter {
	
	private String[] streetnames;
	private int[] specificNumber; //wieviele Knoten auf der Straﬂe besucht werden
	private double[] distance;

	private final static String STREETS_SELECT = "select streetname, count(*) from streets";
	
	public DBAdapterRouteByLetter(Node[] result) throws SQLException {	
		streetnames = null;
		specificNumber = null;
		distance = null;
		
		initStreetnames(result);
		initDistance(result);
	}
	
	private String buildStreetnamesSelectStatement(Node[] result) {
		
//		select count(streetname)
//		from nodes inner join edges on
//		nodes.id = edges.fromNodeID
//		inner join ways on
//		edges.wayID = ways.ID
//		inner join streets on
//		ways.ID = streets.wayID;
		
//		select streetname, count(*) 
//		from streets inner join edges on way.id = edges.wayid 
//		inner join nodes on nodes.id = edges.fromNodeID 
//		where nodes.ID in  (  273184315 , 334539283 , ... , 274026832 , 0 ) 
//		group by streetname;
		
		StringBuilder sb = new StringBuilder(STREETS_SELECT);
		//sb.append(" inner join ways on way.id = streets.wayid");
		sb.append(" inner join edges on streets.wayid = edges.wayid");
		sb.append(" inner join nodes on nodes.id = edges.fromNodeID");
		sb.append(" where nodes.ID in ").append(" ( ");
		
		for (Node node : result){
			sb.append(" " + node + " ,");
		}
		
		sb.append(" 0 )"); // 0 da am Ende der Schleife noch ein Komma gemacht wird
		sb.append(" group by ").append("streetname;");

//		System.out.println(sb.toString());
		return sb.toString();
	}
	

	private void initStreetnames(Node[] result) throws SQLException {
		int tableLength;
		PreparedStatement pStmt = DBConnector.getConnection().prepareStatement(buildStreetnamesSelectStatement(result));
		ResultSet resultSet = pStmt.executeQuery();
		pStmt = null;
		resultSet.last();
		tableLength = resultSet.getRow();
		resultSet.beforeFirst();
		streetnames = new String [tableLength];
		specificNumber = new int [tableLength];
		
		for (int i = 0; resultSet.next(); i++){
			streetnames[i] = resultSet.getString(1);
			specificNumber[i] = resultSet.getInt(2);
		}
	}
	
	private void initDistance(Node[] result) {
		for (int sn : specificNumber){
			for (int i = 0; i < sn; i++){
				System.out.println("test " + result[i].getDistanceTo(result[i+1]));
				distance[i] =+ result[i].getDistanceTo(result[i+1]);
			}
		}
	}
	
	@Override
	public String toString() {
		String text = "";

		for (int i = 0; i < streetnames.length; i++){
//			text = text + streetnames[i]+ "\n"
			text = text + streetnames[i] + " " + distance[0] + "\n";
		}
		return text;
	}
	
	public String[] getStreetnames() {
		return streetnames;
	}

	public String getStreetname(int pos) {
		return streetnames[pos];
	}
	
	public double getDistancePos(int pos) {
		return distance[pos];
	}
	
	public double[] getDistance() {
		return distance;
	}
}
