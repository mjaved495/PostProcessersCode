package edu.cornell.scholars.collaboration.datamodel.data;

public class Collaboration {

	// "\""+wosId+"\",\""+year+"\",\""+homeUnitName+"\",\""+unitName+"\",\""+departmentName+"\",\""+personName+"\","+netId
	private String wosId;
	private String articleTitle;
	private Integer year;
	private String homeUnitName;
	private String collabUnitName;
	private String departmentName;
	private String personName;
	private String netId;
	private String description;
	private String departmentCode;
	private String pubmedId;
	
	
	public Collaboration(){
		
	}

	public Collaboration(String wosId, Integer year, String homeUnitName, String collabUnitName, String departmentName,
			String personName, String netId, String title, String pubmedId) {
		super();
		this.wosId = wosId;
		this.year = year;
		this.homeUnitName = homeUnitName;
		this.collabUnitName = collabUnitName;
		this.departmentName = departmentName;
		this.personName = personName;
		this.netId = netId;
		this.articleTitle = title;
		this.setPubmedId(pubmedId);
	}
	public String getWosId() {
		return wosId;
	}
	public void setWosId(String wosId) {
		this.wosId = wosId;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getHomeUnitName() {
		return homeUnitName;
	}
	public void setHomeUnitName(String homeUnitName) {
		this.homeUnitName = homeUnitName;
	}
	public String getCollabUnitName() {
		return collabUnitName;
	}
	public void setCollabUnitName(String collabUnitName) {
		this.collabUnitName = collabUnitName;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getNetId() {
		return netId;
	}
	public void setNetId(String netId) {
		this.netId = netId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public String getPubmedId() {
		return pubmedId;
	}

	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articleTitle == null) ? 0 : articleTitle.hashCode());
		result = prime * result + ((collabUnitName == null) ? 0 : collabUnitName.hashCode());
		result = prime * result + ((departmentCode == null) ? 0 : departmentCode.hashCode());
		result = prime * result + ((departmentName == null) ? 0 : departmentName.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((homeUnitName == null) ? 0 : homeUnitName.hashCode());
		result = prime * result + ((netId == null) ? 0 : netId.hashCode());
		result = prime * result + ((personName == null) ? 0 : personName.hashCode());
		result = prime * result + ((pubmedId == null) ? 0 : pubmedId.hashCode());
		result = prime * result + ((wosId == null) ? 0 : wosId.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		Collaboration other = (Collaboration) obj;
		if (articleTitle == null) {
			if (other.articleTitle != null)
				return false;
		} else if (!articleTitle.equals(other.articleTitle))
			return false;
		if (collabUnitName == null) {
			if (other.collabUnitName != null)
				return false;
		} else if (!collabUnitName.equals(other.collabUnitName))
			return false;
		if (departmentCode == null) {
			if (other.departmentCode != null)
				return false;
		} else if (!departmentCode.equals(other.departmentCode))
			return false;
		if (departmentName == null) {
			if (other.departmentName != null)
				return false;
		} else if (!departmentName.equals(other.departmentName))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (homeUnitName == null) {
			if (other.homeUnitName != null)
				return false;
		} else if (!homeUnitName.equals(other.homeUnitName))
			return false;
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
		if (pubmedId == null) {
			if (other.pubmedId != null)
				return false;
		} else if (!pubmedId.equals(other.pubmedId))
			return false;
		if (wosId == null) {
			if (other.wosId != null)
				return false;
		} else if (!wosId.equals(other.wosId))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}
	
	
	
}
