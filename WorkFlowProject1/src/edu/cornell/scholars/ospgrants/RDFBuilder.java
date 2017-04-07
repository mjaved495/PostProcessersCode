package edu.cornell.scholars.ospgrants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;


public class RDFBuilder {

	private static final Logger LOGGER = Logger.getLogger( RDFBuilder.class.getName() );


	// input file names
	public static String ORG_LIST_MASTER = null;
	public static String INPUT_TXT_FILE = null;

	//output file names
	public static String OUTPUT_NT_FILE = null;

	public static String SCHOLARS_IND = "http://scholars.cornell.edu/individual/";
	public static String SCHOLARS_NS = "http://scholars.cornell.edu/ontology/vivoc.owl#";
	public static String SCHOLARS_GRANT_NS = "http://scholars.cornell.edu/ontology/ospcu.owl#";
	public static String VIVO_NS = "http://vivoweb.org/ontology/core#";
	public static String OBO_NS = "http://purl.obolibrary.org/obo/";

	public Double totalAmount  = 0d;
	public Long fundingcount = 0l;
	public Map<String, Double> dept_amnt = new HashMap<String, Double>();
	public Map<String, Integer> dept_count = new HashMap<String, Integer>();
	public Map<String, Double> fundOrg = new HashMap<String, Double>();

