package edu.cornell.scholars.collaborationwheel;

import java.util.HashSet;
import java.util.Set;


public class College {

	private String name;
	private String description;
	private Set<Department> children;
	
	public College(String name, String description, Set<Department> children) {
		super();
		this.name = name;
		this.description = description;
		this.children = children;
	}
	
	public College() {
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<Department> getChildren() {
		return children;
	}
	public void setChildren(Set<Department> children) {
		this.children = children;
	}
	public void addDepartment(Department d) {
		if(this.children == null){
			this.children = new HashSet<Department>();
		}
		this.children.add(d);
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		College other = (College) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
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
		return true;
	}

	public Department getDepartment(String department) {
		if(children != null){
			for(Department d : children){
				if(d.getDescription().equals(department)){
					return d;
				}
			}
		}
		return null;
	}

	
	
}
