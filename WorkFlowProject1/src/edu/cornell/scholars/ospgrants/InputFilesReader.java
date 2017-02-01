package edu.cornell.scholars.ospgrants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.xml.sax.SAXException;

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;


public class InputFilesReader {

	private static final Logger LOGGER = Logger.getLogger( InputFilesReader.class.getName() );

	//input file names
	public static String INPUT_AWRAD_FILENAME = null;
	public static String INPUT_INVESTIGATOR_FILENAME = null;		
	public static String PERSON_NETID_DEPT_MAPPER_FILENAME = null;
	public static String DEPARTMENT_MAPPER_FILENAME = null;
	public static String ALL_GRANTS_FILE = null;

	//output file names
	public static String OUTPUT_TXT_FILE = null;

	public Map<String, Award> awd_entries = null;
	public Map<String, Investigator> inv_entries = null;
	private Map<String, Department> departmentMap = null;
	private Set<Department> newDept = new HashSet<Department>();
	private Map<String, Grant> existingGrants = null;

	public static void main(String[] args) {
		InputFilesReader reader = new InputFilesReader();
		try {
			reader.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private void setLocalDirectories() {
		INPUT_AWRAD_FILENAME = Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/"+
				Configuration.OSP_AWARDS_FILENAME;
		INPUT_INVESTIGATOR_FILENAME = Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/"+
				Configuration.OSP_INV_FILENAME;		
		PERSON_NETID_DEPT_MAPPER_FILENAME = Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/"+
				Configuration.PERSON_2_DEPT_UNIT_MAP_FILENAME;
		DEPARTMENT_MAPPER_FILENAME = Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/"+
				Configuration.OSP_ADMNDEPT_FILENAME;
		ALL_GRANTS_FILE = Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/"+
				Configuration.ALL_GRANTS_FILENAME;

		//output file names
		OUTPUT_TXT_FILE = Configuration.POSTPROCESS_RESULTSET_FOLDER + "/" + Configuration.date +"/"+ 
				Configuration.GRANTS_FOLDER +"/"+ Configuration.OSP_GRANT_TXT;
	}
	
	public void runProcess() throws IOException, ParserConfigurationException, SAXException{
		setLocalDirectories();

		existingGrants = readAllGrantsFile(ALL_GRANTS_FILE);

		AwardsDataReader obj1 = new AwardsDataReader();
		awd_entries = obj1.loadAwardData(new File(INPUT_AWRAD_FILENAME));

		awd_entries = getNewAwardsOnly(awd_entries, existingGrants);

		//saveDistinctSponsors(awd_entries, "resources/output/AwdDistinctSponsorNames.csv");		
		//saveSponsorsFlow(awd_entries, "resources/output/AwdSponsors.csv");

		InvestigatorDataReader obj2 = new InvestigatorDataReader();
		inv_entries = obj2.loadInvestigatorData(new File(INPUT_INVESTIGATOR_FILENAME));

		generateDeptMapFile(new File(DEPARTMENT_MAPPER_FILENAME));

		List<Person_NetIdDeptMap> list = readPersonNetIdDeptMapperFile(new File(PERSON_NETID_DEPT_MAPPER_FILENAME));
		mergeData(list, awd_entries, inv_entries, new File(OUTPUT_TXT_FILE));

		//update mapping file.
		updateDepartmentMapperFile(new File(DEPARTMENT_MAPPER_FILENAME));

	}

	private Map<String, Award> getNewAwardsOnly(Map<String, Award> awd_entries2, Map<String, Grant> existingGrants2) {
		Map<String, Award> newAwards = new HashMap<String, Award>();
		Set<String> existingIds = existingGrants2.keySet();

		Set<String> latestIds = awd_entries2.keySet();
		for(String id: latestIds){
			if(!existingIds.contains(id)){
				newAwards.put(id, awd_entries2.get(id));
				//System.out.println(awd_entries2.get(id).getAWARD_PROP_FULL_TITLE());
			}
		}
		LOGGER.info("GRANTS: New award entries:"+ newAwards.size());
		return newAwards;
	}

	private void updateDepartmentMapperFile(File deptFile) throws IOException {
		int counter=0;
		PrintWriter pw = null;
		FileWriter fw = new FileWriter(deptFile, true);
		pw = new PrintWriter(fw);
		for(Department dept: newDept){
			LOGGER.warning("Department Not Found: "+dept.getDeptId()+", "+ dept.getDeptName()+", "+dept.getRollupName());
			if(departmentMap.get(dept.getDeptId()) == null){
				pw.print("\n"+dept.toString());
				counter++;
			}
		}   
		pw.close();
		LOGGER.info("GRANTS: "+counter+" new rows added in "+ DEPARTMENT_MAPPER_FILENAME);
	}

	private void saveSponsorsFlow(Map<String, Award> awd_entries, String filePath) throws FileNotFoundException {
		PrintWriter printWriter;
		printWriter = new PrintWriter (filePath);	
		for(Award awd: awd_entries.values()){
			printWriter.println("\""+
					awd.getAWARD_PROP_SPONSOR_ID()+"\",\""+
					awd.getAWARD_PROP_SPONSOR_NAME()+"\",\""+
					awd.getFLOW_THROUGH_SPONSOR_ID()+"\",\""+
					awd.getFLOW_THROUGH_SPONSOR_NAME() + "\",\""+
					awd.getSP_LEV_1()+ "\",\""+
					awd.getSP_LEV_2()+"\",\""+
					awd.getSP_LEV_3()+"\""
					);
		}
		printWriter.close();
	}

	private void saveDistincxtSponsors(Map<String, Award> awd_entries, String filePath) throws FileNotFoundException {
		Set<String> distinctSponsors = new HashSet<String>();
		for(Award awd: awd_entries.values()){
			distinctSponsors.add(awd.getAWARD_PROP_SPONSOR_NAME());
		}
		PrintWriter printWriter;
		printWriter = new PrintWriter (filePath);	
		for(String sponsor: distinctSponsors){
			printWriter.println("\""+sponsor+"\"");
		}
		printWriter.close();
	}

	private void mergeData(List<Person_NetIdDeptMap> list, Map<String, Award> awd_entries2, Map<String, Investigator> inv_entries2, File file) throws FileNotFoundException {
		PrintWriter printWriter;
		int counter = 0;
		Set<String> investigators = inv_entries2.keySet();
		printWriter = new PrintWriter (file.getAbsolutePath());	
		for(String inv: investigators){
			String prjId = inv.substring(0, inv.indexOf("-"));
			Award award = awd_entries2.get(prjId);
			if(award == null) continue;
			Investigator investigator = inv_entries2.get(inv);
			Person_NetIdDeptMap entity = findMappedPerson(list, investigator.getINVPROJ_INVESTIGATOR_NETID()); 
			if(entity == null) continue ;  // no investigator found
			// Mapping the Department coming from the OSP Feed.
			String deptId = award.getAWARD_PROP_DEPARTMENT_ID();
			Department dept = getDeptURIFromControlFile(deptId);

			if(dept == null || dept.getVivoURI() == null || dept.getVivoURI().isEmpty()){
				printWriter.println(entity.getCollege()+"\t"+entity.getPersonURI()+"\t"+entity.getDept()+"\t"+entity.getDeptURI()+"\t\t"
						+investigator.toString()+"\t"+award.toString()); 
				counter++;

				//add not-found department in the list.
				newDept.add(new Department(award.getAWARD_PROP_DEPARTMENT_ID(), award.getAWARD_PROP_DEPARTMENT(),award.getROLLUP_DEPT_NAME(), null));
			}else{
				printWriter.println(entity.getCollege()+"\t"+entity.getPersonURI()+"\t"+entity.getDept()+"\t"+entity.getDeptURI()+"\t"+dept.getVivoURI()+"\t"
						+investigator.toString()+"\t"+award.toString()); 
				counter++;
			}				
		}
		printWriter.close();
		LOGGER.info("GRANTS:" + counter+ " number of rows added in the output TSV file.");
	}

	private Department getDeptURIFromControlFile(String deptId) {
		return departmentMap.get(deptId);
	}

	private void generateDeptMapFile(File file) throws IOException{
		departmentMap = generateDeptMap(file);
	}
	private Map<String, Department> generateDeptMap(File file) throws IOException {
		Map<String, Department> map = new HashMap<String, Department>(); 
		BufferedReader br = null;
		String line = "";
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				try {
					String deptId = nextLine[0].trim();
					String deptName = nextLine[1].trim();
					String rollupName = nextLine[2].trim();
					String deptScholarsURI = nextLine[3].trim();
					Department dept = new Department(deptId, deptName, rollupName, deptScholarsURI);
					map.put(deptId, dept);
				}catch (ArrayIndexOutOfBoundsException exp) {
					exp.printStackTrace();
				}
			}
			reader.close();
		}
		br.close();
		LOGGER.info("GRANTS: Administring department mapper file size:"+ map.size());
		return map;
	}

