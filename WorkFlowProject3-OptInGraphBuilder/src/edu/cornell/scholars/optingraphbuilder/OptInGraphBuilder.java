package edu.cornell.scholars.optingraphbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import com.opencsv.CSVReader;

//SELECT ?per ?netId ?dept ?coll
//WHERE
//{
//      ?per a foaf:Person .
//      ?per hr:netId ?netId .
//      ?per vivo:relatedBy ?pos .
//      ?pos a vivo:Position .
//      ?pos vivo:relates ?coll .
//      ?coll a foaf:Organization .
//      ?pos hr:positionIn ?dept .    
//}

public class OptInGraphBuilder {

	private static final String OUTPUT_FILE = null;
	private static final String CONTROL_FILE_OUTPUT_FILE = null;

	public static void main(String[] args) {

		OptInGraphBuilder  obj = new OptInGraphBuilder();
		File controlFile = new File("resources/optin/Scholars/OptInControlFile-cals.csv");
		Set<OptInEntity> optinData = obj.readFile(controlFile);
		File personControlFile = new File("resources/optin/Scholars/sparqlquery-PeopleAndTheirPositions-11-04-2017.csv");
		Collection<PersonEntity> personData = obj.readPersonControlFile(personControlFile);

		obj.generateData(optinData, personData, OUTPUT_FILE, CONTROL_FILE_OUTPUT_FILE);

	}
	
	public void runProcess(String oPTIN_CONTROL_FILE, String oPTIN_QUERY_FILE, String oPTIN_OUTPUT_AGENT_NT,
			String oPTIN_OUTPUT_NT) {
		Set<OptInEntity> optinData = readFile(new File(oPTIN_CONTROL_FILE));
		Collection<PersonEntity> personData = readPersonControlFile(new File(oPTIN_QUERY_FILE));
		generateData(optinData, personData, oPTIN_OUTPUT_NT, oPTIN_OUTPUT_AGENT_NT);
	}
	
	private void generateData(Set<OptInEntity> optinData, Collection<PersonEntity> personData, String filePath, String controlFilePath) {
		
		Set<String> optInURIs = new HashSet<String>();
		Set<String> optOutURIs = new HashSet<String>();
		Set<String> personURIsInControlFile = new HashSet<String>();
		
		for(OptInEntity entity: optinData){
			if(entity.getOptin().equalsIgnoreCase("true")){
				optInURIs.add(entity.getURI());
			}else if(entity.getOptin().equalsIgnoreCase("false")){
				optOutURIs.add(entity.getURI());
			}
			
			//getting list of person URIs in control file
			if(entity.getType().equalsIgnoreCase("person")){
				personURIsInControlFile.add(entity.getURI());
			}
		}

		//Creating rdf model for the URIs in control file.
		Model rdfmodelAgent = ModelFactory.createDefaultModel();	
		Property isoptIn = rdfmodelAgent.createProperty("http://scholars.cornell.edu/ontology/vivoc.owl#isOptIn");
		for(String optin: optInURIs){
			Resource agent = rdfmodelAgent.createResource(optin);
			agent.addProperty(isoptIn, "true");
		}
		for(String optout: optOutURIs){
			Resource agent = rdfmodelAgent.createResource(optout);
			agent.addProperty(isoptIn, "false");
		}
		
		int pendingcount = 0;
		int loadingcount = optinData.size();
		
		//Creating rdf model for the person based on their dept/unit.
		Model rdfmodel = ModelFactory.createDefaultModel();	
		Property isOptIn = rdfmodel.createProperty("http://scholars.cornell.edu/ontology/vivoc.owl#isOptIn");
		for(PersonEntity per : personData){
			
			//if person URI exist in control file ignore and continue. person has the veto power.
			if(personURIsInControlFile.contains(per.getPersonURI())) continue;
			
			Resource person = rdfmodel.createResource(per.getPersonURI());
			
			String optIn = null;
			for(String o: per.getCollege()){
				if(optInURIs.contains(o)){
					optIn = "true";         // true have highest precedence. So if either College is true. person in true.
					break;
				}else if(optOutURIs.contains(o)){
					optIn = "false";        // then false
				}
			}
			
			// People may have positions in opt in department with a opt out college.
			for(String o: per.getDepartment()){
				if(optInURIs.contains(o)){
					optIn = "true";         // For example Professor, Earth and Atmospheric Sciences, College of Agriculture and Life Sciences
					break;
				}else if(optOutURIs.contains(o)){
					optIn = "false";        // then false
				}
			}
			
			if(optIn != null){
				person.addProperty(isOptIn, optIn);
				loadingcount++;
			}else{
				pendingcount++;
			}
		}
		try {
			System.out.println("pending: "+ pendingcount);
			System.out.println("optin: "+ loadingcount);
			
			saveRDFModel(rdfmodel, filePath);
			saveRDFModel(rdfmodelAgent, controlFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void saveRDFModel(Model rdfModel, String filePath) throws FileNotFoundException {
		PrintWriter printer = null;
		printer = new PrintWriter(filePath);
		rdfModel.write(printer, "N-Triples");
		printer.flush();
		printer.close();
	}

	private Collection<PersonEntity> readPersonControlFile(File personControlFile) {
		Map<String, PersonEntity> map = new HashMap<String, PersonEntity>();

		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		try {
			br = new BufferedReader(new FileReader(personControlFile));
			while ((line = br.readLine()) != null) {
				lineCount++;

				if(line.trim().length() == 0) continue;
				if (lineCount == 1) continue;  //header

				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					try {
						if(map.get(tokens[0]) == null){
							PersonEntity person = new PersonEntity(tokens[0].trim(), tokens[1].trim());
							person.addDepartment(tokens[2].trim());
							person.addCollege(tokens[3].trim());
							map.put(tokens[0].trim(), person);
						}else{
							PersonEntity person = map.get(tokens[0].trim());
							person.addDepartment(tokens[2].trim());
							person.addCollege(tokens[3].trim());
						}
					}catch (ArrayIndexOutOfBoundsException exp) {
						for (String s : tokens) {
							System.out.println("Exception: "+ lineCount+" :"+ s);
						}
						System.out.println();
						continue;
					}
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
		System.out.println(" line count: "+ lineCount);
		System.out.println("person count:"+map.size());
		return map.values();
	}

	private Set<OptInEntity> readFile(File file){ 
		Set<OptInEntity> data = new HashSet<OptInEntity>();
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				lineCount++;

				if(line.trim().length() == 0) continue;
				if (lineCount == 1) continue;  //header

				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					try {
						data.add(new OptInEntity(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim()));
					}catch (ArrayIndexOutOfBoundsException exp) {
						for (String s : tokens) {
							System.out.println("Exception: "+ lineCount+" :"+ s);
						}
						System.out.println();
						continue;
					}
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
		System.out.println("control file line count:"+lineCount);

		return data;
	}

}
