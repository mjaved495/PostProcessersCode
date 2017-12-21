package edu.cornell.scholars.collaboration.globalcollabharvester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

import edu.cornell.scholars.collaboration.datamodel.data.Author;
import edu.cornell.scholars.collaboration.gridmapper.Article_TSV;
import edu.cornell.scholars.config.Configuration;

public class CornellAffiliationFinder {

	private Map<String, Set<String>> affiliationMap = null;
	private static String NETID_2_UNITS_MAP_FILE = Configuration.QUERY_RESULTSET_FOLDER + "/" 
			+ Configuration.date + "/" + Configuration.NETID_2_UNITS_MAP_FILE ; 
	
	public CornellAffiliationFinder(){
		affiliationMap = readNetId2UnitMap(new File(NETID_2_UNITS_MAP_FILE));
	}
	
	public Author getCornellAffiliation(Article_TSV entry, Map<String, Person2ArticleMap> person2articleMap) {
		
		Collection<Person2ArticleMap> values = person2articleMap.values();
		String wosId = entry.getWosId();
		String pmId = entry.getPubmedId();
		boolean articleFound = false;
		String uri = null;
		
		/**
		 * Match the article URI
		 */
		for(Person2ArticleMap value: values){
			String vwosId = value.getWosId();
			String vpmId = value.getPubmedId();
			if(wosId != null && vwosId != null && wosId.trim().equals(vwosId.trim())){
				//System.out.println("Article Matched");
				articleFound = true;
			}else if((pmId != null && vpmId != null) && (pmId.trim().equals(vpmId.trim()) && (!pmId.isEmpty() && !vpmId.isEmpty()))){
				//System.out.println("Article Matched");
				articleFound = true;
			}
			if(articleFound){
				uri = value.getArticleURI();
				break;
			}
		}
		
		/**
		 * Get rows that are associated to this article only.
		 */
		Set<String> keys = person2articleMap.keySet();
		Set<String> article_keys = new HashSet<String>();
		for(String key: keys){
			String trimmedURI = key.substring(key.indexOf("-")+1);
			if(trimmedURI.equals(uri)){
				article_keys.add(key);
			}
		}
		
		String auth = null;
		Author person = null;
		try{
			String author = entry.getAuthor();
			String lastNameE  = author.substring(0, author.indexOf(",")).trim();
			int i = author.indexOf(",");
			String givenNameE = null;
			if(author.indexOf(" ", i+2)>0){
				givenNameE = author.substring(i+1, author.indexOf(" ", i+2)).trim();
			}else{
				givenNameE = author.substring(i+1).trim();
			}
			for(String key: article_keys){
				Person2ArticleMap obj = person2articleMap.get(key);
				auth = obj.getName();
				String lastName  = auth.substring(0, auth.indexOf(",")).trim();
				int j = auth.indexOf(",");
				String givenName = null;
				if(auth.indexOf(" ", j+2)>0){
					givenName = auth.substring(j+1, auth.indexOf(" ", j+2)).trim();
				}else{
					givenName = auth.substring(j+1).trim();
				}
				
				if(lastNameE.equalsIgnoreCase(lastName)){
					if(givenNameE.equalsIgnoreCase(givenName)){
//						System.out.println(auth);
//						System.out.println(entry.getAuthor());
						String netId = obj.getNetId();
						person = new Author();
						person.setAuthorName(auth);
						person.setAuthorURI("http://scholars.cornell.edu/individual/"+netId);
						person.setCornellAffiliation(getAffiliations(netId));
						break;
					}else if(givenNameE.substring(0, 1).equalsIgnoreCase(givenName.substring(0, 1))){
//						System.err.println(auth);
//						System.err.println(entry.getAuthor());
						String netId = obj.getNetId();
						person = new Author();
						person.setAuthorName(auth);
						person.setAuthorURI("http://scholars.cornell.edu/individual/"+netId);
						person.setCornellAffiliation(getAffiliations(netId));
						break;
					}
				}
			}
		}catch(Exception exp){
			System.err.println(entry.getAuthor());
			System.err.println(auth);
		}
		
		return person;
	}

	private static Map<String, Set<String>> readNetId2UnitMap(File file) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					String netId = tokens[0].trim();
					String[] units = tokens[1].trim().split(";;");
					Set<String> set = new HashSet<String>();
					for(String unit: units){
						set.add(unit);
					}
 					map.put(netId, set);
				}
				reader.close();
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	private Set<String> getAffiliations(String netId) {
		return affiliationMap.get(netId);
	}

	
}
