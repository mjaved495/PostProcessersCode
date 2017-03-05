package edu.cornell.scholars.collaboration.datamodel.data;

import java.util.HashSet;
import java.util.Set;

//International Collaboration POJO
public class ExternalCollaboration {
	private String articleId;
	private String articleIdType;
	private String articleURI;
	private String articleTitle;
	private String yearOfPublication;
	private Set<String> subjectAreas;
	private Set<Author> authors;

	public ExternalCollaboration(){	
	}

	public ExternalCollaboration(String articleId, String articleIdType, String articleURI, String articleTitle,
			String yearOfPublication, Set<Author> authors, Set<String> subjectAreas) {
		super();
		this.articleId = articleId;
		this.articleIdType = articleIdType;
		this.articleURI = articleURI;
		this.articleTitle = articleTitle;
		this.yearOfPublication = yearOfPublication;
		this.authors = authors;
		this.subjectAreas = subjectAreas;
	}

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getArticleIdType() {
		return articleIdType;
	}

	public void setArticleIdType(String articleIdType) {
		this.articleIdType = articleIdType;
	}

	public String getArticleURI() {
		return articleURI;
	}

	public void setArticleURI(String articleURI) {
		this.articleURI = articleURI;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public String getYearOfPublication() {
		return yearOfPublication;
	}

	public void setYearOfPublication(String yearOfPublication) {
		this.yearOfPublication = yearOfPublication;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public Set<String> getSubjectAreas() {
		return subjectAreas;
	}

	public void setSubjectAreas(Set<String> subjectAreas) {
		this.subjectAreas = subjectAreas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articleId == null) ? 0 : articleId.hashCode());
		result = prime * result + ((articleIdType == null) ? 0 : articleIdType.hashCode());
		result = prime * result + ((articleTitle == null) ? 0 : articleTitle.hashCode());
		result = prime * result + ((articleURI == null) ? 0 : articleURI.hashCode());
		result = prime * result + ((authors == null) ? 0 : authors.hashCode());
		result = prime * result + ((subjectAreas == null) ? 0 : subjectAreas.hashCode());
		result = prime * result + ((yearOfPublication == null) ? 0 : yearOfPublication.hashCode());
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
		ExternalCollaboration other = (ExternalCollaboration) obj;
		if (articleId == null) {
			if (other.articleId != null)
				return false;
		} else if (!articleId.equals(other.articleId))
			return false;
		if (articleIdType == null) {
			if (other.articleIdType != null)
				return false;
		} else if (!articleIdType.equals(other.articleIdType))
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
		if (authors == null) {
			if (other.authors != null)
				return false;
		} else if (!authors.equals(other.authors))
			return false;
		if (subjectAreas == null) {
			if (other.subjectAreas != null)
				return false;
		} else if (!subjectAreas.equals(other.subjectAreas))
			return false;
		if (yearOfPublication == null) {
			if (other.yearOfPublication != null)
				return false;
		} else if (!yearOfPublication.equals(other.yearOfPublication))
			return false;
		return true;
	}

	public void addAuthor(Author author) {
		if(authors == null){
			authors = new HashSet<Author>();
		}
		authors.add(author);
	}

	


}
