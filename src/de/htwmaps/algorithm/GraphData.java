package de.htwmaps.algorithm;

/**
 * 
 * Diese Klasse beinhaltet alle Daten zum Aufbau eines Graphs
 * @author Tim Bartsch
 *
 */
public class GraphData {
	//Nodes
	private int[] allNodeIDs;
	private float[] allNodeLats;
	private float[] allNodeLons;
	//Edges
	private int[] wayIDs;
	private int[] edgeStartNodeIDs;
	private int[] edgeEndNodeIDs;
	private double[] edgeLengths;
	private boolean[] oneways;
	private int[] highwayTypes;
	
	public GraphData(){ }
	
	public void build(int[] allNodeIDs,
							float[] allNodeLats,
							float[] allNodeLons,
							int[] wayIDs,
							int[] edgeStartNodeIDs,
							int[] edgeEndNodeIDs,
							double[] edgeLengths,
							boolean[] oneways,
							int[] highwayTypes) {
		
		this.allNodeIDs = allNodeIDs;
		this.allNodeLats = allNodeLats;
		this.allNodeLons = allNodeLons;
		this.wayIDs = wayIDs;
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

	public int[] getWayIDs() {
		return wayIDs;
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
