package edu.cornell.scholars.subjectareagraphbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import com.opencsv.CSVReader;


public class SubjectAreaGraphBuilder {

	private static String SCHOLARS_IND = "http://scholars.cornell.edu/individual/";
	private static String SKOS_CONCEPT = "http://www.w3.org/2004/02/skos/core#Concept";
	
	private static final Logger LOGGER = Logger.getLogger(SubjectAreaGraphBuilder.class.getName());

	private Set<String> subjectAreas = new HashSet<String>();
	private static Set<String> stopwords = new HashSet<String>();
	
	public static void main (String args[]){
		
		SubjectAreaGraphBuilder obj = new SubjectAreaGraphBuilder();
		try {
			
			stopwords.add("Science & Technology-Other Topics");
			
			// Read WOS file
			obj.readWOSJournalCSVFile(new File("resources/JournalClassification-WOS.csv"));
			// Read FOR file
			obj.readFORJournalCSVFile(new File("resources/JournalClassification-FOR.csv"));
			// create TRIPLES GRAPH
			obj.createGraph("resources/subjectAreasMaster.nt");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private void createGraph(String output) throws NoSuchAlgorithmException, FileNotFoundException {
		Set<Integer> set = gen(subjectAreas.size());
		int sa_count = 0;
		Model rdfmodel = ModelFactory.createDefaultModel();	
	
		Iterator<Integer> i = set.iterator();
		for(String sa: subjectAreas){
			int id = i.next();
			String uri = SCHOLARS_IND+"SA-"+id;
			Resource subA = rdfmodel.createResource(uri);
			subA.addProperty(RDF.type, rdfmodel.createResource(SKOS_CONCEPT));
			subA.addProperty(RDFS.label, sa);
			sa_count++;
		}
		saveRDFModel(rdfmodel, output);
		LOGGER.info("SA: subject area count...."+ sa_count);
	}

	private void saveRDFModel(Model rdfModel, String filePath) throws FileNotFoundException {
		PrintWriter printer = null;
		printer = new PrintWriter(filePath);
		rdfModel.write(printer, "N-Triples");
		printer.flush();
		printer.close();
	}

	
	private HashSet<Integer> gen(int count) throws NoSuchAlgorithmException {
		HashSet<Integer> set = new HashSet<>();
		SecureRandom randomGenerator = SecureRandom.getInstance("SHA1PRNG");         
		while(set.size() < count){
			int s = randomGenerator.nextInt(99999);
			if(s > 9999){
				set.add(s);
			}
		}
		return set;
	}
	
	private void readWOSJournalCSVFile(File file) throws IOException {
		LOGGER.info("SA: Reading Web of Science Journals data....");
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount == 1 || line.trim().length() == 0) continue; //header
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					String subjectArea = tokens[12];
					if(subjectArea.contains("Other Topics")){
						System.out.println("test now"+ subjectArea);
					}
					
					if(stopwords.contains(subjectArea)){
						LOGGER.info("found stop word"+ subjectArea);
					}else{
						subjectAreas.add(subjectArea);
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
		LOGGER.info("SA: WOS line count:"+lineCount);
	}

	private void readFORJournalCSVFile(File file) throws IOException {	
		LOGGER.info("SA: Reading FOR Journals data....");
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount == 1 || line.trim().length() == 0) continue; //header
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					subjectAreas.add(tokens[2].trim());
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

	
}
