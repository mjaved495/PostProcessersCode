package edu.cornell.scholars.optingraphbuilder;

import java.util.HashSet;
import java.util.Set;

public class PersonEntity {

	private String personURI;
	private String netId;
	private Set<String> department;
	private Set<String> college;
	
	public PersonEntity(String personURI, String netId) {
		super();
		this.personURI = personURI;
		this.netId = netId;
	}
	
	public PersonEntity(String personURI, String netId, Set<String> department, Set<String> college) {
		super();
		this.personURI = personURI;
		this.netId = netId;
		this.department = department;
		this.college = college;
	}
	
	public String getPersonURI() {
		return personURI;
	}
	public void setPersonURI(String personURI) {
		this.personURI = personURI;
	}
	public String getNetId() {
		return netId;
	}
	public void setNetId(String netId) {
		this.netId = netId;
	}
	public Set<String> getDepartment() {
		return department;
	}
	public void setDepartment(Set<String> department) {
		this.department = department;
	}
	public Set<String> getCollege() {
		return college;
	}
	public void setCollege(Set<String> college) {
		this.college = college;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((college == null) ? 0 : college.hashCode());
		result = prime * result + ((department == null) ? 0 : department.hashCode());
		result = prime * result + ((netId == null) ? 0 : netId.hashCode());
		result = prime * result + ((personURI == null) ? 0 : personURI.hashCode());
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
		PersonEntity other = (PersonEntity) obj;
		if (college == null) {
			if (other.college != null)
				return false;
		} else if (!college.equals(other.college))
			return false;
		if (department == null) {
			if (other.department != null)
				return false;
		} else if (!department.equals(other.department))
			return false;
		if (netId == null) {
			if (other.netId != null)
				return false;
		} else if (!netId.equals(other.netId))
			return false;
		if (personURI == null) {
			if (other.personURI != null)
				return false;
		} else if (!personURI.equals(other.personURI))
			return false;
		return true;
	}

	public void addDepartment(String dep) {
		if (department == null) {
			department = new HashSet<String>();
		}
		department.add(dep);
	}

	public void addCollege(String coll) {
		if (college == null) {
			college = new HashSet<String>();
		}
		college.add(coll);
	}
	
	
}
