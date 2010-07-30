package de.htwmaps.database.importscripts;

import java.awt.geom.Arc2D;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.batik.ext.awt.geom.Polygon2D;

import de.htwmaps.database.DBConnector;
/**
 * 
 * @author Stanislaw Tartakowski
 *
 */
public class UpdateStreets {
	
	public void updateStreets() throws SQLException, IOException {
		HashSet<Integer> markedWays = new HashSet<Integer>();
		PreparedStatement ps = DBConnector.getConnection().prepareStatement("UPDATE `ways` SET `cityName` = ?, `cityNodeID` = ?, `is_in` = ? WHERE `ID` = ?");
		ResultSet allWaysNodes = DBConnector.getConnection().createStatement().executeQuery("SELECT edges_all1.node1lon, edges_all1.node1lat, edges_all2.node2lon, edges_all2.node2lat, ways.id FROM ways, edges_all edges_all1, edges_all edges_all2" +
																								" where startEdgeID = edges_all1.id and endEdgeID = edges_all2.id");
		ResultSet allPolyWays = DBConnector.getConnection().createStatement().executeQuery("SELECT distinct wayID FROM edges_borders");
		ResultSet allCities = DBConnector.getConnection().createStatement().executeQuery("SELECT lon, lat, name, id, is_in, cityCategory FROM cities");
		Statement allEdgesInPolyWayStatement = DBConnector.getConnection().createStatement();
		//----------Kreis
		double diameterHamlet = 0.01;
		double diameterSuburb = 0.017;
		double diameterVillage = 0.021;
		double diameterTown = 0.05;
		double diameterCity = 0.1;
		double diameter = 0.0;
		Arc2D arc = new Arc2D.Double();
		while (allCities.next()) {
			String is_in = allCities.getString(5);
			String city = allCities.getString(3);
			System.out.println("bearbeite " + city);
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
			while(allWaysNodes.next()) {
				if (markedWays.contains(allWaysNodes.getInt(5))) continue;
				if (arc.contains(allWaysNodes.getFloat(1), allWaysNodes.getFloat(2)) || arc.contains(allWaysNodes.getFloat(3), allWaysNodes.getFloat(4))) {
					markedWays.add(allWaysNodes.getInt(5));
					if (!is_in.isEmpty()) {
						is_in = removeSillyTag(is_in, city);
					}
					ps.setString(1, city);
					ps.setInt(2, allCities.getInt(4));
					ps.setString(3, is_in);
					ps.setInt(4, allWaysNodes.getInt(5));
					ps.executeUpdate();
				}
			}
			allWaysNodes.beforeFirst();
		}
		allCities.beforeFirst();
		//---------Polygon
		HashSet<Integer> markedCities = new HashSet<Integer>();
		markedWays.clear();
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
			while (allCities.next()) {
				if (markedCities.contains(allCities.getInt(4))) continue;
				if (polygon.contains(allCities.getFloat(1), allCities.getFloat(2))) {
					markedCities.add(allCities.getInt(4));
					String city = allCities.getString(3);
					String is_in = allCities.getString(5);
					while(allWaysNodes.next()) {
						if (markedWays.contains(allWaysNodes.getInt(5))) continue;
						if (polygon.contains(allWaysNodes.getFloat(1), allWaysNodes.getFloat(2)) || polygon.contains(allWaysNodes.getFloat(3), allWaysNodes.getFloat(4))) {
							markedWays.add(allWaysNodes.getInt(5));
							if (!is_in.isEmpty()) {
								is_in = removeSillyTag(is_in, city);
							}
							ps.setString(1, city);
							ps.setInt(2, allCities.getInt(4));
							ps.setString(3, is_in);
							ps.setInt(4, allWaysNodes.getInt(5));
							ps.executeUpdate();
						}
					}
					allWaysNodes.beforeFirst();
					break;
				}
			}
			allCities.beforeFirst();
		}  
	}
	
	public String removeSillyTag(String is_in, String city) {
		is_in = is_in.replace(", ", ",");
		StringBuilder sb = new StringBuilder(is_in);
		int pos = 0;
		
		while ((pos = sb.indexOf(city)) != -1) {
			sb.delete(getStartPos(city, pos, sb), getEndpos(city, pos, sb));
		}
		while ((pos = sb.indexOf("Europa")) != -1) {
			sb.delete(getStartPos("Europa", pos, sb), getEndpos("Europa", pos, sb));
		}
		while ((pos = sb.indexOf("Europe")) != -1) {
			sb.delete(getStartPos("Europe", pos, sb), getEndpos("Europe", pos, sb));
		}
		while ((pos = sb.indexOf("Germany")) != -1) {
			sb.delete(getStartPos("Germany", pos, sb), getEndpos("Germany", pos, sb));
		}
		while ((pos = sb.indexOf("Bundesrepublik")) != -1) {
			sb.delete(getStartPos("Bundesrepublik", pos, sb), getEndpos("Bundesrepublik", pos, sb));
		}
		while ((pos = sb.indexOf("Regionalverband")) != -1) {
			sb.delete(getStartPos("Regionalverband", pos, sb), getEndpos("Regionalverband", pos, sb));
		}
		while ((pos = sb.indexOf("Deutschland")) != -1) {
			sb.delete(getStartPos("Deutschland", pos, sb), getEndpos("Deutschland", pos, sb));
		}
		while ((pos = sb.indexOf("Stadtverband")) != -1) { 
			sb.delete(getStartPos("Stadtverband", pos, sb), getEndpos("Stadtverband", pos, sb));
		}
		while ((pos = sb.indexOf("Regierungsbezirk")) != -1) {
			sb.delete(getStartPos("Regierungsbezirk", pos, sb), getEndpos("Regierungsbezirk", pos, sb));
		}
		for (int i = 0; i < sb.length(); i++) {
			if (i == 0 && sb.charAt(i) == ',') {
				sb.delete(0, i + 1);
				i = -1;
				continue;
			}
			if (i < sb.length() - 1 && sb.charAt(i) == ',' && sb.charAt(i + 1) == ',') {
				sb.delete(i, i + 1);
				i--;
			}
			if (i == sb.length() - 1 && sb.charAt(i) == ',') {
				sb.delete(i, i + 1);
			}
		}
		
		ArrayList<String> al = new ArrayList<String>(5);
		pos = 0;
		String tmp;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == ',' || i + 1 == sb.length()) {
				if (i + 1 == sb.length()) {
					i++;
				}
				tmp = sb.substring(pos == 0 ? pos : pos + 1, i);
				if (al.contains(tmp)) {
					sb.delete(pos, i);
					i = pos;
				} else {
					al.add(tmp);
					pos = i;
				}
			}
		}
		return sb.toString();
	}

	private int getStartPos(String city, int pos, StringBuilder sb) {
		for (int i = pos; i > -1; i--) {
			if (sb.charAt(i) == ',') {
				return i + 1;
			}
		}
		return 0;
	}

	private int getEndpos(String string, int pos, StringBuilder sb) {
		for (int i = pos; i < sb.length(); i++) {
			if (sb.charAt(i) == ',') {
				return i;
			}
		}
		return sb.length();
	}

	public static void main(String[] args) {
		try {
			new UpdateStreets().updateStreets();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
