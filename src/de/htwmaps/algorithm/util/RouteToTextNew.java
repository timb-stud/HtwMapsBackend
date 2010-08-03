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
	
	private ArrayList<Double> distance = null;
	private ArrayList<Integer> wayID = null;
	private ArrayList<String> streetnames = null;
	private ArrayList<String> highwayValue = null;
	private ArrayList<String> ref = null;
	private ArrayList<String> state = null;
	private ArrayList<String> city = null;
	private double totallength = 0.0;
	

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
		ResultSet streetRS = null;
		String preview = null, current = null;
		highwayValue = new ArrayList<String>();
		ref = new ArrayList<String>();
		streetnames = new ArrayList<String>();
		state = new ArrayList<String>();
		city = new ArrayList<String>();
		wayID = new ArrayList<Integer>();
		distance = new ArrayList<Double>();
		
		System.out.println("Anzahl Nodes: " + route.length);
		int n =0;
		  for(int i=route.length-1; i > 0; i--){
			  System.out.println(route[i] + " Edges Liste: " + route[i].getEdgeList());
			  for(Edge e : route[i].getEdgeList()){
				    if(e.getSuccessor().equals(route[i-1]) || e.getPredecessor().equals(route[i-1])){
				      totallength += e.getLenght();
				      
				      n++;
				      
				      fillDriveOn(e);
				      
				    //Strassennamen + distance dazu ermitteln
				      streetRS = null;
				      streetRS = DBAdapterRouteToText.getStreetnameRS(e.getWayID());
				      streetRS.first();
				      current = streetRS.getString(1);
						
						if (preview == null)
							preview = current;
		
						if (preview.equals(current)){
								dist += e.getLenght();
						} else {
								streetnames.add(preview);
								distance.add(dist);
								city.add(streetRS.getString(2));
								state.add(streetRS.getString(3));
								highwayValue.add(streetRS.getString(4));
								ref.add(streetRS.getString(5));
								dist = e.getLenght();
								
								if (!(streetnames.isEmpty())) //nur zum Test hinzugefuegt
									wayID.add(e.getWayID());
						}
						preview = current;
				 } else {
					 System.out.println(e.getSuccessor().toString() + " " + route[i-1].toString());
				 }
			  }
		  }
		  System.out.println("Besuchte Kanten: " + n);
	}
	
	private void fillDriveOn(Edge e) {
		//motorway 1
		//motorway_link
		//trunk 1
		
		
		switch (e.getHighwayType()){
		      case 1:
		    	  double i =+ e.getLenght();
		    	  break;
		      case 2:
		    	  break;
		      case 3:
		    	  break;
		      case 4:
		    	  break;
		      case 5:
		    	  break;
		      case 6:
		    	  break;
		      case 7:
		    	  break;
		      case 8:
		    	  break;
		      case 9:
		    	  break;
		      case 10:
		    	  break;
		      case 11:
		    	  break;
		      case 12:
		    	  break;
		      case 13:
		    	  break;
		      default:
		    	  break;
	      }
		
	}

	private void fillStreetnames() {

	}
	
	@Override
	public String toString() {
		int i = 0; 
		
		DecimalFormat df = new DecimalFormat("0.00");
		StringBuilder sb = new StringBuilder();
		Iterator<String> sn = streetnames.iterator();
		Iterator<String> bundeland = state.iterator();
		Iterator<String> stadt = city.iterator();
		Iterator<Integer> wID = wayID.iterator();
		Iterator<Double> dist = distance.iterator();
		while(sn.hasNext()){
			sb.append("WayID + 1: " + wID.next() + "\t Distance: " + df.format(dist.next()) + " km " 
					+ "\t Strasse: " + sn.next() + "\t Ort/Stadt: " + stadt.next() + "\t Bundesland: " + bundeland.next() + "\n");
			i++;
		}
		
		sb.append("\nAnzahl Strassen: " + i + " Gesamt Entfernung: " + df.format(totallength) + " km");
		
		return sb.toString();
	}

	public ArrayList<Double> getDistance() {
		return distance;
	}
	
	public ArrayList<String> getDistanceText() {
		DecimalFormat df = new DecimalFormat("0.00");
		ArrayList<String> distString = new ArrayList();
		
		for (double d : distance){
			if (d > 1)
				distString.add(df.format(d) + " km");
			else
				distString.add(df.format(d * 1000) + " m");
		}
		return distString;
	}


	public ArrayList<String> getStreetnames() {
		return streetnames;
	}

	public double getTotallength() {
		return totallength;
	}
	
}
