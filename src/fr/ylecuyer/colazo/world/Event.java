package fr.ylecuyer.colazo.world;

public class Event {

	private int ID;
	private String name;
	private String short_description;
	private String long_description;
	private long when;
	private String where;
	
	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getShortDescription() {
		return short_description;
	}
	
	public void setShortDescription(String short_description) {
		this.short_description = short_description;
	}
	
	public String getLongDescription() {
		return long_description;
	}
	
	public void setLongDescription(String long_description) {
		this.long_description = long_description;
	}
	
	public long getWhen() {
		return when;
	}
	
	public void setWhen(long when) {
		this.when = when;
	}
	
	public String getWhere() {
		return where;
	}
	
	public void setWhere(String where) {
		this.where = where;
	}
	
	
	
}
