package de.htwmaps.algorithm.util;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.mysql.jdbc.PreparedStatement;

import de.htwmaps.algorithm.Node;
import de.htwmaps.database.DBAdapterRouteToText;

public class RouteToText {
	
	private final String STRECKE_FOLGEN = "Dem Streckenverlauf folgen bis ";
	private final String AUFFAHRT_RICHTUNG = "Nehmen Sie die Auffahrt Richtung ";
	private final String AUSFAHRT_RICHTUNG = "Nehmen Sie die Ausfahrt Richtung ";
	private final String WECHSEL_STRASSE = "wechseln Sie auf die Strasse ";
	private final String START_STRASSE = "Sie Starten auf der Strasse  ";
	private final String ZIEL= "Sie haben ihr Ziel erreicht. ";
	private final String BEI = "Bei %s %s abbiegen.";
	private final String WEITERAUF = "Weiter auf ?";
	
	
	DBAdapterRouteToText streetnames = null;

	public RouteToText(Node[] route) {
			long time = System.currentTimeMillis();
			streetnames = new DBAdapterRouteToText(route);
			System.out.println("\n RouteToText Algo: " + (System.currentTimeMillis() - time) + "ms");
			System.out.println(streetnames.toString());
	}
	
	public ArrayList<String> getStreets() {
		return streetnames.getStreetnames();
	}
	
	public ArrayList<String> getDistance() {
		DecimalFormat df = new DecimalFormat("0.00");
		ArrayList<String> distString = new ArrayList();
		
		for (double d : streetnames.getDistance()){
			if (d > 1000)
				distString.add(df.format(d * 100) + " km");
			else
				distString.add(df.format(d) + " m");
		}
		return distString;
	}
	
	
	private String getNextDirectionByConditions(Node fromNode, Node switchNode, Node toNode) {
		 //von unten nach oben
		if (fromNode.getY() < switchNode.getY())
			return (switchNode.getX() < toNode.getX())?"rechts":"links";
		
		//von oben nach unten
		else if (fromNode.getY() > switchNode.getY())
			return (switchNode.getX() > toNode.getX())?"rechts":"links";
		
		//von links nach rechts
		else if (fromNode.getX() < switchNode.getX())
			return (switchNode.getY() > toNode.getY())?"rechts":"links";
		
		//von rechts nach links
		else if (fromNode.getX() > switchNode.getX())
			return (switchNode.getY() < toNode.getY())?"rechts":"links";
		
		return "geradeaus";
	}
	
	
	private void getNextDirectionByVector() {
		
	}
	
	private String buildRouteInfo() {	
		ArrayList<String> streets = streetnames.getStreetnames();
//		ArrayList<Double> distance = streetnames.getDistance();
		PreparedStatement ps = null;
		ArrayList<String> typ = null;
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < streets.size(); i++){
			
			if (i == 0){
				sb.append(START_STRASSE + streets.get(i) );
			} else if ( i + 1 == streets.size()){
				sb.append(ZIEL);
			} else{
				sb.append("Bei " + streets.get(i) + "abbiegen.");
			}
			
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return buildRouteInfo();
	}
	
}
