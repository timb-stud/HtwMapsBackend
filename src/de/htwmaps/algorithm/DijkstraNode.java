package de.htwmaps.algorithm;

/**
 * @author Stanislaw Tartakowski
 * 
 * Node representing location on earth
 */
public class DijkstraNode extends Node {

	private double dist;									//should be volatile
	private DijkstraNode predecessor; 						//should be volatile
	private boolean removed, touchedByTh1, touchedByTh2;	//should be volatile

	/**
	 * @param id unique node on earth
	 * @param y latitude
	 * @param x longitude
	 */
	public DijkstraNode(float x, float y, int id) {
		super(x, y, id);
		setDist(Double.MAX_VALUE);
	}
	
	/**
	 * 
	 * @return removed flag. Algorithm depending feature to optimize running time.
	 */
	public boolean isRemovedFromQ() {
		return removed;
	}

	/**
	 * 
	 * @param removed Algorithm depending feature to optimize running time.
	 */
	public void setRemovedFromQ(boolean removed) {
		this.removed = removed;
	}

	/**
	 * 
	 * @return whole distance to start node
	 */
	public double getDist() {
		return dist;
	}

	/**
	 * 
	 * @param dist new distance to start node
	 */
	public void setDist(double dist) {
		this.dist = dist;
	}

	/**
	 * @param new predecessor as a new item of the result set
	 */
	public void setPredecessor(DijkstraNode predecessor) {
		this.predecessor = predecessor;
	}

	/**
	 * 
	 * @return first predecessor as last item of the result set
	 */
	public DijkstraNode getPredecessor() {
		return predecessor;
	}
	
	/**
	 * 
	 * @param touchedByTh1 indicator for the reverse running thread, that this node can be
	 * the connection between the two result sets
	 */
	public void setTouchedByTh1(boolean touchedByTh1) {
		this.touchedByTh1 = touchedByTh1;
	}

	/**
	 * 
	 * @return if this thread can be a connection node of the result sets
	 */
	public boolean isTouchedByTh1() {
		return touchedByTh1;
	}

	/**
	 * 
	 * @param touchedByTh2 watch setTouchedByTh1
	 */
	public void setTouchedByTh2(boolean touchedByTh2) {
		this.touchedByTh2 = touchedByTh2;
	}

	/**
	 * 
	 * @return watch isTouchedByTh1
	 */
	public boolean isTouchedByTh2() {
		return touchedByTh2;
	}
}
