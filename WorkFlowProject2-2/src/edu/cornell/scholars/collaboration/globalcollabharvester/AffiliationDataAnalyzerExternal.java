package edu.cornell.scholars.collaboration.globalcollabharvester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

import edu.cornell.scholars.collaboration.datamodel.data.ArticleToIdMap;
import edu.cornell.scholars.collaboration.datamodel.data.Author;
import edu.cornell.scholars.collaboration.datamodel.data.AuthorAffiliation;
import edu.cornell.scholars.collaboration.datamodel.data.ExternalCollaboration;
import edu.cornell.scholars.collaboration.gridmapper.Article_TSV;
import edu.cornell.scholars.collaboration.gridreader.GridModel;
import edu.cornell.scholars.config.Configuration;

public class AffiliationDataAnalyzerExternal {

	private static final Logger LOGGER = Logger.getLogger(AffiliationDataAnalyzerExternal.class.getName());
	private final static String USA = "USA";
	private final static String UNITED_STATES = "UNITED STATES";
	private final static String WOS = "WOS";
	private final static String PUBMED = "PUBMED";
	private final static String NEWYORK = "NY";
	private final static String GRID_BASE_URI = "https://www.grid.ac/institutes/";
	public static int localoubCounter = 0;
	private List<String> usaStateList = null;
	private List<String> countryList = null;
	private Map<String, String> id2uriMap = null;
	private CornellAffiliationFinder cornellCollabFinder = null;
	
	private List<ExternalCollaboration> extCollab = null;
	public List<ExternalCollaboration> getExtCollab() {
		return extCollab;
	}

	public AffiliationDataAnalyzerExternal(){
		extCollab = new ArrayList<ExternalCollaboration>();
		cornellCollabFinder = new CornellAffiliationFinder();
		populateLists();
	}

	private void populateLists() {
		usaStateList = readFile(Configuration.SUPPL_FOLDER +"/"+Configuration.USA_STATE_FILE);	
		countryList = readFile(Configuration.SUPPL_FOLDER +"/"+Configuration.COUNTRIES_FILE);
		List<ArticleToIdMap> articleURIMap = readArtile2IdMapFile(Configuration.QUERY_RESULTSET_FOLDER+"/"+Configuration.date+
				"/"+Configuration.ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV);
		id2uriMap = prepareMap(articleURIMap);
	}

	private Map<String, String> prepareMap(List<ArticleToIdMap> articleURIMap) {
		Map<String, String> id2uriMap = new HashMap<String, String>();
		for(ArticleToIdMap obj: articleURIMap){
			String wosId = obj.getWosId();
			if(wosId != null && !wosId.isEmpty()){
				id2uriMap.put(wosId, obj.getArticleURI());
			}else {
				String pubmedId = obj.getPubmedId();
				if(pubmedId != null && !pubmedId.isEmpty()){
					id2uriMap.put(pubmedId, obj.getArticleURI());
				}
			}
		}
		return id2uriMap;
	}
	
	public void processCollaborationData(Set<Article_TSV> article, Map<String, GridModel> gridMap,
		 Map<String, Person2ArticleMap> person2articleMap, Map<String, String> orgCodeMap, Map<String, Set<String>> subareaMap) {
		
		/**
		 * if there is not Cornell Author associated, Return.
		 * This may happen for those articles that were published before faculty joins Cornell.
		 */
		if (noCornellAuthorAssociated(article) || 
				allCornellAuthorsAssociated(article)) return;  

		Set<String> authorNames = new HashSet<String>();
		ExternalCollaboration collabEntry = new ExternalCollaboration();
		List<Article_TSV> list = new ArrayList<Article_TSV>(article);

		Article_TSV entry = list.get(0);
		String id =  entry.getWosId();
		if(id == null || id.isEmpty()){
			id =  entry.getPubmedId();
		}
		String idType = id.startsWith(WOS)? WOS:PUBMED;
		String scholarsURI = getScholarsURI(id);
		if(scholarsURI == null){
			scholarsURI = getScholarsURI(entry.getPubmedId());
		}
		Set<String> subjectAreas = null;
		if(scholarsURI != null){
			subjectAreas = subareaMap.get(scholarsURI.trim());
		}
		String articleTitle = entry.getTitle();
		
		if(articleTitle.equalsIgnoreCase("Private Forest Landowners: Estimating Population Parameters")){
			System.out.println("article found");
		}
		
		
		String year = entry.getYear();
		for(Iterator<Article_TSV> i = article.iterator(); i.hasNext();){
			Article_TSV en = i.next();
			String authorName = en.getAuthor();
			String authorAff = en.getAffiliation();
			//System.out.println(authorName);
			//System.out.println(authorAff);
			String country = null;
			String state = null;
			Author author = null;
			if(!authorAff.isEmpty()){
				// The check can be more sophisticated like 'if aff string ends with two letter code that is one of the states and 5digit code', then it is USA.
				if(authorAff.contains(USA) || authorAff.matches(".*(Ithaca|ITHACA|Cornell|CORNELL|NY 14853|NY 14850).*")){ 
					country = UNITED_STATES;
					if (authorAff.matches(".*(Ithaca|ITHACA|CORNELL|Cornell|NY 14853|NY 14850).*")) {
						// THIS IS MAIN METHOD TO GET AFFILIATION OF AN AUTHOR
						author = cornellCollabFinder.getCornellAffiliation(en, person2articleMap);
						state = NEWYORK;
					}else{
						if(authorNames.contains(authorName)){
							continue;
						}else{
							authorNames.add(authorName);
						}
						state = findUSAState(authorAff, usaStateList);
					}
				}else{
					country = getCountry(authorAff, countryList);
					if(country != null){
						if(authorNames.contains(authorName)){
							continue;
						}else{
							authorNames.add(authorName);
						}
					}else{
						// country not found, lets try to find US STATE
						String usstate = findUSAState(authorAff, usaStateList);
						if(usstate != null){
							country = UNITED_STATES;
							state = usstate;
						}else{
							LOGGER.warning("Country and US State not found: "+authorAff);
						}
					}
				}
			}

			if(author == null){
				author = new Author();
				author.setAuthorName(authorName);
			}
			if(authorAff.indexOf(",") >0){
				author.setAuthorAffiliation(new AuthorAffiliation(authorAff.substring(0, authorAff.indexOf(","))));
			}
			author.setCountry(country);
			author.setState(state);
			
			//replace affiliation string with grid org where ever possible
			try{
				GridModel grid = gridMap.get(authorAff);
				if(grid != null){
					AuthorAffiliation aa = new AuthorAffiliation();
					aa.setLocalName(grid.getGridOrg());
					aa.setGridURI(GRID_BASE_URI + grid.getGridId());
					author.setAuthorAffiliation(aa);
				}
			}catch(NullPointerException exp){
				LOGGER.warning(authorAff);
				LOGGER.info(exp.getMessage());
			}
		
			collabEntry.addAuthor(author);
		}// end of TSV Iteration
		
		collabEntry.setArticleId(id);
		collabEntry.setArticleIdType(idType);
		collabEntry.setArticleURI(scholarsURI);
		collabEntry.setSubjectAreas(subjectAreas);
		collabEntry.setArticleTitle(articleTitle);
		collabEntry.setYearOfPublication(year);

		extCollab.add(collabEntry);
	}

