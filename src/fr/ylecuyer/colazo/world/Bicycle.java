package fr.ylecuyer.colazo.world;

/**
 * @author ylecuyer
 * 
 * Description of a bicycle
 */
public class Bicycle {

	/**
	 * Bicycle's description
	 */
	private String description = "";
	
	/**
	 * Bicycle's photo URI
	 */
	private String photo_URI = null;

	private long ID;
	
	/**
	 * Getter for description
	 * 
	 * @return Bicycle's description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter for description
	 * 
	 * @param description Bicycle's description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Getter for photo
	 * 
	 * @return Bicycle's photo URI
	 */
	public String getPhoto_URI() {
		return photo_URI;
	}

	/**
	 * Setter for photo
	 * 
	 * @param photo Bicycle photo URI
	 */
	public void setPhoto_URI(String photo_URI) {
		this.photo_URI = photo_URI;
	}

	public long getID() {
		return ID;
	}

	public void setID(long _ID) {
		ID = _ID;
	}	
}