	public static void main(String[] args) {
		RDFBuilder builder = new RDFBuilder();
		try {
			builder.runProcess();
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}


	private void setLocalDirectories() {
		ORG_LIST_MASTER = Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/"+
				Configuration.ALL_ORGANIZATION_MAP_FILENAME;
		INPUT_TXT_FILE = Configuration.POSTPROCESS_RESULTSET_FOLDER + "/" + Configuration.date  +"/"+ 
				Configuration.GRANTS_FOLDER +"/"+ Configuration.OSP_GRANT_TXT;

		//output file names
		OUTPUT_NT_FILE = Configuration.POSTPROCESS_RESULTSET_FOLDER + "/" + Configuration.date  +"/"+ 
				Configuration.GRANTS_FOLDER +"/"+ Configuration.OSP_GRANT_NT;
	}

	public void runProcess() throws NoSuchAlgorithmException, IOException {
		setLocalDirectories();
		if(!new File(INPUT_TXT_FILE).exists()){
			LOGGER.info("GRANTS: AwdInv-all.txt does not exists....RETURINING.");
			return;
		}
		
		generateRDF(INPUT_TXT_FILE, OUTPUT_NT_FILE);
	}

	private void generateRDF(String input , String output) throws IOException, NoSuchAlgorithmException {

		File inputFile = new File(input);

		Map<String, String> org_map = readOrgFile(ORG_LIST_MASTER);

		List<GrantModel> data = readFile(inputFile);
		
		if(data.size() == 0){
			LOGGER.info("GRANTS: "+data.size() + " new grants found in txt file....returning.");
			return;
		}
		
		Set<Integer> uniqueKeys = gen(data.size());

		Map<String, String> map = new HashMap<String, String>();  // <project id, project uri>

		Model model = ModelFactory.createDefaultModel();

		Resource vivoGrant = model.createResource(VIVO_NS+"Grant");
		Resource vivoContract = model.createResource(VIVO_NS+"Contract");
		Resource PI_ROLE = model.createResource(VIVO_NS+"PrincipalInvestigatorRole");
		Resource COPI_ROLE = model.createResource(VIVO_NS+"CoPrincipalInvestigatorRole");
		Resource YEAR_PRECISION = model.createResource(VIVO_NS+"yearPrecision");
		Resource ADMINROLE = model.createResource(VIVO_NS+"AdministratorRole");
		Resource DTV = model.createResource(VIVO_NS+"DateTimeValue");
		Resource DTI = model.createResource(VIVO_NS+"DateTimeInterval");
		Resource fundingOrganization = model.createResource(VIVO_NS+"FundingOrganization");

		Property RO_0000052 = model.createProperty(OBO_NS+"RO_0000052");
		Property RO_0000053 = model.createProperty(OBO_NS+"RO_0000053");
		Property vivoRelates = model.createProperty(VIVO_NS+"relates");
		Property vivoRelatedBy= model.createProperty(VIVO_NS+"relatedBy");
		Property dtiProp = model.createProperty(VIVO_NS+"dateTimeInterval");
		Property vivoStart = model.createProperty(VIVO_NS+"start");
		Property vivoEnd = model.createProperty(VIVO_NS+"end");
		Property vivodateTime = model.createProperty(VIVO_NS+"dateTime");
		Property vivodateTimePrecision = model.createProperty(VIVO_NS+"dateTimePrecision");
		Property vivoAwardAmount = model.createProperty(VIVO_NS+"totalAwardAmount");
		Property vivolocalAwardId = model.createProperty(VIVO_NS+"localAwardId");
		Property vivoAssigns = model.createProperty(VIVO_NS+"assigns");
		Property vivoAssignedBy = model.createProperty(VIVO_NS+"assignedBy");

		Resource schCoop = model.createResource(SCHOLARS_GRANT_NS+"CooperativeAgreement");
		Property awardStatus = model.createProperty(SCHOLARS_GRANT_NS+"awardStatus");
		Property sponsorLevelOne = model.createProperty(SCHOLARS_GRANT_NS+"sponsorLevelOne");
		Property sponsorLevelTwo = model.createProperty(SCHOLARS_GRANT_NS+"sponsorLevelTwo");
		Property sponsorLevelThree = model.createProperty(SCHOLARS_GRANT_NS+"sponsorLevelThree");

		for(GrantModel obj: data){
			// if the person's role is not PI or CO, then we do not add this entry. we may find other entries for the same grant
			// where the rols is PI or CO, then the entry will be added in the rdf model.
			if(!obj.getPersonRole().equals("CO") && !obj.getPersonRole().equals("PI")) continue;

			Resource gt = null;
			if(map.get(obj.getProjectId()) == null){

				// if grant does not exist in the model
				String fragment = "gnt"+ obj.getProjectId();
				gt = model.createResource(SCHOLARS_IND+fragment);

				// not always :Grant
				String type = obj.getGrantType();
				if(type.equalsIgnoreCase("GRANT")){
					gt.addProperty(RDF.type, vivoGrant);
				}else if(type.equalsIgnoreCase("CONTRACT")){
					gt.addProperty(RDF.type, vivoContract);
				}else if(type.equalsIgnoreCase("COOP")){
					gt.addProperty(RDF.type, schCoop);
				}
				String grntTitle = obj.getGrantTitle().replaceAll("^\"|\"$", "");
				gt.addProperty(RDFS.label, grntTitle);

				String deptURI = obj.getDepartmentURI();
				if(deptURI != null & !deptURI.isEmpty()){
					Resource dept = model.createResource(deptURI);
					gt.addProperty(vivoRelates, dept);
					dept.addProperty(vivoRelatedBy, gt);
					Resource adminRole = model.createResource(gt.getURI()+"-ADMR");
					adminRole.addProperty(RDF.type, ADMINROLE);
					gt.addProperty(vivoRelates, adminRole);
					adminRole.addProperty(vivoRelatedBy, gt);
					adminRole.addProperty(RO_0000052, dept);
					dept.addProperty(RO_0000053, adminRole);
				}

				collectFundingOrg(obj);

				Resource dti = model.createResource(gt.getURI()+"-DTI");
				dti.addProperty(RDF.type, DTI);
				gt.addProperty(dtiProp, dti);

				String sponsorName = obj.getSponsorName().replaceAll("^\"|\"$", "");
				Resource sponsor = null;
				if(org_map.get(sponsorName) != null){
					String sponsorURI = org_map.get(sponsorName);
					sponsor = model.createResource(sponsorURI);
				}else{ 
					Iterator<Integer> i = uniqueKeys.iterator();
					int id = i.next();
					String sponsorURI = SCHOLARS_IND +"org"+id;
					//uri should not exist earlier
					while(org_map.values().contains(sponsorURI)){
						id = i.next();
						sponsorURI = SCHOLARS_IND +"org"+id; // create new URI
					}
					if(!org_map.values().contains(sponsorURI)){
						sponsor = model.createResource(sponsorURI);
						sponsor.addProperty(RDFS.label, sponsorName);
					}
				}
				if(sponsor != null){		
					gt.addProperty(vivoAssignedBy, sponsor);
					sponsor.addProperty(vivoAssigns, gt);
					sponsor.addProperty(RDF.type, fundingOrganization);
					org_map.put(sponsorName, sponsor.getURI());

				}else{
					LOGGER.warning("Could not create new sponsor organization URI.");
				}

				Resource startDTV = model.createResource(gt.getURI()+"-DTI-S");
				startDTV.addProperty(RDF.type, DTV);
				startDTV.addProperty(vivodateTimePrecision, YEAR_PRECISION);

				Calendar startCal = Calendar.getInstance();
				//"2011-09-01T00:00:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>
				startCal.set(Integer.parseInt(obj.getStartDate()), 0, 1, 0, 0, 0);
				Literal startValue = model.createTypedLiteral(startCal);
				startDTV.addProperty(vivodateTime, startValue);
				dti.addProperty(vivoStart, startDTV);

				Resource endDTV = model.createResource(gt.getURI()+"-DTI-E");
				endDTV.addProperty(RDF.type, DTV);
				endDTV.addProperty(vivodateTimePrecision, YEAR_PRECISION);
				Calendar endCal = Calendar.getInstance();
				//"2011-09-01T00:00:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>
				endCal.set(Integer.parseInt(obj.getEndDate()), 12, 31, 0, 0, 0);
				Literal endValue = model.createTypedLiteral(endCal);
				endDTV.addProperty(vivodateTime, endValue);
				dti.addProperty(vivoEnd, endDTV);

				gt.addProperty(vivoAwardAmount, Double.toString(obj.getGrantTotal()));
				gt.addProperty(vivolocalAwardId, obj.getProjectId());
				gt.addProperty(awardStatus, obj.getAwardStatus());

				String sp1 = obj.getSpLevel1().replaceAll("^\"|\"$", "");
				if(sp1 != null && !sp1.isEmpty()){
					gt.addProperty(sponsorLevelOne, sp1);
				}
				String sp2 = obj.getSpLevel2().replaceAll("^\"|\"$", "");
				if(sp2 != null && !sp2.isEmpty()){
					gt.addProperty(sponsorLevelTwo, sp2);
				}
				String sp3 = obj.getSpLevel3().replaceAll("^\"|\"$", "");
				if(sp3 != null && !sp3.isEmpty()){
					gt.addProperty(sponsorLevelThree, sp3);
				}
				map.put(obj.getProjectId(), gt.getURI());
			}else{
				// if grant already exist in the model (the model that we are creating). This is the case when this 
				//row adds a new person with a new role in existing grant.

				//grant already exists
				gt = model.createResource(map.get(obj.getProjectId()));
			}

			// Adding Person and his/her Role
			String personURI = obj.getPersonURI();
			Resource person = model.createResource(personURI);
			gt.addProperty(vivoRelates, person);
			person.addProperty(vivoRelatedBy, gt);
			// inheres, bearer of (role of person)
			Resource piRole = model.createResource(gt.getURI()+"-"+person.getLocalName());
			gt.addProperty(vivoRelates, piRole);
			piRole.addProperty(vivoRelatedBy, gt);
			String personRole = obj.getPersonRole();
			if(personRole.equals("PI")){
				piRole.addProperty(RDF.type, PI_ROLE);
			}else if(personRole.equals("CO")){
				piRole.addProperty(RDF.type, COPI_ROLE);
			}	
			piRole.addProperty(RO_0000052, person);
			person.addProperty(RO_0000053, piRole);
		}
		saveRDFGraph(model, output);
	}

	private void collectFundingOrg(GrantModel obj) {
		String org = obj.getSponsorName().replaceAll("\"", "");
		if(fundOrg.get(org) == null){
			fundOrg.put(org, obj.getGrantTotal());
		}else{
			Double val = fundOrg.get(org);
			val += obj.getGrantTotal();
			fundOrg.put(org, val);
		}
	}


	private Map<String, String> readOrgFile(String file) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		CSVReader reader;
		reader = new CSVReader(new FileReader(file),'|');
		String [] token;	
		while ((token = reader.readNext()) != null) {
			map.put(token[1], token[0]);
		}
		reader.close();
		return map;
	}

