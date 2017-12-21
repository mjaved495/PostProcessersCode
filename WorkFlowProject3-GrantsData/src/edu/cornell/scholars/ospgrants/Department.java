package edu.cornell.scholars.ospgrants;

public class Department {

	String deptId;
	String deptName;
	String rollupName;
	String vivoURI;
	
	public Department(String deptId, String deptName, String rollupName, String deptVIVOURI) {
		this.deptId = deptId;
		this.deptName = deptName;
		this.rollupName = rollupName;
		this.vivoURI = deptVIVOURI;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getRollupName() {
		return rollupName;
	}

	public void setRollupName(String rollupName) {
		this.rollupName = rollupName;
	}

	public String getVivoURI() {
		return vivoURI;
	}

	public void setVivoURI(String vivoURI) {
		this.vivoURI = vivoURI;
	}

	@Override
	public String toString() {
		return "\""+deptId+"\",\"" + deptName + "\",\"" + rollupName + "\",\""
				+ vivoURI+ "\"";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deptId == null) ? 0 : deptId.hashCode());
		result = prime * result + ((deptName == null) ? 0 : deptName.hashCode());
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
		if (deptId == null) {
			if (other.deptId != null)
				return false;
		} else if (!deptId.equals(other.deptId))
			return false;
		if (deptName == null) {
			if (other.deptName != null)
				return false;
		} else if (!deptName.equals(other.deptName))
			return false;
		return true;
	}

	
	
}
