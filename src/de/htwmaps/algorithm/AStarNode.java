package de.htwmaps.algorithm;

/**
 * Node representation used by A Star
 * @author Tim Bartsch
 *
 */
public class AStarNode extends Node{
	private double f; // h + g
	private double g; // length from start Node to this node
	private AStarNode predeccessor;

	/**
	 * Constructs a Node with the given parameters.
	 * @param id an unique identification number
	 * @param x latitude
	 * @param y longitude
	 */
	public AStarNode(int id, double x, double y) {
		super(x, y, id);
		this.f = -1;
		this.g = -1;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
	}

	public AStarNode getPredeccessor() {
		return predeccessor;
	}

	public void setPredeccessor(AStarNode predeccessor) {
		this.predeccessor = predeccessor;
	}
}
