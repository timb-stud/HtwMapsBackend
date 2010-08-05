package de.htwmaps.algorithm;


public abstract class ShortestPathAlgorithm {
	GraphData graphData;
	
	public static final int ROUTE_OPTION_SHORTEST = 1337;
	public static final int ROUTE_OPTION_FASTEST = 4711;
	public static final int MOTORWAY = 1;
	public static final int PRIMARY = 5;
	public static final int SECONDARY = 7;
	public static final int RESIDENTIAL = 10;
	public static final int ROAD = 11;
	public static final int LIVING_STREET = 13;
	public int PRIMARY_SPEED = 80; //Landstra�e
	public int SECONDARY_SPEED = 60; //Ortsverbindung
	public int RESIDENTIAL_SPEED = 45; //Innerorts
	public int ROAD_SPEED = 50; //unclassified
	public int LIVING_STREET_SPEED = 5; //Spielstrasse
	
	public ShortestPathAlgorithm(GraphData gd) {
		this.graphData = gd;
	}
	
	public abstract Node[] findPath(int startNodeID, 
									int goalNodeID, 
									int routeOption, 
									int motorwaySpeed, 
									int primarySpeed,
									int secondarySpeed,
									int residentialSpeed,
									int roadSpeed,
									int livingStreetSpeed) throws PathNotFoundException;
	
	
	public abstract Node[] findPath(int startNodeID, 
									int goalNodeID, 
									int routeOption, 
									int motorwaySpeed, 
									int primarySpeed,
									int residentialSpeed) throws PathNotFoundException;
	
}