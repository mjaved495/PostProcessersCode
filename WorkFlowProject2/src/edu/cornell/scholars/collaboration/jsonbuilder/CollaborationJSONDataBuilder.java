package edu.cornell.scholars.collaboration.jsonbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import edu.cornell.scholars.collaboration.datamodel.data.ArticleToIdMap;
import edu.cornell.scholars.collaboration.datamodel.data.Collaboration;
import edu.cornell.scholars.collaboration.datamodel.json.Collaboration_JSON;
import edu.cornell.scholars.collaboration.datamodel.json.Department_JSON;
import edu.cornell.scholars.collaboration.datamodel.json.Person_JSON;
import edu.cornell.scholars.collaboration.datamodel.json.Publication_JSON;
import edu.cornell.scholars.collaboration.datamodel.json.Unit_JSON;
import edu.cornell.scholars.config.Configuration;


public class CollaborationJSONDataBuilder {

	private static String COLLABORATIONS_CSV_FILE = Configuration.POSTPROCESS_RESULTSET_FOLDER   + "/" + Configuration.date + "/"
			+Configuration.COLLABORATION_FOLDER+"/"+Configuration.COLLAB_INTERNAL_FOLDER+"/" + Configuration.INT_COLLABORATIONS_FILE_CSV;
	private static String ARTICLE_2_ID_MAP_FILE = Configuration.QUERY_RESULTSET_FOLDER +"/"+Configuration.date+"/"+Configuration.ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV;
	private static String COLLABORATIONS_JSON_FILEPATH = Configuration.POSTPROCESS_RESULTSET_FOLDER   + "/" + Configuration.date + "/"
			+ Configuration.COLLABORATION_FOLDER+"/"+ Configuration.COLLAB_INTERNAL_FOLDER+"/";

	private List<Collaboration> collaborations = null;
	private Map<String, String> name_netId_map = null;
	private List<ArticleToIdMap> article2IdMapData = null;
	private Map<String, Set<String>> wosId2UnitsMap = null;
	
	public static void main(String[] args) {
		CollaborationJSONDataBuilder obj = new CollaborationJSONDataBuilder();
		obj.runProcess();
	}

	public void runProcess() {

		article2IdMapData = readArtile2IdMapFile(ARTICLE_2_ID_MAP_FILE);
		collaborations = readCollaborationFile(COLLABORATIONS_CSV_FILE);
		/**
		 * Main method that converts csv to json collaboration data.
		 */
		prepareJSONDataFiles(article2IdMapData, collaborations);


	}

	private void prepareJSONDataFiles(List<ArticleToIdMap> article2IdMapData, List<Collaboration> collaborations) {

		name_netId_map = createNameNetIdMap(collaborations);
		//		Map<String, Integer> col_size_map = new HashMap<String, Integer>();
		//		col_size_map = getCollabCountMap(collaborations);

		Map<String, String> distinctUnitMap = getDistinctUnits(collaborations);
		
		//create set of distinct units.
		Set<String> distinctUnitSet = distinctUnitMap.keySet();
		
		wosId2UnitsMap = getWosIdToUnitsMap(collaborations);
		
		for(String unit: distinctUnitSet){
			if(unit.isEmpty()) continue;
			Collaboration_JSON collaborations_json = createUnitLevelJSONObject(unit, collaborations);
			
			Unit_JSON json_interdept = getInterDepartmentalCollaborationData(collaborations_json, unit);
			Collaboration_JSON collaborations_json_crossUnit = getCrossUnitCollaborationData(collaborations_json, unit);
			
			String fileCode = distinctUnitMap.get(unit);
			
			String absFilePathInterDept = COLLABORATIONS_JSON_FILEPATH+fileCode+"-interdept.json";
			String absFilePathCrossUnit = COLLABORATIONS_JSON_FILEPATH+fileCode+"-crossunit.json";
			//String jsonInString = createJSONString(collaborations_json, absFilePath);
			
			if(json_interdept != null){
				String jsonInString_interDept = createInterDeptJSONString(json_interdept, absFilePathInterDept);
				saveJSONFile(jsonInString_interDept, absFilePathInterDept);
			}
			if(collaborations_json_crossUnit != null){
				String jsonInString_crossUnit = createJSONString(collaborations_json_crossUnit, absFilePathCrossUnit);
				saveJSONFile(jsonInString_crossUnit, absFilePathCrossUnit);
			}
		}
	}
	
