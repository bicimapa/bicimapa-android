package fr.ylecuyer.colazo.world;

/**
 * @author ylecuyer
 *
 * Class which describes a log entry for a bicycle part
 */
public class LogEntry {
	
	/**
	 * Description of what has been done
	 */
	private String description = "";
	
	/**
	 * Reason of why this change has been made
	 */
	private String reason = "";
	
	/**
	 * Cost of the change
	 */
	private double cost = 0;
	
	/**
	 * Change's date
	 */
	private long timestamp = 0;
	
	private long ID;
	
	/**
	 * Getter for description
	 * 
	 * @return Description of what has been done
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Setter for description
	 * 
	 * @param description Description of what has been done
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Getter for reason
	 * 
	 * @return Reason of why this change has been made
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Setter for reason
	 * 
	 * @param reason Reason of why this change has been made
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * Getter for cost
	 * 
	 * @return Cost of the change
	 */
	public double getCost() {
		return cost;
	}
	
	/**
	 * 
	 * Setter for cost
	 * 
	 * @param cost Cost of the change
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	/**
	 * Getter for timestamp
	 * 
	 * @return Change's date
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Setter for timestamp
	 * 
	 * @param timestamp Change's date
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getID() {
		return ID;
	}

	public void setID(long _ID) {
		ID = _ID;
	}	
}
