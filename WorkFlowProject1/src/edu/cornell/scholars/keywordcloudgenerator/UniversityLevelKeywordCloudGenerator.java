package edu.cornell.scholars.keywordcloudgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.keywordminer.Mesh;
import edu.cornell.scholars.workflow1.MainEntryPoint_WorkFlow1;

public class UniversityLevelKeywordCloudGenerator {

	private static final Logger LOGGER = Logger.getLogger(UniversityLevelKeywordCloudGenerator.class.getName());

	private static String ARTICLE_2_PERSON_MAP = null;
	private static String ALL_KW_FILENAME = null;
	private static String ALL_MESHTERM_FILENAME = null;
	private static String ARTICLE_2_KW_FILENAME = null;
	private static String ARTICLE_2_MESH_FILENAME = null;
	private static String ARTICLE_2_INFERRED_KEYWORDS_MAP = null;
	private static String UNIVERISTY_KEYWORD_CLOUD = null;

	private Set<String> allkeywords = null;
	private Set<Mesh> allMesh = null;
	private Set<String> allMeshWords = null;
	private Map<String, Set<String>> articleMeshMap = null;
	private Map<String, Set<String>> articleKWMap = null;
	private Map<String, Set<String>> articleINFKWMap = null;
	private Map<String, Set<Person>> article2person = null;
	private Set<String> NFPersonArticles = null;

