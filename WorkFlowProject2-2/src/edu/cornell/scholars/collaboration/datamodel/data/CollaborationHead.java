package edu.cornell.scholars.collaboration.datamodel.data;

import java.util.HashSet;
import java.util.Set;


public class CollaborationHead {

	// list of units involved in collaboration
	private Set<Unit> units;

	// number of total collaborations
	//private Integer collabCount;
		
	// title of the home unit against whom we are counting the collaborations.
	private String homeUnitName;

	private String wosId;
	private String pubmedId;
	private String articleTitle;
	
	private String year;
	
	public CollaborationHead(){
		units = new HashSet<Unit>();
	}
	
	
	public Set<Unit> getUnits() {
		return units;
	}

	public void addUnit(Unit u) {
		this.units.add(u);
	}
	
	public void setUnits(Set<Unit> units) {
		this.units = units;
	}

//	public Integer getCollabCount() {
//		return collabCount;
//	}
//
//	public void setCollabCount(Integer collabCount) {
//		this.collabCount = collabCount;
//	}

	public String getHomeUnitName() {
		return homeUnitName;
	}

	public void setHomeUnitName(String homeUnitName) {
		this.homeUnitName = homeUnitName;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
	}


	public String getWosId() {
		return wosId;
	}


	public void setWosId(String wosId) {
		this.wosId = wosId;
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
		result = prime * result + ((homeUnitName == null) ? 0 : homeUnitName.hashCode());
		result = prime * result + ((pubmedId == null) ? 0 : pubmedId.hashCode());
		result = prime * result + ((units == null) ? 0 : units.hashCode());
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
		CollaborationHead other = (CollaborationHead) obj;
		if (articleTitle == null) {
			if (other.articleTitle != null)
				return false;
		} else if (!articleTitle.equals(other.articleTitle))
			return false;
		if (homeUnitName == null) {
			if (other.homeUnitName != null)
				return false;
		} else if (!homeUnitName.equals(other.homeUnitName))
			return false;
		if (pubmedId == null) {
			if (other.pubmedId != null)
				return false;
		} else if (!pubmedId.equals(other.pubmedId))
			return false;
		if (units == null) {
			if (other.units != null)
				return false;
		} else if (!units.equals(other.units))
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
