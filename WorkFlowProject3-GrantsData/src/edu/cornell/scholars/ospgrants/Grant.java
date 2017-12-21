package edu.cornell.scholars.ospgrants;

/**
 * Grant Model
 * @author mj495
 *
 */

public class Grant {

	private String localawardId;
	private String scholarsURI;
	private String type;
	
	
	public Grant(String localawardId, String scholarsURI, String type) {
		super();
		this.localawardId = localawardId;
		this.scholarsURI = scholarsURI;
		this.type = type;
	}
	
	public String getScholarsURI() {
		return scholarsURI;
	}
	public void setScholarsURI(String scholarsURI) {
		this.scholarsURI = scholarsURI;
	}
	public String getLocalawardId() {
		return localawardId;
	}
	public void setLocalawardId(String localawardId) {
		this.localawardId = localawardId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
