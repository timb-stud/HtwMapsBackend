package de.htwmaps.algorithm;
import java.util.LinkedList;

/**
 * Node representation used by A Star
 * @author Tim Bartsch
 *
 */
public class AStarNode implements Comparable<AStarNode> {
	private int id;
	private LinkedList<AStarNode> successors;
	private LinkedList<Double> distances;
	private LinkedList<Boolean> oneways;
	private LinkedList<Integer> highwaytypes;
	private double f; // h + g
	private double g; // length from start Node to this node
	private double x;
	private double y;
	private AStarNode predeccessor;

	/**
	 * Constructs a Node with the given parameters.
	 * @param id an unique identification number
	 * @param x latitude
	 * @param y longitude
	 */
	public AStarNode(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
		successors = new LinkedList<AStarNode>();
		distances = new LinkedList<Double>();
		this.f = -1;
		this.g = -1;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
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

	public boolean equals(Object o) {
		if (o instanceof AStarNode) {
			AStarNode n = (AStarNode) o;
			if (this.id == n.id)
				return true;
		}
		return false;
	}

	@Override
	public int compareTo(AStarNode n) {
		if (this.f == n.f)
			return 0;
		if (this.f < n.f)
			return -1;
		else
			return 1;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(").append(id).append(")");
		return sb.toString();
	}
}
