package fr.ylecuyer.colazo.world;

public class Place {

	private String name;
	private String description;
	private double latitude;
	private double longitude;
	private String category;
	private String route;
	private long sitio_id;
	
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
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getRoute() {
		return route;
	}
	
	public void setRoute(String route) {
		this.route = route;
	}

	public long getSitio_id() {
		return sitio_id;
	}

	public void setSitio_id(long sitio_id) {
		this.sitio_id = sitio_id;
	}

	
}
