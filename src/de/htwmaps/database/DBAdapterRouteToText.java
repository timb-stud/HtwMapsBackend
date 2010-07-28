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
public class DBAdapterRouteToText {

	private ArrayList<String> streetnames = null;
	private ArrayList<Double> distance = null;
	private ArrayList<Node> nodeArray = null;

	
	public DBAdapterRouteToText(Node[] route) {
		try {
			initRouteByLetter(route);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initRouteByLetter(Node[] route) throws SQLException{
		PreparedStatement pStmt;
		
		//Sicherstellen, dass kein View bereits existiert
		String dropView = "drop view if exists nodesView;";
		
		long time = System.currentTimeMillis();
		//View mit Index und Nodes aus route erstellen
		String createView = buildViewStatement(route);
//		System.out.println(createView);
		System.out.println("Dauer Create: " + (System.currentTimeMillis() - time));
		
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
		
		calcStreetsDist(route, resultSet);
	}
	
	private void calcStreetsDist(Node[] route, ResultSet resultSet) throws SQLException {
		double dist = 0;
		String preview = null, current = null;
		streetnames = new ArrayList<String>();
		distance = new ArrayList<Double>();
		nodeArray = new ArrayList<Node>();


		resultSet.beforeFirst();
		while (resultSet.next()){
			current = resultSet.getString(2);
			
			if (preview == null)
				preview = current;

			if (resultSet.isLast()){
				dist += resultSet.getDouble(3);
				streetnames.add(preview);
				distance.add(dist);
			}else if (preview.equals(current)){
					dist += resultSet.getDouble(3);
			} else {
//					System.out.println(current + " " + dist);
					streetnames.add(preview);
					distance.add(dist);
					dist = resultSet.getDouble(3);
			}
			preview = current;
		}
	}
	
	private String buildViewStatement(Node[] route) {
		int i= 1;
		StringBuilder sb = new StringBuilder();
		
		sb.append("create view nodesView as ");
		sb.append("select " + i + " as routeIndex , " + route[i - 1] + " as node1ID, " + route[i] + " as node2ID");
		for (i = 2; i < route.length; i++){
			sb.append(" union select " + i + ", " + route[i - 1] + ", " + route[i]);
		}
		sb.append("; ");
		
		return sb.toString();
	}
	
	private String buildStreetsStatement() {
		StringBuilder sb = new StringBuilder();
		sb.append("select edges2.ID, streetname, length " +
				"from nodesView inner join edges2 on " +
				"((nodesView.node1ID = edges2.node1ID " +
				"and " +
				"nodesView.node2ID = edges2.node2ID) " +
				"or " +
				"(nodesView.node2ID = edges2.node1ID " +
				"and " +
				"nodesView.node1ID = edges2.node2ID)) " +
				"inner join streets on " +
				"edges2.wayID = streets.wayID " +
				"group by edges2.ID " +
				"order by routeIndex;");
		
		return sb.toString();
	}

	
	@Override
	public String toString() {
		int i = 0; 
		double gDist = 0, h = 0;
		
		DecimalFormat df = new DecimalFormat("0.00");
		StringBuilder sb = new StringBuilder();
		Iterator<String> sn = streetnames.iterator();
		Iterator<Double> dist = distance.iterator();
		while(sn.hasNext()){
			sb.append("Distance: " + df.format(h = (dist.next())) + " m " + "\t Strasse: " + sn.next() + "\n");
			i++;
			gDist += h;
		}
		
		sb.append("\nAnzahl Strassen: " + i + " Gesamt Entfernung: " + df.format(gDist) + " m");
		
		return sb.toString();
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
