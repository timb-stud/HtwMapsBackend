package de.htwmaps.database.importscripts;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class ParseEdges {

	//Attribute
    private String inputFile, outputFile;

    /**
     * Konstruktor
     * @param input Pfad + Name der Datei aus welcher gelesen wird
     * @param output Pfad + Name in welche geschrieben wird
     * @throws IOException 
     */
    public ParseEdges(String input, String output) {
    	this.inputFile = input;
    	this.outputFile = output;
    	parseEdgesFile();
    }    


    /**
     * Durchsucht die OSM Datei nach allen benoetigten Edges Informationen 
     * und schreibt diese in SQL-Form in eine Datei.
     * @throws IOException
     */
    private void parseEdgesFile() {
        String id = null, tablename = null;
        StringBuilder sb = new StringBuilder();

        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            //input & output Stream werden hier erstellt
            InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
            BufferedWriter ways_fw = new BufferedWriter( new FileWriter(outputFile));
            
            XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(in);
            LinkedList<String> retCache = new LinkedList<String>() ;
            
            int retCounter = 0;

            while (xmlStreamReader.hasNext()) {
                int event = xmlStreamReader.next();                

                if (event == XMLStreamConstants.START_ELEMENT) {
				
                    if (xmlStreamReader.getName().toString().equals("way")) {
                        tablename = "edges";

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
//                    if (xmlStreamReader.getLocalName().equals("tag")) {
//
//                        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
//                            String attributeName = xmlStreamReader.getAttributeLocalName(i).toString();
//                            if (attributeName.equals("k") && xmlStreamReader.getAttributeValue(i).equals("length")) {
//                                length = xmlStreamReader.getAttributeValue(i + 1).toString();
//                            }
//                        }
//                    }
                }                
                //bei END ELEMENT wird in Datei geschrieben
                if (event == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("way")) {
                    for (int i = 0; i < retCache.size(); i++) {
                        if (i+1 < retCounter) {
                            sb.append("INSERT INTO `").append(tablename).append("` (`fromNodeID`, `toNodeID`, `wayID`) VALUES (")
                              .append(retCache.get(i) + ", " + retCache.get(i+1) + "," + id + ")\n");
                        }
                    }
                        ways_fw.write(sb.toString());
                        sb.delete(0, sb.length());
                        retCounter = 0;
                }                
            }
            ways_fw.close();
            
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
