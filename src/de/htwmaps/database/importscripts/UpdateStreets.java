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
		BufferedWriter bfw = new BufferedWriter(new FileWriter(new File("qqq")));
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
						int pos = is_in.indexOf(',');
						if (pos > -1) {
							city = is_in.substring(0, pos) + ", " + city;
						} else {
							city = is_in + ", " + city;
						}
					}
					cityNodes.add(allCities.getInt(4));
					break;
				}
			}
			allCities.beforeFirst();
			while(allWaysStartNodes.next()) {
				if (polygon.contains(allWaysStartNodes.getFloat(1), allWaysStartNodes.getFloat(2))) {
					bfw.write("way id = " + allWaysStartNodes.getInt(4) + " name = " + allWaysStartNodes.getString(3) + " ort = " + city + " ort_node_ID = " + cityNodes.get(cityNodes.size() - 1) + "\r\n");
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
				int pos = is_in.indexOf(',');
				if (pos > -1) {
					city = is_in.substring(0, pos) + ", " + city;
				} else {
					city = is_in + ", " + city;
				}
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
								diameter = 0.3;
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
					bfw.write("way id = " + allWaysStartNodes.getInt(4) + " name = " + allWaysStartNodes.getString(3) + " ort = " + city + " ort_node_ID = " + cityNodes.get(cityNodes.size() - 1) + "\r\n");
				}
			}
			allWaysStartNodes.beforeFirst();
		}
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		new UpdateStreets().updateStreets();
	}
}
