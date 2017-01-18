package edu.cornell.scholars.keywordcloudgenerator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Keyword implements Comparable<Keyword>{

	private Set<Person> persons;
	private Integer countOfArticle;
	private Integer countOfPerson;
	private String keyword;
	private Set<String> types;

	public Set<Person> getPersons() {
		return persons;
	}
	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}
	public void addPerson(Person person) {
		if(persons == null){
			persons = new HashSet<Person>();
		}
		this.persons.add(person);
	}
	public Integer getCountByPerson() {
		return countOfPerson;
	}
	public void setCountOfPerson(Integer countByPerson) {
		this.countOfPerson = countByPerson;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String toString() {
		String str = "\""+this.keyword+"\",\"";

		for(Person person: persons){
			str = str.concat(person.getPersonName()+",");
		}
		str = str.trim().substring(0, str.length()-1).concat("\",\""+persons.size()+"\",\""+countOfArticle+"\"");

		return str;
	}

	@Override
	public int compareTo(Keyword o) {
		return this.keyword.compareTo(o.getKeyword());
	}
	
	
	public Integer getCountOfArticle() {
		return countOfArticle;
	}
	public void setCountOfArticle(Integer countOfArticle) {
		this.countOfArticle = countOfArticle;
	}


	public Set<String> getTypes() {
		return types;
	}
	public void setTypes(Set<String> types) {
		this.types = types;
	}
	public void addTypes(String type) {
		if(types == null){
			types = new HashSet<String>();
		}
		this.types.add(type);
	}

	public static class SortByPersonCount implements Comparator<Keyword>{ //inner class to sort by title
		@Override
		public int compare(Keyword kw1, Keyword kw2) {
			return kw2.getCountByPerson().compareTo(kw1.getCountByPerson());
		}
	}
	
}
