package edu.cornell.scholars.collaboration.datamodel.data;

public class ArticleToIdMap {

	private String articleURI;
	private String wosId;
	private String pubmedId;
	
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
	
	@Override
	public String toString() {
		return articleURI + "," + wosId + "," + pubmedId;
	}
	
}
