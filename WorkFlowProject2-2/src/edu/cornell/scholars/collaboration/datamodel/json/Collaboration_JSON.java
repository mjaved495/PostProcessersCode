package edu.cornell.scholars.collaboration.datamodel.json;

import java.util.HashSet;
import java.util.Set;

public class Collaboration_JSON {

	private String name;
	private String description;
	private Set<Unit_JSON> children;
	
	public Collaboration_JSON(){
		this.children = new HashSet<>();
	}
	
	public Collaboration_JSON(String name, Set<Unit_JSON> children) {
		super();
		this.name = name;
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<Unit_JSON> getChildren() {
		return children;
	}
	public void setChildren(Set<Unit_JSON> children) {
		this.children = children;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
