package de.htwmaps.database.importscripts;

import java.awt.geom.Arc2D;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import de.htwmaps.database.DBConnector;

/**
 * @author Stanislaw Tartakowski
 * @coAuthor Yassir Klos (Strassen werden jetzt direkt in die DB geschrieben, und nicht erst in eine Datei)
 * Hilfsklasse zum Befuellen der Datenbank
 *
 * Diese Klasse markiert alle Strassen auf der Datenbank mit ihrem zugehoerigen Ort. Der Ort wird ueber einen Kreisradius und einen zentralen Punkt definiert.
*/ 
public class UpdateStreets {
	private double diameter;
	private Statement cityNodeStatement, everyWayStartNodeStatement, everyCityNodeStatement;
	private String place;
	
	/*
	 * diameter ist der Durchmesser (in km) des Kreises(Ortes) in dem alle Wege erfasst werden sollen
	 */
	public UpdateStreets(String place, double diameter) throws Exception {
		this.place = place;
		this.diameter = 0.0099 * diameter;
		cityNodeStatement = DBConnector.getConnection().createStatement();
		everyWayStartNodeStatement = DBConnector.getConnection().createStatement();
		everyCityNodeStatement = DBConnector.getConnection().createStatement();
	}
	
	/*
	 * Diese Methode prüft alle Ways die es in der DB gibt, ob diese in einem bestimmten Kreis(Ort) liegen und schreibt das ergebnis in eine datei.
	 */
	public void writeEveryWayToFile() throws Exception {
		PreparedStatement ps = DBConnector.getConnection().prepareStatement("INSERT INTO streets (ID, wayID, streetname, nodeID, cityname) VALUES (null, ?, ?, ?, ?)");
		Arc2D arc = new Arc2D.Double();
		ResultSet everyCityNode = everyCityNodeStatement.executeQuery("select nodes.id from"
																	+ " nodes, r_node_tag, k_tags"
																	+ " where nodes.id = r_node_tag.nodeID and"
																	+ " r_node_tag.tagID = k_tags.id and"
																	+ " k_tags.key = 'place' and k_tags.value = '" + place + "'");
		while (everyCityNode.next()) {
			try {
				ResultSet cityNode = cityNodeStatement.executeQuery("select lon, lat, value"
																+ " from nodes, r_node_tag, k_tags"
																+ " where nodes.id = " + everyCityNode.getInt(1)
																+ " and nodes.id = r_node_tag.nodeID and"
																+ " r_node_tag.tagID = k_tags.ID and k_tags.key = 'name'");
				cityNode.next();
				double centerX = cityNode.getFloat(1) - (diameter / 2.0);
				double centerY = cityNode.getFloat(2) - (diameter / 2.0);
				arc.setArc(centerX, centerY, diameter, diameter, 0, 360, 0);
				/*
				 * alle nicht geschloßenen wege mit namen.
				 */
				ResultSet everyWayStartNode = everyWayStartNodeStatement.executeQuery("select lon, lat, ways.id, k_tags.value from `db1057229-2`.nodes, `db1057229-2`.ways, `db1057229-2`.r_way_tag, k_tags where ways.id in (SELECT ways.ID FROM ways, r_way_tag, k_tags WHERE ways.ID = r_way_tag.wayID AND k_tags.ID = r_way_tag.tagID AND k_tags.key = 'highway' AND k_tags.value IN ('motorway','motorway_link','trunk','trunk_link','primary','primary_link','secondary','secondary_link','tertiary','unclassified','road','residential','living_street')) and nodes.id = ways.startNodeID and ways.id = r_way_tag.wayID and r_way_tag.tagID = k_tags.ID and k_tags.key = 'name' and ways.startNodeID != ways.endNodeID");

				while (everyWayStartNode.next()) {
					if (arc.contains(everyWayStartNode.getFloat(1), everyWayStartNode.getFloat(2))) {
						ps.setInt(1, everyWayStartNode.getInt(3));
						ps.setString(2, everyWayStartNode.getString(4));
						ps.setInt(3, everyCityNode.getInt(1));
						ps.setString(4, cityNode.getString(3));
						ps.executeUpdate();
					}
				}
			} catch (Exception e) {
				System.out.println("fehler");
				continue;
			}
		}
		ps.close();
	}
	
	public static void main() throws Exception {
		/*
		 * 
			place=city 		Node 	10km
			place=town 		Node 	5km
			place=village 	Node 	2km
			place=hamlet 	Node 	0.7km
			place=suburb 	Node 	1km
		 */
		String typ = "suburb";
		double diameter = 1;
		
		UpdateStreets w = new UpdateStreets(typ, diameter);
		w.writeEveryWayToFile();
	}
}
