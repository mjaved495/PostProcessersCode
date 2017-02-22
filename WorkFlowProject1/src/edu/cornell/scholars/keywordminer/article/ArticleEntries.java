package edu.cornell.scholars.keywordminer.article;

public class ArticleEntries {

	private String articleURI;
	private String subjectAreas;
	private String articleTitle;
	private String articleAbstract;
	
	public String getArticleURI() {
		return articleURI;
	}
	public void setArticleURI(String articleURI) {
		this.articleURI = articleURI;
	}
	
	public String getSubjectArea() {
		return subjectAreas;
	}
	public void setSubjectAreas(String subjectAreas) {
		this.subjectAreas = subjectAreas;
	}
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public String getArticleAbstract() {
		return articleAbstract;
	}
	public void setArticleAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articleAbstract == null) ? 0 : articleAbstract.hashCode());
		result = prime * result + ((articleTitle == null) ? 0 : articleTitle.hashCode());
		result = prime * result + ((articleURI == null) ? 0 : articleURI.hashCode());
		result = prime * result + ((subjectAreas == null) ? 0 : subjectAreas.hashCode());
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
		ArticleEntries other = (ArticleEntries) obj;
		if (articleAbstract == null) {
			if (other.articleAbstract != null)
				return false;
		} else if (!articleAbstract.equals(other.articleAbstract))
			return false;
		if (articleTitle == null) {
			if (other.articleTitle != null)
				return false;
		} else if (!articleTitle.equals(other.articleTitle))
			return false;
		if (articleURI == null) {
			if (other.articleURI != null)
				return false;
		} else if (!articleURI.equals(other.articleURI))
			return false;
		if (subjectAreas == null) {
			if (other.subjectAreas != null)
				return false;
		} else if (!subjectAreas.equals(other.subjectAreas))
			return false;
		return true;
	}
	
	
	
}
