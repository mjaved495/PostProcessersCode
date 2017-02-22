package edu.cornell.scholars.keywordcloudgenerator;

import java.util.HashMap;
import java.util.Map;

public class Person {

	private String personName;
	private String personURI;
	private Integer count;
	private Map<String, String> dt_yr_map = new HashMap<String, String>();
	
	
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



	public Integer getArticleCount() {
		return count;
	}

	public void setArticleCount(Integer count) {
		this.count = count;
	}

	public Map<String, String> getDt_yr_map() {
		return dt_yr_map;
	}

	public void setDt_yr_map(Map<String, String> dt_yr_map) {
		this.dt_yr_map = dt_yr_map;
	}
	
	
	
}
