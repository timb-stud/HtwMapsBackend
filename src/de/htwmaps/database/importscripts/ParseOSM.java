package de.htwmaps.database.importscripts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.htwmaps.database.DBConnector;

public class ParseOSM {

    private final String READ_PATH;

    /**
     * Konstruktor
     * @param READ_PATH Pfad + Name der Datei aus welcher gelesen wird
     */
    public ParseOSM(String READ_PATH) {
    	this.READ_PATH = READ_PATH;
    	start();
    }    


    /**
     * Parst die OSM Datei und schreibt alle relevanten Daten in die Datenbank
     */
    private void start() {
    	final String SQL1 = "INSERT INTO `edges4` (`ID`, `wayID`, `startNodeID`, `endNodeID`) VALUES (NULL, ?, ?, ?)";
    	final String SQL2 = "SELECT ID FROM `edges4` WHERE `startNodeID` = ? AND `endNodeID` = ?";
    	final String SQL3 = "INSERT INTO `ways2` (`ID`, `startEdgeID`, `endEdgeID`, `nameValue`, `cityName`, `cityNodeID`, `highwayValue`, `onewayValue`, `ref`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	boolean taggeWays = false;
    	boolean oneway = false;
    	int startEdgeID = 0;
    	int endEdgeID = 0;
    	String startNodeID;
    	String endNodeID;
        String wayID = null;
        String onewayValue = "";
        String[] tags = new String[3];
        tags[0] = "";
        tags[1] = "";
        tags[2] = "";
        ResultSet rs = null;
        LinkedList<String> nd_ref = new LinkedList<String>();
        try {
        	PreparedStatement ps1 = DBConnector.getConnection().prepareStatement(SQL1);
        	PreparedStatement ps2 = DBConnector.getConnection().prepareStatement(SQL2);
        	PreparedStatement ps3 = DBConnector.getConnection().prepareStatement(SQL3);
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(READ_PATH));
            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.next() == XMLStreamConstants.START_ELEMENT) {
                	if (xmlStreamReader.getName().toString().equals("node")) {
                		//nodes werden spaeter geparst
                	}
                	if (xmlStreamReader.getName().toString().equals("way")) {
                		wayID = xmlStreamReader.getAttributeValue(0).toString();
                		taggeWays = true;
                	}
                	if (taggeWays) {
	                    if (xmlStreamReader.getLocalName().equals("nd")) {
	                    	nd_ref.add(xmlStreamReader.getAttributeValue(0).toString());
	                    }
	                    if (xmlStreamReader.getLocalName().equals("tag")) {
	                    	if (xmlStreamReader.getAttributeValue(0).equals("name")) {
	                    		tags[0] = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if ((xmlStreamReader.getAttributeValue(0).equals("highway")) && (
	                    		xmlStreamReader.getAttributeValue(1).equals("motorway") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("motorway_link") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("trunk") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("trunk_link") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("primary") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("primary_link") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("secondary") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("secondary_link") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("tertiary") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("unclassified") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("road") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("residential") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("living_street"))) {
	                    		tags[1] = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("ref")) {
	                    		tags[2] = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("oneway")) {
	                    		//moegliche oneway tags: yes/no/true
	                    		oneway = true;
	                    		onewayValue = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    }
                	}
                }
                if (xmlStreamReader.getEventType() == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("way")) {
                	if (!tags[1].equals("")) {
	                    for (int i = 0; i < nd_ref.size() - 1; i++) {
	                    	startNodeID =  nd_ref.get(i);
	                    	endNodeID = nd_ref.get(i + 1);
	                    	ps1.setString(1, wayID);
	                    	ps1.setString(2,startNodeID);
	                    	ps1.setString(3,endNodeID);
	                    	ps1.executeUpdate();
	                        if (i == 0) {
		                    	ps2.setString(1, startNodeID);
		                    	ps2.setString(2, endNodeID);
		                    	ps2.executeQuery();
		                    	rs = ps2.executeQuery();
		                    	rs.next();
		                    	startEdgeID = rs.getInt(1);
	                        }
	                        if (i == nd_ref.size() - 2) {
		                    	ps2.setString(1, startNodeID);
		                    	ps2.setString(2, endNodeID);
		                    	rs = ps2.executeQuery();
		                    	rs.next();
		                    	endEdgeID = rs.getInt(1);
	                        }
	                    }
	                    ps3.setString(1, wayID);
	                    ps3.setInt(2, startEdgeID);
	                    ps3.setInt(3, endEdgeID);
	                    ps3.setString(4, tags[0]);
	                    ps3.setString(5, "");
	                    ps3.setInt(6, 0);
	                    ps3.setString(7, tags[1]);
	                    //ps3.setBoolean(8, oneway);
	                    ps3.setString(8, onewayValue);
	                    ps3.setString(9, tags[2]);
	                    ps3.executeUpdate();
                	}
                	tags[0] = "";
                	tags[1] = "";
                	tags[2] = "";
                	oneway = false;
                	onewayValue = "";
                    nd_ref.clear();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}