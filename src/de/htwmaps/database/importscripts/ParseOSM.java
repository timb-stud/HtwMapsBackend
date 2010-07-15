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
    	final String SQL1 = "INSERT INTO `nodes` (`ID`, `lat`, `lon`, `partOfHighway`) VALUES (?, ?, ?, 0)";
    	final String SQL2 = "INSERT INTO `cities` (`ID`, `lat`, `lon`, `name`) VALUES (?, ?, ?, ?)";
    	final String SQL3 = "INSERT INTO `edges_all` (`ID`, `node1ID`, `node1lat`, `node1lon`, `node2ID`, `node2lat`, `node2lon`, `wayID`, `isOneway`, `speedID`, `length`, `visited`) VALUES (NULL, ?, NULL, NULL, ?, NULL, NULL, ?, ?, ?, NULL, 0)";
    	final String SQL4 = "SELECT ID FROM `edges_all` WHERE `node1ID` = ? AND `node2ID` = ?";
    	final String SQL5 = "INSERT INTO `ways` (`ID`, `startEdgeID`, `endEdgeID`, `nameValue`, `cityName`, `cityNodeID`, `highwayValue`, `isOneway`, `ref`) VALUES (?, ?, ?, ?, NULL, NULL, ?, ?, ?)";
    	boolean taggeWays = false;
    	boolean oneway = false;
    	int startEdgeID = 0;
    	int endEdgeID = 0;
    	int speedID = 0;
    	String startNodeID = "";
    	String endNodeID = "";
        String wayID = "";
        String nodeID = "";
        String nodeLat = "";
        String nodeLon = "";
        String nodeAttributeName = "";
        String onewayValue = "";
        String[] tags = new String[5];
        tags[0] = ""; //Node place
        tags[1] = ""; //Node name
        tags[2] = ""; //Way nameValue
        tags[3] = ""; //Way highwayValue
       	tags[4] = ""; //Way ref
        ResultSet rs = null;
        LinkedList<String> nd_ref = new LinkedList<String>();
        try {
        	PreparedStatement ps1 = DBConnector.getConnection().prepareStatement(SQL1);
        	PreparedStatement ps2 = DBConnector.getConnection().prepareStatement(SQL2);
        	PreparedStatement ps3 = DBConnector.getConnection().prepareStatement(SQL3);
        	PreparedStatement ps4 = DBConnector.getConnection().prepareStatement(SQL4);
        	PreparedStatement ps5 = DBConnector.getConnection().prepareStatement(SQL5);
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(READ_PATH));
            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.next() == XMLStreamConstants.START_ELEMENT) {
                	//Nodes werden geholt
                	if (xmlStreamReader.getName().toString().equals("node")) {
						for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
							nodeAttributeName = xmlStreamReader.getAttributeLocalName(i);
							if (nodeAttributeName.equals("id")) {
	                            nodeID = xmlStreamReader.getAttributeValue(i);
							}
							if (nodeAttributeName.equals("lat")) {
								nodeLat = xmlStreamReader.getAttributeValue(i);
							}
							if (nodeAttributeName.equals("lon")) {
								nodeLon = xmlStreamReader.getAttributeValue(i);
							}
						}
                	}
                	// Tagwoerter von den Nodes werden geholt
                	if (!taggeWays) {
	                    if (xmlStreamReader.getLocalName().equals("tag")) {
	                    	if (xmlStreamReader.getAttributeValue(0).equals("place") && ( //Nehme nur Nodes mit einem Staedte-Tag
	                    		xmlStreamReader.getAttributeValue(1).equals("city") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("town") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("village") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("hamlet") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("isolated_dwelling"))) {
	                    		tags[0] = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("name")) {
	                    		tags[1] = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    }
                	}
                    //Ways werden geholt
                	if (xmlStreamReader.getName().toString().equals("way")) {
                		wayID = xmlStreamReader.getAttributeValue(0).toString();
                		taggeWays = true;
                	}
                	//Tagwoerter von den Ways werden geholt
                	if (taggeWays) {
	                    if (xmlStreamReader.getLocalName().equals("nd")) {
	                    	nd_ref.add(xmlStreamReader.getAttributeValue(0).toString());
	                    }
	                    if (xmlStreamReader.getLocalName().equals("tag")) {
	                    	if (xmlStreamReader.getAttributeValue(0).equals("name")) {
	                    		tags[2] = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("highway")) {
	                    		if (xmlStreamReader.getAttributeValue(1).equals("motorway")) {
	                    			speedID = 1;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("motorway_link")) {
	                    			speedID = 1;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("trunk")) {
	                    			speedID = 1;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("trunk_link")) {
	                    			speedID = 1;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("primary")) {
	                    			speedID = 5;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("primary_link")) {
	                    			speedID = 5;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("secondary")) {
	                    			speedID = 7;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("secondary_link")) {
	                    			speedID = 7;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("tertiary")) {
	                    			speedID = 7;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("unclassified")) {
	                    			speedID = 10;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("road")) {
	                    			speedID = 10;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("residential")) {
	                    			speedID = 12;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("living_street")) {
	                    			speedID = 13;
	                    		}
	                    		tags[3] = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("ref")) {
	                    		tags[4] = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("oneway")) {
	                    		onewayValue = xmlStreamReader.getAttributeValue(1);
	                    		//moegliche positive onewayValue Werte: yes/true/1
	                    		if (onewayValue.equals("yes") || onewayValue.equals("true") || onewayValue.equals("1")) {
	                    			oneway = true;
	                    		} else {
	                    			oneway = false;
	                    		}
	                    	}
	                    }
                	}
                }
            	//Nodes werden in DB geschrieben
                if (xmlStreamReader.getEventType() == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("node")) {
                    if (!tags[0].equals("")) { //Wenn der Node einen Place Tag hat
                    	ps2.setString(1, nodeID);
                    	ps2.setString(2, nodeLat);
                    	ps2.setString(3, nodeLon);
                    	ps2.setString(4, tags[1]);
                    	ps2.executeUpdate();
                    } else {
                    	ps1.setString(1, nodeID);
                    	ps1.setString(2, nodeLat);
                    	ps1.setString(3, nodeLon);
                    	ps1.executeUpdate();
                	}
                    nodeID = "";
                    nodeLat = "";
                    nodeLon = "";
                    tags[0] = "";
                    tags[1] = "";
                }
                //Ways werden in DB geschrieben
                if (xmlStreamReader.getEventType() == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("way")) {
                	if (speedID != 0) { //Nur in DB eintragen, wenn eine speedID gesetzt ist (Highway-Tag)
	                    for (int i = 0; i < nd_ref.size() - 1; i++) {
	                    	startNodeID =  nd_ref.get(i);
	                    	endNodeID = nd_ref.get(i + 1);
	                    	ps3.setString(1, startNodeID);
	                    	ps3.setString(2,endNodeID);
	                    	ps3.setString(3,wayID);
	                    	ps3.setBoolean(4,oneway);
	                    	ps3.setInt(5, speedID);
	                    	ps3.executeUpdate();
	                        if (i == 0) {
		                    	ps4.setString(1, startNodeID);
		                    	ps4.setString(2, endNodeID);
		                    	ps4.executeQuery();
		                    	rs = ps4.executeQuery();
		                    	rs.next();
		                    	startEdgeID = rs.getInt(1);
	                        }
	                        if (i == nd_ref.size() - 2) {
		                    	ps4.setString(1, startNodeID);
		                    	ps4.setString(2, endNodeID);
		                    	rs = ps4.executeQuery();
		                    	rs.next();
		                    	endEdgeID = rs.getInt(1);
	                        }
	                    }
	                    ps5.setString(1, wayID);
	                    ps5.setInt(2, startEdgeID);
	                    ps5.setInt(3, endEdgeID);
	                    ps5.setString(4, tags[2]);
	                    ps5.setString(5, tags[3]);
	                    ps5.setBoolean(6, oneway);
	                    ps5.setString(7, tags[4]);
	                    ps5.executeUpdate();
                	}
                	tags[0] = "";
                	tags[1] = "";
                	tags[2] = "";
                	tags[3] = "";
                	tags[4] = "";
                	speedID = 0;
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