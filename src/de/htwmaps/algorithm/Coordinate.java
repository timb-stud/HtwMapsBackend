package de.htwmaps.algorithm;

public class Coordinate {
	
	protected float lat;
	protected float lon;

	public Coordinate(float lat, float lon) {
		
		this.lat = lat;
		this.lon = lon;
		
	}

	public float getLat() {
		return lat;
	}

	public float getLon() {
		return lon;
	}
	
	public String toString() {
		return lat + "/" + lon;
	}

}
