package edu.cornell.scholars.collaborationwheel;

import java.util.HashSet;
import java.util.Set;

public class PersonCollaborator1 {

	private String name;
	private String description;
	private String orgCode;
	private String uri;
	private Set<PersonCollaborator2> children;


	public PersonCollaborator1(){

	}

	public PersonCollaborator1(String name, String description, Set<PersonCollaborator2> children) {
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
	public Set<PersonCollaborator2> getChildren() {
		return children;
	}
	public void setChildren(Set<PersonCollaborator2> children) {
		this.children = children;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((orgCode == null) ? 0 : orgCode.hashCode());
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
		PersonCollaborator1 other = (PersonCollaborator1) obj;
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
		if (orgCode == null) {
			if (other.orgCode != null)
				return false;
		} else if (!orgCode.equals(other.orgCode))
			return false;
		return true;
	}

	public PersonCollaborator2 getPersonCollaborator2(String person2) {
		if(children != null){
			for(PersonCollaborator2 p2 : children){
				if(p2.getName().equals(person2)){
					return p2;
				}
			}
		}
		return null;
	}

	public void addPersonCollaborator2(PersonCollaborator2 p2) {
		if(this.children == null){
			this.children = new HashSet<PersonCollaborator2>();
		}
		this.children.add(p2);	
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}



}
