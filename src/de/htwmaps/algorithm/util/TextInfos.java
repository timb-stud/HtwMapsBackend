package de.htwmaps.algorithm.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import de.htwmaps.algorithm.Edge;
import de.htwmaps.algorithm.Node;

public class TextInfos {
	private String name;
	private String addition;
	private String state;
	private String city;
	private double distance;
	private String direction;
	private LinkedList<Edge> edgeList;
	
	public TextInfos(String streetname, String ref, String city, String state, double distance, LinkedList<Edge> edgeList, String direction) {
		this.name = streetname;
		this.addition = ref;
		this.city = city;
		this.state = state;
		this.distance = distance;
		this.edgeList = new LinkedList<Edge>();
		this.edgeList.addAll(edgeList);
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.00");
		String text = df.format((distance / 1000)) + " km \t" + name + "\t" + addition + "\t" + city + "\t" + state;
		return text;
	}
	
	public String getName() {
		return name;
	}

	public String getAddition() {
		return addition;
	}

	public String getState() {
		return state;
	}

	public String getCity() {
		return city;
	}

	public double getDistance() {
		return distance;
	}

	public LinkedList<Edge> getEdgeList() {
		return this.edgeList;
	}

	public String getDirection() {
		return direction;
	}
}
