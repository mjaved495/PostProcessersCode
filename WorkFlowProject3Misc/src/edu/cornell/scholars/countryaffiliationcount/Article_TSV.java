package edu.cornell.scholars.countryaffiliationcount;

import java.util.List;

public class Article_TSV extends Article{

	private String affiliation;
	private String journalType;
	private String wosId;
	private String pubmedId;
	private String year;
	private List<String> wosCategories;
	private List<String> researchAreas;
	
	public Article_TSV() {
		super();	
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getJournalType() {
		return journalType;
	}

	public void setJournalType(String journalType) {
		this.journalType = journalType;
	}

	public String getWosId() {
		return wosId;
	}

	public void setWosId(String wosId) {
		this.wosId = wosId;
	}

	public String getPubmedId() {
		return pubmedId;
	}

	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
	}

	public List<String> getResearchAreas() {
		return researchAreas;
	}

	public void setResearchAreas(List<String> researchAreas) {
		this.researchAreas = researchAreas;
	}

	public List<String> getWosCategories() {
		return wosCategories;
	}

	public void setWosCategories(List<String> wosCategories) {
		this.wosCategories = wosCategories;
	}

	@Override
	public String toString() {
		return "\"" + getAuthor() + "\",\"" + affiliation + "\",\""+getTitle()+ "\",\"" +getJournal()+ "\",\"" +getLanguage()+ "\",\"" + journalType + "\",\"" + wosId
				+ "\",\"" + pubmedId + "\",\"" + year + "\",\"" + getIssn() + "\",\"" + getEissn() + "\",\"" + getDoi() + "\",\"" + getKeywords() +"\",\"" + wosCategories + "\",\""
				+ researchAreas + "\"";
	}
	
}
