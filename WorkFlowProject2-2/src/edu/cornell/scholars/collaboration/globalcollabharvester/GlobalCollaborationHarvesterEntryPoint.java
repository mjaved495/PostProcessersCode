package edu.cornell.scholars.collaboration.globalcollabharvester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.opencsv.CSVReader;

import edu.cornell.scholars.collaboration.gridmapper.Article_TSV;
import edu.cornell.scholars.collaboration.gridreader.GridModel;
import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.workflow22.MainEntryPoint_WorkFlow2_2;

public class GlobalCollaborationHarvesterEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(GlobalCollaborationHarvesterEntryPoint.class.getName());
	
	//input files
	private static String WOSDATAFILE = null;
	private static String AFFILIATION_GRID_FILE = null;
	private static String PERSON_2_ARTICLE_MAP = null;
	private static String ARTICLE_2_SUBJECTAREA = null;
	
	//output files
	private static String GLOBAL_COLLAB_STATE_JSON = null;
	private static String GLOBAL_COLLAB_COUNTRY_JSON = null;
	
	private List<Article_TSV> data = null;
	private Map<String, GridModel> gridDataMap = null;
	private Set<String> lastNameSet = new HashSet<String>();
	private Map<String, Person2ArticleMap> person2articleMap = null;
	
	public static void main(String args[]){
		try {
			MainEntryPoint_WorkFlow2_2.init("resources/setup.properties");
			GlobalCollaborationHarvesterEntryPoint obj = new GlobalCollaborationHarvesterEntryPoint();
			obj.setLocalDirectories();
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public GlobalCollaborationHarvesterEntryPoint(){
		person2articleMap = new HashMap<String, Person2ArticleMap>();
	}
	
	private void setLocalDirectories() {
		WOSDATAFILE = Configuration.SUPPL_FOLDER+"/"
				+ Configuration.WOS_DATA_FOLDER+"/"+Configuration.WOS_DATA_FILENAME;;
		AFFILIATION_GRID_FILE = Configuration.SUPPL_FOLDER+"/"+Configuration.AFF_GRID_MAP;
		ARTICLE_2_SUBJECTAREA = Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/"
				+ Configuration.ARTICLE_2_SUBJECTAREA_FILENAME;
		PERSON_2_ARTICLE_MAP =  Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/"
				+ Configuration.PERSON_2_ARTICLE_MAP_FILENAME;
		
		GLOBAL_COLLAB_STATE_JSON = Configuration.POSTPROCESS_RESULTSET_FOLDER   + "/" + Configuration.date + "/"
				+ Configuration.COLLABORATION_FOLDER+"/"+Configuration.COLLAB_EXTERNAL_FOLDER+"/" + Configuration.EXT_COLLABORATIONS_FILE_STATE_JSON;
		GLOBAL_COLLAB_COUNTRY_JSON = Configuration.POSTPROCESS_RESULTSET_FOLDER   + "/" + Configuration.date + "/"
				+ Configuration.COLLABORATION_FOLDER+"/"+Configuration.COLLAB_EXTERNAL_FOLDER+"/" + Configuration.EXT_COLLABORATIONS_FILE_COUNTRY_JSON;
	}

	public void runProcess() throws JsonGenerationException, JsonMappingException, IOException {
		setLocalDirectories();
		data = readWOSDataFile(WOSDATAFILE);
		gridDataMap = readGRIDMapperFile(AFFILIATION_GRID_FILE);
		person2articleMap = readPerson2ArticleMapFile(PERSON_2_ARTICLE_MAP);
		Map<String, Set<String>> subareaMap = readArticle2SubjectArea(ARTICLE_2_SUBJECTAREA);	
		/**
		 * Read the Affiliations.
		 */
		AffiliationDataAnalyzer aff_analyzer = new AffiliationDataAnalyzer();
		aff_analyzer.analyzeAffiliations(data, person2articleMap, gridDataMap, subareaMap);

		/**
		 * save the identified global collaboration data is json
		 */
		aff_analyzer.saveGlobalCollaborations(GLOBAL_COLLAB_STATE_JSON, GLOBAL_COLLAB_COUNTRY_JSON);
		
	}
	
	private Map<String, Person2ArticleMap> readPerson2ArticleMapFile(String filePath) {
		Map<String, Person2ArticleMap> map = new HashMap<String, Person2ArticleMap>();
		BufferedReader br = null;
		int count =0;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			while ((line = br.readLine()) != null) {
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				count++;
				while ((tokens = reader.readNext()) != null) {
					if(count == 1) continue; // header row.
					String name = tokens[0].trim();
					String netId = tokens[1].trim();
					String articleURI = tokens[2].trim();
					String wosId = tokens[3].trim();
					String pubmedId = tokens[4].trim();
					Person2ArticleMap obj = new Person2ArticleMap();
					obj.setName(name);
					obj.setNetId(netId);
					obj.setArticleURI(articleURI);
					obj.setWosId(wosId);
					obj.setPubmedId(pubmedId);
					map.put(netId+"-"+articleURI, obj);
				}
				reader.close();
			}
		}catch (FileNotFoundException e) {
			LOGGER.severe(line);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe(line);
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
		LOGGER.info("GLOBAL COLLAB: "+count+" lines read from person 2 article map file.");
		return map;
	}

	private Map<String, Set<String>> readArticle2SubjectArea(String filePath) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		BufferedReader br = null;
		int count =0;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			while ((line = br.readLine()) != null) {
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				count++;
				while ((tokens = reader.readNext()) != null) {
					if(count == 1) continue; // header row.
					String subAreas[]= tokens[1].trim().split(";;");
					Set<String> set = new HashSet<String>();
					for(String sa: subAreas){
						set.add(sa.trim());
					}
					map.put(tokens[0].trim(), set);
				}
				reader.close();
			}
		}catch (FileNotFoundException e) {
			LOGGER.severe(line);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe(line);
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
		LOGGER.info("GLOBAL COLLAB: "+count+" lines read from article 2 subject area map file.");
		return map;
	}

	private List<Article_TSV> readWOSDataFile(String filePath) {
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
					count++;
				}
			}
			buf.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		LOGGER.info("GLOBAL COLLAB: "+count+" lines read.");
		return articles;
	}
	
	private List<String> getSplitList(String string) {
		string = string.replaceAll("[\\[\\]]", "").trim();
		List<String> data = new ArrayList<String>(Arrays.asList(string.split(",")));
		return data;
	}
	
	private Map<String, GridModel> readGRIDMapperFile(String filePath) {
		int count = 0;
		Map<String, GridModel> map = new HashMap<String, GridModel>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			while ((line = br.readLine()) != null) {
				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					GridModel obj = new GridModel();
					String affiliationString = tokens[0];
					obj.setGridId(tokens[1]);
					obj.setGridOrg(tokens[2]);
					obj.setGridCity(tokens[3]);
					obj.setGridState(tokens[4]);
					obj.setGridCountry(tokens[5]);
					map.put(affiliationString, obj);
					count++;
				}	
			}
		}catch (FileNotFoundException e) {
			LOGGER.severe(line);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe(line);
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
		LOGGER.info("GLOBAL COLLAB: "+count+" lines read from grid map file.");
		return map;
	}
	
}
