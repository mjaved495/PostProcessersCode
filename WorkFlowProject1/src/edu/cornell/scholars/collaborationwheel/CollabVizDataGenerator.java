package edu.cornell.scholars.collaborationwheel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.workflow1.MainEntryPoint_WorkFlow1;


public class CollabVizDataGenerator {

	private static final Logger LOGGER = Logger.getLogger(CollabVizDataGenerator.class.getName());
	private static String QUERY_RESULT_FOLDER;
	private static String OUTPUT_FOLDER;
	private static String ORGCODE_FILE;
	
	private Map<String, String> orgFullNameToCodeMap = null;
	private Map<String, String> orgCodeToFullNameMap = null;
	private Set<Collaboration> inputData = null;
	
	public static void main(String[] args) {
		try {
			MainEntryPoint_WorkFlow1.init("resources/setup.properties");
			CollabVizDataGenerator obj = new CollabVizDataGenerator();
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void runProcess() throws IOException {
		setLocalDirectories();
		orgFullNameToCodeMap = readOrgCodeMapFile(new File(ORGCODE_FILE));
		readInputFiles(new File(QUERY_RESULT_FOLDER));
	}

	private void readInputFiles(File folder) throws IOException {
		File[] inputFiles = folder.listFiles();
		for(File inputFile: inputFiles){
			if(!inputFile.getName().endsWith(".csv")) continue;
			inputData = readInputFile(inputFile);
			String outputFilePath = getOutputFilePath(inputFile);
			processData(inputData, inputFile.getName(), outputFilePath);
		}
	}

	private String getOutputFilePath(File inputFile) {
		String filename = inputFile.getName();
		String jsonFileName = filename.substring(0, filename.lastIndexOf(".")).concat(".json");
		return OUTPUT_FOLDER+"/"+jsonFileName;
	}

	private void setLocalDirectories() {
		QUERY_RESULT_FOLDER = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date 
				+ "/" + Configuration.COLLAB_WHEEL_DATA_FOLDER;
		ORGCODE_FILE = Configuration.SUPPL_FOLDER + "/"+ Configuration.ORG_ORGCODE_MAP_FILE;
		OUTPUT_FOLDER = Configuration.POSTPROCESS_RESULTSET_FOLDER+"/"+Configuration.date+"/"+Configuration.COLLABORATION_FOLDER+"/"+Configuration.COLLAB_INTERNAL_FOLDER;
	}

	/**
	 * Collaboration Data builder
	 * @param data
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	private void processData(Set<Collaboration> data, String inputFileName, String outputFilePath) throws JsonGenerationException, JsonMappingException, IOException {
		College college = new College();
		
		college.setName(getCollegeCode(inputFileName).toUpperCase());
		college.setDescription(getFullCollegeName(inputFileName));
		processACollege(college, data);

		createJSONData(college, outputFilePath);
		LOGGER.info("PROCESSC COMPELTED");
	}
	
	private String getFullCollegeName(String inputFileName) {
		return orgCodeToFullNameMap.get(getCollegeCode(inputFileName)) ;
	}
	private String getCollegeCode(String inputFileName) {
		return inputFileName.substring(inputFileName.indexOf("-")+1, inputFileName.indexOf("."));
	}

	private void processACollege(College college, Set<Collaboration> data){
		Department d = null;
		PersonCollaborator1 p1 = null;
		PersonCollaborator2 p2 = null;
		for(Collaboration collab: data){
			String department = collab.getDepartment1();
			d = college.getDepartment(department);
			if(d == null){ // creating new department
				d = createNewDepartment(department);
				
				if(d == null) {
					LOGGER.severe("No Org code found for "+ department +"...returning");
					continue;
				}
				college.addDepartment(d);
			} 
			// department exists
			String person1 = collab.getPersonName1();
			p1 = d.getPersonCollaborator1(person1);
			if(p1 == null){ // creating new personCollaborator1
				p1 = createNewPersonCollaborator1(person1, college.getName());
				d.addPersonCollaborator1(p1);
			}
			// personCollaborator1 exists
			String person2 = collab.getPersonName2();
			p2 = p1.getPersonCollaborator2(person2);
			if(p2 == null){  // creating new personCollaborator2
				p2 = createNewPersonCollaborator2(person2, d.getName());
				p1.addPersonCollaborator2(p2);
			}
			// personCollaborator2 exists
			String article = collab.getArticleURI();
			String date = collab.getDate();
			String title = collab.getArticleTitle();
			Article art = new Article();
			art.setUri(article);
			art.setDate(date);
			art.setTitle(title);
			p2.addArticle(art);
			p2.setSize(p2.getPubs().size());
		}
	}
	private PersonCollaborator2 createNewPersonCollaborator2(String person2, String orgCode) {
		PersonCollaborator2 p = new PersonCollaborator2();
		p.setName(person2);
		p.setDescription(person2);
		p.setOrgCode(orgCode);
		p.setSize(1);
		return p;
	}
	private PersonCollaborator1 createNewPersonCollaborator1(String person1, String orgCode) {
		PersonCollaborator1 p = new PersonCollaborator1();
		p.setName(person1);
		p.setDescription(person1);
		p.setOrgCode(orgCode);
		return p;
	}
	private Department createNewDepartment(String department) {
		Department d = new Department();
		String unitCode = getCode(department);
		if(unitCode != null){
			d.setName(unitCode);
			d.setDescription(department);
			return d;
		}
		return null;
	}
	private String getCode(String org) {
		if(orgFullNameToCodeMap.get(org.toLowerCase()) != null){
			return orgFullNameToCodeMap.get(org.toLowerCase());
		}
		return null;
	}
	@SuppressWarnings("resource")
	private Set<Collaboration> readInputFile(File inputFile) throws IOException {
		Set<Collaboration> set = new HashSet<Collaboration>();
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(inputFile));
		while ((line = br.readLine()) != null) {
			lineCount++;
			
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),',','\"');
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					String dept1 = tokens[0].trim();
					String perURI1 = tokens[1].trim();
					String perName1 = tokens[2].trim();
					String perURI2 = tokens[3].trim();
					String perName2 = tokens[4].trim();
					String articleTitle = tokens[5].trim();
					String articleURI = tokens[6].trim();
					String date = tokens[7].trim();
					Collaboration collab = new Collaboration(dept1, perURI1, perName1, perURI2,
							perName2, articleTitle, articleURI, date);
					set.add(collab);			
				}catch (ArrayIndexOutOfBoundsException exp) {
					for (String s : tokens) {
						LOGGER.warning("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
					LOGGER.warning("\n");
					continue;
				}
			}
		}
		LOGGER.info("input file line count: "+ lineCount);
		LOGGER.info("read set size: "+ set.size());
		return set;
	}
	private Map<String, String> readOrgCodeMapFile(File file) {
		Map<String, String> map  = new HashMap<String, String>();
		orgCodeToFullNameMap =  new HashMap<String, String>();
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file),',','\"');
			String [] nextLine = null;	
			while ((nextLine = reader.readNext()) != null) {
				if(!nextLine[0].trim().isEmpty() && !nextLine[1].trim().isEmpty()){
					map.put(nextLine[0].trim().toLowerCase(), nextLine[1].trim());
					orgCodeToFullNameMap.put(nextLine[1].trim().toLowerCase(), nextLine[0].trim());
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	private void createJSONData(College colleges2, String outputfilePath) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		mapper.writeValue(new File(outputfilePath), colleges2);
		jsonInString = mapper.writeValueAsString(colleges2);
	}
}
