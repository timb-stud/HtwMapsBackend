package de.htwmaps.database.importscripts;

import java.util.Date;

import de.htwmaps.algorithm.Edge;

public class XMLParser {
	public XMLParser() {
	}

	/**
	 * 
	 * @param args[0] Dateiname der XMLDatei, die geparst werden soll
	 */
	public static void main(String[] args) {
		String READ_PATH = args[0];
		print("XMLParser running...");

		//Parse Methoden: nodes/ways/edges/tags werden in die DB eingetragen
		print("Parsing nodes...");
		//new ParseNodes(READ_PATH);
		print("Parsing nodes finished!");

		print("Parsing ways...");
		//new ParseWays(READ_PATH);
		print("Parsing ways finished!");

		print("Parsing edges...");
		//new ParseEdges(READ_PATH);
		print("Parsing edges finished!");

		print("Parsing tags...");
		new ParseTags(READ_PATH);
		print("Parsing tags finished!");

		//Update Methoden: nodes/ways/edges/tags muessen schon in der DB eingetragen sein
		print("Updating PartOfHighway...");
		//new UpdatePartOfHightway();
		print("Updating finished!");
		
		print("Update edge.length");
		new CalculateEdgeLength();
		print("Update edge.length finished!");

		print("Updating streets...");
		try {
			//UpdateStreets.main();
		} catch (Exception e) {
			e.printStackTrace();
		}
		print("Updating finished!");

		print("XMLParser finished!");
	}

	private static void print(String s) {
		Date currentTime = new Date();
		System.out.println(currentTime + ": " + s);
	}
}