	private Person_NetIdDeptMap findMappedPerson(List<Person_NetIdDeptMap> list, String invproj_INVESTIGATOR_NETID) {
		for(Person_NetIdDeptMap obj: list){
			if(obj.getNetId().toUpperCase().equals(invproj_INVESTIGATOR_NETID.toUpperCase())){
				return obj;
			}
		}
		return null;
	}

	private List<Person_NetIdDeptMap> readPersonNetIdDeptMapperFile(File file) {
		List<Person_NetIdDeptMap> list = new ArrayList<Person_NetIdDeptMap>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file),'|');
			String [] nextLine;	
			Person_NetIdDeptMap obj  = null;
			while ((nextLine = reader.readNext()) != null) {
				obj = new Person_NetIdDeptMap();
				obj.setNetId(nextLine[0].trim());
				obj.setPersonURI(nextLine[1].trim());
				obj.setDept(nextLine[2].trim());
				obj.setDeptURI(nextLine[3].trim());
				obj.setCollege(nextLine[4].trim());
				list.add(obj);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info("GRANTS: Person to NetId/Dept Map size"+ list.size()); 
		return list;
	}

	private Map<String, Grant> readAllGrantsFile(String filePath) throws IOException {
		Map<String, Grant> grants = new HashMap<String, Grant>();
		Reader in;
		in = new FileReader(filePath);
		Iterable<CSVRecord> records = null;
		records = CSVFormat.EXCEL.withDelimiter(',').withQuote('"').parse(in);
		for (CSVRecord record : records) {
			String id = record.get(0);
			String uri = record.get(1);
			String typeURI = record.get(2);
			Grant g = new Grant(id, uri, typeURI);
			grants.put(id, g);
		}
		LOGGER.info("GRANTS: Existing grants in Scholars"+ grants.size()); 
		return grants;
	}

}
