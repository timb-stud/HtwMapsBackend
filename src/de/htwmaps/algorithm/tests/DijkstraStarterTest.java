package de.htwmaps.algorithm.tests;

import java.util.Arrays;

import junit.framework.TestCase;
import de.htwmaps.algorithm.DijkstraStarter;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;

public class DijkstraStarterTest extends TestCase {

	public void testFindShortestPath1() {
		Node[] result;
		int[] expectedResult = {1,3,5,7};
		DijkstraStarter ds = new DijkstraStarter();
		//
		int[] allNodeIDs = {1,2,3,4,5,6,7};
		float[] x = {0,6,6,16,16,22,28};
		float[] y = {10,16,6,10,6,10,6};
		int startNodeID = 1;
		int goalNodeID = 7;
		int[] fromNodeIDs = {1,1,2,3,4,4,5,6};
		int[] toNodeIDs =   {2,3,4,5,5,6,7,7};
		double[] fromToDistances = {8.49, 7.21, 11.66, 10, 12, 6, 4, 7.21};
		boolean[] oneways = {false, false, false, false, false, false, false, false};
		int[] highwayTypes = {0,0,0,0,0,0,0,0};
		
		try{
			result = ds.findShortestPath(allNodeIDs, x, y, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, fromToDistances, oneways, highwayTypes);
			System.out.println(Arrays.toString(result));
			for(int i = 0; i < result.length; i++){
				assertEquals(expectedResult[i], result[i].getId());
			}
		}catch(PathNotFoundException e){
			fail("Path not found");
		}
	}

}
