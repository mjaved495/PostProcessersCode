package edu.cornell.scholars.collaboration.collabharvester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

import edu.cornell.scholars.collaboration.gridmapper.Article_TSV;
import edu.cornell.scholars.collaboration.gridreader.GridModel;
import edu.cornell.scholars.config.Configuration;

public class WOSTSVFileReader {
	
	private final static String WOSDATAFILE = Configuration.QUERY_RESULTSET_FOLDER+"/"+Configuration.date+"/"
			+Configuration.WOS_DATA_FOLDER+"/"+Configuration.WOS_DATA_FILENAME;;
	private final static String AFFILIATION_GRID_FILE = Configuration.SUPPL_FOLDER+"/"+Configuration.AFF_GRID_MAP;
	private final static String WOS_PERSON_LIST_MASTER = Configuration.SUPPL_FOLDER+"/"+Configuration.WOS_PERSON_NETID_MASTER_FILE;
	private final static String WOS_PERSON_LIST_CURRENT = Configuration.QUERY_RESULTSET_FOLDER+"/"+Configuration.date+"/"+Configuration.WOS_PERSON_NETID_CURRENT_FILE;
	
	private final static String INT_COLLAB_CSV = Configuration.POSTPROCESS_RESULTSET_FOLDER   + "/" + Configuration.date + "/"
			+Configuration.COLLABORATION_FOLDER+"/"+Configuration.COLLAB_INTERNAL_FOLDER+"/" + Configuration.INT_COLLABORATIONS_FILE_CSV;
	
	private List<Article_TSV> data = null;
	private Map<String, GridModel> gridDataMap = null;
	private Set<String> lastNameSet = new HashSet<String>();

	public static void main(String[] args) {
		WOSTSVFileReader obj = new WOSTSVFileReader();
		obj.runProcess();
	}

	public void runProcess() {

		data = readWOSDataFile(WOSDATAFILE);
		gridDataMap = readGRIDMapperFile(AFFILIATION_GRID_FILE);

		/**
		 * Reads the master file generating manually WOSPERSON_NETID_MAP.
		 * Loads the new author names (from data file) in the master file. 
		 * We will not read the file again as no new person to netid map will be created. This work is done manually.
		 * To get the Department(s) we need to rely on the master file 'Persons_of_EN_In_VIVO'.
		 */
		List<WOSPerson_NetIdMap> wosperson_netid_masterList = readPersonNetIdMapperFile(WOS_PERSON_LIST_MASTER);
		List<WOSPerson_NetIdMap> wosperson_netid_currentList = readPersonNetIdMapperFile(WOS_PERSON_LIST_CURRENT);
		List<WOSPerson_NetIdMap> mergedList = mergeMasterCurrentList(wosperson_netid_masterList, wosperson_netid_currentList);
		loadNewAuthorNamesInMasterFile(data, mergedList);
		
		/**
		 * Read the Affiliations.
		 */
		AffiliationDataAnalyzer aff_analyzer = new AffiliationDataAnalyzer();
		aff_analyzer.analyzeAffiliations(data, mergedList, gridDataMap);
		
		/**
		 * save the identified internal collaboration data is csv and json
		 */
		aff_analyzer.saveInternalCollaborations(CollaborationDataAnalyzer.collaborations, INT_COLLAB_CSV);
		aff_analyzer.saveInternalCollaborationsInJSON();
		/**
		 * save the identified global collaboration data is csv
		 */
		aff_analyzer.saveGlobaleCollaborations();
	}

	private List<WOSPerson_NetIdMap> mergeMasterCurrentList(List<WOSPerson_NetIdMap> masterList, List<WOSPerson_NetIdMap> currentList) {
		List<WOSPerson_NetIdMap> mergedList = new ArrayList<WOSPerson_NetIdMap>();
		mergedList.addAll(masterList);
		mergedList.addAll(currentList);
		generateLastNameSet(mergedList);
		return mergedList;
	}

	private void generateLastNameSet(List<WOSPerson_NetIdMap> mergedList) {
		for(WOSPerson_NetIdMap obj: mergedList){
			lastNameSet.add(getLastName(obj.getName()));
		}
	}

	private void loadNewAuthorNamesInMasterFile(List<Article_TSV> data, List<WOSPerson_NetIdMap> mergedList) {
		Set<String> authors = new HashSet<String>();
		for(Article_TSV art: data){
			//if last name does not exist in name to netId mapper, then there is no point to add this name in 
			//the master list. this is most probably an external author
			if(lastNameSet.contains(getLastName(art.getAuthor()))){
				authors.add(art.getAuthor());
			}
		}
		for(String author: authors){
			boolean found = false;
			for(WOSPerson_NetIdMap obj: mergedList){
				if(obj.getName().equals(author)){
					found = true;
					continue;
				}
			}
			if(!found){
				mergedList.add(new WOSPerson_NetIdMap(author, ""));
			}
		}
		saveUpdatedList(mergedList, Configuration.WOS_PERSON_NETID_MASTER_FILE);
	}

	private void saveUpdatedList(List<WOSPerson_NetIdMap> list,  String filePath) {
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (filePath);	
			for(WOSPerson_NetIdMap obj: list){
				printWriter.println(obj.toString()); 
				//name, netid, articleid
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 

	}

	private List<WOSPerson_NetIdMap> readPersonNetIdMapperFile(String filePath) {
		List<WOSPerson_NetIdMap> list = new ArrayList<WOSPerson_NetIdMap>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filePath),',','\"');
			String [] nextLine;	
			WOSPerson_NetIdMap obj  = null;
			while ((nextLine = reader.readNext()) != null) {
				obj = new WOSPerson_NetIdMap();
				obj.setName(nextLine[0]);
				obj.setNetId(nextLine[1]);
				list.add(obj);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	private String getLastName(String name) {
		if(name.indexOf(",") >0){
			return name.substring(0, name.indexOf(","));
		}else{
			return name;
		}
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
					
					// for debugging purposes
//					if(nextLine[6].replaceAll("\\\"", "") == "WOS:A1983PX68700002"){
//						System.out.println(lineJustFetched);
//					}
				
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
		System.out.println(count+" lines read.");
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
			System.err.println(line);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(line);
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
		System.out.println(count+" lines read from grid map file.");
		return map;
	}

}
