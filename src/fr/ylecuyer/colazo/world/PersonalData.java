package fr.ylecuyer.colazo.world;

/**
 * @author ylecuyer
 * 
 * Class that represents user's personal data
 *
 */
/**
 * @author ylecuyer
 *
 */
public class PersonalData {
	
	/**
	 * User's last names
	 */
	private String last_names = null;
	
	
	/**
	 * User's first names
	 */
	private String first_names = null;
	
	
	/**
	 * User's cedula number
	 */
	private String cedula = null;
	
	
	/**
	 * User's RH (A+, A-, B+, B-, AB+, AB-, O+, O-)
	 */
	private String RH = null;
	
	
	/**
	 * URI of the emergency contact
	 */
	private String emergency_contact_URI = null;
	
	
	/**
	 * Getter for user's last names
	 * 
	 * @return User's last names
	 */
	public String getLast_names() {
		return last_names;
	}
	
	/**
	 * Setter for user's last names
	 * 
	 * @param last_names User's last names
	 */
	public void setLast_names(String last_names) {
		this.last_names = last_names;
	}
	
	/**
	 * Getter for user's first names
	 * 
	 * @return User's first names
	 */
	public String getFirst_names() {
		return first_names;
	}
	
	/**
	 * Setter for user's first names
	 * 
	 * @param first_names User's first names
	 */
	public void setFirst_names(String first_names) {
		this.first_names = first_names;
	}
	
	/**
	 * Getter for user's cedula number
	 * 
	 * @return User's cedula number
	 */
	public String getCedula() {
		return cedula;
	}
	
	/**
	 * Setter for user's cedula number
	 * 
	 * @param cedula User's cedula number
	 */
	public void setCedula(String cedula) {
		
		//TODO check that cedula is a valid number
		
		this.cedula = cedula;
	}
	
	/**
	 * Getter for user's RH
	 * 
	 * @return User's RH (A+, A-, B+, B-, AB+, AB-, O+, O-)
	 */
	public String getRH() {
		return RH;
	}
	
	/**
	 * Setter for user's RH
	 * 
	 * @param RH User's RH (A+, A-, B+, B-, AB+, AB-, O+, O-)
	 */
	public void setRH(String RH) {
		this.RH = RH;
	}
	
	/**
	 * Getter for user's emergency contact
	 * 
	 * @return URI of emergency contact
	 */
	public String getEmergency_contact_URI() {
		return emergency_contact_URI;
	}
	
	/**
	 * Setter for user's emergency contact
	 * 
	 * @param emergency_contact_URI URI of user's emergency contact
	 */
	public void setEmergency_contact_URI(String emergency_contact_URI) {
		this.emergency_contact_URI = emergency_contact_URI;
	}
}
