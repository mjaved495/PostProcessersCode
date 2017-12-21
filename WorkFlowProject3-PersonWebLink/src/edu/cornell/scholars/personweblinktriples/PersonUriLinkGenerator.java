package edu.cornell.scholars.personweblinktriples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
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
import org.apache.jena.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class PersonUriLinkGenerator {

	private static final Logger LOGGER = Logger.getLogger(PersonUriLinkGenerator.class.getName());
	
	public static final String FACULTY_PAGE =  "Faculty Page";
	public static final String WEB_PAGE =  "Web Page";
	public static final String VCARD_HASURL = "http://www.w3.org/2006/vcard/ns#hasURL";
	public static final String VCARD_URL = "http://www.w3.org/2006/vcard/ns#URL";
	public static final String VCARD_url = "http://www.w3.org/2006/vcard/ns#url";

	
	public void runProcess(String inputFile, String outputFile) {
		try {
			Map<String, String> current_mapping = readInputFile(new File(inputFile));
			generateRDF(current_mapping, outputFile);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	private void generateRDF(Map<String, String> mapping, String outputFilePath) {		
		Model model = ModelFactory.createDefaultModel();	
		Set<String> netIds = mapping.keySet();
		Property hasURL = model.createProperty(VCARD_HASURL);
		Property url = model.createProperty(VCARD_url);
		Resource URL = model.createResource(VCARD_URL);
		for(String netId: netIds){
			Boolean isValidpersonalURL  = isValidURL(mapping.get(netId));
			if(isValidpersonalURL){
				Resource vcardPerson =    model.createResource("http://scholars.cornell.edu/individual/"+netId+"-VI");
				Resource vcardPersonURL = model.createResource("http://scholars.cornell.edu/individual/"+netId+"-VURL");
				vcardPersonURL.addProperty(RDF.type, URL);
				vcardPerson.addProperty(hasURL, vcardPersonURL);
				vcardPersonURL.addProperty(url, mapping.get(netId));
			}
		}

		try {
			saveRDFModel(model, outputFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Boolean isValidURL(String testURL) {
		try {
			URL url = new URL(testURL);
			// We want to check the current URL
		    HttpURLConnection.setFollowRedirects(false);
		    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		    // We don't need to get data
		    httpURLConnection.setRequestMethod("HEAD");
		    // Some websites don't like programmatic access so pretend to be a browser
		    httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
		    int responseCode = httpURLConnection.getResponseCode();
		    //System.out.println(responseCode);
		    return true;
		} catch (MalformedURLException e) {
			System.out.println(testURL + " -(Not Valid URL)");
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.info("Weblinks: IOException: "+ testURL);
			return true;  // FOR BTI specifically
		}
		return false;
	}



	private void saveRDFModel(Model rdfModel, String filePath) throws FileNotFoundException {
		PrintWriter printer = null;
		printer = new PrintWriter(filePath);
		rdfModel.write(printer, "N-Triples");
		printer.flush();
		printer.close();
	}


	private Map<String, String> readInputFile (File xmlFile) throws ParserConfigurationException, SAXException, IOException{
		Map<String, String> map = new HashMap<String, String>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		NodeList entryList = doc.getElementsByTagName("euser");
		for(int index=0; index< entryList.getLength(); index++){
			Node node = entryList.item(index);
			Element nodeElement = (Element) node;
			String netId = nodeElement.getElementsByTagName("netid").item(0).getTextContent();
			//System.out.println(netId);
			Node websitesNodes = nodeElement.getElementsByTagName("websites").item(0);
			Element websitesElement = (Element) websitesNodes;
			NodeList websiteNodes = websitesElement.getElementsByTagName("website");
			for(int index1=0; index1< websiteNodes.getLength(); index1++){
				Node webNode = websiteNodes.item(index1);
				Element webElement = (Element) webNode;
				String webLabel = webElement.getElementsByTagName("label").item(0).getTextContent().trim();
				//System.out.println(webLabel);
				if(webLabel.equalsIgnoreCase(FACULTY_PAGE) || webLabel.equalsIgnoreCase(WEB_PAGE)){
					//System.out.println(webLabel);
					String webURL = webElement.getElementsByTagName("url").item(0).getTextContent().trim();
					map.put(netId, webURL);
				}else{
					System.out.println(netId+ ": URL do not have correct label");
				}
			}
		}// end of reading entries.
		//System.out.println(map.size());
		return map;
	}
}
