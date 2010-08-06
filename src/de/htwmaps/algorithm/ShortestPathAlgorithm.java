package de.htwmaps.algorithm;


public abstract class ShortestPathAlgorithm {
	GraphData graphData;
	
	public static final int MOTORWAY = 1;
	public static final int PRIMARY = 5;
	public static final int SECONDARY = 7;
	public static final int RESIDENTIAL = 10;
	public static final int ROAD = 11;
	public static final int LIVING_STREET = 13;
	public int motorwaySpeed = 130; //Autobahn
	public int primarySpeed = 80; //Landstraﬂe
	public int secondarySpeed = 60; //Ortsverbindung
	public int residentialSpeed = 45; //Innerorts
	public int roadSpeed = 50; //unclassified
	public int livingStreetSpeed = 5; //Spielstrasse
	
	
	public ShortestPathAlgorithm(GraphData gd) {
		if (gd == null) {
			throw new IllegalArgumentException("Graph data must not be null");
		}
		this.graphData = gd;
	}
	
	public abstract Node[] findShortestPath(int startNodeID, int goalNodeID) throws PathNotFoundException;
	
	public abstract Node[] findFastestPath(int startNodeID, 
									int goalNodeID, 
									int motorwaySpeed, 
									int primarySpeed,
									int secondarySpeed,
									int residentialSpeed,
									int roadSpeed,
									int livingStreetSpeed) throws PathNotFoundException;
	
	
	public abstract Node[] findFastestPath(int startNodeID, 
									int goalNodeID, 
									int motorwaySpeed, 
									int primarySpeed,
									int residentialSpeed) throws PathNotFoundException;

	public int getMotorwaySpeed() {
		return motorwaySpeed;
	}

	public void setMotorwaySpeed(int motorwaySpeed) {
		this.motorwaySpeed = motorwaySpeed;
	}

	public int getPrimarySpeed() {
		return primarySpeed;
	}

	public void setPrimarySpeed(int primarySpeed) {
		this.primarySpeed = primarySpeed;
	}

	public int getSecondarySpeed() {
		return secondarySpeed;
	}

	public void setSecondarySpeed(int secondarySpeed) {
		this.secondarySpeed = secondarySpeed;
	}

	public int getResidentialSpeed() {
		return residentialSpeed;
	}

	public void setResidentialSpeed(int residentialSpeed) {
		this.residentialSpeed = residentialSpeed;
	}

	public int getRoadSpeed() {
		return roadSpeed;
	}

	public void setRoadSpeed(int roadSpeed) {
		this.roadSpeed = roadSpeed;
	}

	public int getLivingStreetSpeed() {
		return livingStreetSpeed;
	}

	public void setLivingStreetSpeed(int livingStreetSpeed) {
		this.livingStreetSpeed = livingStreetSpeed;
	}

	
	
}