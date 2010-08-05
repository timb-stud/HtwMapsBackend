package de.htwmaps.algorithm;


public interface ShortestPathAlgorithm {
	public static final int SHORTEST_ROUTE = 1337;
	public static final int FASTEST_ROUTE = 4711;
	public static final int MOTORWAY = 1;
	public static final int PRIMARY = 5;
	public static final int SECONDARY = 7;
	public static final int RESIDENTIAL = 10;
	public static final int ROAD = 11;
	public static final int LIVING_STREET = 13;
	public static double MOTORWAY_SPEED = 130.0;
	public static double PRIMARY_SPEED = 70.0;
	public static double SECONDARY_SPEED = 50.0;
	public static double RESIDENTIAL_SPEED = 35.0;
	public static double ROAD_SPEED = 45.0;
	public static double LIVING_STREET_SPEED = 5.0;
	
	Node[] findShortestPath(int[] allNodeIDs,
							float[] lon,
							float[] lat,
							int startNodeID,
							int goalNodeID,
							int[] edgeIDs,
							int[] edgeStartNodeIDs,
							int[] edgeEndNodeIDs,
							double[] edgeLengths,
							boolean[] oneways,
							int[] highwayTypes,
							int searchOption) throws PathNotFoundException;
}
