package de.htwmaps.algorithm.util;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.htwmaps.algorithm.Node;

public class TextInfos {
	private String name;
	private String addition;
	private String state;
	private String city;
	private double distance;
	private long time;
	private Node switchNode;
	private String direction;
	
	public TextInfos(String streetname, String ref, String city, String state, double distance) {
		this.name = streetname;
		this.addition = ref;
		this.city = city;
		this.state = state;
		this.distance = distance;
	}
	
	public TextInfos(String streetname, String ref, String city, String state, double distance, Node node, String direction) {
		this.name = streetname;
		this.addition = ref;
		this.city = city;
		this.state = state;
		this.distance = distance;
		this.switchNode = node;
		this.direction = direction;
	}
	
	public TextInfos(String streetname, String ref, String city, String state, double distance, Node node) {
		this.name = streetname;
		this.addition = ref;
		this.city = city;
		this.state = state;
		this.distance = distance;
		this.switchNode = node;
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

	public long getTime() {
		return time;
	}

	public Node getSwitchNode() {
		return switchNode;
	}

	public String getDirection() {
		return direction;
	}

}
