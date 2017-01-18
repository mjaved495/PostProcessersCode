package edu.cornell.scholars.keywordcloudgenerator;

public class Person {

	private String personName;
	private String personURI;
	private Integer count;
	
	
	public Person(String personName, String personURI) {
		super();
		this.personName = personName;
		this.personURI = personURI;
	}
	
	public Person() {
		// TODO Auto-generated constructor stub
	}

	public String getPersonURI() {
		return personURI;
	}
	public void setPersonURI(String personURI) {
		this.personURI = personURI;
	}
	public String getPersonName() {
		return personName.replaceAll(",", " ");
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((personName == null) ? 0 : personName.hashCode());
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
		Person other = (Person) obj;
		if (personName == null) {
			if (other.personName != null)
				return false;
		} else if (!personName.equals(other.personName))
			return false;
		if (personURI == null) {
			if (other.personURI != null)
				return false;
		} else if (!personURI.equals(other.personURI))
			return false;
		return true;
	}

	public Integer getArticleCount() {
		return count;
	}

	public void setArticleCount(Integer count) {
		this.count = count;
	}
	
	
	
}
