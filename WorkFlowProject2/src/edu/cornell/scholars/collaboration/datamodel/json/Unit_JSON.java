package edu.cornell.scholars.collaboration.datamodel.json;

import java.util.HashSet;
import java.util.Set;

public class Unit_JSON {

	private String name;
	private String description;
	private Set<Department_JSON> children;
	
	public Unit_JSON(){
		this.children = new HashSet<Department_JSON>();
	}

	public Unit_JSON(String name, Set<Department_JSON> children) {
		super();
		this.name = name;
		this.children = children;
	}

	public Unit_JSON(String unit) {
		this.name =  unit;
		this.children = new HashSet<Department_JSON>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Department_JSON> getChildren() {
		return children;
	}

	public void setChildren(Set<Department_JSON> children) {
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
		Unit_JSON other = (Unit_JSON) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
}
