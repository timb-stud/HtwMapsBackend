package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import de.htwmaps.algorithm.Node;

public class DBAdapterRouteByLetter {
	
	private ArrayList<String> streetnames;
	private ArrayList<Integer> specificNumber; //wieviele Knoten auf der Straﬂe besucht werden
	private ArrayList<Double> distance;

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
		
		StringBuilder sb = new StringBuilder("select nodes.id, streetname, count(*) from streets");
		//sb.append(" inner join ways on way.id = streets.wayid");
		sb.append(" inner join edges on streets.wayid = edges.wayid");
		sb.append(" inner join nodes on nodes.id = edges.fromNodeID");
		sb.append(" where nodes.ID in ").append("( ");
		
		for (Node node : result){
			sb.append(" " + node + " ,");
		}
		sb.append(" 0 )");// 0 da am Ende der Schleife noch ein Komma gemacht wird
		sb.append(" group by ").append("streetname;");
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
			streetnames = new ArrayList<String>();
			specificNumber = new ArrayList<Integer>();
			
			for (Node node : result){	
				resultSet.beforeFirst();
				while (resultSet.next()){
					if (resultSet.getString(1).equals(node.toString())){
						streetnames.add(resultSet.getString(2));
						specificNumber.add(resultSet.getInt(3));
						break;
					}
				}
			}
	}
	
	private void initDistance(Node[] result) {
		double dist = 0;
		distance = new ArrayList<Double>();
		for (int sn : specificNumber){
			for (int i = 0; i < sn; i++){
				dist =+ result[i].getDistanceTo(result[i+1]);
			}
			distance.add(dist);
		}
	}
	
	@Override
	public String toString() {
		String text = null;
		Iterator<String> sn = streetnames.iterator();
		Iterator<Double> dist = distance.iterator();
		while(sn.hasNext()){
			text = text + sn.next() + " " + dist.next() + "\n";
		}
		return text;
	}
	
	public ArrayList<String> getStreetnames() {
		return streetnames;
	}

	public String getStreetname(int pos) {
		return streetnames.get(pos);
	}
	
	public double getDistancePos(int pos) {
		return distance.get(pos);
	}
	
	public ArrayList<Double> getDistance() {
		return distance;
	}
}
