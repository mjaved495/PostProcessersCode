package edu.cornell.scholars.collaboration.globalcollabharvester;

public class Person2ArticleMap {

	private String name;
	private String netId;
	private String articleURI;
	private String wosId;
	private String pubmedId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articleURI == null) ? 0 : articleURI.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((netId == null) ? 0 : netId.hashCode());
		result = prime * result + ((pubmedId == null) ? 0 : pubmedId.hashCode());
		result = prime * result + ((wosId == null) ? 0 : wosId.hashCode());
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
		Person2ArticleMap other = (Person2ArticleMap) obj;
		if (articleURI == null) {
			if (other.articleURI != null)
				return false;
		} else if (!articleURI.equals(other.articleURI))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (netId == null) {
			if (other.netId != null)
				return false;
		} else if (!netId.equals(other.netId))
			return false;
		if (pubmedId == null) {
			if (other.pubmedId != null)
				return false;
		} else if (!pubmedId.equals(other.pubmedId))
			return false;
		if (wosId == null) {
			if (other.wosId != null)
				return false;
		} else if (!wosId.equals(other.wosId))
			return false;
		return true;
	}
	
	
	
}
