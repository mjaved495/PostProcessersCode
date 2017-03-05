package edu.cornell.scholars.collaboration.globalcollabharvester;

public class Person2NetIdMasterMap {

	private String name;
	private String netId;
	
	public Person2NetIdMasterMap(String author, String netID) {
		super();
		this.name = author;
		this.netId = netID;
	}
	
	public Person2NetIdMasterMap() {
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((netId == null) ? 0 : netId.hashCode());
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
		Person2NetIdMasterMap other = (Person2NetIdMasterMap) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (netId == null) {
			if (other.netId != null)
				return false;
		} else if (!netId.equals(other.netId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "\"" + name + "\",\"" + netId + "\"";
	}
	
}