	private Collaboration_JSON getCrossUnitCollaborationData(Collaboration_JSON collaborations_json, String unit) {
		Collaboration_JSON collaborations_json_crossUnit = new Collaboration_JSON();
		collaborations_json_crossUnit.setName(collaborations_json.getName());
		collaborations_json_crossUnit.setDescription(collaborations_json.getDescription());
		Set<Unit_JSON> newUnits = new HashSet<Unit_JSON>();
		Set<Unit_JSON> units = collaborations_json.getChildren();
		for(Unit_JSON u: units){
			String d = u.getDescription();
			if(!d.toUpperCase().equals(unit.toUpperCase())){
				newUnits.add(u);
			}
		}
		collaborations_json_crossUnit.setChildren(newUnits);
		return collaborations_json_crossUnit;
	}

	private Unit_JSON getInterDepartmentalCollaborationData(Collaboration_JSON collaborations_json,
			String unit) {
		Set<Unit_JSON> units = collaborations_json.getChildren();
		for(Unit_JSON u: units){
			String d = u.getDescription();
			if(d.toUpperCase().equals(unit.toUpperCase())){
				return u;
			}
		}
		return null;
	}

	private Collaboration_JSON createUnitLevelJSONObject(String homeUnit, List<Collaboration> collaborations){

		Collaboration_JSON collaborations_json = new Collaboration_JSON();
		collaborations_json.setName(homeUnit);
		collaborations_json.setDescription(homeUnit);

		for(Collaboration collaboration: collaborations){
			
			
			Set<String> set = wosId2UnitsMap.get(collaboration.getWosId());
			if(!set.contains(homeUnit)) continue;
			//System.out.println(wosId2UnitsMap.get(collaboration.getWosId()).toString());
			Set<Unit_JSON> units = collaborations_json.getChildren();

			String unitName = collaboration.getCollabUnitName();

			String personName = "";
			// get unique names across data.
			if(!collaboration.getNetId().isEmpty()){
				personName = name_netId_map.get(collaboration.getNetId());
			}else{
				personName = name_netId_map.get(collaboration.getPersonName());
			}

			String deptName = collaboration.getDepartmentName();
			String deptCode = collaboration.getDepartmentCode();
			String unitDescription = collaboration.getDescription();
			//String mapping = homeName+SEPARATOR+unitName+SEPARATOR+deptName+SEPARATOR+personName;

			if(!containsUnit(units, unitName)){
				//if(!containsUnit(units, UnitAbbreviation.getAbbreviation(unitName))){

				// college do not exists and so do the department and person for this college
				Person_JSON newPerson = new Person_JSON();

				newPerson.setName(personName);
				if(!collaboration.getNetId().isEmpty()){
					newPerson.setUri("http://scholars.cornell.edu/individual/"+collaboration.getNetId());
				}
				newPerson.setDescription(personName);

				String title = collaboration.getArticleTitle();
				String uri = getURI(collaboration.getWosId(), collaboration.getPubmedId(), article2IdMapData);
				Publication_JSON pub = new Publication_JSON(uri, title);
				newPerson.getPubs().add(pub);

				newPerson.setSize(1);
				Department_JSON newDepartment = new Department_JSON();

				newDepartment.setName(deptCode);  // name is the code
				//newDepartment.setName(UnitAbbreviation.getAbbreviation(deptName));
				newDepartment.setDescription(deptName);  // deptName is full name

				newDepartment.getChildren().add(newPerson);
				Unit_JSON newUnit = new Unit_JSON();

				newUnit.setName(unitName);
				//newUnit.setName(UnitAbbreviation.getAbbreviation(unitName));
				newUnit.setDescription(unitDescription);

				newUnit.getChildren().add(newDepartment);
				units.add(newUnit);

			}else{
				//college exists.  get it

				Unit_JSON existingUnit = getUnit(units, unitName);
				//Unit_JSON existingUnit = getUnit(units, UnitAbbreviation.getAbbreviation(unitName));

				// get list of departments of the existing college
				Set<Department_JSON> departments = existingUnit.getChildren();
				// does department exists for that college ?

				if(containsDepartment(departments, deptCode)){
					//if(containsDepartment(departments, UnitAbbreviation.getAbbreviation(deptName))){


					// Department does exists for this college. Get it
					Department_JSON existingDepartment = getDepartment(departments,deptCode);
					//Department_JSON existingDepartment = getDepartment(departments,UnitAbbreviation.getAbbreviation(deptName));


					// get list of persons of the existing department
					Set<Person_JSON> persons = existingDepartment.getChildren();
					// Does the person exists for this department
					if(containsPerson(persons, personName)){
						// Person does exists. Get it.
						Person_JSON existingPerson = getPerson(persons, personName);
						// Increment count by 1.
						int i = existingPerson.getSize();
						existingPerson.setSize(++i);

						String title = collaboration.getArticleTitle();
						String uri = getURI(collaboration.getWosId(), collaboration.getPubmedId(), article2IdMapData);
						Publication_JSON pub = new Publication_JSON(uri, title);
						existingPerson.getPubs().add(pub);

					}else{
						// Person does not exists
						// create a new person with count 1
						Person_JSON newPerson = new Person_JSON();

						newPerson.setName(personName);
						if(!collaboration.getNetId().isEmpty()){
							newPerson.setUri("http://scholars.cornell.edu/individual/"+collaboration.getNetId());
						}
						newPerson.setDescription(personName);

						String title = collaboration.getArticleTitle();
						String uri = getURI(collaboration.getWosId(), collaboration.getPubmedId(), article2IdMapData);
						Publication_JSON pub = new Publication_JSON(uri, title);
						newPerson.getPubs().add(pub);

						newPerson.setSize(1);
						// Add new person to the existing department.
						existingDepartment.getChildren().add(newPerson);
					}	
				}else{
					// Department does not exist and so the person in this department.	
					// Create a new person with count 1 
					Person_JSON newPerson = new Person_JSON();

					newPerson.setName(personName);
					if(!collaboration.getNetId().isEmpty()){
						newPerson.setUri("http://scholars.cornell.edu/individual/"+collaboration.getNetId());
					}
					newPerson.setDescription(personName);

					String title = collaboration.getArticleTitle();
					String uri = getURI(collaboration.getWosId(), collaboration.getPubmedId(), article2IdMapData);
					Publication_JSON pub = new Publication_JSON(uri, title);
					newPerson.getPubs().add(pub);

					newPerson.setSize(1);
					// Create a new department
					Department_JSON newDepartment = new Department_JSON();

					newDepartment.setName(collaboration.getDepartmentCode());  // name is the code
					//newDepartment.setName(UnitAbbreviation.getAbbreviation(deptName));
					newDepartment.setDescription(deptName);

					// Add new person to the new department
					newDepartment.getChildren().add(newPerson);
					// add new department to existing unit.
					existingUnit.getChildren().add(newDepartment);
				}	
			}
		}

		return collaborations_json;
	}

