package de.htwmaps.database.importscripts;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Date;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ParseNodes {
	private final String READ_PATH;
	private final String WRITE_PATH;

    public ParseNodes(String READ_PATH, String WRITE_PATH) {
    	this.READ_PATH = READ_PATH;
    	this.WRITE_PATH = WRITE_PATH;
    	start();
    }    

    private void start() {
    	int counter = 0;
    	int event = 0;
    	int id = 0;
    	float lat = 0.0F;
    	float lon = 0.0F;
    	String attributeName;
        Date currentTime;
        StringBuilder sb = new StringBuilder();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(new FileInputStream(READ_PATH));
			BufferedWriter bw = new BufferedWriter(new FileWriter(WRITE_PATH));
            while (xmlStreamReader.hasNext()) {
                event = xmlStreamReader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					if ((xmlStreamReader.getName().toString().equals("way"))) {
						break;
					}
                    if (xmlStreamReader.getName().toString().equals("node")) {
						for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
							attributeName = xmlStreamReader.getAttributeLocalName(i);
							if (attributeName.equals("id")) {
								id = Integer.parseInt(xmlStreamReader.getAttributeValue(i));
							}
							if (attributeName.equals("lat")) {
								lat = Float.parseFloat(xmlStreamReader.getAttributeValue(i));
							}
							if (attributeName.equals("lon")) {
							lon = Float.parseFloat(xmlStreamReader.getAttributeValue(i));
							}
						}
	                    sb.append("INSERT INTO nodes(ID, lat, lon) values(" + id + ", " + lat + ", " + lon +");\n");
	    				counter++;
	                    if (counter % 400000 == 0) {
	                    	bw.write(sb.toString());
	                    	sb.delete(0, sb.length());
	                        currentTime = new Date();
	                        System.out.println(currentTime + ": " + counter + " nodes geparst");
	                    }
                    }
				}
            }
            bw.write(sb.toString());
            bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}