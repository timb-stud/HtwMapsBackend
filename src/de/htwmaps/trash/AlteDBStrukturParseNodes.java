package de.htwmaps.trash;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.htwmaps.database.DBConnector;

public class AlteDBStrukturParseNodes {
	private final String READ_PATH;

    public AlteDBStrukturParseNodes(String READ_PATH) {
    	this.READ_PATH = READ_PATH;
    	start();
    }    

    private void start() {
    	int counter = 0;
    	String attributeName;
        Date currentTime;
		try {
	        PreparedStatement ps = DBConnector.getConnection().prepareStatement("INSERT INTO nodes(ID, lat, lon) values(?, ?, ?)");
			XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(READ_PATH));
            while (xmlStreamReader.hasNext()) {
				if (xmlStreamReader.next() == XMLStreamConstants.START_ELEMENT) {
					if ((xmlStreamReader.getName().toString().equals("way"))) {
						break;
					}
                    if (xmlStreamReader.getName().toString().equals("node")) {
						for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
							attributeName = xmlStreamReader.getAttributeLocalName(i);
							if (attributeName.equals("id")) {
	                            ps.setInt(1, Integer.parseInt(xmlStreamReader.getAttributeValue(i)));
							}
							if (attributeName.equals("lat")) {
								ps.setFloat(2, Float.parseFloat(xmlStreamReader.getAttributeValue(i)));
							}
							if (attributeName.equals("lon")) {
								ps.setFloat(3, Float.parseFloat(xmlStreamReader.getAttributeValue(i)));
							}
						}
	                    ps.executeUpdate();
	    				counter++;
	                    if (counter % 1000000 == 0) {
	                        currentTime = new Date();
	                        System.out.println(currentTime + ": " + counter + " nodes geparst");
	                    }
                    }
				}
            }
            ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}