package edu.cornell.scholars.collaboration.datamodel.json;

import java.util.HashSet;
import java.util.Set;

public class Person_JSON {

	private String name;
	private String description;
	private int size;
	private String uri;
	private Set<Publication_JSON> pubs;
	
	public Person_JSON(){
		this.setPubs(new HashSet<Publication_JSON>());
	}
	
	public Person_JSON(String name, int size, String uri, Set<Publication_JSON> pubs) {
		super();
		this.name = name;
		this.size = size;
		this.setUri(uri);
		this.pubs = pubs;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pubs == null) ? 0 : pubs.hashCode());
		result = prime * result + size;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Person_JSON other = (Person_JSON) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pubs == null) {
			if (other.pubs != null)
				return false;
		} else if (!pubs.equals(other.pubs))
			return false;
		if (size != other.size)
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Set<Publication_JSON> getPubs() {
		return pubs;
	}

	public void setPubs(Set<Publication_JSON> pubs) {
		this.pubs = pubs;
	}
	
	
	
}
