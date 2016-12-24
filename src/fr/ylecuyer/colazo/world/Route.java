package fr.ylecuyer.colazo.world;

import java.util.ArrayList;

public class Route {
	
	String name;
	String description;
	long ID;
	ArrayList<Point> points;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getID() {
		return ID;
	}
	public void setID(long _ID) {
		ID = _ID;
	}
	public ArrayList<Point> getPoints() {
		return points;
	}
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	

}
