package edu.cornell.scholars.collaboration.datamodel.data;

import java.util.Set;

public class Author {

	private String authorName;
	private String authorURI;
	private AuthorAffiliation authorAffiliation;
	private String country;
	private String state;
	private Set<String> cornellAffiliation;

	public Author(){

	}

	public Author(String authorName, String authorURI, AuthorAffiliation authorAffiliation, String country,
			String state, Set<String> cornellAffiliation) {
		super();
		this.authorName = authorName;
		this.authorURI = authorURI;
		this.authorAffiliation = authorAffiliation;
		this.country = country;
		this.state = state;
		this.cornellAffiliation = cornellAffiliation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authorAffiliation == null) ? 0 : authorAffiliation.hashCode());
		result = prime * result + ((authorName == null) ? 0 : authorName.hashCode());
		result = prime * result + ((authorURI == null) ? 0 : authorURI.hashCode());
		result = prime * result + ((cornellAffiliation == null) ? 0 : cornellAffiliation.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		Author other = (Author) obj;
		if (authorAffiliation == null) {
			if (other.authorAffiliation != null)
				return false;
		} else if (!authorAffiliation.equals(other.authorAffiliation))
			return false;
		if (authorName == null) {
			if (other.authorName != null)
				return false;
		} else if (!authorName.equals(other.authorName))
			return false;
		if (authorURI == null) {
			if (other.authorURI != null)
				return false;
		} else if (!authorURI.equals(other.authorURI))
			return false;
		if (cornellAffiliation == null) {
			if (other.cornellAffiliation != null)
				return false;
		} else if (!cornellAffiliation.equals(other.cornellAffiliation))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorURI() {
		return authorURI;
	}

	public void setAuthorURI(String authorURI) {
		this.authorURI = authorURI;
	}

	public AuthorAffiliation getAuthorAffiliation() {
		return authorAffiliation;
	}

	public void setAuthorAffiliation(AuthorAffiliation authorAffiliation) {
		this.authorAffiliation = authorAffiliation;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Set<String> getCornellAffiliation() {
		return cornellAffiliation;
	}

	public void setCornellAffiliation(Set<String> cornellAffiliation) {
		this.cornellAffiliation = cornellAffiliation;
	}

	
}
