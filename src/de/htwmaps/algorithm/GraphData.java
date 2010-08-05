package de.htwmaps.algorithm;

/**
 * 
 * Diese Klasse beinhaltet alle Daten zum Aufbau eines Graphs
 * @author Tim Bartsch
 *
 */
public class GraphData {
	//Nodes
	int[] allNodeIDs;
	float[] allNodeLats;
	float[] allNodeLons;
	//Edges
	int[] edgeIDs;
	int[] edgeStartNodeIDs;
	int[] edgeEndNodeIDs;
	double[] edgeLengths;
	boolean[] oneways;
	int[] highwayTypes;
	
	public GraphData(	int[] allNodeIDs,
							float[] allNodeLats,
							float[] allNodeLons,
							int[] edgeIDs,
							int[] edgeStartNodeIDs,
							int[] edgeEndNodeIDs,
							double[] edgeLengths,
							boolean[] oneways,
							int[] highwayTypes) {
		
		this.allNodeIDs = allNodeIDs;
		this.allNodeLats = allNodeLats;
		this.allNodeLons = allNodeLons;
		this.edgeIDs = edgeIDs;
		this.edgeStartNodeIDs = edgeStartNodeIDs;
		this.edgeEndNodeIDs = edgeEndNodeIDs;
		this.edgeLengths = edgeLengths; 
		this.oneways = oneways;
		this.highwayTypes = highwayTypes;
	}

	public int[] getAllNodeIDs() {
		return allNodeIDs;
	}

	public float[] getAllNodeLats() {
		return allNodeLats;
	}

	public float[] getAllNodeLons() {
		return allNodeLons;
	}

	public int[] getEdgeIDs() {
		return edgeIDs;
	}

	public int[] getEdgeStartNodeIDs() {
		return edgeStartNodeIDs;
	}

	public int[] getEdgeEndNodeIDs() {
		return edgeEndNodeIDs;
	}

	public double[] getEdgeLengths() {
		return edgeLengths;
	}

	public boolean[] getOneways() {
		return oneways;
	}

	public int[] getHighwayTypes() {
		return highwayTypes;
	}
	
	
}