	private Set<Integer> gen(int size) throws NoSuchAlgorithmException {
		Set<Integer> set = new HashSet<>();
		SecureRandom randomGenerator = SecureRandom.getInstance("SHA1PRNG");         
		while(set.size() < size){
			int s = randomGenerator.nextInt(99999);
			if(s > 9999){
				set.add(s);
			}
		}
		return set;
	}

	private void saveRDFGraph(Model model, String output) throws FileNotFoundException {
		LOGGER.info("GRANTS: RDF triples count: "+model.listStatements().toList().size());
		PrintWriter printer = null;
		printer = new PrintWriter(output);
		model.write(printer, "N-Triples");
		printer.close();
	}

	private List<GrantModel> readFile(File file) throws IOException {
		LOGGER.info("GRANTS: Reading Grants TSV file..."+ file.getAbsolutePath());
		List<GrantModel> data = new ArrayList<GrantModel>();
		FileInputStream is = new FileInputStream(file.getAbsolutePath());
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader buf = new BufferedReader(isr);
		ArrayList<String[]> rows = new ArrayList<String[]>();
		String lineJustFetched = null;
		String[] wordsArray;
		while(true){
			lineJustFetched = buf.readLine();
			if(lineJustFetched == null){  
				break; 
			}else{
				if(lineJustFetched.trim().length() == 0){
					continue;
				}
				//System.out.println(lineJustFetched);
				wordsArray = lineJustFetched.split("\t");
				rows.add(wordsArray);
			}
		}
		GrantModel obj  = null;
		for(int index = 0; index<rows.size();index++){
			String[] nextLine = rows.get(index);
			obj = new GrantModel();
			obj.setUnit(nextLine[0].trim());
			obj.setPersonURI(nextLine[1].trim());
			obj.setDepartmentURI(nextLine[4].trim());
			obj.setNetId(getLowercase(nextLine[5].trim()));
			obj.setProjectId(nextLine[16].trim());
			obj.setPersonRole(nextLine[12].trim());
			obj.setInvId(nextLine[15].trim());
			obj.setProposalId(nextLine[17].trim()); // not much useful
			obj.setGrantTitle(nextLine[18].trim());
			obj.setDepartmentId(nextLine[19].trim());
			obj.setDepartmentName(nextLine[20].trim());
			obj.setSponsorName(nextLine[21].trim());
			obj.setSponsorId(nextLine[22].trim());
			obj.setStartDate(nextLine[23].trim());
			obj.setEndDate(nextLine[24].trim());
			String amount = nextLine[25].trim();
			if(amount.isEmpty()){
				amount = "0";
			}
			obj.setGrantTotal(Double.parseDouble(amount));
			obj.setGrantType(nextLine[26].trim());
			obj.setSpLevel1(nextLine[29].trim());
			obj.setSpLevel2(nextLine[30].trim());
			obj.setSpLevel3(nextLine[31].trim());
			obj.setRollupDeptName(nextLine[32].trim());
			obj.setAwardStatus(nextLine[33].trim());
			data.add(obj);
		}
		buf.close();

		LOGGER.info("GRANTS: Grants TSV file size:"+ data.size());
		return data;
	}



	private String getLowercase(String trim) {
		return trim.toLowerCase();
	}

}