	private Map<String, String> getDistinctUnits(List<Collaboration> collaborations) {
		Map<String, String> units = new HashMap<String, String>();
		for(Collaboration collaboration : collaborations){
			String code = collaboration.getCollabUnitName();
			if(code.contains("/")){
				String unitSplit[] = collaboration.getDescription().split(",");
				String codeSplit[] = collaboration.getCollabUnitName().split("/");
				for(int i = 0; i<unitSplit.length; i++){
					units.put(unitSplit[i].trim(), codeSplit[i].trim());
				}
			}else{
				units.put(collaboration.getDescription().trim(), collaboration.getCollabUnitName().trim());
			}
		}
		return units;
	}

	private String getURI(String wosId, String pubmedId, List<ArticleToIdMap> data) {	
		for(ArticleToIdMap iData: data){	
			if(iData.getWosId().equalsIgnoreCase(wosId) && !iData.getWosId().isEmpty() && !wosId.isEmpty()){
				return iData.getArticleURI();
			}else if(iData.getPubmedId().equalsIgnoreCase(pubmedId) && !iData.getPubmedId().isEmpty() && !pubmedId.isEmpty()){
				return iData.getArticleURI();
			}
		}
		System.err.println("URI NOT FOUND FOR ID-: "+ wosId +"--"+pubmedId);
		return null;
	}


	private boolean containsUnit(Set<Unit_JSON> units, String unitName) {
		for(Unit_JSON unit: units){
			if(unit.getName().equals(unitName)){
				return true;
			}
		}
		return false;
	}

	private boolean containsDepartment(Set<Department_JSON> departments, String deptCode) {
		for(Department_JSON department: departments){
			if(department.getName().equals(deptCode)){
				return true;
			}
		}
		return false;
	}

	private boolean containsPerson(Set<Person_JSON> persons, String personName) {
		for(Person_JSON person: persons){
			if(person.getName().equals(personName)){
				return true;
			}
		}
		return false;
	}

	private Person_JSON getPerson(Set<Person_JSON> persons, String personName) {
		for(Person_JSON pers_json: persons ){
			if(pers_json.getName().equals(personName)){
				return pers_json;
			}
		}
		return null;
	}

