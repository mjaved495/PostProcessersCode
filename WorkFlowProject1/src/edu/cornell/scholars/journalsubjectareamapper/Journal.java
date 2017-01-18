package edu.cornell.scholars.journalsubjectareamapper;

import java.util.HashSet;
import java.util.Set;

public class Journal {
	
	private String uri;
	private String title;
	private String issn;
	private String eissn;
	private Set<String> wos_subjectAreas;
	private String for_subjectArea;
	
	public Journal(){
	}

	public Journal(Journal j){
		this.uri = j.getUri();
		this.title = j.getTitle();
		this.issn= j.getIssn();
		this.eissn = j.getEissn();
		this.wos_subjectAreas = j.getWOSSubjectAreas();
		this.for_subjectArea = j.getFORSubjectArea();
	}
	
	public String getFORSubjectArea() {
		return for_subjectArea;
	}

	public void setForSubjectArea(String forSubjectArea) {
		this.for_subjectArea = forSubjectArea;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public Set<String> getWOSSubjectAreas() {
		return wos_subjectAreas;
	}

	public void setWOSSubjectAreas(Set<String> subjectAreas) {
		this.wos_subjectAreas = subjectAreas;
	}
	public void addWOSSubjectAreas(String subjectArea) {
		if(this.wos_subjectAreas == null){
			this.wos_subjectAreas = new HashSet<String>();
		}
		this.wos_subjectAreas.add(subjectArea);
	}

	@Override
	public String toString() {
		return getUri()+ ",\"" + getTitle() + "\",\"" + getIssn() +"\",\""+ getEissn() + "\",\""+ getWOSSubjectAreas()+"\",\""+for_subjectArea+"\"";
	}
	
}