	public static void main(String[] args) {
		try {
			MainEntryPoint_WorkFlow1.init("resources/setup.properties");
			UniversityLevelKeywordCloudGenerator obj = new UniversityLevelKeywordCloudGenerator();
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
		ARTICLE_2_PERSON_MAP = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date 
				+ "/" + Configuration.ARTICLE_2_PERSON_MAP_FILENAME;

		ALL_KW_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ALL_KEYWORDS_FILENAME;
		ALL_MESHTERM_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ALL_MESHTERMS_FILENAME;

		ARTICLE_2_KW_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ARTICLE_2_KEYWORDSET_MAP_FILENAME;
		ARTICLE_2_MESH_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ARTICLE_2_MESHTERM_MAP_FILENAME;
		ARTICLE_2_INFERRED_KEYWORDS_MAP = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date 
				+ "/" + Configuration.ARTICLE_2_INFERREDKEYWORD_FILENAME;

		UNIVERISTY_KEYWORD_CLOUD = Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.HOMEPAGE_KEYWORD_CLOUD_FOLDER + "/" + Configuration.HOMEPAGE_KEYWORD_CLOUD;
	}

	public void runProcess() throws IOException, ParserConfigurationException, SAXException {

		NFPersonArticles = new HashSet<String>();

		article2person = readArticle2PersonMapFile(ARTICLE_2_PERSON_MAP);
		allkeywords = getLines(new File(ALL_KW_FILENAME));
		allMesh = getMeshLines(new File(ALL_MESHTERM_FILENAME));
		articleKWMap   = getArticleKeywordMeSHMap(new File(ARTICLE_2_KW_FILENAME));
		articleMeshMap = getArticleKeywordMeSHMap(new File(ARTICLE_2_MESH_FILENAME));
		articleINFKWMap = getArticleKeywordMeSHMap(new File(ARTICLE_2_INFERRED_KEYWORDS_MAP));

		//iterate over keywords and find articles and then persons for those articles.
		Map<String, Keyword> keywordMap = getKeywordToPersonMap(allkeywords, articleKWMap, article2person);
		//iterate over mesh terms and find articles and then persons for those articles.
		Map<String, Keyword> meshMap = getMeshTermToPersonMap(allMesh, articleMeshMap, article2person);

		Map<String, Keyword> groupedMap = new HashMap<String, Keyword>();
		groupedMap.putAll(keywordMap);
		groupedMap.putAll(meshMap);

		//System.out.println(NFPersonArticles.size()); // not used right now.

		computeWordCloudData(groupedMap);
		
		//printKeywordMap(groupedMap);


	}

	private void printKeywordMap(Map<String, Keyword> keywordMap) {
		Set<String> keys = keywordMap.keySet();
		for(String key: keys){
			Keyword kw = keywordMap.get(key);
			LOGGER.info("\""+key+"\",\""+kw.getCountByPerson()+"\"");
		}
	}

	private void computeWordCloudData(Map<String, Keyword> kwMap) throws JsonGenerationException, JsonMappingException, IOException {
		List<Keyword> kwListByPersonCount = new ArrayList<Keyword>(kwMap.values());
		Collections.sort(kwListByPersonCount, new Keyword.SortByPersonCount());
		List<Keyword> subKWListByPersonCount = kwListByPersonCount.subList(0, 300);
		mapDataToJSON(subKWListByPersonCount, UNIVERISTY_KEYWORD_CLOUD);
	}


	private Map<String, Keyword> getMeshTermToPersonMap(Set<Mesh> allMesh2, Map<String, Set<String>> articleMeshMap2,
			Map<String, Set<Person>> article2person2) {

		Map<String, Keyword> meshMap = new HashMap<String, Keyword>();

		for(Mesh mesh: allMesh2){
			String meshLabel = mesh.getMeshLabel();
			Set<String> listOfArticles = getArticleListForAMeshTerm(meshLabel, articleMeshMap2); 
			if(listOfArticles.size() >0){
				Set<Person> listOfPersons  = getPersonsListForAKeywordMesh(listOfArticles, article2person2);
				//System.out.println("\""+keyword+"\",\""+ listOfPersons.size()+"\"");
				if(listOfPersons.size() > 0){
					Keyword kw = new Keyword();
					for(Person per: listOfPersons){
						kw.addPerson(per);
					}
					kw.addTypes("MESH");
					kw.setCountOfArticle(listOfArticles.size());
					kw.setCountOfPerson(kw.getPersons().size());
					kw.setKeyword(meshLabel);
					meshMap.put(meshLabel, kw);
				}
			}	
		}	
		return meshMap;
	}

	private Map<String, Keyword> getKeywordToPersonMap(Set<String> allkeywords2, Map<String, Set<String>> articleKWMap2, Map<String, Set<Person>> article2person2) {

		Map<String, Keyword> keywordMap = new HashMap<String, Keyword>();

		for(String keyword: allkeywords2){
			Set<String> listOfArticles = getArticleListForAKeyword(keyword, articleKWMap2); 
			if(listOfArticles.size() >0){
				Set<Person> listOfPersons  = getPersonsListForAKeywordMesh(listOfArticles, article2person2);
				//System.out.println("\""+keyword+"\",\""+ listOfPersons.size()+"\"");
				if(listOfPersons.size() > 0){
					Keyword kw = new Keyword();
					for(Person per: listOfPersons){
						kw.addPerson(per);
					}
					kw.addTypes("KEYWORD");
					kw.setCountOfArticle(listOfArticles.size());
					kw.setCountOfPerson(kw.getPersons().size());
					kw.setKeyword(keyword);
					keywordMap.put(keyword, kw);
				}
			}	
		}	
		return keywordMap;
	}

	private Set<Person> getPersonsListForAKeywordMesh(Set<String> listOfArticles, Map<String, Set<Person>> article2person2) {
		Set<Person> persons = new HashSet<Person>();
		for(String article: listOfArticles){
			Set<Person> personSet = article2person2.get(article);
			try{
				addNewPersonsWithArticleCount(persons, personSet);		
			}catch(NullPointerException exp){
				NFPersonArticles.add(article);
			}
		}
		return persons;
	}

	private void addNewPersonsWithArticleCount(Set<Person> allPersonSet, Set<Person> newPersonSet) {
		// if person already exist in allPersonSet, increment count by 1.
		// if person do not have in allPersonSet, add new Person and made count 1.
		for(Person newPerson: newPersonSet){
			if(allPersonSet.contains(newPerson)){
				Person p = getPerson(allPersonSet, newPerson);
				p.setArticleCount(p.getArticleCount()+1);
			}else{
				newPerson.setArticleCount(1);	
				allPersonSet.add(newPerson);
			}
		}
	}

	private Person getPerson(Set<Person> allPersonSet, Person newPerson) {
		Person p = null;
		for(Iterator<Person> i = allPersonSet.iterator(); i.hasNext();){
			Person person = i.next();
			if(person.equals(newPerson)){
				p = person;
				break;
			}
		}
		return p;
	}

	private Set<String> getArticleListForAMeshTerm(String meshLabel, Map<String, Set<String>> articleMeshMap2) {
		Set<String> articles = new HashSet<String>();
		Set<String> keys = articleMeshMap2.keySet();
		for(String key: keys){
			Set<String> keywords = articleMeshMap2.get(key);
			if(keywords.contains(meshLabel.toUpperCase())){
				articles.add(key);
			}
		}
		return articles;
	}

	private Set<String> getArticleListForAKeyword(String keyword, Map<String, Set<String>> articleKWMap2) {
		Set<String> articles = new HashSet<String>();
		Set<String> keys = articleKWMap2.keySet();
		for(String key: keys){
			Set<String> keywords = articleKWMap2.get(key);
			if(keywords.contains(keyword.toUpperCase())){
				articles.add(key);
			}
		}
		return articles;
	}

	private Map<String, Set<Person>> readArticle2PersonMapFile(String filePath) throws IOException {
		Reader in;
		Map<String, Set<Person>> map = new HashMap<String, Set<Person>>();
		in = new FileReader(filePath);
		Iterable<CSVRecord> records = null;
		records = CSVFormat.EXCEL.withDelimiter(',').withQuote('"').parse(in);
		for (CSVRecord record : records) {
			String articleURI = record.get(0);
			Person person = new Person();
			String personURI = record.get(1);
			String personName = getPersonName(record.get(2), record.get(3), record.get(4));
			person.setPersonURI(personURI);
			person.setPersonName(personName);
			if(map.get(articleURI)!= null){
				Set<Person> p = map.get(articleURI);
				p.add(person);
				map.put(articleURI, p);
			}else{
				Set<Person> p = new HashSet<Person>();
				p.add(person);
				map.put(articleURI, p);
			}
		}
		LOGGER.info("UNIV-LEVEL KEYWORD CLOUD: Article to Person Set Map size:"+ map.size());
		return map;
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
		LOGGER.info("article to keywords/mesh line count:"+lineCount);
		return map;
	}


	private Set<Mesh> getMeshLines (File xmlFile) throws ParserConfigurationException, SAXException, IOException{
		allMeshWords = new HashSet<String>();
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
				allMeshWords.add(obj.getMeshLabel());
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
	private String getPersonName(String firstName, String middleName, String familyName) {
		return firstName+" "
				+ (middleName.trim().length()>0 ? middleName:"")+" "
				+familyName;
	}

	private void mapDataToJSON(Collection<Keyword> collection, String filePath) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
			mapper.writeValue(new File(filePath), collection);
			jsonInString = mapper.writeValueAsString(collection);
			//System.out.println(jsonInString);
	}
}
