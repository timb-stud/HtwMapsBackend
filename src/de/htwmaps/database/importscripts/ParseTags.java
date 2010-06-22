package de.htwmaps.database.importscripts;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.htwmaps.database.DBConnector;

public class ParseTags {
	private final String READ_PATH;
	private final String WRITE_PATH;

    public ParseTags(String READ_PATH, String WRITE_PATH) {
    	this.READ_PATH = READ_PATH;
    	this.WRITE_PATH = WRITE_PATH;
    	start();
    }    

    private void start() {
    	boolean isNode = true;
    	int counter = 0;
    	int event = 0;
    	int id = 0;
    	String attributeName;
    	String parentValue = "";
		String key;
		String value;
		Date currentTime;
		ResultSet rs = null;
        StringBuilder sb = new StringBuilder();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        Connection con = DBConnector.getConnection();
		try {
	        PreparedStatement ps1 = con.prepareStatement("SELECT ID FROM `k_tags` WHERE `key` = ? AND `value` = ?");
	        PreparedStatement ps2 = con.prepareStatement("INSERT INTO `k_tags` (`ID`, `key`, `value`) VALUES (NULL, ?, ?);");
			XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(new FileInputStream(READ_PATH));
			BufferedWriter bw = new BufferedWriter(new FileWriter(WRITE_PATH));
            while (xmlStreamReader.hasNext()) {
                event = xmlStreamReader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					if ((xmlStreamReader.getName().toString().equals("relation"))) {
						break;
					}
					if ((xmlStreamReader.getName().toString().equals("node"))) {
						isNode = true;
						parentValue = xmlStreamReader.getAttributeValue(0);
					}
					if ((xmlStreamReader.getName().toString().equals("way"))) {
						isNode = false;
						parentValue = xmlStreamReader.getAttributeValue(0);
					}
                    if (xmlStreamReader.getName().toString().equals("tag")) {
                        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
                            attributeName = xmlStreamReader.getAttributeLocalName(i);
                            if (attributeName.equals("k") && !(xmlStreamReader.getAttributeValue(i).equals("created_by") || xmlStreamReader.getAttributeValue(i).equals("ele"))) {
                            	key = xmlStreamReader.getAttributeValue(i);
                                value = xmlStreamReader.getAttributeValue(i + 1);
                                value = value.replaceAll("'", " ");
                                ps1.setString(1, key);
                                ps1.setString(2, value);
                                rs = ps1.executeQuery();
                        		if (rs.next()) {
                        			id = rs.getInt("ID");
                        		}
	                            ps2.setString(1, key);
	                            ps2.setString(2, value);
	                            ps2.executeUpdate();
	                            ps1.setString(1, key);
	                            ps1.setString(2, value);
	                            rs = ps1.executeQuery();
	                            rs.next();
	                            id = rs.getInt("ID");
                    		}
                    	}
                    	counter++;
                    	if (counter % 40000 == 0) {
                    		bw.write(sb.toString());
                    		sb.delete(0, sb.length());
                    		currentTime = new Date();
                    		System.out.println(currentTime + ": " + counter + " tags geparst");
                    	}
                    	if (isNode) {
                    		sb.append("INSERT INTO `r_node_tag` (`ID`, `nodeID`, `tagID`) VALUES (NULL, '" + parentValue + "', '" + id + "');").append("\n");
                    	} else {
                    		sb.append("INSERT INTO `r_way_tag` (`ID`, `wayID`, `tagID`) VALUES (NULL, '" + parentValue + "', '" + id + "');").append("\n");
                    	}
                    }
                }
            }
            ps1.close();
            ps2.close();
    		bw.write(sb.toString());
    		bw.close();
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}