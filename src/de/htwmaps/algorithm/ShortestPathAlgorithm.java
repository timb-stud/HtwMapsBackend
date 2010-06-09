package de.htwmaps.algorithm;


import java.util.HashMap;


public interface ShortestPathAlgorithm {
	Node[] findShortestPath(HashMap<Integer, Node> allNodes,
							int startNodeID,
							int goalNodeID,
							int[] fromNodeIDs,
							int[] toNodeIDs,
							double[] fromToDistances,
							boolean[] oneways,
							int[] highwayTypes);
}
