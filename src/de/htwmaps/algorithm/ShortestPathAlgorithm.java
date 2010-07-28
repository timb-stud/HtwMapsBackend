package de.htwmaps.algorithm;


public interface ShortestPathAlgorithm {
	public static int SHORTEST_ROUTE = 0;
	public static int FASTEST_ROUTE = 1;
	
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
							int searchType) throws PathNotFoundException;
}
