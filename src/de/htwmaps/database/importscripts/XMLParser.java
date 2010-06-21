package de.htwmaps.database.importscripts;

import java.util.Date;

public class XMLParser {
	private static final String READ_PATH = "D:/HTWMaps/saarland.osm";
	private static final String WRITE_PATH = "D:/HTWMaps/output/saarland/";

	public XMLParser() {
	}

	public static void main(String[] args) {
		print("XMLParser running...");

		print("Parsing nodes...");
		//new ParseNodes(READ_PATH, WRITE_PATH + "nodes.sql");
		print("Parsing nodes finished!");

		print("Parsing ways...");
		//new ParseWays(READ_PATH, WRITE_PATH + "ways.sql");
		print("Parsing ways finished!");

		print("Parsing edges...");
		//new ParseEdges(READ_PATH, WRITE_PATH + "edges.sql");
		print("Parsing edges finished!");

		print("Parsing tags...");
		new ParseTags(READ_PATH, WRITE_PATH + "tags.sql");
		print("Parsing tags finished!");

		print("Updating PartsOfHighway...");
		//new UpdatePartOfHightway();
		print("Updating finished!");

		print("Parsing streets...");
		try {
			UpdateStreets.main(WRITE_PATH + "streets.sql");
		} catch (Exception e) {
			e.printStackTrace();
		}
		print("Parsing streets finished!");

		print("XMLParser finished!");
	}

	private static void print(String s) {
		Date currentTime = new Date();
		System.out.println(currentTime + ": " + s);
	}
}
