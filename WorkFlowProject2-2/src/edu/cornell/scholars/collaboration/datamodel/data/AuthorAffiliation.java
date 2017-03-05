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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gridURI == null) ? 0 : gridURI.hashCode());
		result = prime * result + ((localName == null) ? 0 : localName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthorAffiliation other = (AuthorAffiliation) obj;
		if (gridURI == null) {
			if (other.gridURI != null)
				return false;
		} else if (!gridURI.equals(other.gridURI))
			return false;
		if (localName == null) {
			if (other.localName != null)
				return false;
		} else if (!localName.equals(other.localName))
			return false;
		return true;
	}
	
	
	
}
