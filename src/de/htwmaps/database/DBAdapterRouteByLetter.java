package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import de.htwmaps.algorithm.Node;


/**
 * Klasse erwartet eine gefundene Route und erstellt aus dieser ein Liste mit 
 * den befahrenen Strassen und die dazugehoerige Laenge
 * 
 * @author CR
 * @version 01.07.10
 *
 */
public class DBAdapterRouteByLetter {

	private ArrayList<String> streetnames = null;
	private ArrayList<Double> distance = null;

	
	public DBAdapterRouteByLetter(Node[] route) throws SQLException {
		initRouteByLetter(route);
	}
	
	private void initRouteByLetter(Node[] route) throws SQLException{
		PreparedStatement pStmt;
		
		//Sicherstellen, dass kein View bereits existiert
		String dropView = "drop view if exists nodesView;";
		
		//View mit Index und Nodes aus route erstellen
		String createView = buildViewStatement(route);
		System.out.println(createView);
		
		//Join Query mit View um benoetigte Daten zu bekommen
		String execQuery = buildStreetsStatement();
		System.out.println(execQuery);
		

		pStmt = DBConnector.getConnection().prepareStatement(dropView);
		pStmt.executeUpdate();
		pStmt = null;
		pStmt = DBConnector.getConnection().prepareStatement(createView);
		pStmt.executeUpdate();
		pStmt = null;
		pStmt = DBConnector.getConnection().prepareStatement(execQuery);
		ResultSet resultSet =  pStmt.executeQuery();
		pStmt = null;
		
		resultSet.last();
		int rsSize = resultSet.getRow();
		resultSet.first();
		double dist = 0;
		String preview = null, current = null;
		streetnames = new ArrayList<String>();
		distance = new ArrayList<Double>();

		for (int i = 1; resultSet.next(); i++){
			current = resultSet.getString(2);
			
			if (preview == null)
				preview = current;
			
			if (i+1 == rsSize){
				System.out.println("\n i(-1): " + i + " RS: " + rsSize + " Route " + route.length + "\n");
				dist += route[i - 1].getDistanceTo(route[i]);
				streetnames.add(current);
				distance.add(dist);
				dist = 0;
			} else if (preview.equals(current)){
					dist += route[i - 1].getDistanceTo(route[i]);
			} else {
					streetnames.add(current);
					distance.add(dist);
					dist = 0;
			}
			preview = current;
		}
	}
	
	//muss noch umgeschrieben werden, weil sich DB Aufbau aendert
	private String buildStreetsStatementOLD() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("select edges.wayid, streetname from `nodesView` "); 
		sb.append("inner join `edges` on ");
		sb.append("nodeid = fromNodeID ");
		sb.append("inner join `streets` on ");
		sb.append("edges.wayid = streets.wayid ");
		sb.append("order by routeIndex asc; ");
		
		return sb.toString();
	}
	
	private String buildStreetsStatement() {
		StringBuilder sb = new StringBuilder();
		sb.append("select nodeID, value " +
				"from edges inner join r_way_tag on " +
				"edges.wayID = r_way_tag.wayID inner join k_tags on " +
				"tagid = k_tags.ID inner join nodesView on " +
				"nodeID = fromNodeID  xor nodeID = toNodeID " +
				"where k_tags.key = 'name' " +
				"group by nodeID " +
				"order by routeIndex;");
		
		return sb.toString();
	}

	private String buildViewStatement(Node[] route) {
		StringBuilder sb = new StringBuilder();
//		sb.append("delimiter // ");
//		sb.append("drop view if exists nodesView; ");
		sb.append("create view nodesView as ");
		
		int i= 1;
		sb.append("select " + i + " as routeIndex , " + route[i - 1] + " as nodeID");
		for (i = 2; i <= route.length; i++){
			sb.append(" union select " + i + ", " + route[i - 1]);
		}
		sb.append("; ");
//		sb.append(" delimiter ;");

		return sb.toString();
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.00");
		String text = "";
		Iterator<String> sn = streetnames.iterator();
		Iterator<Double> dist = distance.iterator();
		while(sn.hasNext()){
			text += "Strasse: " + sn.next() + "\t\tDistance: " + df.format(dist.next()) + " km \n";
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
