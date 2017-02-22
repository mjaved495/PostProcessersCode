package edu.cornell.scholars.keywordminer;

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
import edu.cornell.scholars.workflow1.MainEntryPoint_WorkFlow1;

public class KeywordMinerEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(KeywordMinerEntryPoint.class.getName());

	//input file names
	private static String ARTICLE_FILENAME = null;
	private static String ALL_KW_FILENAME = null;
	private static String ALL_MESHTERM_FILENAME = null;
	private static String ARTICLE_2_KW_FILENAME = null;
	private static String ARTICLE_2_MESH_FILENAME = null;

	//output file names
	private static String ARTICLE_MAP_CSVDATA_FILEPATH = null;
	private static String ARTICLE_MAP_NTDATA_FILEPATH =  null;

	private static int count = 0;
	private static int articlecount = 0;
	private static int articleLevelMinedWordCount = 0;

	private Set<String> allkeywords = null;
	private Set<Mesh> allMesh = null;
	private List<ArticleEntries> article_rows = null;
	private Set<String> matchWords = new HashSet<String>();

	private Map<String, String> meshMap = new HashMap<String, String>();
	private Map<String, Set<String>> articleMeshMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> articleKWMap = new HashMap<String, Set<String>>();

	public static void main(String[] args) {
		try {
			MainEntryPoint_WorkFlow1.init("resources/setup.properties");
			KeywordMinerEntryPoint obj = new KeywordMinerEntryPoint();
			obj.setLocalDirectories();
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
		ARTICLE_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ARTICLE_2_TITLE_ABSTRACT_MAP_FILENAME;
		ALL_KW_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ALL_KEYWORDS_FILENAME;
		ALL_MESHTERM_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ALL_MESHTERMS_FILENAME;
		ARTICLE_2_KW_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ARTICLE_2_KEYWORDSET_MAP_FILENAME;
		ARTICLE_2_MESH_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ARTICLE_2_MESHTERM_MAP_FILENAME;

		//output file names
		ARTICLE_MAP_CSVDATA_FILEPATH = Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+ Configuration.INFERRED_KEYWORDS_FOLDER +"/"+ Configuration.INF_KEYWORDS_CSV;
		ARTICLE_MAP_NTDATA_FILEPATH =  Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+ Configuration.INFERRED_KEYWORDS_FOLDER +"/"+ Configuration.INF_KEYWORDS_NT;
		
	}

	public void runProcess() throws IOException, ParserConfigurationException, SAXException {
		setLocalDirectories();
		article_rows = readArticleMapFile(new File(ARTICLE_FILENAME));
		allkeywords = getLines(new File(ALL_KW_FILENAME));
		allMesh = getMeshLines(new File(ALL_MESHTERM_FILENAME));
		articleKWMap   = getArticleKeywordMeSHMap(new File(ARTICLE_2_KW_FILENAME));
		articleMeshMap = getArticleKeywordMeSHMap(new File(ARTICLE_2_MESH_FILENAME));

		Map<String, ArticleEntriesData> articleDataMap =  createArticleMapOfEntries(article_rows);
		LOGGER.info(articleDataMap.size()+" rows of article data map.");

		compareAndProcess(articleDataMap);
		saveArticleMapDataInAFile(articleDataMap, ARTICLE_MAP_CSVDATA_FILEPATH);
		saveDataInARDF(articleDataMap, ARTICLE_MAP_NTDATA_FILEPATH);

	}

	private void saveDataInARDF(Map<String, ArticleEntriesData> articleDataMap, String filePath) throws FileNotFoundException {
		String VIVOC_NS = "http://scholars.cornell.edu/ontology/vivoc.owl#";
		Model rdfmodel = ModelFactory.createDefaultModel();	
		Property inferredKeyword = rdfmodel.createProperty(VIVOC_NS+"inferredKeyword");
		Set<String> articleURIs = articleDataMap.keySet();

		for(Iterator<String> i =  articleURIs.iterator(); i.hasNext();){
			String articleURI = i.next();
			ArticleEntriesData data = articleDataMap.get(articleURI);
			Set<String> keywords = data.getKeywords();
			Set<String> mesh = data.getMeshTerms();

			Set<String> uniqKeyword = new HashSet<String>();

			if(keywords != null && !keywords.isEmpty()){
				Resource article = rdfmodel.createResource(articleURI);
				for(String kw: keywords){
					if(!uniqKeyword.contains(kw.toLowerCase())){
						article.addProperty(inferredKeyword, kw);
						uniqKeyword.add(kw.toLowerCase());
					}
				}
			}
			if(mesh != null && !mesh.isEmpty()){
				Resource article = rdfmodel.createResource(articleURI);
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

	private void compareAndProcess(Map<String, ArticleEntriesData> articleDataMap) {	
		Set<String> article_key = articleDataMap.keySet();
		LOGGER.info("processing "+ article_key.size() +" article rows");
		for(Iterator<String> i = article_key.iterator(); i.hasNext();){
			String articleURI = i.next();
			ArticleEntriesData articleEntry = articleDataMap.get(articleURI);
			Set<String> distnctTermsFoundForAnArticle = new HashSet<String>();
			articleLevelMinedWordCount = 0;
			Set<String> titleStrings = articleEntry.getTitlewords();
			// calling this method for each subject area of an article.
			boolean matchFound = process(articleEntry, titleStrings, allkeywords, allMesh,
					distnctTermsFoundForAnArticle);
			if(matchFound){
				articlecount++;
			}
			articleEntry.setMinedKeywordCount(articleLevelMinedWordCount);
		}
		LOGGER.info("Inferred Keywords: "+count+" keywords are inferred.");
		LOGGER.info("Inferred Keywords: "+articlecount+" article matches");

	}

	private boolean process(ArticleEntriesData articleEntry, Set<String> titleStrings, 
			Set<String> keywords, Set<Mesh> pubmeds, Set<String> distinctTermsFoundForAnArticle) {
		String uri = articleEntry.getArticleURI();
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
				Set<String> existingKW = articleKWMap.get(uri);
				Set<String> existingMesh = articleMeshMap.get(uri);	
				// Add inferred keyword only if it does not currently exists in Keyword or MeSH terms of this Article.
				if((existingKW == null || !existingKW.contains(titleString.toUpperCase()))  && 
						(existingMesh == null || !existingMesh.contains(titleString.toUpperCase()))){
					matchWords.add(titleString);
					articleEntry.addKeywords(titleString);
					count++;
					if(!distinctTermsFoundForAnArticle.contains(titleString)){
						articleLevelMinedWordCount++;
						distinctTermsFoundForAnArticle.add(titleString);
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
				Set<String> existingKW = articleKWMap.get(uri);
				Set<String> existingMesh = articleMeshMap.get(uri);	
				// Add inferred keyword only if it does not currently exists in Keyword or MeSH terms of this Article.
				if((existingKW == null || !existingKW.contains(titleString.toUpperCase()))  && 
						(existingMesh == null || !existingMesh.contains(titleString.toUpperCase()))){
					matchWords.add(titleString);
					articleEntry.addMeshTerms(titleString);
					count++;
					if(!distinctTermsFoundForAnArticle.contains(titleString)){
						articleLevelMinedWordCount++;
						distinctTermsFoundForAnArticle.add(titleString);
					}
					matchFound=true;
				}
			}
		}
		return matchFound;
	}

	private static void saveArticleMapDataInAFile(Map<String, ArticleEntriesData> articleDataMap,
			String articleMapDataFilepath) throws FileNotFoundException {
		PrintWriter printWriter;
		printWriter = new PrintWriter (articleMapDataFilepath);	
		Set<String> keySet = articleDataMap.keySet();
		for(String key: keySet){
			ArticleEntriesData obj = articleDataMap.get(key);
			printWriter.println(obj.toString());
		}
		printWriter.close();
	}

	private static Map<String, ArticleEntriesData> createArticleMapOfEntries(List<ArticleEntries> article_rows) {
		Map<String, ArticleEntriesData> map = new HashMap<String, ArticleEntriesData>();
		for(int index=0; index<article_rows.size();index++){
			ArticleEntries obj = article_rows.get(index);
			String articleURI = obj.getArticleURI();
			if(map.get(articleURI) == null){
				ArticleEntriesData data = new ArticleEntriesData();
				data.setArticleURI(obj.getArticleURI());
				data.setArticleTitle(obj.getArticleTitle());
				data.setArticleAbstract(obj.getArticleAbstract());
				String title = obj.getArticleTitle();
				if(obj.getArticleAbstract() != null){
					Set<String> abstractWords = getWords(obj.getArticleAbstract());
					for(String word: abstractWords){
						data.addTitlewords(word); 
					}
				}
				Set<String> words = getWords(title);
				for(String word: words){
					data.addTitlewords(word); 
				}
				map.put(articleURI, data);
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

	private List<ArticleEntries> readArticleMapFile(File file) throws IOException {
		List<ArticleEntries> rows = new ArrayList<ArticleEntries>();
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(line.trim().length() == 0) continue;
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),'|');	
			ArticleEntries obj = new ArticleEntries();
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					obj.setArticleURI(tokens[0]);
					obj.setArticleTitle(tokens[1]);
					if(tokens[2] != null){
						obj.setArticleAbstract(tokens[2]);
					}
				}catch (ArrayIndexOutOfBoundsException exp) {
					for (String s : tokens) {
						LOGGER.warning("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
					LOGGER.warning("\n");
					continue;
				}
			}
			rows.add(obj);
		}
		LOGGER.info("article to title-abstract line count:"+lineCount);
		br.close();
		return rows;
	}

	private Map<String, Set<String>> getArticleKeywordMeSHMap(File file) throws IOException {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(line.trim().length() == 0) continue;
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),'|');	
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					Set<String> kwords = new HashSet<String>();
					String kw[] = tokens[1].split(";;");
					for(String k : kw){
						kwords.add(k.trim().toUpperCase());
					}
					map.put(tokens[0], kwords);
				}catch (ArrayIndexOutOfBoundsException exp) {
					for (String s : tokens) {
						LOGGER.warning("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
					LOGGER.warning("\n");
					continue;
				}
			}
		}
		br.close();
		LOGGER.info("article to keywords-mesh line count:"+lineCount);
		return map;
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
