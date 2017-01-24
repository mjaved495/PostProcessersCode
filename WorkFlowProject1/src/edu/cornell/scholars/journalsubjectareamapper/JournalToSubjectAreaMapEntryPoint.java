package edu.cornell.scholars.journalsubjectareamapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;

public class JournalToSubjectAreaMapEntryPoint {

	//input file names
	private final static String JOURNAL_INPUT_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
			Configuration.JOURNAL_2_ISSN_EISSN_SUBJECTAREA_MAP_FILENAME;	
	private final static String subjectAreaFile = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
			Configuration.ALL_SUBJECTAREAS_FILENAME;
	private final static String WOS_DataFile = Configuration.SUPPL_FOLDER +"/"+ Configuration.WOS_JOURNAL_CLSFCN_FILENAME;		
	private final static String FOR_DataFile = Configuration.SUPPL_FOLDER +"/"+ Configuration.FOR_JOURNAL_CLSFCN_FILENAME;	
	//output file names
	private final static String Journal2SubjectAreaDataFile = Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
			+"/"+Configuration.SUBJECTAREA_FOLDER+"/"+ Configuration.JOURNAL_2_SUBJECTAREA_CSV;
	private final static String Journal2SubjectAreaRDFFile = Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
			+"/"+Configuration.SUBJECTAREA_FOLDER+"/"+ Configuration.JOURNAL_2_SUBJECTAREA_NT;

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
		JournalToSubjectAreaMapEntryPoint jrnlep = new JournalToSubjectAreaMapEntryPoint();
		jrnlep.runProcess();
	}

	public void runProcess() {

		// Read JOURNAL file 
		readSCHLJournalCSVFile(new File(JOURNAL_INPUT_FILENAME));

		// Read WOS file
		readWOSJournalCSVFile(new File(WOS_DataFile));

		// Read FOR file
		readFORJournalCSVFile(new File(FOR_DataFile));

		// Read SCHOLARS SUBJECT AREA file
		readSubjectAreasMap(new File(subjectAreaFile));
		System.out.println("SubjectArea count: "+subjectAreaMap.size());

		// Read the scholars journal data and fill in the subject areas section
		merged_data = fillSubjectAreaData();

		saveJournalsCSV(merged_data, Journal2SubjectAreaDataFile);
		saveJournalsRDF(merged_data, Journal2SubjectAreaRDFFile);

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
			Journal obj = new Journal();
			obj.setUri(schlrs_journal.getUri());
			obj.setTitle(schlrs_journal.getTitle());
			obj.setIssn(eissn);
			obj.setEissn(eissn);

			if(forISSNMap.get(issn) != null){ //forISSNMap  option1
				String subjectArea = forISSNMap.get(issn);
				if(!subjectArea.trim().isEmpty()){
					obj.setForSubjectArea(subjectArea);
				}
				filledFORCount++;
			}else if(forEISSNMap.get(eissn) != null){ //forEISSNMap option2
				String subjectArea = forEISSNMap.get(eissn);
				if(!subjectArea.trim().isEmpty()){
					obj.setForSubjectArea(subjectArea);
				}
				filledFORCount++;
			}
			if(wosISSNMap.get(issn) != null){ //wosISSNMap  option3
				Set<String> subjectAreas = wosISSNMap.get(issn);
				if(subjectAreas.size() >0){
					for(String subjectArea: subjectAreas){
						if (!subjectArea.isEmpty()){
							obj.addWOSSubjectAreas(subjectArea);
						}
					}	
				}
				filledWOSCount++;
			}else if(wosEISSNMap.get(eissn) != null){ //wosEISSNMap option4
				Set<String> subjectAreas = wosEISSNMap.get(issn);
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
		System.out.println("Filled FOR Journal count: "+ filledFORCount);
		System.out.println("Filled WOS Journal count: "+ filledWOSCount);

		System.out.println("MERGED JRNL count: "+ data.size());
		return data;
	}

	private void readSCHLJournalCSVFile(File file) {
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				lineCount++;
				if(line.trim().length() == 0) continue; //header or empty line
				CSVReader reader = new CSVReader(new StringReader(line),'|');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					try {
						String uri = tokens[0];
						if(schlrs_journals_map.get(uri) == null){
							Journal new_obj = new Journal();
							new_obj.setUri(uri);
							new_obj.setTitle(tokens[1]);
							new_obj.setIssn(tokens[2]);
							new_obj.setEissn(tokens[3]);
							new_obj.addWOSSubjectAreas(tokens[4]);
							schlrs_journals_map.put(uri, new_obj);
						}else{
							Journal exst_obj = schlrs_journals_map.get(uri);
							exst_obj.addWOSSubjectAreas(tokens[4]);
						}
						lineCount++;
					}catch (ArrayIndexOutOfBoundsException exp) {
						for (String s : tokens) {
							System.out.println("Exception: "+ lineCount+" :"+ s);
						}
						System.out.println();
						continue;
					}
				}
				reader.close();
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
		System.out.println("SCHLRS JRNL count: "+ schlrs_journals_map.size());
	}

	private void readWOSJournalCSVFile(File file) {
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;

		Set<String> issns = new HashSet<String>();
		Set<String> eissns = new HashSet<String>();

		try {
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
							System.err.println("WOS: "+issn+" issn already exists.");
						}
						if(eissns.contains(issn)){
							System.err.println("WOS: "+eissn+" eissn already exists.");
						}

						if(wosISSNMap.get(issn) != null){
							Set<String> subjectAreas = wosISSNMap.get(issn);
							subjectAreas.add(subjectArea);
						}else{
							Set<String> subjectAreas = new HashSet<String>();
							subjectAreas.add(subjectArea);
							wosISSNMap.put(issn, subjectAreas);
						}
						if(wosEISSNMap.get(eissn) != null){
							Set<String> subjectAreas = wosEISSNMap.get(eissn);
							subjectAreas.add(subjectArea);
						}else{
							Set<String> subjectAreas = new HashSet<String>();
							subjectAreas.add(subjectArea);
							wosEISSNMap.put(eissn, subjectAreas);
						}

						lineCount++;
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
		System.out.println("WOS line count:"+lineCount);
	}

	private void readFORJournalCSVFile(File file) {	
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;

		Set<String> issns = new HashSet<String>();
		Set<String> eissns = new HashSet<String>();

		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				lineCount++;
				if(lineCount == 1 || line.trim().length() == 0) continue; //header
				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					try {
						//System.out.println(tokens[4]);

						if(issns.contains(tokens[5].trim())){
							System.err.println("FOR: "+tokens[5]+" issn already exists.");
						}else if(!tokens[5].trim().isEmpty()){
							issns.add(tokens[5].trim());
							forISSNMap.put(tokens[5].trim(), tokens[2].trim()); // <issn,subjectArea>
						}

						if(eissns.contains(tokens[6].trim())){
							System.err.println("FOR: "+tokens[6]+" eissn already exists.");
						}else if(!tokens[6].trim().isEmpty()){
							eissns.add(tokens[6].trim());
							forEISSNMap.put(tokens[6].trim(), tokens[2].trim());  // <eissn,subjectArea>
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
		System.out.println("FOR line count:"+lineCount);
	}

	private void readSubjectAreasMap(File file) {
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				lineCount++;
				if(lineCount == 1 || line.trim().length() == 0) continue; //header
				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),'|');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					try {
						String label = tokens[0];
						String uri = tokens[1];
						if(!uri.startsWith("http://scholars.cornell.edu/individual/")) continue; // looking for only local URIs
						subjectAreaMap.put(label, uri);
						lineCount++;
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
		System.out.println("SubjectArea line count:"+lineCount);
	}

	private void saveJournalsCSV(Set<Journal> data, String filePath) {
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (filePath);	
			for(Journal obj: data){
				printWriter.println(obj.toString());
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	private void saveJournalsRDF(Set<Journal> merged_data2, String filePath) {
		HashSet<Integer> set = gen();
		Model rdfmodel = ModelFactory.createDefaultModel();	
		Property hasSubjectArea = rdfmodel.createProperty(VIVO_NS+"hasSubjectArea");
		Collection<String> subjectURIsColl = subjectAreaMap.values();
		Set<String> subjectURIs = new HashSet<String>(subjectURIsColl);
		Set<String> subjectLabelSet = subjectAreaMap.keySet();
		Set<String> subjectLabels = new HashSet<String>(subjectLabelSet);
		int jrnl_count = 0;
		try {
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
					}else{
						int id = i.next();
						uri = SCHOLARS_IND+"SA-"+id;
						while(subjectURIs.contains(uri)){
							id = i.next();
							uri= SCHOLARS_IND+"SA-"+id;
						}
						subA = rdfmodel.createResource(uri);
						subA.addProperty(RDF.type, SKOS_CONCEPT);
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
						}else{
							int id = i.next();
							uri = SCHOLARS_IND+"SA-"+id;
							while(subjectURIs.contains(uri)){
								id = i.next();
								uri= SCHOLARS_IND+"SA-"+id;
							}
							subA = rdfmodel.createResource(uri);
							subA.addProperty(RDF.type, SKOS_CONCEPT);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveRDFModel(rdfmodel, filePath);
		System.out.println("Journal to Subject Area Graph Count (in RDF): "+jrnl_count);
	}

	public HashSet<Integer> gen() {
		HashSet<Integer> set = new HashSet<>();
		try {
			SecureRandom randomGenerator = SecureRandom.getInstance("SHA1PRNG");         
			while(set.size() < merged_data.size()){
				int s = randomGenerator.nextInt(99999);
				if(s > 9999){
					set.add(s);
				}
			}
			return set;
		} catch (NoSuchAlgorithmException nsae) {
			// Forward to handler
		}
		return null;
	}
	
	private void saveRDFModel(Model rdfModel, String filePath) {
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(filePath);
			rdfModel.write(printer, "N-Triples");
			printer.flush();
			printer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
}