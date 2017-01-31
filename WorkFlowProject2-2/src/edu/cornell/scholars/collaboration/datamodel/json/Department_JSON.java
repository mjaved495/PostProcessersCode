package edu.cornell.scholars.collaboration.datamodel.json;

import java.util.HashSet;
import java.util.Set;

public class Department_JSON {

	private String name;   //code
	private String description;  // full name
	private Set<Person_JSON> children;
	
	public Department_JSON(){
		this.children = new HashSet<Person_JSON>();
	}
	
	public Department_JSON(String name, Set<Person_JSON> children) {
		super();
		this.name = name;
		this.children = children;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Person_JSON> getChildren() {
		return children;
	}

	public void setChildren(Set<Person_JSON> children) {
		this.children = children;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Department_JSON other = (Department_JSON) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
