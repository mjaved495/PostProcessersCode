package edu.cornell.scholars.collaboration.datamodel.data;

import java.util.HashSet;
import java.util.Set;

public class Department {

	// title of the department
	private String deptName;
	
	// number of dept-level collaborations
	//private Integer deparmtentCount;
	
	// list of persons that are collaborating from this department
	private Set<Person> personList;

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
//	public Integer getDeparmtentCount() {
//		return deparmtentCount;
//	}
//
//	public void setDeparmtentCount(Integer deparmtentCount) {
//		this.deparmtentCount = deparmtentCount;
//	}

	public void addPerson(Person per){
		if(this.personList == null){
			this.personList = new HashSet<Person>();
		}
		this.personList.add(per);
	}
	public Set<Person> getPersonList() {
		return personList;
	}

	public void setPersonList(Set<Person> personList) {
		this.personList = personList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deptName == null) ? 0 : deptName.hashCode());
		result = prime * result + ((personList == null) ? 0 : personList.hashCode());
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
		if (deptName == null) {
			if (other.deptName != null)
				return false;
		} else if (!deptName.equals(other.deptName))
			return false;
		if (personList == null) {
			if (other.personList != null)
				return false;
		} else if (!personList.equals(other.personList))
			return false;
		return true;
	}
	
	
	
	
	
}
