package de.htwmaps.trash;

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

public class AlteDBStrukturParseEdges {

	//Attribute
    private final String READ_PATH;

    /**
     * Konstruktor
     * @param READ_PATH Pfad + Name der Datei aus welcher gelesen wird
     */
    public AlteDBStrukturParseEdges(String READ_PATH) {
    	this.READ_PATH = READ_PATH;
    	start();
    }    


    /**
     * Durchsucht die OSM Datei nach allen benoetigten Edges Informationen 
     * und schreibt diese in die Datenbank
     */
    private void start() {
    	int counter = 0;
    	int event = 0;
    	int retCounter = 0;
        Date currentTime;
        LinkedList<String> ll = new LinkedList<String>();
        try {
        	PreparedStatement ps = DBConnector.getConnection().prepareStatement("INSERT INTO `edges` (`fromNodeID`, `toNodeID`, `wayID`) VALUES (?, ?, ?)");
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(READ_PATH));
            while (xmlStreamReader.hasNext()) {
                event = xmlStreamReader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (xmlStreamReader.getName().toString().equals("way")) {
                    	ps.setInt(3, Integer.parseInt(xmlStreamReader.getAttributeValue(0).toString()));
                    }
                    if (xmlStreamReader.getLocalName().equals("nd")) {
                    	ll.add(retCounter, xmlStreamReader.getAttributeValue(0).toString());
                        retCounter++;
                    }
                }
                if (event == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("way")) {
                    for (int i = 0; i < ll.size(); i++) {
                        if (i + 1 < retCounter) {
                        	ps.setInt(1, Integer.parseInt(ll.get(i)));
                        	ps.setInt(2, Integer.parseInt(ll.get(i)));
                        	ps.executeUpdate();
                        	counter++;
                        	if (counter % 1000000 == 0) {
                        		currentTime = new Date();
                        		System.out.println(currentTime + ": " + counter + " edges geparst");
                        	}
                        }
                    }
                    ll.clear();
                    retCounter = 0;
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