	private Department_JSON getDepartment(Set<Department_JSON> departments, String deptCode) {
		for(Department_JSON dept_json: departments ){
			if(dept_json.getName().equals(deptCode)){
				return dept_json;
			}
		}
		return null;
	}

	private Unit_JSON getUnit(Set<Unit_JSON> units, String unitName) {
		for(Unit_JSON unit_json: units ){
			if(unit_json.getName().equals(unitName)){
				return unit_json;
			}
		}
		return null;
	}

	//	private Map<String, Integer> getCollabCountMap(List<Collaboration> collaborations) {
	//		Map<String, Integer> map = new HashMap<String, Integer>();
	//		for(Collaboration collaboration: collaborations){
	//			String home = collaboration.getHomeUnitName();
	//			String unit = collaboration.getCollabUnitName();
	//			String dept = collaboration.getDepartmentName();
	//			String pers = collaboration.getPersonName();
	//			String mapping = home+SEPARATOR+unit+SEPARATOR+dept+SEPARATOR+pers;
	//			if(map.get(mapping)== null){
	//				map.put(mapping, 1);
	//			}else{
	//				Integer i = map.get(mapping);
	//				i = i+1;
	//				map.put(mapping, i);
	//			}
	//		}
	//		return map;
	//	}

	private Map<String, String> createNameNetIdMap(List<Collaboration> collaborations) {
		Map<String, String> map = new HashMap<String, String>();
		for(Collaboration collaboration: collaborations){
			if(!collaboration.getNetId().isEmpty()){
				map.put(collaboration.getNetId(), collaboration.getPersonName());
			}else{
				map.put(collaboration.getPersonName(), collaboration.getPersonName());
			}
		}
		return map;
	}

	private Map<String, Set<String>> getWosIdToUnitsMap(List<Collaboration> collaborations) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>> ();
		Set<String> distinctWOSIds = new HashSet<String>();
		for(Collaboration collab : collaborations){
			String wosId = collab.getWosId();
			distinctWOSIds.add(wosId);
			Set<String> collegeSet = map.get(wosId);
			if(collegeSet != null){
				String code = collab.getCollabUnitName();
				if(code.contains("/")){
					String unitSplit[] = collab.getDescription().split(",");
					for(int i = 0; i<unitSplit.length; i++){
						collegeSet.add(unitSplit[i].trim());
						map.put(wosId, collegeSet);
					}
				}else{
					collegeSet.add(collab.getDescription());
					map.put(wosId, collegeSet);
				}
			}else{
				Set<String> newSet = new HashSet<String>();
				String code = collab.getCollabUnitName();
				if(code.contains("/")){
					String unitSplit[] = collab.getDescription().split(",");
					for(int i = 0; i<unitSplit.length; i++){
						newSet.add(unitSplit[i].trim());
						map.put(wosId, newSet);
					}
				}else{
					newSet.add(collab.getDescription());
					map.put(wosId, newSet);
				}
			}
		}
	
		System.out.println("distinct wosIds:"+distinctWOSIds.size());
		System.out.println("distinct wosIds map count:"+map.size());
		
		return map;
	}

	
	private List<Collaboration> readCollaborationFile(String filePath) {
		List<Collaboration> list = new ArrayList<Collaboration>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			while ((line = br.readLine()) != null) {
				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					Collaboration obj = new Collaboration();
					obj.setWosId(tokens[0]);
					obj.setYear(Integer.parseInt(tokens[1]));
					obj.setDescription(tokens[2]);
					obj.setCollabUnitName(tokens[3]);
					obj.setDepartmentName(tokens[4]);
					obj.setDepartmentCode(tokens[5]);
					obj.setPersonName(tokens[6]);
					obj.setNetId(tokens[7]);
					obj.setPubmedId(tokens[8]);
					obj.setArticleTitle(tokens[9]);
					list.add(obj);
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
		return list;
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
	
	private static void saveJSONFile(String jsonString, String filePath) {

		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (filePath);	
			printWriter.println(jsonString);
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
	}
	
	private String createInterDeptJSONString(Unit_JSON json_interdept, String absFilePathInterDept) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		try {

			mapper.writeValue(new File(absFilePathInterDept), json_interdept);
			jsonInString = mapper.writeValueAsString(json_interdept);
			//System.out.println(jsonInString);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonInString;
	}
	
	private String createJSONString(Collaboration_JSON collaborations_json, String absFilePath) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		try {

			mapper.writeValue(new File(absFilePath), collaborations_json);
			jsonInString = mapper.writeValueAsString(collaborations_json);
			//System.out.println(jsonInString);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonInString;
	}

}
