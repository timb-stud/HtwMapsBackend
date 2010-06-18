package de.htwmaps.algorithm.tests;

import java.sql.SQLException;
import java.util.Arrays;

import junit.framework.TestCase;
import de.htwmaps.algorithm.DijkstraStarter;
import de.htwmaps.algorithm.Node;
import de.htwmaps.algorithm.PathNotFoundException;
import de.htwmaps.database.ResultSetToArray_ohne_fehler;

public class DijkstraStarterTest extends TestCase {

	public void testFindShortestPath1() throws SQLException {
		System.out.println("Dijkstra test");
		ResultSetToArray_ohne_fehler r = new ResultSetToArray_ohne_fehler();
		Node[] result;
		int[] expectedResult = {1,3,5,7};
		DijkstraStarter ds = new DijkstraStarter();
		//
		int[] allNodeIDs = r.getNodeID();
		float[] x = r.getLon();
		float[] y = r.getLat();
		int startNodeID = 587836344;
		int goalNodeID = 274026832;
		int[] fromNodeIDs = r.getFromNodeID();
		int[] toNodeIDs =   r.getToNodeID();
		double[] fromToDistances = r.getLength1();
		boolean[] oneways = r.getOneWay();
		int[] highwayTypes = null;
		
		try{
			long time = System.currentTimeMillis();
			result = ds.findShortestPath(allNodeIDs, x, y, startNodeID, goalNodeID, fromNodeIDs, toNodeIDs, fromToDistances, oneways, highwayTypes);
			System.out.println(System.currentTimeMillis() - time + " ms");
			System.out.println(Arrays.toString(result));
			for(int i = 0; i < result.length; i++){
				assertEquals(expectedResult[i], result[i].getId());
			}
		}catch(PathNotFoundException e){
			fail("Path not found");
		}
	}

}
