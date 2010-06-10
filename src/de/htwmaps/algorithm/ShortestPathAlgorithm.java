package de.htwmaps.algorithm;


public interface ShortestPathAlgorithm {
	
	Node[] findShortestPath(int[] nodeIDs,
							int startNodeID,
							int goalNodeID,
							int[] fromNodeIDs,
							int[] toNodeIDs,
							double[] fromToDistances,
							boolean[] oneways,
							int[] highwayTypes) throws PathNotFoundException;
}
