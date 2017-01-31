package edu.cornell.scholars.countryaffiliationcount;

import java.util.List;

public class Article {

	// Name, Username, Propriety ID, Source, ISSN, eISSN, Publicate Type, Reporting date, Authors, Author URL, DOI, Journal, 
	// Language, Country, Pagination (start), Pagination (End), Publication date, Title, Citation count, SNIP rank, SJR rank, Keywords.
	private String author;
	private String netid;
	private String articleId;
	private String idSource;
	private String issn;
	private String eissn;
	private String publicationType;
	private String reportingDate;
	private String authors;
	private String authorsURL;
	private String doi;
	private String journal;
	private String language;
	private String country;
	private String pageStart;
	private String pageEnd;
	private String publicationDate;
	private String title;
	private int citationCount;
	private double snipRank;
	private double sjrRank;
	private List<String> keywords;
	
	public Article(){
		
	}

	
	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getNetid() {
		return netid;
	}


	public void setNetid(String netid) {
		this.netid = netid;
	}


	public String getArticleId() {
		return articleId;
	}


	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}


	public String getIdSource() {
		return idSource;
	}


	public void setIdSource(String idSource) {
		this.idSource = idSource;
	}


	public String getIssn() {
		return issn;
	}


	public void setIssn(String issn) {
		this.issn = issn;
	}


	public String getEissn() {
		return eissn;
	}


	public void setEissn(String eissn) {
		this.eissn = eissn;
	}


	public String getPublicationType() {
		return publicationType;
	}


	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
	}


	public String getReportingDate() {
		return reportingDate;
	}


	public void setReportingDate(String reportingDate) {
		this.reportingDate = reportingDate;
	}


	public String getAuthors() {
		return authors;
	}


	public void setAuthors(String authors) {
		this.authors = authors;
	}


	public String getAuthorsURL() {
		return authorsURL;
	}


	public void setAuthorsURL(String authorsURL) {
		this.authorsURL = authorsURL;
	}


	public String getDoi() {
		return doi;
	}


	public void setDoi(String doi) {
		if(!doi.isEmpty()){
			this.doi = doi;
		}
	}


	public String getJournal() {
		return journal;
	}


	public void setJournal(String journal) {
		this.journal = journal;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getPageStart() {
		return pageStart;
	}


	public void setPageStart(String pageStart) {
		this.pageStart = pageStart;
	}


	public String getPageEnd() {
		return pageEnd;
	}


	public void setPageEnd(String pageEnd) {
		this.pageEnd = pageEnd;
	}


	public String getPublicationDate() {
		return publicationDate;
	}


	public void setPublicationDate(String publicationDate) {
		if(publicationDate != null && !publicationDate.isEmpty()){
			this.publicationDate = publicationDate;
		}
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public int getCitationCount() {
		return citationCount;
	}


	public void setCitationCount(int citationCount) {
		this.citationCount = citationCount;
	}


	public double getSnipRank() {
		return snipRank;
	}


	public void setSnipRank(double snipRank) {
		this.snipRank = snipRank;
	}


	public double getSjrRank() {
		return sjrRank;
	}


	public void setSjrRank(double sjrRank) {
		this.sjrRank = sjrRank;
	}


	public List<String> getKeywords() {
		return keywords;
	}


	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}


	@Override
	public String toString() {
		return "Article [name=" + author + ", netid=" + netid + ", articleId=" + articleId + ", idSource=" + idSource
				+ ", issn=" + issn + ", eissn=" + eissn + ", publicationType=" + publicationType + ", reportingDate="
				+ reportingDate + ", authors=" + authors + ", authorsURL=" + authorsURL + ", doi=" + doi + ", journal="
				+ journal + ", language=" + language + ", country=" + country + ", pageStart=" + pageStart
				+ ", pageEnd=" + pageEnd + ", publicationDate=" + publicationDate + ", title=" + title
				+ ", citationCount=" + citationCount + ", snipRank=" + snipRank + ", sjrRank=" + sjrRank + ", keywords="
				+ keywords + "]";
	}

	
	
}

