package edu.cornell.scholars.collaboration.datamodel.data;

public class Person {

	// name of the person
	private String personName;
	
	// Count of collaborations for this person
	//private Integer collabCount;
	
	// net Id if available
	private String netId;

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

//	public Integer getCollabCount() {
//		return collabCount;
//	}
//
//	public void setCollabCount(Integer collabCount) {
//		this.collabCount = collabCount;
//	}

	public String getNetId() {
		return netId;
	}

	public void setNetId(String netId) {
		this.netId = netId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((netId == null) ? 0 : netId.hashCode());
		result = prime * result + ((personName == null) ? 0 : personName.hashCode());
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
		Person other = (Person) obj;
		if (netId == null) {
			if (other.netId != null)
				return false;
		} else if (!netId.equals(other.netId))
			return false;
		if (personName == null) {
			if (other.personName != null)
				return false;
		} else if (!personName.equals(other.personName))
			return false;
		return true;
	}
	
	
	
}
