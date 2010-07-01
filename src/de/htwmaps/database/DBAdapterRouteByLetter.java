package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import de.htwmaps.algorithm.Node;

public class DBAdapterRouteByLetter {

	private ArrayList<String> streetnames = null;
	private ArrayList<Double> distance = null;

	
	public DBAdapterRouteByLetter(Node[] route) throws SQLException {
		initRouteByLetter(route);
	}
	
	private void initRouteByLetter(Node[] route) throws SQLException{
		PreparedStatement pStmt;
		
//		//Sicherstellen, dass kein View bereits existiert
//		String dropView = "drop view if exists nodesView;";
		
		//View mit Index und Nodes aus route erstellen
		String createView = buildViewStatement(route);
		
		//Join Query mit View um benoetigte Daten zu bekommen
		String execQuery = buildStreetsStatement(route);
		
		
//		pStmt = DBConnector.getConnection().prepareStatement(dropView);
//		pStmt.executeQuery();
//		pStmt = null;
		pStmt = DBConnector.getConnection().prepareStatement(createView);
		pStmt.executeQuery();
		pStmt = null;
		pStmt = DBConnector.getConnection().prepareStatement(execQuery);
		ResultSet resultSet =  pStmt.executeQuery();
		pStmt = null;
		
		resultSet.last();
		int rsSize = resultSet.getRow();
		resultSet.first();
		double dist = 0;
		String preview = null, current = null;
		
		for (int i = 1; resultSet.next(); i++){
			current = resultSet.getString(3);
			if (preview == null)
				preview = current;
			
			if (i == rsSize){
				dist =+ route[i - 1].getDistanceTo(route[i]);
				streetnames.add(current);
				distance.add(dist);
				dist = 0;
			} else if (preview == current){
					dist =+ route[i - 1].getDistanceTo(route[i]);
			} else {
					streetnames.add(current);
					distance.add(dist);
					dist = 0;
			}
			preview = current;
		}
	}
	
	//muss noch umgeschrieben werden, weil sich DB Aufbau aendert
	private String buildStreetsStatement(Node[] route) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("select * from nodesView "); 
		sb.append("inner join Nodes on ");
		sb.append("nodeid = id ");
		sb.append("sorted by index; ");
		
		return sb.toString();
	}

	private String buildViewStatement(Node[] route) {
		StringBuilder sb = new StringBuilder("delimiter // ");
		sb.append("drop view if exists nodesView; ");
		sb.append("create view nodesView as ");
		
		int i= 1;
		sb.append("select " + i + " as index , " + route[i - 1] + " as nodeID");
		for (i = 2; i < route.length; i++){
			sb.append(" union select " + i + ", " + route[i - 1]);
		}
		sb.append("; delimiter ;");

		return sb.toString();
	}
	
	@Override
	public String toString() {
		String text = "";
		Iterator<String> sn = streetnames.iterator();
		Iterator<Double> dist = distance.iterator();
		while(sn.hasNext()){
			text = text + sn.next() + " " + dist.next() + "\n";
		}
		return text;
	}
	
}
