package edu.cornell.scholars.journalsubjectareamapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.workflow1.MainEntryPoint_WorkFlow1;

public class JournalToSubjectAreaMapEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(JournalToSubjectAreaMapEntryPoint.class.getName());

	//input file names
	private static String JOURNAL_INPUT_FILENAME = null;	
	private static String JOURNALID_MASTER_FILENAME = null;
	private static String subjectAreaFile = null;
	private static String WOS_DataFile = null;		
	private static String FOR_DataFile = null;	
	//output file names
	private static String Journal2SubjectAreaDataFile = null;
	private static String Journal2SubjectAreaRDFFile = null;

	private static String VIVO_NS = "http://vivoweb.org/ontology/core#";
	private static String SCHOLARS_IND = "http://scholars.cornell.edu/individual/";
	private static String SKOS_CONCEPT = "http://www.w3.org/2004/02/skos/core#Concept";

	private Map<String, String> forISSNMap = new HashMap<String, String>(); // <issn, subjectArea>
	private Map<String, String> forEISSNMap = new HashMap<String, String>();// <eissn,subjectArea>
	private Map<String, Set<String>> wosISSNMap =  new HashMap<String, Set<String>>(); //<issn, list of subjectArea>
	private Map<String, Set<String>> wosEISSNMap = new HashMap<String, Set<String>>();// <eissn,list of subjectArea>
	private Map<String, String> subjectAreaMap = new HashMap<String, String>(); // <label, uri>
	private Map<String, Journal> schlrs_journals_map = new HashMap<String, Journal>(); // <uri of the journal, journal object>
	private Set<Journal> merged_data = new HashSet<Journal>();

	public static void main(String[] args) {
		try {
			MainEntryPoint_WorkFlow1.init("resources/setup.properties");
			JournalToSubjectAreaMapEntryPoint jrnlep = new JournalToSubjectAreaMapEntryPoint();
			jrnlep.runProcess();
		} catch (NoSuchAlgorithmException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setLocalDirectories() {
		
		JOURNAL_INPUT_FILENAME = 	Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.JOURNAL_2_ISSN_EISSN_SUBJECTAREA_MAP_FILENAME;	
		subjectAreaFile =			 Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ALL_SUBJECTAREAS_FILENAME;
		
		WOS_DataFile = 				Configuration.SUPPL_FOLDER +"/"+ Configuration.WOS_JOURNAL_CLSFCN_FILENAME;		
		FOR_DataFile = 				Configuration.SUPPL_FOLDER +"/"+ Configuration.FOR_JOURNAL_CLSFCN_FILENAME;	
		JOURNALID_MASTER_FILENAME = Configuration.SUPPL_FOLDER +"/"+ Configuration.JOURNAL_MASTER_FILENAME;
		
		//output file names
		Journal2SubjectAreaDataFile = 	Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+Configuration.SUBJECTAREA_FOLDER+"/"+ Configuration.JOURNAL_2_SUBJECTAREA_CSV;
		Journal2SubjectAreaRDFFile = 	Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+Configuration.SUBJECTAREA_FOLDER+"/"+ Configuration.JOURNAL_2_SUBJECTAREA_NT;
	}

	public void runProcess() throws IOException, NoSuchAlgorithmException, InterruptedException {
		setLocalDirectories();

		Set<String> journalURIs = readMasterJournalFile(new File(JOURNALID_MASTER_FILENAME));

		// Read JOURNAL file 
		Set<String> newJournalURIs = readAndFilterSCHLJournalCSVFile(new File(JOURNAL_INPUT_FILENAME), journalURIs);
		if(newJournalURIs.size() == 0) {
			LOGGER.info("JOURNAL: 0 new journals found....returning");
			return;
		}
		
		// Read WOS file
		readWOSJournalCSVFile(new File(WOS_DataFile));

		// Read FOR file
		readFORJournalCSVFile(new File(FOR_DataFile));

		// Read SCHOLARS SUBJECT AREA file
		readSubjectAreasMap(new File(subjectAreaFile));
		LOGGER.info("JRNL2SA: subjectArea count "+subjectAreaMap.size());

		// Read the scholars journal data and fill in the subject areas section
		merged_data = fillSubjectAreaData();
		if(merged_data.size() > 0){
			saveJournalsCSV(merged_data, Journal2SubjectAreaDataFile);
			saveJournalsRDF(merged_data, Journal2SubjectAreaRDFFile);
			updateMasterFile(newJournalURIs, new File(JOURNALID_MASTER_FILENAME));
		}
	}

	private void updateMasterFile(Set<String> newJournalURIs, File masterFile) throws IOException {
		// Update the MASTER JOURNAL FILE.
		LOGGER.info("COLLAB1: updating the master journal id file....");
		int counter=0;
		PrintWriter pw = null;
		FileWriter fw = new FileWriter(masterFile, true);
		pw = new PrintWriter(fw);
		for(String uri:newJournalURIs){
			pw.print("\n\""+uri +"\",\""+Configuration.date+"\"");  //id, source
			counter++;
		}

		pw.close();
		LOGGER.info("JOURNAL: updating the master journal id file....completed");
		LOGGER.info("JOURNAL: "+counter+" new rows added in "+ masterFile.getName());
	}

	private Set<String> readMasterJournalFile(File file) throws IOException {
		LOGGER.info("COLLAB1: reading master journal file....");
		Set<String> set = new HashSet<String>();
		CSVReader reader;
		reader = new CSVReader(new FileReader(file),',','\"');
		String [] nextLine;	
		while ((nextLine = reader.readNext()) != null) {
			if(nextLine[0].isEmpty()) continue;
			set.add(nextLine[0]);
		}
		reader.close();
		LOGGER.info("JOURNAL: reading master journal file....completed");
		LOGGER.info("JOURNAL: master journal size:"+ set.size());
		return set;
	}

	/**
	 * Main method that fills in the subject 
	 * area data for any journal.
	 */
	private Set<Journal> fillSubjectAreaData() {
		int filledFORCount = 0;
		int filledWOSCount = 0;

		Set<Journal> data = new HashSet<Journal>();
		Collection<Journal> journals = schlrs_journals_map.values();

		for(Journal schlrs_journal: journals){
			String issn = schlrs_journal.getIssn();
			String eissn = schlrs_journal.getEissn();
			
			if(issn.equals("1468-0777") || eissn.equals("1471-5902")){
				//System.out.println("here i am ");
			}
			
			
			
			Journal obj = new Journal();
			obj.setUri(schlrs_journal.getUri());
			obj.setTitle(schlrs_journal.getTitle());
			obj.setIssn(issn);
			obj.setEissn(eissn);

			if(!issn.isEmpty() && forISSNMap.get(issn) != null){ //forISSNMap  option1
				String subjectArea = forISSNMap.get(issn);
				if(!subjectArea.trim().isEmpty()){
					obj.setForSubjectArea(subjectArea);
				}
				filledFORCount++;
			}else if(!eissn.isEmpty() && forEISSNMap.get(eissn) != null){ //forEISSNMap option2
				String subjectArea = forEISSNMap.get(eissn);
				if(!subjectArea.trim().isEmpty()){
					obj.setForSubjectArea(subjectArea);
				}
				filledFORCount++;
			}
			if(!issn.isEmpty() && wosISSNMap.get(issn) != null){ //wosISSNMap  option3
				Set<String> subjectAreas = wosISSNMap.get(issn);
				if(subjectAreas.size() >0){
					for(String subjectArea: subjectAreas){
						if (!subjectArea.isEmpty()){
							obj.addWOSSubjectAreas(subjectArea);
						}
					}	
				}
				filledWOSCount++;
			}else if(!eissn.isEmpty() && wosEISSNMap.get(eissn) != null){ //wosEISSNMap option4
				Set<String> subjectAreas = wosEISSNMap.get(eissn);
				if(subjectAreas != null && subjectAreas.size() >0){
					for(String subjectArea: subjectAreas){
						if (!subjectArea.isEmpty()){
							obj.addWOSSubjectAreas(subjectArea);
						}
					}	
				}
				filledWOSCount++;
			}	
			data.add(obj);
		}
		LOGGER.info("JRNL2SA: Filled FOR Journal count: "+ filledFORCount);
		LOGGER.info("JRNN2SA: Filled WOS Journal count: "+ filledWOSCount);
		LOGGER.info("JRNL2SA: MERGED JRNL count: "+ data.size());
		return data;
	}

	private Set<String> readAndFilterSCHLJournalCSVFile(File file, Set<String> journalURI) throws IOException {
		LOGGER.info("JRNL2SA: Reading Scholars Journals data....");
		Set<String> newIds = new HashSet<String>();
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(line.trim().length() == 0) continue; //header or empty line
			CSVReader reader = new CSVReader(new StringReader(line),'|');	
			//CSVReader reader = new CSVReader(new StringReader(line),',','\"');
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					String uri = tokens[0];
					if(journalURI.contains(uri)) continue;
					newIds.add(uri);
					Journal new_obj = new Journal();
					new_obj.setUri(uri);
					new_obj.setTitle(tokens[1]);
					new_obj.setIssn(tokens[2]);
					new_obj.setEissn(tokens[3]);
					schlrs_journals_map.put(uri, new_obj);
					lineCount++;
				}catch (ArrayIndexOutOfBoundsException exp) {
					LOGGER.warning(exp.getMessage());
					for (String s : tokens) {
						LOGGER.info("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
					continue;
				}
			}
			reader.close();
		}
		br.close();
		LOGGER.info("JRNL2SA: scholars journal count "+ schlrs_journals_map.size());
		return newIds;
	}

	private void readWOSJournalCSVFile(File file) throws IOException {
		LOGGER.info("JRNL2SA: Reading Web of Science Journals data....");
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;

		Set<String> issns = new HashSet<String>();
		Set<String> eissns = new HashSet<String>();
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount == 1 || line.trim().length() == 0) continue; //header
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					String issn = tokens[5];
					String eissn = tokens[6];
					String subjectArea = tokens[12];

					if(issns.contains(issn)){
						LOGGER.warning("WOS: "+issn+" issn already exists.");
					}
					if(eissns.contains(eissn)){
						LOGGER.warning("WOS: "+eissn+" eissn already exists.");
					}

					if(issn != null && wosISSNMap.get(issn) != null){
						Set<String> subjectAreas = wosISSNMap.get(issn);
						subjectAreas.add(subjectArea);
					}else if(issn != null){
						Set<String> subjectAreas = new HashSet<String>();
						subjectAreas.add(subjectArea);
						wosISSNMap.put(issn, subjectAreas);
					}
					if(eissn != null && wosEISSNMap.get(eissn) != null){
						Set<String> subjectAreas = wosEISSNMap.get(eissn);
						subjectAreas.add(subjectArea);
					}else if(eissn != null){
						Set<String> subjectAreas = new HashSet<String>();
						subjectAreas.add(subjectArea);
						wosEISSNMap.put(eissn, subjectAreas);
					}

					lineCount++;
				}catch (ArrayIndexOutOfBoundsException exp) {
					LOGGER.warning(exp.getMessage());
					for (String s : tokens) {
						LOGGER.info("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
					continue;
				}
			}
		}
		br.close();
		LOGGER.info("JRNL2SA: WOS line count:"+lineCount);
	}

	private void readFORJournalCSVFile(File file) throws IOException {	
		LOGGER.info("JRNL2SA: Reading FOR Journals data....");
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;

		Set<String> issns = new HashSet<String>();
		Set<String> eissns = new HashSet<String>();
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount == 1 || line.trim().length() == 0) continue; //header
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					String issn = tokens[5];
					String eissn = tokens[6];
					if(issns.contains(tokens[5].trim())){
						LOGGER.warning("FOR: "+tokens[5]+" issn already exists.");
					}else if(!tokens[5].trim().isEmpty()){
						issns.add(tokens[5].trim());
						forISSNMap.put(tokens[5].trim(), tokens[2].trim()); // <issn,subjectArea>
					}

					if(eissns.contains(tokens[6].trim())){
						LOGGER.warning("FOR: "+tokens[6]+" eissn already exists.");
					}else if(!tokens[6].trim().isEmpty()){
						eissns.add(tokens[6].trim());
						forEISSNMap.put(tokens[6].trim(), tokens[2].trim());  // <eissn,subjectArea>
					}
				}catch (ArrayIndexOutOfBoundsException exp) {
					LOGGER.warning(exp.getMessage());
					for (String s : tokens) {
						LOGGER.info("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
					continue;
				}
			}
		}
		br.close();
		LOGGER.info("JRNL2SA: FOR line count:"+lineCount);
	}

	private void readSubjectAreasMap(File file) throws IOException {
		LOGGER.info("JRNL2SA: Reading subject area input file..");
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount == 1 || line.trim().length() == 0) continue; //header
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),'|');	
			//CSVReader reader = new CSVReader(new StringReader(line),',','\"');
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					String label = tokens[0];
					String uri = tokens[1];
					if(!uri.startsWith("http://scholars.cornell.edu/individual/")) continue; // looking for only local URIs
					subjectAreaMap.put(label, uri);
					lineCount++;
				}catch (ArrayIndexOutOfBoundsException exp) {
					LOGGER.warning(exp.getMessage());
					for (String s : tokens) {
						LOGGER.info("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
					continue;
				}
			}
		}
		br.close();
		LOGGER.info("JRNL2SA: subject area file read line count: "+lineCount);
	}

	private void saveJournalsCSV(Set<Journal> data, String filePath) throws FileNotFoundException {
		PrintWriter printWriter;
		printWriter = new PrintWriter (filePath);	
		for(Journal obj: data){
			printWriter.println(obj.toString());
		}
		printWriter.close();
	}

	private void saveJournalsRDF(Set<Journal> merged_data2, String filePath) throws InterruptedException, FileNotFoundException, NoSuchAlgorithmException {
		HashSet<Integer> set = gen();
		Model rdfmodel = ModelFactory.createDefaultModel();	
		Property hasSubjectArea = rdfmodel.createProperty(VIVO_NS+"hasSubjectArea");
		Collection<String> subjectURIsColl = subjectAreaMap.values();
		Set<String> subjectURIs = new HashSet<String>(subjectURIsColl);
		Set<String> subjectLabelSet = subjectAreaMap.keySet();
		Set<String> subjectLabels = new HashSet<String>(subjectLabelSet);
		int jrnl_count = 0;
		Iterator<Integer> i = set.iterator();
		for(Journal d: merged_data2){	
			//				if(d.getUri().equals("http://scholars.cornell.edu/individual/jrnl-0001219")){
			//					System.out.println(d.toString());
			//				}
			String sa = null;
			String uri= null;
			if(d.getFORSubjectArea() != null && !d.getFORSubjectArea().trim().isEmpty()){
				Resource subA = null;
				sa = d.getFORSubjectArea().trim();
				if(subjectLabels.contains(sa)){
					uri = subjectAreaMap.get(sa);
					subA = rdfmodel.createResource(uri);
					subA.addProperty(RDF.type, rdfmodel.createResource(SKOS_CONCEPT));
					subA.addProperty(RDFS.label, sa);
				}else{
					int id = i.next();
					uri = SCHOLARS_IND+"SA-"+id;
					while(subjectURIs.contains(uri)){
						id = i.next();
						uri= SCHOLARS_IND+"SA-"+id;
					}
					subA = rdfmodel.createResource(uri);
					subA.addProperty(RDF.type, rdfmodel.createResource(SKOS_CONCEPT));
					subA.addProperty(RDFS.label, sa);

					subjectURIs.add(uri);
					subjectLabels.add(sa);
					subjectAreaMap.put(sa, uri);
				}
				String journalURI = d.getUri();
				Resource jrnl = rdfmodel.createResource(journalURI);
				if(subA != null){
					jrnl.addProperty(hasSubjectArea, subA);
					jrnl_count++;
				}	
			}else if(d.getWOSSubjectAreas() != null && d.getWOSSubjectAreas().size()>0) {
				Set<String> sas = d.getWOSSubjectAreas();
				String journalURI = d.getUri();
				Resource jrnl = rdfmodel.createResource(journalURI);
				int localcount = -1;
				for(String sarea: sas){
					sa = sarea.trim();
					if(sa.isEmpty()) continue;
					Resource subA = null;
					if(subjectLabels.contains(sa)){
						uri = subjectAreaMap.get(sa);
						subA = rdfmodel.createResource(uri);
						subA.addProperty(RDF.type, rdfmodel.createResource(SKOS_CONCEPT));
						subA.addProperty(RDFS.label, sa);
					}else{
						int id = i.next();
						uri = SCHOLARS_IND+"SA-"+id;
						while(subjectURIs.contains(uri)){
							id = i.next();
							uri= SCHOLARS_IND+"SA-"+id;
						}
						subA = rdfmodel.createResource(uri);
						subA.addProperty(RDF.type, rdfmodel.createResource(SKOS_CONCEPT));
						subA.addProperty(RDFS.label, sa);

						subjectURIs.add(uri);
						subjectLabels.add(sa);
						subjectAreaMap.put(sa, uri);
					}
					if(subA != null){
						jrnl.addProperty(hasSubjectArea, subA);	
						if(localcount++ < 0){
							jrnl_count++;
						}
					}
				}
			}
		}
		Thread.sleep(100);
		saveRDFModel(rdfmodel, filePath);
		LOGGER.info("JRNL2SA: Journal to Subject Area Graph Count (in RDF): "+jrnl_count);
	}

	private HashSet<Integer> gen() throws NoSuchAlgorithmException {
		HashSet<Integer> set = new HashSet<>();
		SecureRandom randomGenerator = SecureRandom.getInstance("SHA1PRNG");         
		while(set.size() < merged_data.size()){
			int s = randomGenerator.nextInt(99999);
			if(s > 9999){
				set.add(s);
			}
		}
		return set;
	}

	private void saveRDFModel(Model rdfModel, String filePath) throws FileNotFoundException {
		PrintWriter printer = null;
		printer = new PrintWriter(filePath);
		rdfModel.write(printer, "N-Triples");
		printer.flush();
		printer.close();
	}


}
