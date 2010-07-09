package de.htwmaps.trash;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.htwmaps.database.DBConnector;

public class AlteDBStrukturParseTags {
	private final String READ_PATH;

    public AlteDBStrukturParseTags(String READ_PATH) {
    	this.READ_PATH = READ_PATH;
    	start();
    }    

    private void start() {
    	boolean isNode = true;
    	int counter = 0;
    	int id = 0;
    	String parentValue = null;
		String key;
		String value;
		Date currentTime;
		ResultSet rs = null;
		try {
	        PreparedStatement ps1 = DBConnector.getConnection().prepareStatement("SELECT ID FROM `k_tags` WHERE `key` = ? AND `value` = ?");
	        PreparedStatement ps2 = DBConnector.getConnection().prepareStatement("INSERT INTO `k_tags` (`ID`, `key`, `value`) VALUES (NULL, ?, ?)");
	        PreparedStatement ps3 = DBConnector.getConnection().prepareStatement("INSERT INTO `r_node_tag` (`ID`, `nodeID`, `tagID`) VALUES (NULL, ?, ?)");
	        PreparedStatement ps4 = DBConnector.getConnection().prepareStatement("INSERT INTO `r_way_tag` (`ID`, `wayID`, `tagID`) VALUES (NULL, ?, ?)");
			XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(READ_PATH));
            while (xmlStreamReader.hasNext()) {
				if (xmlStreamReader.next() == XMLStreamConstants.START_ELEMENT) {
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
                        if (xmlStreamReader.getAttributeLocalName(0).equals("k") && !(xmlStreamReader.getAttributeValue(0).equals("created_by") || xmlStreamReader.getAttributeValue(0).equals("ele"))) {
                        	key = xmlStreamReader.getAttributeValue(0);
                        	value = xmlStreamReader.getAttributeValue(1);
                            value = value.replaceAll("'", " ");
//                        	if (key.length() > 49){
//                        		key = key.substring(0, 49);
//                        	}
//                            if (value.length() > 299) {
//                            	value = value.substring(0, 299);
//                            }
                            ps1.setString(1, key);
                            ps1.setString(2, value);
                            rs = ps1.executeQuery();
                    		if (rs.next()) {
                    			id = rs.getInt("ID");
                    		} else {
                    			ps2.setString(1, key);
                    			ps2.setString(2, value);
                    			ps2.executeUpdate();
                    			ps1.setString(1, key);
                    			ps1.setString(2, value);
                    			rs = ps1.executeQuery();
                    			rs.next();
                    			id = rs.getInt("ID");
                    		}
                        	if (isNode) {
                        		ps3.setString(1, parentValue);
                        		ps3.setInt(2, id);
                        		ps3.executeUpdate();
                        	} else {
                        		ps4.setString(1, parentValue);
                        		ps4.setInt(2, id);
                        		ps4.executeUpdate();
                            }
                        	counter++;
                        	if (counter % 100000 == 0) {
                        		currentTime = new Date();
                        		System.out.println(currentTime + ": " + counter + " tags geparst");
                        	}
                    	}
                    }
                }
            }
            ps1.close();
            ps2.close();
            ps3.close();
            ps4.close();
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
}