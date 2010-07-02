package de.htwmaps.database.importscripts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;



import de.htwmaps.database.DBConnector;

public class ParseWays {
	private final String READ_PATH;

    public ParseWays(String READ_PATH) {
    	this.READ_PATH = READ_PATH;
    	start();
    }

    private void start() {
    	int counter = 0;
    	int event = 0;
        Date currentTime;
        LinkedList<Integer> ll = new LinkedList<Integer>();
		try {
			PreparedStatement ps = DBConnector.getConnection().prepareStatement("INSERT INTO ways(ID, startNodeID, endNodeID) values(?, ?, ?)");
			XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(READ_PATH));
            while (xmlStreamReader.hasNext()) {
            	event = xmlStreamReader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					if ((xmlStreamReader.getName().toString().equals("relation"))) {
						break;
					}
                    if (xmlStreamReader.getName().toString().equals("way")) {
                        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
                            if (xmlStreamReader.getAttributeLocalName(i).equals("id")) {
                            	ps.setInt(1, Integer.parseInt(xmlStreamReader.getAttributeValue(i)));
                            }
                        }
                    }
                    if (xmlStreamReader.getName().toString().equals("nd")) {
                    	ll.add(Integer.parseInt(xmlStreamReader.getAttributeValue(0)));
                    }
				}
                if (event == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("way")) {
                	ps.setInt(2, ll.getFirst());
                	ps.setInt(3, ll.getLast());
                	ps.executeUpdate();
                	ll.clear();
                	counter++;
                	if (counter % 100000 == 0) {
                		currentTime = new Date();
                		System.out.println(currentTime + ": " + counter + " ways geparst");
                	}
                }
            }
            ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}