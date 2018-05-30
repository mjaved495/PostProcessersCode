package edu.cornell.scholars.scopusauthorkeywords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.workflow1.MainEntryPoint_WorkFlow1;


public class ScopusAuthorKeywordsEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(ScopusAuthorKeywordsEntryPoint.class.getName());

	public static final String VIVO_NS = "http://vivoweb.org/ontology/core#";
	public static String OUTPUT_FILE_NT = null;
	public static String SCOPUS_FILE_FOLDER = null;
	public static String EXISTING_KEYWORDS = null;

	private Map<String, Set<String>> existingKeywords = null;
	private Map<String, String> urlToScopusId = null;

	public static void main(String[] args) {
		try {
			MainEntryPoint_WorkFlow1.init("resources/setup.properties");
			ScopusAuthorKeywordsEntryPoint obj = new ScopusAuthorKeywordsEntryPoint();
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runProcess() throws IOException {
		setLocalDirectories();

		try {
			readInputFileAndGenerateMaps(EXISTING_KEYWORDS);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			LOGGER.warning("**** ERROR.... -- returning");
			return;
		} catch (SAXException e) {
			e.printStackTrace();
			LOGGER.warning("**** ERROR.... -- returning");
			return;
		}

		FreeTextKeywordReader extractor = new FreeTextKeywordReader();
		Map<String, Set<String>> scholarsURLKeywordMap = extractor.extractNewKeywords(existingKeywords, urlToScopusId, SCOPUS_FILE_FOLDER);

		if(scholarsURLKeywordMap == null || scholarsURLKeywordMap.size() == 0){
			LOGGER.info(scholarsURLKeywordMap.size()+" author keywords found.....returning.");
			return;
		}

		LOGGER.info("**** Generating Triples....");
		generateTriples(OUTPUT_FILE_NT, scholarsURLKeywordMap);
	}

	private void readInputFileAndGenerateMaps(String xmlFile) throws IOException, ParserConfigurationException, SAXException {
		existingKeywords = new HashMap<String, Set<String>>();
		urlToScopusId = new HashMap<String, String>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		NodeList entryList = doc.getElementsByTagName("result");
		for(int index=0; index< entryList.getLength(); index++){
			Node node = entryList.item(index);
			//System.out.println(node.getNodeName());
			Element eElement = (Element) node;
			NodeList bindingNodes = eElement.getElementsByTagName("binding");
			
			String url = null, scopusId = null, keywords = null;
			for(int i = 0; i< bindingNodes.getLength(); i++){
				Node n = bindingNodes.item(i);
				Element bindElement = (Element) n;
				String att = bindElement.getAttribute("name");
				switch(att){
				case "article": 
					url = bindElement.getElementsByTagName("uri").item(0).getTextContent();
					break;
				case "scopusId": 
					scopusId = bindElement.getElementsByTagName("literal").item(0).getTextContent();
					scopusId = scopusId.substring(scopusId.lastIndexOf("-")+1);
					break;
				case "keywords": 
					keywords = bindElement.getElementsByTagName("literal").item(0).getTextContent();
					break;	
				}
			}
			
			Set<String> kwords = new HashSet<String>();
			if(keywords != null){
				String kw[] = keywords.split(";;");
				for(String k : kw){
					if(k.contains("&#")){
						LOGGER.warning(url+" - Poorly formatted keyword: " + k);
						continue;
					}else if(!k.trim().isEmpty()){
						kwords.add(k.trim().toUpperCase());
					}
				}
			}
			
			urlToScopusId.put(scopusId, url);
			existingKeywords.put(scopusId, kwords);
			
		}// end of reading entries.
		LOGGER.info(entryList.getLength() + " rows read in the input file.");
		LOGGER.info(urlToScopusId.size() + " URL to Scopus ID Map size.");
	}

	private void readInputFileAndGenerateMaps2(String inputFilePath) throws IOException {

		existingKeywords = new HashMap<String, Set<String>>();
		urlToScopusId = new HashMap<String, String>();

		File inputFile = new File(inputFilePath);
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(inputFile));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(lineCount == 819){
				LOGGER.info(lineCount + " rows not correctly formatted.");
			}
			
			//remove all quotes
			line = line.replaceAll("\"", "");
			if(line.trim().length() == 0 || lineCount == 1) continue;  // header or empty
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),',', '"');	
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					if(tokens.length < 3){
						LOGGER.info(lineCount + " rows not correctly formatted.");
					}
					String url = tokens[0];
					String scopusId = tokens[1];
					scopusId = scopusId.substring(scopusId.lastIndexOf("-")+1);
					String keywords = tokens[2];
					Set<String> kwords = new HashSet<String>();
					String kw[] = keywords.split(";;");
					for(String k : kw){
						if(!k.trim().isEmpty()){
							kwords.add(k.trim().toUpperCase());
						}
						
					}

					urlToScopusId.put(scopusId, url);
					existingKeywords.put(scopusId, kwords);

				}catch (ArrayIndexOutOfBoundsException exp) {
					for (String s : tokens) {
						LOGGER.warning("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
					LOGGER.warning("\n");
					continue;
				}
			}
		}
		LOGGER.info(lineCount + " rows read in the input file.");
		LOGGER.info(urlToScopusId.size() + " URL to Scopus ID Map size.");
		br.close();
	}

	private void generateTriples(String outputfilename, Map<String, Set<String>> scholarsURLKeywordMap) {
		Model model = ModelFactory.createDefaultModel();
		Property freetextKeyword = model.createProperty(VIVO_NS+"freetextKeyword");

		Set<String> urls = scholarsURLKeywordMap.keySet();
		for(String url: urls){
			Resource articleURI = model.createResource(url);
			Set<String> keywords = scholarsURLKeywordMap.get(url);
			for(String keyword: keywords){
				articleURI.addProperty(freetextKeyword, keyword);
			}
		}

		try {
			saveRDFGraph(model, outputfilename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void saveRDFGraph(Model model, String output) throws FileNotFoundException {
		PrintWriter printer = null;
		printer = new PrintWriter(output);
		model.write(printer, "N-Triples");
		printer.close();
	}

	private void setLocalDirectories() {
		SCOPUS_FILE_FOLDER = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.SCOPUS_FOLDER;

		EXISTING_KEYWORDS = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.SCOPUS_ARTICLEID_2_FREETEXTKEYWORDS;

		OUTPUT_FILE_NT = Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+ Configuration.SCOPUS_FOLDER +"/"+ Configuration.SCOPUS_FREETEXTKEYWORDS_OUTPUT_FILE;			
	}
}
