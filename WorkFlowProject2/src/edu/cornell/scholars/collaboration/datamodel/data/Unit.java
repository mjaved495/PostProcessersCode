package edu.cornell.scholars.collaboration.datamodel.data;

import java.util.HashSet;
import java.util.Set;

public class Unit {

	// list of departments that are part of this unit
	private Set<Department> departments;
	// number of unit-level collaborations
	//private Integer collegeCount;
	// title of the unit
	private String unitName;

	public Unit(){
		departments = new HashSet<Department>();
	}
	
	
	public void addDepartments(Department dept) {
		this.departments.add(dept);
	}
	
	public Set<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(Set<Department> departments) {
		this.departments = departments;
	}

//	public Integer getCollegeCount() {
//		return collegeCount;
//	}
//
//	public void setCollegeCount(Integer collegeCount) {
//		this.collegeCount = collegeCount;
//	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((departments == null) ? 0 : departments.hashCode());
		result = prime * result + ((unitName == null) ? 0 : unitName.hashCode());
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
		Unit other = (Unit) obj;
		if (departments == null) {
			if (other.departments != null)
				return false;
		} else if (!departments.equals(other.departments))
			return false;
		if (unitName == null) {
			if (other.unitName != null)
				return false;
		} else if (!unitName.equals(other.unitName))
			return false;
		return true;
	}
	
	
	
}
