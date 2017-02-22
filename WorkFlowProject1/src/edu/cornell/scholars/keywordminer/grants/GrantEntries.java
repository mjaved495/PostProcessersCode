package edu.cornell.scholars.keywordminer.grants;

public class GrantEntries {

	private String grantURI;
	private String subjectAreas;
	private String grantTitle;
	private String grantAbstract;
	
	public String getGrantURI() {
		return grantURI;
	}
	public void setGrantURI(String articleURI) {
		this.grantURI = articleURI;
	}
	
	public String getSubjectArea() {
		return subjectAreas;
	}
	public void setSubjectAreas(String subjectAreas) {
		this.subjectAreas = subjectAreas;
	}
	public String getGrantTitle() {
		return grantTitle;
	}
	public void setGrantTitle(String articleTitle) {
		this.grantTitle = articleTitle;
	}
	public String getGrantAbstract() {
		return grantAbstract;
	}
	public void setGrantAbstract(String articleAbstract) {
		this.grantAbstract = articleAbstract;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grantAbstract == null) ? 0 : grantAbstract.hashCode());
		result = prime * result + ((grantTitle == null) ? 0 : grantTitle.hashCode());
		result = prime * result + ((grantURI == null) ? 0 : grantURI.hashCode());
		result = prime * result + ((subjectAreas == null) ? 0 : subjectAreas.hashCode());
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
		GrantEntries other = (GrantEntries) obj;
		if (grantAbstract == null) {
			if (other.grantAbstract != null)
				return false;
		} else if (!grantAbstract.equals(other.grantAbstract))
			return false;
		if (grantTitle == null) {
			if (other.grantTitle != null)
				return false;
		} else if (!grantTitle.equals(other.grantTitle))
			return false;
		if (grantURI == null) {
			if (other.grantURI != null)
				return false;
		} else if (!grantURI.equals(other.grantURI))
			return false;
		if (subjectAreas == null) {
			if (other.subjectAreas != null)
				return false;
		} else if (!subjectAreas.equals(other.subjectAreas))
			return false;
		return true;
	}
	
	
	
}
