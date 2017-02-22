package edu.cornell.scholars.keywordminer.grants;

import java.util.HashSet;
import java.util.Set;

public class GrantEntriesData extends GrantEntries{

	private Set<String> words;
	private Set<String> meshTerms;
	private Set<String> keywords;
	private int minedKeywordCount;
	
	public Set<String> getWords() {
		return words;
	}

	public void setWords(Set<String> words) {
		this.words = words;
	}
	public void addWords(String word) {
		if(this.words == null){
			this.words = new HashSet<String>();
		}
		this.words.add(word);
	}

	public Set<String> getMeshTerms() {
		return meshTerms;
	}

	public void setMeshTerms(Set<String> meshTerms) {
		this.meshTerms = meshTerms;
	}
	public void addMeshTerms(String meshTerm) {
		if(this.meshTerms == null){
			this.meshTerms = new HashSet<String>();
		}
		this.meshTerms.add(meshTerm);
	}
	
	public Set<String> getKeywords() {
		return keywords;
	}
	
	public void addKeywords(String keyword) {
		if(this.keywords == null){
			this.keywords = new HashSet<String>();
		}
		this.keywords.add(keyword);
	}

	public int getMinedKeywordCount() {
		return minedKeywordCount;
	}

	public void setMinedKeywordCount(int minedKeywordCount) {
		this.minedKeywordCount = minedKeywordCount;
	}

	@Override
	public String toString() {
		return "\"" + getGrantURI() + "\",\"" + getGrantTitle().replaceAll("\"", "") + "\",\"" + minedKeywordCount + "\",\"" + meshTerms
				+ "\",\"" + keywords + "\"";
	}
	
	
	
}
