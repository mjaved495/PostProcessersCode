package edu.scholars.cornell.orgcoauthorships;

public class CoauthData {
	
	private String source;
	private String target;
	private String articleURI;

	public CoauthData(String source, String target, String articleURI) {
		super();
		this.source = source;
		this.target = target;
		this.articleURI = articleURI;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getArticleURI() {
		return articleURI;
	}
	public void setArticleURI(String articleURI) {
		this.articleURI = articleURI;
	}

	
	
}

