/**
 * 
 */
package fr.ylecuyer.colazo.world;

/**
 * @author ylecuyer
 *
 * Description of a bicycle part
 *
 */
public class BicyclePart {
	
	/**
	 * Part's name
	 */
	private String name;

	private long ID;
	
	/**
	 * Getter for name
	 * 
	 * @return Part's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for name
	 * 
	 * @param name Part's name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public long getID() {
		return ID;
	}

	public void setID(long _ID) {
		ID = _ID;
	}
	
}
