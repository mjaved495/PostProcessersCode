package edu.cornell.scholars.countryaffiliationcount;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CountryLevelCollaborationCounter {
	
	private Set<String> affiliationStringSet = new HashSet<String>();
	
	private String inputfile = "/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/QUERY_RESULTS/2017-01-25/wosdata/WOSDataFile.csv";
	
	public static void main(String[] args) {
		CountryLevelCollaborationCounter obj = new CountryLevelCollaborationCounter();
		List<Article_TSV> data = obj.readFile(obj.inputfile);
		Map<String, Set<String>> countryMap = obj.getCountryMap(data, "FRANCE");
	}

	private Map<String, Set<String>> getCountryMap(List<Article_TSV> data, String countryName) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		Set<String> wosIds = new HashSet<String>();
		for(Article_TSV art: data){
			String affString = art.getAffiliation();
			if(affString.toUpperCase().contains(countryName.toUpperCase())){
				//System.out.println(affString +","+art.getWosId());
				wosIds.add(art.getWosId());
			}
			
		}
		map.put(countryName, wosIds);
		System.out.println(countryName+":"+wosIds.size());
		return map;
	}

	private List<Article_TSV> readFile(String filePath){
		int count = 0;
		List<Article_TSV> articles = new ArrayList<Article_TSV>();
		try{
			FileInputStream is = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader buf = new BufferedReader(isr);
			String lineJustFetched = null;
			while(true){
				lineJustFetched = buf.readLine();
				Article_TSV article = new Article_TSV();
				if(lineJustFetched == null){  
					break; 
				}else{
					if(lineJustFetched.trim().length() == 0){
						continue;
					}
					//System.out.println(lineJustFetched);
					String[] nextLine = lineJustFetched.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
					article.setAuthor(nextLine[0].replaceAll("\\\"", ""));
					article.setAffiliation(nextLine[1].replaceAll("\\\"", ""));
					article.setTitle(nextLine[2].replaceAll("\\\"", ""));
					article.setJournal(nextLine[3].replaceAll("\\\"", ""));
					article.setLanguage(nextLine[4].replaceAll("\\\"", ""));
					article.setPublicationType(nextLine[5].replaceAll("\\\"", ""));
					article.setWosId(nextLine[6].replaceAll("\\\"", ""));
					article.setPubmedId(nextLine[7].replaceAll("\\\"", ""));
					article.setYear(nextLine[8].replaceAll("\\\"", ""));
					article.setIssn(nextLine[9].replaceAll("\\\"", ""));
					article.setEissn(nextLine[10].replaceAll("\\\"", ""));
					article.setDoi(nextLine[11].replaceAll("\\\"", ""));
					article.setKeywords(getSplitList(nextLine[12].replaceAll("\\\"", "")));
					article.setWosCategories(getSplitList(nextLine[13].replaceAll("\\\"", "")));
					article.setResearchAreas(getSplitList(nextLine[14].replaceAll("\\\"", "")));	
					articles.add(article); 

					if(!affiliationStringSet.contains(article.getAffiliation())){
						affiliationStringSet.add(article.getAffiliation());
						//System.out.println(article.getAffiliation());
					}
					count++;
				}
			}
			buf.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(count+" lines read.");
		return articles;
	}	

	private List<String> getSplitList(String string) {
		string = string.replaceAll("[\\[\\]]", "").trim();
		List<String> data = new ArrayList<String>(Arrays.asList(string.split(",")));
		return data;
	}
}
