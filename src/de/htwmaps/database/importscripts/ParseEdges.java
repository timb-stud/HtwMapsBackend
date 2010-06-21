package de.htwmaps.database.importscripts;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class ParseEdges {

	//Attribute
    private String READ_PATH, WRITE_PATH;

    /**
     * Konstruktor
     * @param READ_PATH Pfad + Name der Datei aus welcher gelesen wird
     * @param WRITE_PATH Pfad + Name in welche geschrieben wird
     * @throws IOException 
     */
    public ParseEdges(String READ_PATH, String WRITE_PATH) {
    	this.READ_PATH = READ_PATH;
    	this.WRITE_PATH = WRITE_PATH;
    	start();
    }    


    /**
     * Durchsucht die OSM Datei nach allen benoetigten Edges Informationen 
     * und schreibt diese in SQL-Form in eine Datei.
     * @throws IOException
     */
    private void start() {
    	int counter = 0;
    	int retCounter = 0;
        String id = null;
        Date currentTime;
        StringBuilder sb = new StringBuilder();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(new FileInputStream(READ_PATH));
			BufferedWriter bw = new BufferedWriter(new FileWriter(WRITE_PATH));
            LinkedList<String> retCache = new LinkedList<String>() ;
            while (xmlStreamReader.hasNext()) {
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (xmlStreamReader.getName().toString().equals("way")) {
                        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
                            String attributeName = xmlStreamReader.getAttributeLocalName(i).toString();
                            if (attributeName.equals("id")) {
                                id = xmlStreamReader.getAttributeValue(i).toString();
                                break;
                            }
                        }
                    }
                    if (xmlStreamReader.getLocalName().equals("nd")) {
                        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
                            String attributeName = xmlStreamReader.getAttributeLocalName(i).toString();
                            if (attributeName.equals("ref")) {
                                retCache.add(retCounter, xmlStreamReader.getAttributeValue(i).toString());
                                retCounter++;
                            }
                        }
                    }
                }
                if (event == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("way")) {
                    for (int i = 0; i < retCache.size(); i++) {
                        if (i+1 < retCounter) {
                        	counter++;
                        	if (counter % 100000 == 0) {
                        		currentTime = new Date();
                        		System.out.println(currentTime + ": " + counter + " edges geparst");
                        	}
                            sb.append("INSERT INTO `edges` (`fromNodeID`, `toNodeID`, `wayID`) VALUES (")
                              .append(retCache.get(i) + ", " + retCache.get(i+1) + "," + id + ");\n");
                        }
                    }
                    bw.write(sb.toString());
                    sb.delete(0, sb.length());
                    retCounter = 0;
                }
            }
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
