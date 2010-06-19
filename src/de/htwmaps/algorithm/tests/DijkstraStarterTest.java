package de.htwmaps.algorithm.tests;


import java.util.Arrays;

import de.htwmaps.algorithm.DijkstraStarter;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.util.InitLogger;

public class DijkstraStarterTest {

	public static void main(String[] args) {
		System.out.println("Dijkstra test");
		Node[] result;
		DijkstraStarter ds = new DijkstraStarter();
		InitLogger.INSTANCE.initLogger();
		
		//
		int[] allNodeIDs = {1,2,3,4,5,6,7};
		float[] x = {0,6,6,16,16,22,28};
		float[] y = {10,16,6,10,6,10,6};
		int startNodeID = 1;
		int goalNodeID = 7;
		int[] fromNodeIDs = {1,1,2,3,4,4,5,6};
		int[] toNodeIDs =   {2,3,4,5,5,6,7,7};
		double[] fromToDistances = {8.49, 7.21, 11.66, 10, 12, 6, 4, 7.21};
		boolean[] oneways = {true, true, false, false, false, false, false, false};
		int[] highwayTypes = {0,0,0,0,0,0,0,0};

		try {
			long time = System.currentTimeMillis();
			result = ds.findShortestPath(allNodeIDs, x, y, startNodeID,
					goalNodeID, fromNodeIDs, toNodeIDs, fromToDistances,
					oneways, highwayTypes);
			System.out.println(System.currentTimeMillis() - time + " ms");
			System.out.println(Arrays.toString(result));
		} catch (PathNotFoundException e) {
		}
	}

}