package de.htwmaps.algorithm.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import de.htwmaps.algorithm.Edge;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.ShortestPathAlgorithm;
import de.htwmaps.database.DBAdapterRouteToText;

public class RouteToText {

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

	public RouteToText(Node[] route, Edge[] edges) {
		try {
			createInfo(route, edges);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createInfo(Node[] route, Edge[] edge) throws SQLException {
		LinkedList<Edge> edgeList = new LinkedList<Edge>();
		double dist = 0;
		Node switchNode;
		ResultSet streetRS = null;
		String preview = null, current = null;
		String city = null, state = null, addition = null, selectedAdditon, direction = null;

		info = new ArrayList<TextInfos>();

		for (int i = route.length - 1; i > 0; i--) {
					totallength += edge[i].getLenght();
					switchNode = route[i];
					
					streetRS = null;
					streetRS = DBAdapterRouteToText.getStreetnameRS(edge[i].getWayID());
					streetRS.first();

					// Bestimmt ob Straßennamen oder Straßenbezeichnung (L123)
					if (!(i == 1) && (!streetRS.getString(4).isEmpty())) {
						current = streetRS.getString(4);
						selectedAdditon = streetRS.getString(1);
					} else {
						current = streetRS.getString(1);
						selectedAdditon = streetRS.getString(4);
						System.out.println(current);
					}

					fillDriveOn(edge[i]);

					// nur beim ersten Durchlauf
					if (i == route.length - 1) {
						preview = current;
						addition = selectedAdditon;
						city = streetRS.getString(2);
						state = streetRS.getString(3);
					}

					if (preview.equals(current)) {
						edgeList.add(edge[i]);
						dist += edge[i].getLenght();
					} else {
						direction = getNextDirectionByConditions(route[i+1], switchNode, route[i-1]);
						TextInfos ti = new TextInfos(preview, addition, city, state, dist, edgeList, direction);
						info.add(ti);
						ti = null;
						edgeList.clear();
						edgeList.add(edge[i]);
						dist = edge[i].getLenght();
					}

					// nur bei letzten Durchlauf
					if (i == 1) {
						edgeList.add(edge[i]);
						TextInfos ti = new TextInfos(current, selectedAdditon, city, state, dist, edgeList, direction);
						System.out.println("Ziel: " + ti);
						info.add(ti);
						ti = null;
						edgeList.clear();
					}

					preview = current;
					addition = selectedAdditon;
					city = streetRS.getString(2);
					state = streetRS.getString(3);
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
		StringBuffer sb = new StringBuffer();

		routeText.add("Sie starten in folgdender Straße: " + info.get(0).getName());
		for (int i = 0; i < info.size() -1 ; i++){
			if (info.get(i).getEdgeList().getLast().getHighwayType() != 1 && info.get(i+1).getEdgeList().getLast().getHighwayType() == 1){
				sb.append("Fahren Sie nach ").append(info.get(i).getName()).append("");
				sb.append(" auf die Autobahn ").append(info.get(i+1).getName());
			} else {
				if (!info.get(i).getName().trim().equals(""))
					sb.append("Nach ").append(info.get(i).getName()).append(" ");
				else
					sb.append("Dann ");
				sb.append(info.get(i).getDirection());
				if (!info.get(i+1).getName().trim().equals(""))
					sb.append(" in " + info.get(i+1).getName());
				else
					sb.append(" in die nächste Straße");
				sb.append(" abbiegen.");
			}
			routeText.add(sb.toString());
			sb.setLength(0);
		}
		
		routeText.add("Sie haben Ihr Ziel erreicht");
		
		return routeText;
	}

	private String getNextDirectionByConditions(Node fromNode, Node switchNode,Node toNode) {
		Point2D.Double f = new Point2D.Double(fromNode.getLon(), fromNode.getLat());
		Point2D.Double s = new Point2D.Double(switchNode.getLon(), switchNode.getLat());
		Point2D.Double t = new Point2D.Double(toNode.getLon(), toNode.getLat());
		
		double steigungFS = Math.abs(getSlope(f, s));
		double steigungST = Math.abs(getSlope(s, t));
		
		if (Math.abs(steigungFS - steigungST) < (0.25)) {
			return "geradeaus";
		} else {
			Line2D.Double l = new Line2D.Double(f, s);
			switch (l.relativeCCW(t)) {
				case 1:
					return "rechts";
				case-1:
					return "links";
				default: return " ";
			}
		}
	}

	/*
	 * Gibt die Steigung zwischen 2 punkten zurueck
	 */
	public double getSlope(Point2D startPunkt, Point2D endPunkt) {
		return (endPunkt.getY() - startPunkt.getY()) / (endPunkt.getX() - startPunkt.getX());
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
		Iterator<TextInfos> tInfo = info.iterator();
		sb.append("Distance: " + "\t Strasse: "
				+ "\t\t Additional: " + "\t\t Ort/Stadt: "
				+ "\t\t Bundesland: " + "\n");
		while (tInfo.hasNext()) {
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
