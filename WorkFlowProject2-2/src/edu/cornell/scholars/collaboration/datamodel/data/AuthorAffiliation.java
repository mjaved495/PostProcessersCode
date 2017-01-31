package edu.cornell.scholars.collaboration.datamodel.data;

public class AuthorAffiliation {

	private String localName = null;
	private String gridURI = null;
	
	public AuthorAffiliation(){
		
	}
	
	public AuthorAffiliation(String localName){
		super();
		this.localName = localName;
	}
	
	public AuthorAffiliation(String localName, String gridURI) {
		super();
		this.localName = localName;
		this.gridURI = gridURI;
	}

	public String getLocalName() {
		return localName;
	}
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	public String getGridURI() {
		return gridURI;
	}
	public void setGridURI(String gridURI) {
		this.gridURI = gridURI;
	}
	
	@Override
	public String toString() {
		return "\""+localName + "\",\"" + gridURI+"\"";
	}
	
}
