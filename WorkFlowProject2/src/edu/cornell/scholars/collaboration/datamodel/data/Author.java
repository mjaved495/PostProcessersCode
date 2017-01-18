package edu.cornell.scholars.collaboration.datamodel.data;

public class Author {

	private String authorName;
	private AuthorAffiliation authorAffiliation;
	private String country;
	private String state;
	private String cornellAffiliation;
	
	public Author(){
		
	}
	
	public Author(String authorName, AuthorAffiliation authorAffiliation, String country, String state,
			String cornellAffiliation) {
		super();
		this.authorName = authorName;
		this.authorAffiliation = authorAffiliation;
		this.country = country;
		this.state = state;
		this.cornellAffiliation = cornellAffiliation;
	}
	
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
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
	public String getCornellAffiliation() {
		return cornellAffiliation;
	}
	public void setCornellAffiliation(String cornellAffiliation) {
		if(cornellAffiliation != null && !cornellAffiliation.isEmpty()){
			this.cornellAffiliation = cornellAffiliation;
		}
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		String affiliation = "\"null\",\"null\"";
		if(authorAffiliation != null){
			affiliation = authorAffiliation.toString();
		}
		return "\"" + authorName + "\"," + affiliation + ",\"" + country
				+ "\",\"" + state + "\",\"" + cornellAffiliation + "\"";
	}
	
	
}
