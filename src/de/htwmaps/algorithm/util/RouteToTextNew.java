package de.htwmaps.algorithm.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;


import de.htwmaps.algorithm.Edge;
import de.htwmaps.algorithm.Node;
import de.htwmaps.database.DBAdapterRouteToText;

public class RouteToTextNew {
	
	private ArrayList<String> wayID = null;
	private double totallength = 0.0;
	
	private double autobahn = 0.0;
	private double landstrasse = 0.0;
	private double innerOrts = 0.0;
	
	private long totaltime = 0;
	private long autobahnTime = 0;
	private long landstrasseTime= 0;
	private long innerOrtstime = 0;
	
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
		Node switchNode;
		ResultSet streetRS = null;
		String preview = null, current = null;
		String city = null, state = null, addition = null, selectedAdditon;
		wayID = new ArrayList<String>();
		
		info = new ArrayList<TextInfos>();
		
		  for(int i=route.length-1; i > 0; i--){
			  for(Edge e : route[i].getEdgeList()){
				    if((switchNode = e.getSuccessor()).equals(route[i-1]) || (switchNode = e.getPredecessor()).equals(route[i-1])){
				      totallength += e.getLenght();
				      
				      streetRS = null;
				      streetRS = DBAdapterRouteToText.getStreetnameRS(e.getWayID());
				      streetRS.first();
				      
				      if (!streetRS.getString(4).isEmpty()){
				    	  current = streetRS.getString(4);
				    	  selectedAdditon = streetRS.getString(1);
				      } else {
				    	  current = streetRS.getString(1);
				    	  selectedAdditon = streetRS.getString(4);
				      }
				    	  
				      fillDriveOn(e);
						
						if (i == route.length-1){
							preview = current;
							wayID.add(e.getWayID() + "");
							addition = selectedAdditon;
							city = streetRS.getString(2);
							state = streetRS.getString(3);
						}
						
						if (preview.equals(current)){
								dist += e.getLenght();
				    	} else {
							wayID.add(e.getWayID() + "");
							
							//TextInfos name, ref, city, state, dist
							TextInfos ti = 
								new TextInfos(preview, addition, city, state, dist, switchNode);
							info.add(ti);
							ti = null;
							dist = e.getLenght();
						}
						
						if (i == 1) {
							wayID.add(e.getWayID() + "");
							
							//TextInfos name, ref, city, state, dist
							TextInfos ti = 
								new TextInfos(preview, addition, city, state, dist, switchNode);
							info.add(ti);
							ti = null;
						}
						
						preview = current;
						addition = selectedAdditon;
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
		
		double length = e.getLenght();
//		long time = (length / 1000 / e.getAllowedSpeed());
		long time = (long) (length / e.getSpeed());
		totaltime += time;
		
		switch (e.getHighwayType()){
		      case 1:
		    	  autobahn += length;
		    	  autobahnTime += time;
		    	  break;
		      case 5: case 7:
		    	  landstrasse += length;
		    	  landstrasseTime += time;
		    	  break;
		      case 10: case 11: case 13:
		    	  innerOrts += length;
		    	  innerOrtstime += time;
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
	
	private String longToTime(long lTime){
		DecimalFormat df = new DecimalFormat("00");
		lTime *= 1000; //ms in sekunden
		long stunde = lTime / (60*60);
		long minute = lTime / 60 - (stunde*60);
		long sekunde = lTime % 60;
		return (df.format(stunde) + ":"+ df.format(minute) + ":" + df.format(sekunde));
	}
	
	@Override
	public String toString() {
		int i = 0; 

		DecimalFormat df = new DecimalFormat("0.00");
		StringBuilder sb = new StringBuilder();
		Iterator<String> wID = wayID.iterator();
		Iterator<TextInfos> tInfo = info.iterator();
		sb.append("WayID: "  + "\t\t Distance: " +  "\t Strasse: " +  "\t\t Addizional: " + "\t\t Ort/Stadt: " + "\t\t Bundesland: " + "\n");
		while(tInfo.hasNext()){
			sb.append(wID.next() + "\t");
			sb.append(tInfo.next().toString() + "\n");
			i++;
		}
		
		sb.append("\nAnzahl Strassen: " + i + " Gesamt Entfernung: " + df.format((totallength / 1000)) + " km " + " Gesamt Dauer: " + longToTime(totaltime) + "\n\n");
		
		sb.append("Autobahn: ").append(df.format(autobahn/1000)).append(" km Dauer: ").append(longToTime(autobahnTime)).append("\n");
		sb.append("Landstraße: ").append(df.format(landstrasse/1000)).append(" km Dauer: ").append(longToTime(landstrasseTime)).append("\n");
		sb.append("Innerorts: ").append(df.format(innerOrts/1000)).append(" km Dauer: ").append(longToTime(innerOrtstime)).append("\n");
		
		return sb.toString();
	}

	public double getTotallength() {
		return totallength;
	}

	public double getAutobahn() {
		return autobahn;
	}

	public double getLandstrasse() {
		return landstrasse;
	}

	public double getInnerOrts() {
		return innerOrts;
	}

	public String getTotaltime() {
		return longToTime(totaltime);
	}

	public String getAutobahnTime() {
		return longToTime(autobahnTime);
	}

	public String getLandstrasseTime() {
		return longToTime(landstrasseTime);
	}

	public String getInnerOrtstime() {
		return longToTime(innerOrtstime);
	}

	public ArrayList<TextInfos> getInfo() {
		return info;
	}
	
}
