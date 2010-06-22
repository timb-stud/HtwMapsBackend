package de.htwmaps.database.importscripts;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ParseWays {
	private final String READ_PATH;
	private final String WRITE_PATH;

    public ParseWays(String READ_PATH, String WRITE_PATH) {
    	this.READ_PATH = READ_PATH;
    	this.WRITE_PATH = WRITE_PATH;
    	start();
    }

    private void start() {
    	int counter = 0;
    	int id = 0;
    	int event = 0;
    	int startNodeID = 0;
    	String attributeName;
        Date currentTime;
        LinkedList<Integer> ll = new LinkedList<Integer>();
        StringBuilder sb = new StringBuilder();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try {
			XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(new FileInputStream(READ_PATH));
			BufferedWriter bw = new BufferedWriter(new FileWriter(WRITE_PATH));
            while (xmlStreamReader.hasNext()) {
            	event = xmlStreamReader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					if ((xmlStreamReader.getName().toString().equals("relation"))) {
						break;
					}
                    if (xmlStreamReader.getName().toString().equals("way")) {
                        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
                            attributeName = xmlStreamReader.getAttributeLocalName(i);
                            if (attributeName.equals("id")) {
                            	id = Integer.parseInt(xmlStreamReader.getAttributeValue(i));
                            }
                        }
                    }
                    if (xmlStreamReader.getName().toString().equals("nd")) {
                    	startNodeID = Integer.parseInt(xmlStreamReader.getAttributeValue(0));
                    	ll.add(startNodeID);
                    }
				}
                if (event == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("way")) {
                	counter++;
                	if (counter % 40000 == 0) {
                		bw.write(sb.toString());
                		sb.delete(0, sb.length());
                		currentTime = new Date();
                		System.out.println(currentTime + ": " + counter + " ways geparst");
                	}
                    sb.append("INSERT INTO ways(ID, startNodeID, endNodeID) values(" + id + ", " + ll.getFirst() + ", " + ll.getLast() +");\n");
                	ll.clear();
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