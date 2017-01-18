package edu.cornell.scholars.collaboration.collabharvester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

import edu.cornell.scholars.collaboration.datamodel.data.AffiliationModel;
import edu.cornell.scholars.collaboration.datamodel.data.CollaborationHead;
import edu.cornell.scholars.collaboration.datamodel.data.Department;
import edu.cornell.scholars.collaboration.datamodel.data.Person;
import edu.cornell.scholars.collaboration.datamodel.data.Unit;
import edu.cornell.scholars.collaboration.gridmapper.Article_TSV;
import edu.cornell.scholars.collaboration.gridreader.GridModel;
import edu.cornell.scholars.collaboration.jsonbuilder.CollaborationJSONDataBuilder;
import edu.cornell.scholars.config.Configuration;

public class AffiliationDataAnalyzer {

	private static String AFFILIATION_COLLEGE_MAP_FILE = Configuration.AFFILIATION_STRING_TO_COLLEGE_MAP_FILE;
	private static String ORGCODE_FILE = Configuration.ORG_ORGCODE_MAP_FILE;
	
	//TODO NOT USED FOR NOW. SHOULD WORK ON IT IN FUTURE.
	public  static String NF_AFFILIATION_SET_FILE = Configuration.NOTFOUND_AFFILIATION_SET_FILE;
	
	// created a map of <wosId - Article_TSV entries>
	// created an org code map <orgLabel - orgCode>
			
	private Map<String, AffiliationModel> affiliation_college_map = null;
	private Map<String, String> orgCodeMap = null;
	private Map<String, GridModel> gridMap = null;
	private List<WOSPerson_NetIdMap> wosperson_netid_maplist = null;
	private AffiliationDataAnalyzerExternal externalCollaboration = null;
	private Set<String> NF_Affiliations = new HashSet<String>();
	
	public void analyzeAffiliations(List<Article_TSV> data, List<WOSPerson_NetIdMap> wosperson_netid_maplist, Map<String, GridModel> map ){
		this.externalCollaboration = new AffiliationDataAnalyzerExternal();
		this.wosperson_netid_maplist = wosperson_netid_maplist; 
		this.gridMap  = map;

		// create a map of <WOSOrg - CollegeLabel>
		affiliation_college_map = getAffiliationCollegeMap(new File(AFFILIATION_COLLEGE_MAP_FILE));
		// create a map of <Org - OrgCode>
		orgCodeMap = readOrgCodeMapFile(new File(ORGCODE_FILE));
		//Create a map of <ArticleId - ArticleData>
		Map<String, Set<Article_TSV>> wosId2articleMap = createDataMap(data);
		analyzeData(wosId2articleMap);

		
		System.out.println("total count:"+ CollaborationDataAnalyzer.affiliationCount);
		Set<String> k = CollaborationDataAnalyzer.aff_count_map.keySet();
		for(String s: k){
			System.out.println(s+","+CollaborationDataAnalyzer.aff_count_map.get(s));
		}
		
		System.err.println("NON-MAPPED Affiliation Strings");
		for(String affiliation: NF_Affiliations){
			System.err.println(affiliation);
		}
		
	}

	private void analyzeData(Map<String, Set<Article_TSV>> wosId2articleMap) {
		
		CollaborationDataAnalyzer.initializePrintWriter();
		
		Set<String> wosIds = wosId2articleMap.keySet();
		
		//Iterate over Article_TSV entries to see if it is an internal/external collaboration.
		for(String wosId: wosIds){
			Set<Article_TSV> values = wosId2articleMap.get(wosId);
			harvestCollaborations(values);
		}
		
		CollaborationDataAnalyzer.terminatePrintWriter();
	}

