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
	private Set<String> dates;

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

	public Set<String> getDates() {
		return dates;
	}
	public void setDates(Set<String> dates) {
		this.dates = dates;
	}
	public void addDates(String date) {
		if(this.dates == null){
			this.dates = new HashSet<String>();
		}
		this.dates.add(date);
	}
	
	public static class SortByPersonCount implements Comparator<Keyword>{ //inner class to sort by title
		@Override
		public int compare(Keyword kw1, Keyword kw2) {
			return kw2.getCountByPerson().compareTo(kw1.getCountByPerson());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((countOfArticle == null) ? 0 : countOfArticle.hashCode());
		result = prime * result + ((countOfPerson == null) ? 0 : countOfPerson.hashCode());
		result = prime * result + ((dates == null) ? 0 : dates.hashCode());
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + ((persons == null) ? 0 : persons.hashCode());
		result = prime * result + ((types == null) ? 0 : types.hashCode());
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
		Keyword other = (Keyword) obj;
		if (countOfArticle == null) {
			if (other.countOfArticle != null)
				return false;
		} else if (!countOfArticle.equals(other.countOfArticle))
			return false;
		if (countOfPerson == null) {
			if (other.countOfPerson != null)
				return false;
		} else if (!countOfPerson.equals(other.countOfPerson))
			return false;
		if (dates == null) {
			if (other.dates != null)
				return false;
		} else if (!dates.equals(other.dates))
			return false;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (persons == null) {
			if (other.persons != null)
				return false;
		} else if (!persons.equals(other.persons))
			return false;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}
	
	
	
	
}
