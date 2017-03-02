package edu.cornell.scholars.collaborationwheel;

import java.util.HashSet;
import java.util.Set;

public class Department {

	private String name;
	private String description;
	private Set<PersonCollaborator1> children;

	public Department(){

	}

	public Department(String name, String description, Set<PersonCollaborator1> children) {
		super();
		this.name = name;
		this.description = description;
		this.children = children;
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
	public Set<PersonCollaborator1> getChildren() {
		return children;
	}
	public void setChildren(Set<PersonCollaborator1> children) {
		this.children = children;
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
		Department other = (Department) obj;
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

	public PersonCollaborator1 getPersonCollaborator1(String person1) {
		if(children != null){
			for(PersonCollaborator1 p1 : children){
				if(p1.getName().equals(person1)){
					return p1;
				}
			}
		}
		return null;
	}

	public void addPersonCollaborator1(PersonCollaborator1 p1) {
		if(this.children == null){
			this.children = new HashSet<PersonCollaborator1>();
		}
		this.children.add(p1);
	}


}
