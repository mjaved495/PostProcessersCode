package edu.cornell.scholars.collaborationwheel;

public class Collaboration {
	private String department1;
	private String personURI1;
	private String personName1;
	
	private String department2;
	private String personURI2;
	private String personName2;
	
	private String articleTitle;
	private String articleURI;
	private String date;
	
	public Collaboration(){
		
	}
	
	public Collaboration(
			String department1, String personURI1, String personName1, 
			String department2, String personURI2, String personName2, 
			String articleTitle, String articleURI, String date) {
		super();
		this.department1 = department1;
		this.personURI1 = personURI1;
		this.personName1 = personName1;
		this.setDepartment2(department2);
		this.personURI2 = personURI2;
		this.personName2 = personName2;
		this.articleTitle = articleTitle;
		this.articleURI = articleURI;
		this.date = date;
	}
	
	public String getDepartment1() {
		return department1;
	}
	public void setDepartment1(String department1) {
		this.department1 = department1;
	}
	public String getPersonURI1() {
		return personURI1;
	}
	public void setPersonURI1(String personURI1) {
		this.personURI1 = personURI1;
	}
	public String getPersonName1() {
		return personName1;
	}
	public void setPersonName1(String personName1) {
		this.personName1 = personName1;
	}
	public String getPersonURI2() {
		return personURI2;
	}
	public void setPersonURI2(String personURI2) {
		this.personURI2 = personURI2;
	}
	public String getPersonName2() {
		return personName2;
	}
	public void setPersonName2(String personName2) {
		this.personName2 = personName2;
	}
	public String getArticleURI() {
		return articleURI;
	}
	public void setArticleURI(String articleURI) {
		this.articleURI = articleURI;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public String getDepartment2() {
		return department2;
	}

	public void setDepartment2(String department2) {
		this.department2 = department2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articleTitle == null) ? 0 : articleTitle.hashCode());
		result = prime * result + ((articleURI == null) ? 0 : articleURI.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((department1 == null) ? 0 : department1.hashCode());
		result = prime * result + ((department2 == null) ? 0 : department2.hashCode());
		result = prime * result + ((personName1 == null) ? 0 : personName1.hashCode());
		result = prime * result + ((personName2 == null) ? 0 : personName2.hashCode());
		result = prime * result + ((personURI1 == null) ? 0 : personURI1.hashCode());
		result = prime * result + ((personURI2 == null) ? 0 : personURI2.hashCode());
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
		if (articleURI == null) {
			if (other.articleURI != null)
				return false;
		} else if (!articleURI.equals(other.articleURI))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (department1 == null) {
			if (other.department1 != null)
				return false;
		} else if (!department1.equals(other.department1))
			return false;
		if (department2 == null) {
			if (other.department2 != null)
				return false;
		} else if (!department2.equals(other.department2))
			return false;
		if (personName1 == null) {
			if (other.personName1 != null)
				return false;
		} else if (!personName1.equals(other.personName1))
			return false;
		if (personName2 == null) {
			if (other.personName2 != null)
				return false;
		} else if (!personName2.equals(other.personName2))
			return false;
		if (personURI1 == null) {
			if (other.personURI1 != null)
				return false;
		} else if (!personURI1.equals(other.personURI1))
			return false;
		if (personURI2 == null) {
			if (other.personURI2 != null)
				return false;
		} else if (!personURI2.equals(other.personURI2))
			return false;
		return true;
	}
	
	
	
}
