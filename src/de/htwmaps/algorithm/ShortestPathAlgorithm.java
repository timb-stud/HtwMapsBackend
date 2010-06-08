package de.htwmaps.algorithm;
import java.util.HashMap;


public interface ShortestPathAlgorithm {
	AStarNode[] findShortestPath(	HashMap<Integer, AStarNode> allNodes,
											int startNodeID,
											int goalNodeID,
											int[] fromNodeIDs,
											int[] toNodeIDs,
											double[] fromToDistances,
											boolean[] oneways,
											int[] highwayTypes) throws PathNotFoundException;
}
