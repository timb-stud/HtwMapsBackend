package de.htwmaps.algorithm.util;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TextInfos {
	private String streetname;
	private String ref;
	private String state;
	private String city;
	private double distance;
	private long time;
	
	public TextInfos(String streetname, String ref, String city, String state, double distance, long time) {
		this.streetname = streetname;
		this.ref = ref;
		this.city = city;
		this.state = state;
		this.distance = distance;
		this.time = time;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.00");
		String text = df.format(distance) + "\t\t" + streetname + "\t" + ref + "\t" + city + "\t" + state + "\t" + time;
		return text;
	}
	
	public String getStreetname() {
		return streetname;
	}

	public String getRef() {
		return ref;
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

}