	private void harvestCollaborations(Set<Article_TSV> article) {
	
		Set<String > cornellAffiliationSet = new HashSet<String>();			// list of (Cornellian) Organizations for this article only.
		Set<String > cornellAuthorSet = new HashSet<String>(); 				// list of (Cornellian) authors for this article only.
		List<Article_TSV> cornelliansList = new ArrayList<Article_TSV>(); 	// list of (Cornellian) Article_TSV for this article only.
		boolean externalCollab = false;
		
		for(Article_TSV art: article){
			// if citation entry contain 'Cornell' or 'Ithaca' text, it is considered as local author
			if(art.getAffiliation().toUpperCase().contains("CORNELL") || art.getAffiliation().toUpperCase().contains("ITHACA")){
				String affiliation = art.getAffiliation();
				affiliation = cleanStopWords(affiliation);
				AffiliationModel affiliationModel = affiliation_college_map.get(art.getAffiliation()); 
				// affiliationModel should not be null for being considered in internal collaboration.
				if (affiliationModel != null){
					String collaboratingDepartment = affiliationModel.getDept();
					cornellAffiliationSet.add(collaboratingDepartment);
					cornellAuthorSet.add(art.getAuthor());
					cornelliansList.add(art); 
				}else{
					NF_Affiliations.add(art.getAffiliation());
				}
			}else{  // else case when it is an external affiliation. Cornell or Ithaca is not found in the affiliation string
				
				if(!externalCollab){ // this call should be only once for an article.
					externalCollaboration.processCollaborationData(article, affiliation_college_map, gridMap);
					externalCollab = true;
				}
			}
		}
		
		if(cornellAffiliationSet.size() >= 2 && cornellAuthorSet.size() >= 2){ // When we have two distinct departments and two distinct authors then it is an internal collaboration.
			CollaborationDataAnalyzer.foundCollaboration(wosperson_netid_maplist, affiliation_college_map, cornelliansList);
		}
		
	}

	private String cleanStopWords(String affiliationEntry){
		affiliationEntry = affiliationEntry.replace("Ithaca", "");
		affiliationEntry = affiliationEntry.replace("Cornell Univ", "");
		affiliationEntry = affiliationEntry.replace("NY 14853", "");
		affiliationEntry = affiliationEntry.replace("NY 14850", "");
		affiliationEntry = affiliationEntry.replace("USA", "");
		affiliationEntry = affiliationEntry.replaceAll("[^a-zA-Z\\s\\&]","");
		return affiliationEntry;
	}
	
	private Map<String, AffiliationModel> getAffiliationCollegeMap(File file) {
		Map<String, AffiliationModel> map = new HashMap<String, AffiliationModel>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file),',','\"');
			String [] nextLine;	
			while ((nextLine = reader.readNext()) != null) {
				map.put(nextLine[0], new AffiliationModel(nextLine[0], nextLine[1],nextLine[2]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
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

	public void saveInternalCollaborations(Set<CollaborationHead> collaborations, String filePath) {	
		PrintWriter writer;
		try {
			writer = new PrintWriter (filePath);	
			for(CollaborationHead collaboration: collaborations){
				String wosId = collaboration.getWosId();
				String pubmedId = collaboration.getPubmedId();
				String articleTitle = collaboration.getArticleTitle();
				String year = collaboration.getYear();
				Set<Unit> units = collaboration.getUnits();
				for(Unit unit: units){
					String unitName = unit.getUnitName();
					String unitCode = getCode(unitName);
					if(unitCode.contains("/")){
						String unitSplit[] = unitName.split(",");
						String codeSplit[] = unitCode.split("/");
						for(int i = 0; i<unitSplit.length; i++){
							String distinctUnitName = unitSplit[i].trim();
							String distinctUnitCode = codeSplit[i].trim();
							Set<Department> departments = unit.getDepartments();
							for(Department department: departments){
								String departmentName = department.getDeptName();
								Set<Person> persons = department.getPersonList();
								for(Person persn: persons){
									String personName = persn.getPersonName();
									String netId = persn.getNetId();
									writer.println("\""+wosId+"\",\""+year+"\",\""+distinctUnitName+"\",\""+distinctUnitCode+"\",\""+departmentName+"\",\""+getCode(departmentName)+"\",\""+personName+"\","+netId+","+pubmedId+",\""
											+articleTitle.replace("\"", "")  // this is a temporary solutions to deal with quotes in a title.
											+"\"");
								}
							}
						}
					}else{
						Set<Department> departments = unit.getDepartments();
						for(Department department: departments){
							String departmentName = department.getDeptName();
							Set<Person> persons = department.getPersonList();
							for(Person persn: persons){
								String personName = persn.getPersonName();
								String netId = persn.getNetId();
								writer.println("\""+wosId+"\",\""+year+"\",\""+unitName+"\",\""+unitCode+"\",\""+departmentName+"\",\""+getCode(departmentName)+"\",\""+personName+"\","+netId+","+pubmedId+",\""
										+articleTitle.replace("\"", "")  // this is a temporary solutions to deal with quotes in a title.
										+"\"");
							}
						}
					}	
					
				}// end of unit loop
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
	}

	public void saveGlobaleCollaborations() {
		externalCollaboration.saveData();
	}
	
	private String getCode(String org) {
		if(orgCodeMap.get(org) != null){
			return orgCodeMap.get(org);
		}else{
			return "OTHERS";
		}
	}

	public void saveInternalCollaborationsInJSON() {
		CollaborationJSONDataBuilder obj = new CollaborationJSONDataBuilder();
		obj.runProcess();
	}

}
