package edu.cornell.scholars.optingraphbuilder;

public class OptInEntity {
	
	private String URI;
	private String Title;
	private String type;
	private String optin;
	
	public OptInEntity(String uRI, String title, String type, String optin) {
		super();
		URI = uRI;
		Title = title;
		this.type = type;
		this.optin = optin;
	}
	
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOptin() {
		return optin;
	}
	public void setOptin(String optin) {
		this.optin = optin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Title == null) ? 0 : Title.hashCode());
		result = prime * result + ((URI == null) ? 0 : URI.hashCode());
		result = prime * result + ((optin == null) ? 0 : optin.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		OptInEntity other = (OptInEntity) obj;
		if (Title == null) {
			if (other.Title != null)
				return false;
		} else if (!Title.equals(other.Title))
			return false;
		if (URI == null) {
			if (other.URI != null)
				return false;
		} else if (!URI.equals(other.URI))
			return false;
		if (optin == null) {
			if (other.optin != null)
				return false;
		} else if (!optin.equals(other.optin))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	

}