	private String findUSAState(String entry, List<String> list) {
		for(int index=0;index<list.size();index++){
			String stateName = list.get(index);
			if(entry.length() >= 15){
				entry = entry.substring(entry.length()-15);
				entry = entry.replaceAll(",", " ");
				String ent[] = entry.split(" ");
				for(String e: ent){
					if(e.trim().toUpperCase().equals(stateName)){
						return stateName;
					}
				}
				
			}
		}
		LOGGER.warning("USA State not found: "+entry);
		return null;
	}
	
	private String getCountry(String entry, List<String> countryList) {
		if(entry.trim().isEmpty()) {
			//System.err.println("Country entry is empty: "+entry);
			return null;
		}
		for(int index=0;index<countryList.size();index++){
			String countryName = countryList.get(index);
			try{
				if(entry.length() > 15){
					entry = entry.toUpperCase().substring(entry.length()-15);
				}
			}catch(Exception e){
				//System.out.println(entry);
				e.printStackTrace();
			}
			if(entry.contains(countryName)){
				return countryName;
			}
		}
		return null;
	}
		
	private String getScholarsURI(String id) {
		return id2uriMap.get(id);
	}

	public ArrayList<String> readFile(String filePath){
		ArrayList<String> rows = new ArrayList<String>();
		FileInputStream is;
		try {
			is = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader buf = new BufferedReader(isr);
			String lineJustFetched = null;

			while(true){
				lineJustFetched = buf.readLine();
				if(lineJustFetched == null){  
					break; 
				}else{
					if(lineJustFetched.trim().length() == 0){
						continue;
					}
					rows.add(lineJustFetched.trim().toUpperCase());
				}
			}
			buf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rows;
	}
	
	public static List<ArticleToIdMap> readArtile2IdMapFile(String filePath) {
		List<ArticleToIdMap> list = new ArrayList<ArticleToIdMap>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			ArticleToIdMap obj  = null;
			while ((line = br.readLine()) != null) {
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					if(!tokens[1].trim().isEmpty() || !tokens[2].trim().isEmpty()){  // stop if wos and pmid both are empty
						obj = new ArticleToIdMap();
						obj.setArticleURI(tokens[0].trim());
						obj.setWosId(tokens[1].trim());
						obj.setPubmedId(tokens[2].trim());
						list.add(obj);
					}	
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
		System.out.println("article to id map"+ list.size());
		return list;
	}
	
	private boolean noCornellAuthorAssociated(Set<Article_TSV> article) {
		boolean noCornellianAssociated = true;
		for(Article_TSV a: article){
			if(a.getAffiliation().toUpperCase().contains("CORNELL") || 
					a.getAffiliation().toUpperCase().contains("ITHACA") ){
				noCornellianAssociated = false;
				break;
			}
		}
		
		//FOR DEBUGGING PURPOSE ONLY
//		if(noCornellianAssociated){
//			LOGGER.info("NO CORNELLIAN ASSOCIATED");
//			for(Article_TSV a: article){
//				LOGGER.info(a.getAuthor() +"-"+a.getAffiliation());
//			}
//		}

		return noCornellianAssociated;
	}
	
	private boolean allCornellAuthorsAssociated(Set<Article_TSV> article) {
		boolean allCornellianAssociated = true;
		for(Article_TSV a: article){
			if(!a.getAffiliation().matches(".*(Ithaca|ITHACA|CORNELL|Cornell|NY 14853|NY 14850).*")){
				allCornellianAssociated = false;
				break;
			}
		}
		if(allCornellianAssociated){
			localoubCounter++;
//			System.out.println("\n"+article.iterator().next().getTitle());
//			for(Article_TSV a: article){
//				System.out.println(a.getAffiliation());
//			}
		}
		return allCornellianAssociated;
	}
}
