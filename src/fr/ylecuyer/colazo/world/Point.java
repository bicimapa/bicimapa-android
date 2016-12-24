package fr.ylecuyer.colazo.world;

public class Point {

	long ID;
	double latitude;
	double longitude;
	long route_ID;
	
	public long getID() {
		return ID;
	}
	public void setID(long _ID) {
		ID = _ID;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public long getRoute_ID() {
		return route_ID;
	}
	public void setRoute_ID(long route_ID) {
		this.route_ID = route_ID;
	}
	
}
