package edu.cornell.scholars.collaboration.gridmapper;

public class ArticleEntryModel {

	private String personName;
	private String netId;
	private String articleURI;
	private String wosId;
	private String pubmedId;
	
	public ArticleEntryModel(){}
	
	public ArticleEntryModel(String personName, String netId, String articleURI, String wosId, String pmdId) {
		super();
		this.personName = personName;
		this.netId = netId;
		this.articleURI = articleURI;
		this.wosId = wosId;
		this.pubmedId = pmdId;
	}
	
	public String getPersoName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getNetId() {
		return netId;
	}
	public void setNetId(String netId) {
		this.netId = netId;
	}
	public String getArticleURI() {
		return articleURI;
	}
	public void setArticleURI(String articleURI) {
		this.articleURI = articleURI;
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
	
	
}
