package edu.cornell.scholars.keywordminer.grants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import edu.cornell.scholars.keywordminer.article.Mesh;
import edu.cornell.scholars.workflow1.MainEntryPoint_WorkFlow1;

public class GrantKeywordMinerEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(GrantKeywordMinerEntryPoint.class.getName());

	//input file names
	private static String GRANT_FILENAME = null;
	private static String ALL_KW_FILENAME = null;
	private static String ALL_MESHTERM_FILENAME = null;
	private static String KWMINER_GRANTID_MASTER_FILENAME = null;
	
	//output file names
	private static String GRANT_MAP_CSVDATA_FILEPATH = null;
	private static String GRANT_MAP_NTDATA_FILEPATH =  null;

	private static int count = 0;
	private static int grantcount = 0;
	private static int grantLevelMinedWordCount = 0;

	private Set<String> allkeywords = null;
	private Set<Mesh> allMesh = null;
	private List<GrantEntries> grant_rows = null;
	private Set<String> matchWords = new HashSet<String>();

	private Map<String, Set<String>> grantMeshMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> grantKWMap = new HashMap<String, Set<String>>();

	public static void main(String[] args) {
		try {
			MainEntryPoint_WorkFlow1.init("resources/setup.properties");
			GrantKeywordMinerEntryPoint obj = new GrantKeywordMinerEntryPoint();
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private void setLocalDirectories() {
		GRANT_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.GRANT_2_TITLE_ABSTRACT_MAP_FILENAME;
		ALL_KW_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ALL_KEYWORDS_FILENAME;
		ALL_MESHTERM_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ALL_MESHTERMS_FILENAME;
		KWMINER_GRANTID_MASTER_FILENAME = Configuration.SUPPL_FOLDER +"/"+ 
				Configuration.GRANTID_MASTER_KEYWORDMINER_FILENAME;	
		//output file names
		GRANT_MAP_CSVDATA_FILEPATH = Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+ Configuration.INFERRED_KEYWORDS_FOLDER +"/"+ Configuration.GRANTS_INF_KEYWORDS_CSV;
		GRANT_MAP_NTDATA_FILEPATH =  Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+ Configuration.INFERRED_KEYWORDS_FOLDER +"/"+ Configuration.GRANTS_INF_KEYWORDS_NT;
		
	}

	public void runProcess() throws IOException, ParserConfigurationException, SAXException {
		setLocalDirectories();
		grant_rows = readGrantMapFile(new File(GRANT_FILENAME));
		Set<String> grantURIs = readMasterGrantFile(new File(KWMINER_GRANTID_MASTER_FILENAME));
		List<GrantEntries> newGrant_rows = filterNewGrants(grant_rows, grantURIs);
		if(newGrant_rows.size() == 0){
			LOGGER.info(newGrant_rows.size()+" new grants found.....returning.");
			return;
		}
		
		allkeywords = getLines(new File(ALL_KW_FILENAME));
		allMesh = getMeshLines(new File(ALL_MESHTERM_FILENAME));
		
		Map<String, GrantEntriesData> grantDataMap =  createGrantMapOfEntries(grant_rows);
		LOGGER.info(grantDataMap.size()+" rows of grants data map.");

		compareAndProcess(grantDataMap);
		saveGrantMapDataInAFile(grantDataMap, GRANT_MAP_CSVDATA_FILEPATH);
		saveDataInARDF(grantDataMap, GRANT_MAP_NTDATA_FILEPATH);
		
		updateMasterFile(newGrant_rows, new File(KWMINER_GRANTID_MASTER_FILENAME));
	}
	
	private void updateMasterFile(List<GrantEntries> newGrant_rows, File masterFile) throws IOException {
		// Update the MASTER KW MINDER FILE.
		LOGGER.info("KW MINDER: updating the master article id file....");
		int counter=0;
		PrintWriter pw = null;
		FileWriter fw = new FileWriter(masterFile, true);
		pw = new PrintWriter(fw);
		for(GrantEntries ge:newGrant_rows){
			pw.print("\n\""+ge.getGrantURI() +"\",\""+Configuration.date+"\"");  //id, source
			counter++;
		}
		fw.close();
		pw.close();
		LOGGER.info("KW MINDER: updating the master grant id file....completed");
		LOGGER.info("KW MINDER: "+counter+" new rows added in "+ masterFile.getName());
	}
	
	private List<GrantEntries> filterNewGrants(List<GrantEntries> grant_rows2, Set<String> grantURIs) {
		List<GrantEntries>  newGrants = new ArrayList<GrantEntries>();
		for(GrantEntries ge: grant_rows2){
			String uri = ge.getGrantURI();
			if(!grantURIs.contains(uri)){
				newGrants.add(ge);
			}
		}
		LOGGER.info("KW MINDER: New KW Miner Article size:"+ newGrants.size());
		return newGrants;
	}

	private Set<String> readMasterGrantFile(File file) throws IOException {
		LOGGER.info("KW MINDER: reading master KW Grant file....");
		Set<String> set = new HashSet<String>();
		CSVReader reader;
		reader = new CSVReader(new FileReader(file),',','\"');
		String [] nextLine;	
		while ((nextLine = reader.readNext()) != null) {
			if(nextLine[0].isEmpty()) continue;
			set.add(nextLine[0]);
		}
		reader.close();
		LOGGER.info("KW MINDER: reading master kw miner grant file....completed");
		LOGGER.info("KW MINDER: master kw miner grant size:"+ set.size());
		return set;
	}

	private void saveDataInARDF(Map<String, GrantEntriesData> grantDataMap, String filePath) throws FileNotFoundException {
		String VIVOC_NS = "http://scholars.cornell.edu/ontology/vivoc.owl#";
		Model rdfmodel = ModelFactory.createDefaultModel();	
		Property inferredKeyword = rdfmodel.createProperty(VIVOC_NS+"inferredKeyword");
		Set<String> grantURIs = grantDataMap.keySet();

		for(Iterator<String> i =  grantURIs.iterator(); i.hasNext();){
			String grantURI = i.next();
			GrantEntriesData data = grantDataMap.get(grantURI);
			Set<String> keywords = data.getKeywords();
			Set<String> mesh = data.getMeshTerms();

			Set<String> uniqKeyword = new HashSet<String>();

			if(keywords != null && !keywords.isEmpty()){
				Resource grant = rdfmodel.createResource(grantURI);
				for(String kw: keywords){
					if(!uniqKeyword.contains(kw.toLowerCase())){
						grant.addProperty(inferredKeyword, kw);
						uniqKeyword.add(kw.toLowerCase());
					}
				}
			}
			if(mesh != null && !mesh.isEmpty()){
				Resource article = rdfmodel.createResource(grantURI);
				for(String msh: mesh){
					if(!uniqKeyword.contains(msh.toLowerCase())){
						article.addProperty(inferredKeyword, msh);
						uniqKeyword.add(msh.toLowerCase());
					}
				}
			}		
		}

		PrintWriter printer = null;
		printer = new PrintWriter(filePath);
		rdfmodel.write(printer, "N-Triples");
		printer.flush();
		printer.close();
	}

	private void compareAndProcess(Map<String, GrantEntriesData> grantDataMap) {	
		Set<String> grant_key = grantDataMap.keySet();
		LOGGER.info("processing "+ grant_key.size() +" grant rows");
		for(Iterator<String> i = grant_key.iterator(); i.hasNext();){
			String grantURI = i.next();
			GrantEntriesData grantEntry = grantDataMap.get(grantURI);
			Set<String> distnctTermsFoundForGrant = new HashSet<String>();
			grantLevelMinedWordCount = 0;
			Set<String> words = grantEntry.getWords();
			// calling this method for each subject area of an article.
			boolean matchFound = process(grantEntry, words, allkeywords, allMesh,
					distnctTermsFoundForGrant);
			if(matchFound){
				grantcount++;
			}
			grantEntry.setMinedKeywordCount(grantLevelMinedWordCount);
		}
		LOGGER.info("Inferred Keywords: "+count+" keywords are inferred.");
		LOGGER.info("Inferred Keywords: "+grantcount+" grant matches");

	}

	private boolean process(GrantEntriesData grantEntry, Set<String> titleStrings, 
			Set<String> keywords, Set<Mesh> pubmeds, Set<String> distinctTermsFoundForGrant) {
		String uri = grantEntry.getGrantURI();
		//		if(uri.equals("http://scholars.cornell.edu/individual/UR-6864")){
		//			//System.out.println("uu");
		//		}
		boolean matchFound=false;
		Set<String> UCKeywords = new HashSet<String>();
		for(String keyword : keywords){
			UCKeywords.add(keyword.toUpperCase());
		}
		for(String titleString : titleStrings){
			if(UCKeywords.contains(titleString.toUpperCase())){
				//System.out.println(titleString+" found in the keywords list.");	
				Set<String> existingKW = grantKWMap.get(uri);
				Set<String> existingMesh = grantMeshMap.get(uri);	
				// Add inferred keyword only if it does not currently exists in Keyword or MeSH terms of this Article.
				if((existingKW == null || !existingKW.contains(titleString.toUpperCase()))  && 
						(existingMesh == null || !existingMesh.contains(titleString.toUpperCase()))){
					matchWords.add(titleString);
					grantEntry.addKeywords(titleString);
					count++;
					if(!distinctTermsFoundForGrant.contains(titleString)){
						grantLevelMinedWordCount++;
						distinctTermsFoundForGrant.add(titleString);
					}
					matchFound=true;
				}	
			}
		}

		Set<String> UCMeshTerms = new HashSet<String>();
		for(Mesh mesh : pubmeds){
			UCMeshTerms.add(mesh.getMeshLabel().toUpperCase());
		}
		for(String titleString : titleStrings){
			if(UCMeshTerms.contains(titleString.toUpperCase())){
				//System.out.println(titleString+" found in the keywords list.");
				Set<String> existingKW = grantKWMap.get(uri);
				Set<String> existingMesh = grantMeshMap.get(uri);	
				// Add inferred keyword only if it does not currently exists in Keyword or MeSH terms of this Grant.
				if((existingKW == null || !existingKW.contains(titleString.toUpperCase()))  && 
						(existingMesh == null || !existingMesh.contains(titleString.toUpperCase()))){
					matchWords.add(titleString);
					grantEntry.addMeshTerms(titleString);
					count++;
					if(!distinctTermsFoundForGrant.contains(titleString)){
						grantLevelMinedWordCount++;
						distinctTermsFoundForGrant.add(titleString);
					}
					matchFound=true;
				}
			}
		}
		return matchFound;
	}

	private static void saveGrantMapDataInAFile(Map<String, GrantEntriesData> grantDataMap,
			String grantMapDataFilepath) throws FileNotFoundException {
		PrintWriter printWriter;
		printWriter = new PrintWriter (grantMapDataFilepath);	
		Set<String> keySet = grantDataMap.keySet();
		for(String key: keySet){
			GrantEntriesData obj = grantDataMap.get(key);
			printWriter.println(obj.toString());
		}
		printWriter.close();
	}

	private static Map<String, GrantEntriesData> createGrantMapOfEntries(List<GrantEntries> grant_rows) {
		Map<String, GrantEntriesData> map = new HashMap<String, GrantEntriesData>();
		for(int index=0; index<grant_rows.size();index++){
			GrantEntries obj = grant_rows.get(index);
			String grantURI = obj.getGrantURI();
			if(map.get(grantURI) == null){
				GrantEntriesData data = new GrantEntriesData();
				data.setGrantURI(obj.getGrantURI());
				data.setGrantTitle(obj.getGrantTitle());
				data.setGrantAbstract(obj.getGrantAbstract());
				String title = obj.getGrantTitle();
				if(obj.getGrantAbstract() != null){
					Set<String> abstractWords = getWords(obj.getGrantAbstract());
					for(String word: abstractWords){
						data.addWords(word); 
					}
				}
				Set<String> words = getWords(title);
				for(String word: words){
					data.addWords(word); 
				}
				map.put(grantURI, data);
			}
		}
		return map;
	}

	private static Set<String> getWords(String title) {
		Set<String> result = new HashSet<String>();
		String words[] = title.split(" ");
		for(String word: words){
			result.add(word);
		}
		for(int i=1; i < words.length; i++){
			result.add(words[i-1]+" "+words[i]);
		}
		return result;
	}

	private List<GrantEntries> readGrantMapFile(File xmlFile) throws IOException, SAXException, ParserConfigurationException {
		List<GrantEntries> rows = new ArrayList<GrantEntries>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		NodeList entryList = doc.getElementsByTagName("result");
		for(int index=0; index< entryList.getLength(); index++){
			GrantEntries obj = new GrantEntries();
			Node node = entryList.item(index);
			//System.out.println(node.getNodeName());
			Element eElement = (Element) node;
			NodeList bindingNodes = eElement.getElementsByTagName("binding");
			for(int i=0; i< bindingNodes.getLength(); i++){
				Node n = bindingNodes.item(i);
				Element bindElement = (Element) n;
				String att = bindElement.getAttribute("name");
				switch(att){
				case "grant": 
					obj.setGrantURI(bindElement.getElementsByTagName("uri").item(0).getTextContent());
					break;
				case "grantTitle": 
					obj.setGrantTitle(bindElement.getElementsByTagName("literal").item(0).getTextContent());
					break;
				case "abstract": 
					obj.setGrantAbstract(bindElement.getElementsByTagName("literal").item(0).getTextContent());
					break;
				}
			}
			rows.add(obj);
		}// end of reading entries.
		
		LOGGER.info("grant to title-abstract count:"+rows.size());
		return rows;
	}

	private Set<Mesh> getMeshLines (File xmlFile) throws ParserConfigurationException, SAXException, IOException{
		Set<Mesh> rows = new HashSet<Mesh>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		NodeList entryList = doc.getElementsByTagName("result");
		for(int index=0; index< entryList.getLength(); index++){
			Mesh obj = new Mesh();
			Node node = entryList.item(index);
			//System.out.println(node.getNodeName());
			Element eElement = (Element) node;
			NodeList bindingNodes = eElement.getElementsByTagName("binding");
			for(int i=0; i< bindingNodes.getLength(); i++){
				Node n = bindingNodes.item(i);
				Element bindElement = (Element) n;
				String att = bindElement.getAttribute("name");
				switch(att){
				case "mesh": 
					obj.setMeshURI(bindElement.getElementsByTagName("uri").item(0).getTextContent());
					break;
				case "meshLabel": 
					obj.setMeshLabel(bindElement.getElementsByTagName("literal").item(0).getTextContent());
					break;
				}
			}
			rows.add(obj);
		}// end of reading entries.
		LOGGER.info("Total number of mesh terms:"+ rows.size());
		return rows;
	}

	private Set<String> getLines (File file) throws IOException{
		Set<String> rows = new HashSet<String>();
		CSVReader reader;
		reader = new CSVReader(new FileReader(file),',','\"');
		String [] nextLine;	
		reader.readNext();  // header
		while ((nextLine = reader.readNext()) != null) {
			rows.add(nextLine[0]);
		}
		reader.close();
		LOGGER.info("Total number of freetext keywords:"+ rows.size());
		return rows;
	}
}
