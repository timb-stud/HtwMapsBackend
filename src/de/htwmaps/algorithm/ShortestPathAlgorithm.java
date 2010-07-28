package de.htwmaps.algorithm;


public interface ShortestPathAlgorithm {
	enum Option {SHORTEST_ROUTE, FASTEST_ROUTE};
	
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
							Option searchOption) throws PathNotFoundException;
}
