package edu.cornell.scholars.collaboration.datamodel.data;

import java.util.ArrayList;
import java.util.List;

//International Collaboration POJO
public class ExternalCollaboration {
	private String id;
	private String idType;
	private String scholarURI;
	private String articleTitle;
	private String articleDoi;
	private String yearOfPublication;
	private List<Author> authors;
	
	public ExternalCollaboration(){
		
	}
	public ExternalCollaboration(String id, String idType, String scholarURI, String articleTitle, String articleDoi,
			String yearOfPublication, List<Author> authors) {
		super();
		this.id = id;
		this.idType = idType;
		this.scholarURI = scholarURI;
		this.articleTitle = articleTitle;
		this.articleDoi = articleDoi;
		this.yearOfPublication = yearOfPublication;
		this.authors = authors;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getScholarURI() {
		return scholarURI;
	}
	public void setScholarURI(String scholarURI) {
		this.scholarURI = scholarURI;
	}
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public String getArticleDoi() {
		return articleDoi;
	}
	public void setArticleDoi(String articleDoi) {
		this.articleDoi = articleDoi;
	}
	public String getYearOfPublication() {
		return yearOfPublication;
	}
	public void setYearOfPublication(String yearOfPublication) {
		this.yearOfPublication = yearOfPublication;
	}
	public List<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(List<Author> authors) {
		if(this.authors ==null){
			this.authors = new ArrayList<Author>();
		}
		this.authors = authors;
	}
	public void addAuthor(Author author) {
		if(this.authors == null){
			this.authors = new ArrayList<Author>();
		}
		this.authors.add(author);
	}
	@Override
	public String toString() {
		 return "\"" + id + "\",\"" + idType + "\",\"" + scholarURI
				+ "\",\"" + articleTitle.replaceAll("\"", "") + "\",\"" + articleDoi + "\",\""
				+ yearOfPublication + "\"";
	}
	
	
}
