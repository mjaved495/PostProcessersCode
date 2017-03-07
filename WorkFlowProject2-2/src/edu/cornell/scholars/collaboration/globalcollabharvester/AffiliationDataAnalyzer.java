package edu.cornell.scholars.collaboration.globalcollabharvester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import edu.cornell.scholars.collaboration.datamodel.data.Author;
import edu.cornell.scholars.collaboration.datamodel.data.ExternalCollaboration;
import edu.cornell.scholars.collaboration.gridmapper.Article_TSV;
import edu.cornell.scholars.collaboration.gridreader.GridModel;
import edu.cornell.scholars.config.Configuration;

public class AffiliationDataAnalyzer {

	private static final Logger LOGGER = Logger.getLogger(AffiliationDataAnalyzer.class.getName());
	private AffiliationDataAnalyzerExternal globalCollaboration = null;
	private Map<String, GridModel> gridMap = null;
	private Map<String, String> orgCodeMap = null;
	private Map<String, Set<String>> subareaMap = null;
	private Map<String, Person2ArticleMap> person2articleMap = null;
	private static String ORGCODE_FILE = null;
	
	public AffiliationDataAnalyzer(){
		globalCollaboration = new AffiliationDataAnalyzerExternal();
	}

	public void analyzeAffiliations(List<Article_TSV> data,
			Map<String, Person2ArticleMap> person2articleMap, Map<String, GridModel> gridDataMap, Map<String, Set<String>> subareaMap) {

		setLocalDirectories();
		//this.person_masterList = person_masterList; 
		this.person2articleMap = person2articleMap;
		this.gridMap  = gridDataMap;
		this.subareaMap = subareaMap;

		// create a map of <WOSOrg - CollegeLabel>
		//affiliation_college_map = getAffiliationCollegeMap(new File(AFFILIATION_COLLEGE_MAP_FILE));

		// create a map of <Org - OrgCode>
		orgCodeMap = readOrgCodeMapFile(new File(ORGCODE_FILE));

		//Create a map of <ArticleId - ArticleData>
		Map<String, Set<Article_TSV>> wosId2articleMap = createDataMap(data);
		analyzeData(wosId2articleMap);


	}

	private void analyzeData(Map<String, Set<Article_TSV>> wosId2articleMap) {
		Set<String> wosIds = wosId2articleMap.keySet();
		//Iterate over Article_TSV entries to see if it is a global collaboration.
		for(String wosId: wosIds){
			Set<Article_TSV> article = wosId2articleMap.get(wosId);
			globalCollaboration.processCollaborationData(article, gridMap, person2articleMap, orgCodeMap, subareaMap);
		}
		LOGGER.info("total publications: "+wosIds.size());
		LOGGER.info("local publications: "+AffiliationDataAnalyzerExternal.localoubCounter);
	}






	/**
	 * This creates a map of <ArticleId - ArticleData>
	 * @param data
	 * @return
	 */
	private Map<String, Set<Article_TSV>> createDataMap(List<Article_TSV> data){
		Map<String, Set<Article_TSV>> map = new HashMap<String, Set<Article_TSV>>();
		for(Article_TSV art: data){
			if(map.get(art.getWosId()) == null){
				Set<Article_TSV> set = new HashSet<Article_TSV>();
				set.add(art);
				map.put(art.getWosId(), set);
			}else{
				Set<Article_TSV> set = map.get(art.getWosId());
				set.add(art);
			}
		}
		return map;
	}

	private void setLocalDirectories() {
		ORGCODE_FILE = Configuration.SUPPL_FOLDER + "/"+ Configuration.ORG_ORGCODE_MAP_FILE;
	}

	private Map<String, String> readOrgCodeMapFile(File file) {
		Map<String, String> map  = new HashMap<String, String>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file),',','\"');
			String [] nextLine = null;	
			while ((nextLine = reader.readNext()) != null) {
				if(!nextLine[0].trim().isEmpty() && !nextLine[1].trim().isEmpty()){
					map.put(nextLine[0].trim(), nextLine[1].trim());
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

//	public void saveGlobaleCollaborations(String jsonfile) throws JsonGenerationException, JsonMappingException, IOException {
//		List<ExternalCollaboration> collabData = globalCollaboration.getExtCollab();
//		ObjectMapper mapper = new ObjectMapper();
//		String jsonInString = null;
//		mapper.writeValue(new File(jsonfile), collabData);
//		jsonInString = mapper.writeValueAsString(collabData);
//		//System.out.println(jsonInString);
//
//	}

	public void saveGlobalCollaborations(String stateFileName, String countryFileName) throws JsonGenerationException, JsonMappingException, IOException {
		List<ExternalCollaboration> collabData = globalCollaboration.getExtCollab();
		
		Map<String, Set<ExternalCollaboration>> stateMap = new HashMap<String, Set<ExternalCollaboration>>();
		Map<String, Set<ExternalCollaboration>> countryMap = new HashMap<String, Set<ExternalCollaboration>>();
		
		for(ExternalCollaboration c: collabData){
			Set<Author> authors = c.getAuthors();
			String state = null;
			String country = null;
			for(Author a: authors){
				state = a.getState();
				if(state != null){
					Set<ExternalCollaboration> stateSet = stateMap.get(state);
					if(stateSet == null){
						stateSet = new HashSet<ExternalCollaboration>();
						stateSet.add(c);
						stateMap.put(state, stateSet);
					}else{
						stateSet.add(c);
					}
				}
				country = a.getCountry();
				if(country != null){
					Set<ExternalCollaboration> countrySet = countryMap.get(country);
					if(countrySet == null){
						countrySet = new HashSet<ExternalCollaboration>();
						countrySet.add(c);
						countryMap.put(country, countrySet);
					}else{
						countrySet.add(c);
					}
				}
				if(state == null && country == null){
					System.err.println("Country and State values are NULL: "+ a.getAuthorAffiliation());
				}
			}
		}
		
		saveDataInJSON(stateMap,   stateFileName);
		saveDataInJSON(countryMap, countryFileName);
	}

	private void saveDataInJSON(Map<String, Set<ExternalCollaboration>> map, String fileName) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		mapper.writeValue(new File(fileName), map);
		jsonInString = mapper.writeValueAsString(map);
	}
}
