package edu.cornell.scholars.scopusauthorkeywords;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FreeTextKeywordReader {

	private static final Logger LOGGER = Logger.getLogger(FreeTextKeywordReader.class.getName());
	
	public static final String AUTHOR_KEYWORDS = "authkeywords";
	public static final String AUTHOR_KEYWORD = "author-keyword";
	public static final String DOT = ".";
	
	private Map<String, Set<String>> existingKwMap = null;
	private Map<String, String> urlToScopusIdMap = null;
	
	public Map<String, Set<String>> extractNewKeywords(Map<String, Set<String>> exKwMap, Map<String, String> urlToScopusId, String folderPath) {
		Map<String, Set<String>> scholarsURLKeywordMap = new HashMap<String, Set<String>>();
		existingKwMap = exKwMap;
		urlToScopusIdMap = urlToScopusId;
		
		File folder = new File(folderPath);
		if(!folder.exists()){
			LOGGER.warning("scopus xml file folder does not exists in query_result folder....returning.");
			return null;
		}
		File xmlFiles[] = folder.listFiles();
		for(File file: xmlFiles){
			//System.out.println(file.getName());
			if(file.getName().endsWith(".xml")){
				try {
					String scopusId = file.getName().substring(0, file.getName().indexOf(DOT));
//					if(scopusId.equals("84996917576")){
//						System.out.println(scopusId);
//					}
					Set<String> keywords = processXMLFile(file, scopusId);
					String url = urlToScopusIdMap.get(scopusId);
					if(url != null && (keywords != null && !keywords.isEmpty())){
						scholarsURLKeywordMap.put(url, keywords);
					}
				} catch (ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		return scholarsURLKeywordMap;
	}

	private Set<String> processXMLFile(File file, String scopusId) throws ParserConfigurationException, SAXException, IOException {
		Set<String> existingKeywords = existingKwMap.get(scopusId);
		Set<String> keywords = getNewAuthorKeywords(file, existingKeywords);
		return keywords;
	}

	private Set<String> getNewAuthorKeywords(File xmlFile, Set<String> existingKeywords) throws ParserConfigurationException, SAXException, IOException {
		Set<String> keywords = new HashSet<String>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		NodeList entryList = doc.getElementsByTagName(AUTHOR_KEYWORDS);	
		for(int index=0; index< entryList.getLength(); index++){
			Node node = entryList.item(index);
			//System.out.println(node.getNodeName());
			Element eElement = (Element) node;
			NodeList keywordNodes = eElement.getElementsByTagName(AUTHOR_KEYWORD);
			for(int index2 =0; index2 < keywordNodes.getLength(); index2++){
				Node kwNode = keywordNodes.item(index2);
				String keyword = kwNode.getTextContent();
				//System.out.println(keyword);
				if(existingKeywords != null){
					if(!existingKeywords.contains(keyword.toUpperCase())){ // add only new author keywords
						keywords.add(keyword);
					}
				}else{
					keywords.add(keyword);
				}
				
			}
		}
		return keywords;
	}
}
