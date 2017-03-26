package edu.cornell.scholars.collaborationwheel;

import java.util.HashSet;
import java.util.Set;

public class PersonCollaborator2 {
	
	private String name;
	private String description;
	private String orgCode;
	private Set<Article> pubs;
	private String uri;
	
	public Set<Article> getPubs() {
		return pubs;
	}

	public void setPubs(Set<Article> pubs) {
		this.pubs = pubs;
	}

	private int size;
	
	public PersonCollaborator2(){
		
	}
	
	public PersonCollaborator2(String name, String description, Set<Article> pubs, int size) {
		super();
		this.name = name;
		this.description = description;
		this.pubs = pubs;
		this.setSize(size);
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
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((orgCode == null) ? 0 : orgCode.hashCode());
		result = prime * result + ((pubs == null) ? 0 : pubs.hashCode());
		result = prime * result + size;
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
		PersonCollaborator2 other = (PersonCollaborator2) obj;
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
		if (pubs == null) {
			if (other.pubs != null)
				return false;
		} else if (!pubs.equals(other.pubs))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	public void addArticle(Article art) {
		if(pubs == null){
			pubs = new HashSet<Article>();
		}
		pubs.add(art);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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
