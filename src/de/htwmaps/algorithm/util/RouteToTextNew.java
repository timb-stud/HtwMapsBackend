package de.htwmaps.algorithm.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;


import de.htwmaps.algorithm.Edge;
import de.htwmaps.algorithm.Node;
import de.htwmaps.database.DBAdapterRouteToText;

public class RouteToTextNew {
	
	private ArrayList<String> wayID = null;
	private ArrayList<String> highwayValue = null;
	private double totallength = 0.0;
	private double totaltime = 0.0;
	private double autobahn = 0.0;
	private double landstrasse = 0.0;
	private double innerOrts = 0.0;
	
	private ArrayList<TextInfos> info = null;

	public RouteToTextNew(Node[] route) {
		try {
			createInfo(route);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createInfo(Node[] route) throws SQLException {
		double dist = 0;
		long time = 0;
		ResultSet streetRS = null;
		String preview = null, current = null;
		String city = null, state = null, ref = null;
		highwayValue = new ArrayList<String>();
		wayID = new ArrayList<String>();
		
		info = new ArrayList<TextInfos>();
		
		  for(int i=route.length-1; i > 0; i--){
			  for(Edge e : route[i].getEdgeList()){
				    if(e.getSuccessor().equals(route[i-1]) || e.getPredecessor().equals(route[i-1])){
				      totallength += e.getLenght();
				      
				    //Strassennamen + distance dazu ermitteln
				      streetRS = null;
				      streetRS = DBAdapterRouteToText.getStreetnameRS(e.getWayID());
				      streetRS.first();
				      current = streetRS.getString(1);
				      
				      fillDriveOn(e);
						
						if (i == route.length-1){
							preview = current;
							wayID.add(e.getWayID() + "");
							ref = streetRS.getString(5);
							city = streetRS.getString(2);
							state = streetRS.getString(3);
						}
						
						if (preview.equals(current)){
								dist += e.getLenght();
								time = (long) (dist / 100); //muss noch angepasst werden
				    	} else {
//							highwayValue.add(streetRS.getString(4));
							wayID.add(e.getWayID() + "");
							
							//TextInfos streetname, ref, city, state, dist, time
							TextInfos ti = 
								new TextInfos(preview, ref, city, state, dist, time);
							info.add(ti);
							ti = null;
							dist = e.getLenght();
							time = (long) (dist / 100); //muss noch angepasst werden
						}
						
						if (i == 1) {
//							highwayValue.add(streetRS.getString(4));
							wayID.add(e.getWayID() + "");
							
							//TextInfos streetname, ref, city, state, dist, time
							TextInfos ti = 
								new TextInfos(preview, ref, city, state, dist, time);
							info.add(ti);
							ti = null;
						}
						
						preview = current;
						ref = streetRS.getString(5);
						city = streetRS.getString(2);
						state = streetRS.getString(3);
				 } 
			  }
		  }
	}
	
	private void fillDriveOn(Edge e) {
		//Autobahn 1
		//Landstraße 5 ,7
		//Innerorts 10,11,13
		
//		totallength =+ ;
		
		switch (e.getHighwayType()){
		      case 1:
		    	  autobahn =+ e.getLenght();
		    	  break;
		      case 5: case 7:
		    	  landstrasse =+ e.getLenght();
		    	  break;
		      case 10: case 11: case 13:
		    	  innerOrts =+ e.getLenght();
		    	  break;
		      default:
		    	  break;
	      }
		
	}

	
	private String getNextDirectionByConditions(Node fromNode, Node switchNode, Node toNode) {
		 //von unten nach oben
		if (fromNode.getLon() < switchNode.getLon())
			return (switchNode.getLat() < toNode.getLat())?"rechts":"links";
		
		//von oben nach unten
		else if (fromNode.getLon() > switchNode.getLon())
			return (switchNode.getLat() > toNode.getLat())?"rechts":"links";
		
		//von links nach rechts
		else if (fromNode.getLat() < switchNode.getLat())
			return (switchNode.getLon() > toNode.getLon())?"rechts":"links";
		
		//von rechts nach links
		else if (fromNode.getLat() > switchNode.getLat())
			return (switchNode.getLon() < toNode.getLon())?"rechts":"links";
		
		return "geradeaus";
	}
	
	@Override
	public String toString() {
		int i = 0; 

		DecimalFormat df = new DecimalFormat("0.00");
		StringBuilder sb = new StringBuilder();
		Iterator<String> wID = wayID.iterator();
		Iterator<TextInfos> tInfo = info.iterator();
		sb.append("WayID: "  + "\t\t Distance: " +  "\t Strasse: " +  "\t\t ref: " + "\t\t Ort/Stadt: " + "\t\t Bundesland: " + "\n");
		while(tInfo.hasNext()){
			sb.append(wID.next() + "\t");
			sb.append(tInfo.next().toString() + "\n");
			i++;
		}
		
		sb.append("\nAnzahl Strassen: " + i + " Gesamt Entfernung: " + df.format((totallength / 1000)) + " km");
		
		return sb.toString();
	}

	public double getTotallength() {
		return totallength;
	}
	
}
