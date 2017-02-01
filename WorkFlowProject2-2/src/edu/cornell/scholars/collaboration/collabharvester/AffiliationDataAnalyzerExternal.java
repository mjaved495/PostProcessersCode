package edu.cornell.scholars.collaboration.collabharvester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import edu.cornell.scholars.collaboration.datamodel.data.AffiliationModel;
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
	private final static String COMMASEPARATOR = ",";
	private final static String GRID_BASE_URI = "https://www.grid.ac/institutes/";

	private List<ExternalCollaboration> extCollab = null;
	private List<String> usaStateList;
	private List<String> countryList;
	private Map<String, String> id2uriMap;

	public AffiliationDataAnalyzerExternal(){
		extCollab = new ArrayList<ExternalCollaboration>();
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

	public void processCollaborationData(Set<Article_TSV> article, Map<String, AffiliationModel> affiliation_college_map, Map<String, GridModel> gridMap){

		// if there is not Cornell Author associated, Return.
		// This may happen for those articles that were published 
		// before faculty joins Cornell.
		if (noCornellAuthorAssociated(article)) return;  

		Set<String> authorNames = new HashSet<String>();
		ExternalCollaboration collabEntry = new ExternalCollaboration();
		List<Article_TSV> list = new ArrayList<Article_TSV>(article);

		Article_TSV entry = list.get(0);
		String id =  entry.getWosId();
		//				if(id.equals("WOS:A1983PX68700002")){
		//					System.out.println(entry.toString());	
		//				}
		if(id == null || id.isEmpty()){
			id =  entry.getPubmedId();
		}
		String idType = id.startsWith(WOS)? WOS:PUBMED;
		String scholarsURI = getScholarsURI(id);
		String articleTitle = entry.getTitle();
		String articleDoi = entry.getDoi();
		String year = entry.getYear();
		for(Iterator<Article_TSV> i = article.iterator(); i.hasNext();){
			Article_TSV en = i.next();
			String authorName = en.getAuthor();
			String authorAff = en.getAffiliation();
			//System.out.println(authorName);
			//System.out.println(authorAff);
			String country = null;
			String cornellAff = null;
			String state = null;
			if(!authorAff.isEmpty()){
				// The check can be more sophisticated like 'if aff string ends with two letter code that is one of the states and 5digit code', then it is USA.
				if(authorAff.contains(USA) || authorAff.matches(".*(Ithaca|ITHACA|Cornell|CORNELL|NY 14853|NY 14850).*")){ 
					country = UNITED_STATES;
					if (authorAff.matches(".*(Ithaca|ITHACA|CORNELL|Cornell|NY 14853|NY 14850).*")) {
						cornellAff = getCornellAffiliation(authorAff, affiliation_college_map);
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

			Author author = new Author();
			author.setAuthorName(authorName);
			if(authorAff.indexOf(",") >0){
				author.setAuthorAffiliation(new AuthorAffiliation(authorAff.substring(0, authorAff.indexOf(","))));
			}
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
			
			author.setCountry(country);
			author.setState(state);
			author.setCornellAffiliation(cornellAff);
			collabEntry.addAuthor(author);
		}
		collabEntry.setId(id);
		collabEntry.setIdType(idType);
		collabEntry.setScholarURI(scholarsURI);
		collabEntry.setArticleTitle(articleTitle);
		collabEntry.setArticleDoi(articleDoi);
		collabEntry.setYearOfPublication(year);

		extCollab.add(collabEntry);

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
		
		if(noCornellianAssociated){
			//LOGGER.info("NO CORNELLIAN ASSOCIATED");
			for(Article_TSV a: article){
				//LOGGER.info(a.getAuthor() +"-"+a.getAffiliation());
			}
		}

		return noCornellianAssociated;
	}

	private String getCountry(String entry, List<String> countryList) {
		if(entry.trim().isEmpty()) {
			//System.err.println("Country entry is empty: "+entry);
			return null;
		}
		for(int index=0;index<countryList.size();index++){
			String countryName = countryList.get(index);
			try{
				entry = entry.toUpperCase().substring(entry.length()-15);
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

	private String findUSAState(String entry, List<String> list) {
		for(int index=0;index<list.size();index++){
			String stateName = list.get(index);
			if(entry.length() >= 15){
				entry = entry.substring(entry.length()-15);
				if(entry.toUpperCase().contains(stateName)){
					return stateName;
				}
			}
		}
		LOGGER.warning("USA State not found: "+entry);
		return null;
	}

	private String getCornellAffiliation(String authorAff, Map<String, AffiliationModel> affiliation_college_map) {
		AffiliationModel affModel = affiliation_college_map.get(authorAff); 
		String unit = "";
		if(affModel != null){
			//String collaboratingDepartment = affModel.getDept();
			unit = affModel.getUnit();
		}
		return unit;
	}

	public static List<ArticleToIdMap> readArtile2IdMapFile(String filePath) {
		List<ArticleToIdMap> list = new ArrayList<ArticleToIdMap>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(new File(filePath)),',','\"');
			String [] nextLine;	
			ArticleToIdMap obj  = null;
			while ((nextLine = reader.readNext()) != null) {
				if(!nextLine[1].trim().isEmpty() ||  !nextLine[2].trim().isEmpty()){
					obj = new ArticleToIdMap();
					obj.setArticleURI(nextLine[0].trim());
					obj.setWosId(nextLine[1].trim());
					obj.setPubmedId(nextLine[2].trim());
					list.add(obj);
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
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

	private String getScholarsURI(String id) {
		return id2uriMap.get(id);
	}

	public void saveData() {
		Map<String, Integer> id2countrycountmap = new HashMap<>();
		for(String country: countryList){
			id2countrycountmap.put(country.toUpperCase(), 0);
		}

		for(ExternalCollaboration collab :extCollab){

			Set<String> distinctCountry = new HashSet<String>();
			boolean localAdded = false;

			for(Author author: collab.getAuthors()){
				if(author.getCountry() != null){
					if(author.getCountry().equals("UNITED STATES") && !localAdded){ // do not count Cornell Authors
						if(!author.getAuthorAffiliation().getLocalName().toUpperCase().contains("CORNELL") && !author.getAuthorAffiliation().getLocalName().toUpperCase().contains("ITHACA")){
							id2countrycountmap.put(author.getCountry(), 
									id2countrycountmap.get(author.getCountry())+1);
							localAdded = true;
						}
					}else{
						if(!distinctCountry.contains(author.getCountry())){ // if country is not already counted, then add it 
							id2countrycountmap.put(author.getCountry(), 
									id2countrycountmap.get(author.getCountry())+1);
							distinctCountry.add(author.getCountry());
						}
					}
				}
			}
		}
		String coutFilePath = Configuration.POSTPROCESS_RESULTSET_FOLDER   + "/" + Configuration.date + "/"+ Configuration.COLLABORATION_FOLDER+"/"
				+ Configuration.COLLAB_EXTERNAL_FOLDER+"/" + Configuration.EXT_COLLABORATIONS_COUNT_CSV;
		save(id2countrycountmap, coutFilePath);
		
		String jsonFilePath = Configuration.POSTPROCESS_RESULTSET_FOLDER   + "/" + Configuration.date + "/"+ Configuration.COLLABORATION_FOLDER+"/"+ Configuration.COLLAB_EXTERNAL_FOLDER+"/" + 
				Configuration.EXT_COLLABORATIONS_FILE_JSON;
		createJSONfile(jsonFilePath);
		
		String csvFilePath = Configuration.POSTPROCESS_RESULTSET_FOLDER   + "/" + Configuration.date + "/"
				+Configuration.COLLABORATION_FOLDER+"/"+Configuration.COLLAB_EXTERNAL_FOLDER+"/" + Configuration.EXT_COLLABORATIONS_FILE_CSV;
		createCSVfile(csvFilePath);
		performAnalysis(extCollab);
	}

	private void performAnalysis(List<ExternalCollaboration> extCollab2) {
		Map<String, Set<String>> countryToArticleIdMap = new HashMap<String, Set<String>>();
		for(ExternalCollaboration collab :extCollab){
			List<Author> authors = collab.getAuthors();
			for(Author author: authors){
				String country = author.getCountry();
				if(country != null){
					if(countryToArticleIdMap.get(country) == null){
						Set<String> set = new HashSet<String>();
						set.add(collab.getId());
						countryToArticleIdMap.put(country, set);
					}else{
						Set<String> set = countryToArticleIdMap.get(country);
						set.add(collab.getId());
						countryToArticleIdMap.put(country, set);
					}
				}
			}
		}
		Set<String> keys = countryToArticleIdMap.keySet();
//		for(String key: keys){
//			LOGGER.info(key+" : "+countryToArticleIdMap.get(key).size());
//		}
	}

	private void createCSVfile(String filePath) {
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (filePath);	
			for(ExternalCollaboration collab :extCollab){
				List<Author> authors = collab.getAuthors();
				String str = "";
				for(Author author: authors){
					str = author.toString();
					str += COMMASEPARATOR + collab.toString();
					printWriter.println(str);
				}
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	private void createJSONfile(String filePath) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		try {

			mapper.writeValue(new File(filePath), extCollab);
			jsonInString = mapper.writeValueAsString(extCollab);
			//System.out.println(jsonInString);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (filePath);	
			printWriter.println(jsonInString);
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  

	}

	public void save(Map<String, Integer> data, String filePath) {
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (filePath);	
			Set<String> keys = data.keySet();
			for(Iterator<String> i = keys.iterator(); i.hasNext();){
				String key = i.next();
				printWriter.println("\""+key+"\","+data.get(key));
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}


}
