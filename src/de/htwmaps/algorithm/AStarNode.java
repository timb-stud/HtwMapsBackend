package de.htwmaps.algorithm;
import java.util.LinkedList;

/**
 * Node representation used by A Star
 * @author Tim Bartsch
 *
 */
public class AStarNode extends Node{
	private LinkedList<AStarNode> successors; // TODO LinkedList or ArrayList?
	private LinkedList<Double> distances;	//TODO move to Node
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
		successors = new LinkedList<AStarNode>();
		distances = new LinkedList<Double>();
		this.f = -1;
		this.g = -1;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getH(AStarNode dest) {
		return Math.sqrt((this.x - dest.x) * (this.x - dest.x)
				+ (this.y - dest.y) * (this.y - dest.y));
	}

	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
	}

	public LinkedList<AStarNode> getSuccessors() {
		return successors;
	}

	public void addSuccessor(AStarNode successor, double distToSucc, boolean oneway, int highwayType) {
		this.successors.add(successor);
		this.distances.add(distToSucc);
	}

	public AStarNode getPredeccessor() {
		return predeccessor;
	}

	public void setPredeccessor(AStarNode predeccessor) {
		this.predeccessor = predeccessor;
	}

	public double getDistToSucc(int pos) {
		return distances.get(pos);
	}
}
