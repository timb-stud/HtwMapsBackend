package de.htwmaps.algorithm;


public interface ShortestPathAlgorithm {
	public static final int SHORTEST_ROUTE = 0;
        public static final int FASTEST_ROUTE = 1;
	
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
