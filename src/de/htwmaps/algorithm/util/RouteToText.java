package de.htwmaps.algorithm.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.htwmaps.algorithm.Edge;
import de.htwmaps.algorithm.Node;
import de.htwmaps.database.DBAdapterRouteToText;

public class RouteToText {

	private ArrayList<String> wayID = null;
	private double totallength = 0.0;

	private double autobahn = 0.0;
	private double landstrasse = 0.0;
	private double innerOrts = 0.0;

	private double totaltime = 0;
	private double autobahnTime = 0;
	private double landstrasseTime = 0;
	private double innerOrtstime = 0;

	private DecimalFormat df = new DecimalFormat("0.00");

	private ArrayList<TextInfos> info = null;

	public RouteToText(Node[] route) {
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
		String city = null, state = null, addition = null, selectedAdditon, direction = null;
		wayID = new ArrayList<String>();

		info = new ArrayList<TextInfos>();

		for (int i = route.length - 1; i > 0; i--) {
			for (Edge e : route[i].getEdgeList()) {
//				if (e.getSuccessor().equals(route[i - 1]) || e.getPredecessor().equals(route[i - 1])) {
					if (e.getSuccessor().equals(route[i - 1])) {
					totallength += e.getLenght();
					switchNode = route[i];
					
					streetRS = null;
					streetRS = DBAdapterRouteToText.getStreetnameRS(e.getWayID());
					streetRS.first();

					System.out.println(streetRS.getString(1) + " " + streetRS.getString(4) + " " + route[i] + "lon/lat " + route[i].getLon() + " , " + route[i].getLat());
					
					// Bestimmt ob Straßennamen oder Straßenbezeichnung (L123)
					if (!streetRS.getString(4).isEmpty()) {
						current = streetRS.getString(4);
						selectedAdditon = streetRS.getString(1);
					} else {
						current = streetRS.getString(1);
						selectedAdditon = streetRS.getString(4);
					}

					fillDriveOn(e);

					// nur beim ersten Durchlauf
					if (i == route.length - 1) {
						preview = current;
//						wayID.add(e.getWayID() + "");
						addition = selectedAdditon;
						city = streetRS.getString(2);
						state = streetRS.getString(3);
					}

					if (preview.equals(current)) {
						dist += e.getLenght();
					} else {
////						wayID.add(e.getWayID() + "");
//						System.out.println("fromNode: " + route[i+1] +  " lon/lat " + route[i+1].getLon() + " , " + route[i+1].getLat());
//						System.out.println("switchNode: " + switchNode + " lon/lat: " + switchNode.getLon() + " , " + switchNode.getLat());
//						System.out.println("toNode: " + route[i-1] + "lon/lat: " + route[i-1].getLon() + " , " + route[i-1].getLat());
						direction = getNextDirectionByConditions(route[i+1], switchNode, route[i-1]);
						TextInfos ti = new TextInfos(preview, addition, city, state, dist, switchNode, direction);
						info.add(ti);
						ti = null;
						dist = e.getLenght();
					}

					// nur bei letzten Durchlauf
					if (i == 1) {
//						wayID.add(e.getWayID() + "");
						TextInfos ti = new TextInfos(preview, addition, city, state, dist, switchNode);
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
		// Autobahn 1
		// Landstraße 5 ,7
		// Innerorts 10,11,13

		double length = e.getLenght();
		double time = length / 1000 / e.getSpeed() * 60 * 60;
		totaltime += time;

		switch (e.getHighwayType()) {
		case 1:
			autobahn += length;
			autobahnTime += time;
			break;
		case 5:
		case 7:
			landstrasse += length;
			landstrasseTime += time;
			break;
		case 10:
		case 11:
		case 13:
			innerOrts += length;
			innerOrtstime += time;
			break;
		default:
			break;
		}
	}

	
	public LinkedList<String> buildRouteInfo() {
		LinkedList<String> routeText = new LinkedList<String>();
		String text = null;
		
		for (int i = 0; i < info.size() -1 ; i++){
			if (!info.get(i).getName().trim().equals(""))
				text = "Nach " + info.get(i).getName() + " ";
			else
				text = "Dann ";
			text += info.get(i).getDirection();
			text += " in " + info.get(i+1).getName() + " abbiegen.\n";
			routeText.add(text);
			text = "";
		}
		
		return routeText;
	}
	

	private String getNextDirectionByConditions(Node fromNode, Node switchNode,Node toNode) {
		
		System.out.println("Y fromNode: " + fromNode.getLon() + " , " + fromNode.getLat());
		System.out.println("Y switchNode: " + switchNode.getLon() + " , " + switchNode.getLat());
		System.out.println("Y toNode: " + toNode.getLon() + " , " + toNode.getLat());
		
		//Kreis in 4 Teile teilen
		// LinksOben
		if ((fromNode.getLon() > switchNode.getLon()) && (fromNode.getLat() < switchNode.getLat()))
			return (switchNode.getLon() < toNode.getLon()) ? "rechts1" : "links1";
		// LinksUnten
		else if (fromNode.getLon() > switchNode.getLon() && fromNode.getLat() > switchNode.getLat())
			return (switchNode.getLat() < toNode.getLat()) ? "rechts2" : "links2";
		//RechtsOben
		else if (fromNode.getLon() < switchNode.getLon() && fromNode.getLat() < switchNode.getLat())
			return (switchNode.getLon() > toNode.getLon()) ? "rechts3" : "links3";
		//RechtsUnten
		else if (fromNode.getLon() < switchNode.getLon() && fromNode.getLat() > switchNode.getLat())
			return (switchNode.getLat() > toNode.getLat()) ? "rechts4" : "links4";
		
		//Den Fall geradeaus noch implementieren
		return "geradeaus";
	}
	
	
	private String getNextDirectionByConditionsBest(Node fromNode, Node switchNode,Node toNode) {
		
		
		
		return "gerade aus";
	}
	
	private String getNextDirectionByConditionsOld(Node fromNode, Node switchNode,Node toNode) {
		// von unten nach oben
		if (fromNode.getLon() < switchNode.getLon())
			return (switchNode.getLat() < toNode.getLat()) ? "rechts" : "links"; //t

		// von oben nach unten
		else if (fromNode.getLon() > switchNode.getLon())
			return (switchNode.getLat() > toNode.getLat()) ? "rechts" : "links";

		// von links nach rechts
		else if (fromNode.getLat() < switchNode.getLat())
			return (switchNode.getLon() > toNode.getLon()) ? "rechts" : "links";

		// von rechts nach links
		else if (fromNode.getLat() > switchNode.getLat())
			return (switchNode.getLon() < toNode.getLon()) ? "rechts" : "links";

		return "geradeaus";
	}

	private String genarateTime(double lTime) {
		DecimalFormat df = new DecimalFormat("00");
		int hours = (int) (lTime / (60 * 60));
		int minutes = (int) (lTime / 60 - (hours * 60));
		int seconds = (int) (lTime % 60);
		return (df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds));
	}

	@Override
	public String toString() {
		int i = 0;

		StringBuilder sb = new StringBuilder();
//		Iterator<String> wID = wayID.iterator();
		Iterator<TextInfos> tInfo = info.iterator();
//		sb.append("WayID: \t\t");
		sb.append("Distance: " + "\t Strasse: "
				+ "\t\t Additional: " + "\t\t Ort/Stadt: "
				+ "\t\t Bundesland: " + "\n");
		while (tInfo.hasNext()) {
//			sb.append(wID.next() + "\t");
			sb.append(tInfo.next().toString() + "\n");
			i++;
		}

		sb.append("\nAnzahl Strassen: " + i + " Gesamt Entfernung: " + df.format((totallength / 1000)) + " km " + " Gesamt Dauer: " + genarateTime(totaltime) + "\n\n");
		sb.append("Autobahn: ").append(df.format(autobahn / 1000)).append(" km Dauer: ").append(genarateTime(autobahnTime)).append("\n");
		sb.append("Landstraße: ").append(df.format(landstrasse / 1000)).append(" km Dauer: ").append(genarateTime(landstrasseTime)).append("\n");
		sb.append("Innerorts: ").append(df.format(innerOrts / 1000)).append(" km Dauer: ").append(genarateTime(innerOrtstime)).append("\n");

		return sb.toString();
	}

	public double getTotallength() {
		return totallength;
	}

	public String getTotallengthString() {
		return (totallength > 1000) ? df.format((totallength / 1000)) + " km" : df.format(totallength) + " m";
	}

	public double getAutobahn() {
		return autobahn;
	}

	public String getAutobahnString() {
		return (autobahn > 1000) ? df.format((autobahn / 1000)) + " km" : df.format(autobahn) + " m";
	}

	public double getLandstrasse() {
		return landstrasse;
	}

	public String getLandstrasseString() {
		return (landstrasse > 1000) ? df.format((landstrasse / 1000)) + " km" : df.format(landstrasse) + " m";
	}

	public double getInnerOrts() {
		return innerOrts;
	}

	public String getInnerOrtsString() {
		return (innerOrts > 1000) ? df.format((innerOrts / 1000)) + " km" : df.format(innerOrts) + " m";
	}

	public String getTotaltime() {
		return genarateTime(totaltime);
	}

	public String getAutobahnTime() {
		return genarateTime(autobahnTime);
	}

	public String getLandstrasseTime() {
		return genarateTime(landstrasseTime);
	}

	public String getInnerOrtstime() {
		return genarateTime(innerOrtstime);
	}

	public ArrayList<TextInfos> getInfo() {
		return info;
	}

}
