package de.htwmaps.database.importscripts;

import java.awt.geom.Arc2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.batik.ext.awt.geom.Polygon2D;

import de.htwmaps.database.DBConnector;
/**
 * 
 * @author Stanislaw Tartakowski
 *
 */
public class UpdateStreets {

	
	public void updateStreets() throws SQLException, IOException {
		ArrayList<Polygon2D> polygons = new ArrayList<Polygon2D>();
		ArrayList<Integer> cityNodes = new ArrayList<Integer>();
		
		ResultSet allWaysStartNodes = DBConnector.getConnection().createStatement().executeQuery("SELECT node1lon, node1lat, nameValue, ways.id FROM ways, edges_all" +
																								" where startEdgeID = edges_all.id");
		ResultSet allPolyWays = DBConnector.getConnection().createStatement().executeQuery("SELECT distinct wayID FROM edges_borders");
		ResultSet allCities = DBConnector.getConnection().createStatement().executeQuery("SELECT lon, lat, name, id, is_in, cityCategory FROM cities");
		Statement allEdgesInPolyWayStatement = DBConnector.getConnection().createStatement();
		//---------Polygon
		while (allPolyWays.next()) {
			ResultSet allEdgesInPolyWay = allEdgesInPolyWayStatement.executeQuery("SELECT fromNode.lon, fromNode.lat, toNode.lon, toNode.lat from nodes fromNode, nodes toNode, edges_borders where wayID = " + allPolyWays.getInt(1) +
																										" and fromNode.id = edges_borders.node1ID and toNode.id = edges_borders.node2ID");
			ArrayList<Float> x = new ArrayList<Float>();
			ArrayList<Float> y = new ArrayList<Float>();
			while (allEdgesInPolyWay.next()) {
				x.add(allEdgesInPolyWay.getFloat(1));
				y.add(allEdgesInPolyWay.getFloat(2));
				
				x.add(allEdgesInPolyWay.getFloat(3));
				y.add(allEdgesInPolyWay.getFloat(4));
			}
			float[] xx = new float[x.size()];
			float[] yy = new float[x.size()];
			for (int i = 0; i < x.size(); i++) {
				xx[i] = x.get(i);
				yy[i] = y.get(i);
			}
			Polygon2D polygon = new Polygon2D(xx, yy, x.size());
			polygons.add(polygon);
			String city = "";
			String is_in = "";
			while (allCities.next()) {
				if (polygon.contains(allCities.getFloat(1), allCities.getFloat(2))) {
					city = allCities.getString(3);
					is_in = allCities.getString(5);
					if (!is_in.isEmpty()) {
						is_in = removeSillyTag(is_in);
					}
					cityNodes.add(allCities.getInt(4));
					break;
				}
			}
			allCities.beforeFirst();
			while(allWaysStartNodes.next()) {
				if (polygon.contains(allWaysStartNodes.getFloat(1), allWaysStartNodes.getFloat(2))) {
					System.out.println("UPDATE `ways` SET `cityName` = '" + city + "', `cityNodeID` = " + cityNodes.get(cityNodes.size() - 1) + ", `is_in` = '" + is_in + "' WHERE `ID` = " + allWaysStartNodes.getInt(4));
				}
			}
			allWaysStartNodes.beforeFirst();
		}  
		//----------Kreis
		double diameterHamlet = 0.01;
		double diameterSuburb = 0.017;
		double diameterVillage = 0.021;
		double diameterTown = 0.05;
		double diameterCity = 0.1;
		double diameter = 0.0;
		Arc2D arc = new Arc2D.Double();
		while (allCities.next()) {
			for (Polygon2D polygon : polygons) {
				if (polygon.contains(allCities.getFloat(1), allCities.getFloat(2))) {
					continue;
				}
			}
			String is_in = allCities.getString(5);
			String city = allCities.getString(3);
			if (!is_in.isEmpty()) {
				is_in = removeSillyTag(is_in);
			}
			if (allCities.getString(6).equals("hamlet")) {
				diameter = diameterHamlet;
			} else {
				if (allCities.getString(6).equals("suburb")) {
					diameter = diameterSuburb;
				} else {
					if (allCities.getString(6).equals("village")) {
						diameter = diameterVillage;
					} else {
						if (allCities.getString(6).equals("town")) {
							diameter = diameterTown;
						} else {
							if (allCities.getString(6).equals("city")) {
								diameter = diameterCity;
							} else {
								diameter = 0.003;
							}
						}
					}
				}
			}
			double centerX = allCities.getFloat(1) - (diameter / 2.0);
			double centerY = allCities.getFloat(2) - (diameter / 2.0);
			arc.setArc(centerX, centerY, diameter, diameter, 0, 360, 0);
			while(allWaysStartNodes.next()) {
				if (arc.contains(allWaysStartNodes.getFloat(1), allWaysStartNodes.getFloat(2))) {
					System.out.println("UPDATE `ways` SET `cityName` = '" + city + "', `cityNodeID` = " + cityNodes.get(cityNodes.size() - 1) + ", `is_in` = '" + is_in + "' WHERE `ID` = " + allWaysStartNodes.getInt(4));
				}
			}
			allWaysStartNodes.beforeFirst();
		}
	}
	
	private String removeSillyTag(String is_in) {
		is_in = is_in.trim();
		is_in = is_in.replace(" ", "");
		StringBuilder sb = new StringBuilder(is_in);
		int pos = 0;
		while ((pos = sb.indexOf("Europa")) != -1) {
			sb.delete(pos, getEndpos("Europa", pos, sb));
		}
		while ((pos = sb.indexOf("Europe")) != -1) {
			sb.delete(pos, getEndpos("Europe", pos, sb));
		}
		while ((pos = sb.indexOf("Germany")) != -1) {
			sb.delete(pos, getEndpos("Germany", pos, sb));
		}
		while ((pos = sb.indexOf("Bundesrepublik")) != -1) {
			sb.delete(pos, getEndpos("Bundesrepublik", pos, sb));
		}
		while ((pos = sb.indexOf("Regionalverband")) != -1) {
			sb.delete(pos, getEndpos("Regionalverband", pos, sb));
		}
		while ((pos = sb.indexOf("Deutschland")) != -1) {
			sb.delete(pos, getEndpos("Deutschland", pos, sb));
		}
		while ((pos = sb.indexOf("Stadtverband")) != -1) {
				sb.delete(pos, getEndpos("Stadtverband", pos, sb));
		}
		for (int i = 0; i < sb.length(); i++) {
			if (i == 0 && sb.charAt(i) == ',') {
				sb.delete(0, i + 1);
				i = 0;
				continue;
			}
			if (i < sb.length() - 1 && sb.charAt(i) == ',' && sb.charAt(i + 1) == ',') {
				sb.delete(i, i + 1);
			}
			if (i == sb.length() - 1 && sb.charAt(i) == ',') {
				sb.delete(i, i + 1);
			}
			
		}
		return sb.toString();
	}

	private int getEndpos(String string, int pos, StringBuilder sb) {
		for (int i = pos; i < sb.length(); i++) {
			if (sb.charAt(i) == ',') {
				return i;
			}
		}
		return sb.length();
	}

	public static void main(String[] args) throws SQLException, IOException {
		new UpdateStreets().updateStreets();
	}
}
