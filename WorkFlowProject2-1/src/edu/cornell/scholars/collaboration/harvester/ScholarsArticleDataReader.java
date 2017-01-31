package edu.cornell.scholars.collaboration.harvester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;


public class ScholarsArticleDataReader {

	private static final Logger LOGGER = Logger.getLogger(ScholarsArticleDataReader.class.getName());
	
	//input file names
	private final static String ARTICLEID_MASTER_FILENAME = Configuration.SUPPL_FOLDER +"/"+ Configuration.ARTICLEID_MASTER_FILENAME;
	private final static String ARTICLEID_CURRENT_FILENAME = Configuration.QUERY_RESULTSET_FOLDER + "/" + Configuration.date + "/" + Configuration.ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV;

	private final static String WOS_QUERY_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
			Configuration.WOS_QUERY_FILENAME;


	private Map<String,String> articleIds = null;   //<articleId, source>
	private Map<String,String> articleIds_master = null; //<id, source> 
	private Map<String,String> articleIds_new = null; //<id, source>


	public static void main(String[] args) {
		ScholarsArticleDataReader obj = new ScholarsArticleDataReader();
		try {
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runProcess() throws IOException {	
		articleIds = readAllArticleIdFile(ARTICLEID_CURRENT_FILENAME);
		articleIds_master = readMasterArticleFile(new File(ARTICLEID_MASTER_FILENAME));
		articleIds_new = getNewArticleIds(articleIds, articleIds_master);
		print_wos_query_lines(articleIds_new, WOS_QUERY_FILENAME);
		updateMasterArticleFile(articleIds_new, new File(ARTICLEID_MASTER_FILENAME));
	}

	private Map<String, String> readAllArticleIdFile(String filePath) throws IOException {
		LOGGER.info("COLLAB1: reading all article file....");
		Map<String, String>  currentIdsMap = new HashMap<String, String>();
		CSVReader reader;
		reader = new CSVReader(new FileReader(new File(filePath)),',','\"');
		String [] nextLine;	
		while ((nextLine = reader.readNext()) != null) {
			if(!nextLine[1].trim().isEmpty() ||  !nextLine[2].trim().isEmpty()){
				String wosId = nextLine[1].trim();
				String pubmedId = nextLine[2].trim();
				if(!wosId.isEmpty()){
					currentIdsMap.put(wosId, "Web of Science (Lite)");
				}else if(!pubmedId.isEmpty()){
					currentIdsMap.put(pubmedId, "PubMed");
				}
			}	
		}
		reader.close();
		LOGGER.info("COLLAB1: reading all article file....completed");
		LOGGER.info("COLLAB1: all article size:"+ currentIdsMap.size());
		return currentIdsMap;
	}

	private Map<String, String> getNewArticleIds(Map<String, String> articleIds2,
			Map<String, String> articleIds_master2) {
		LOGGER.info("COLLAB1: getting new article ids...");
		Map<String, String>  newIdsMap = new HashMap<String, String>();
		Set<String> articleIdSet = articleIds2.keySet();
		LOGGER.info("Article Ids (Read): "+articleIdSet.size());
		Set<String> articleIds_masterSet = articleIds_master2.keySet();
		LOGGER.info("Article Ids (Master): "+articleIds_masterSet.size());
		for(String id: articleIdSet){
			if(!articleIds_masterSet.contains(id)){
				newIdsMap.put(id, articleIds2.get(id));
			}
		}
		LOGGER.info("COLLAB1: New Article Ids: "+newIdsMap.size());
		return newIdsMap;
	}

	/**
	 * Updating Master Article File.
	 * @param articleIds_new
	 * @param masterFile
	 * @throws IOException 
	 */
	private void updateMasterArticleFile(Map<String, String> articleIds_new, File masterFile) throws IOException {
		// Update the MASTER ARTICLE FILE.
		LOGGER.info("COLLAB1: updating the master article id file....");
		int counter=0;
		PrintWriter pw = null;
		FileWriter fw = new FileWriter(masterFile, true);
		pw = new PrintWriter(fw);
		Set<String> keys = articleIds_new.keySet();
		for(String key:keys){
			pw.print("\n\""+key +"\",\""+articleIds_new.get(key)+"\"");  //id, source
			counter++;
		}

		pw.close();
		LOGGER.info("COLLAB1: updating the master article id file....completed");
		LOGGER.info("COLLAB1: "+counter+" new rows added in "+ masterFile.getName());
	}

	private Map<String, String> readMasterArticleFile(File file) throws IOException{
		LOGGER.info("COLLAB1: reading master article file....");
		Map<String, String> map = new HashMap<String, String>();
		CSVReader reader;
		reader = new CSVReader(new FileReader(file),',','\"');
		String [] nextLine;	
		while ((nextLine = reader.readNext()) != null) {
			if(nextLine[0].isEmpty()) continue;
			map.put(nextLine[0], nextLine[1]);
		}
		reader.close();
		LOGGER.info("COLLAB1: reading master article file....completed");
		LOGGER.info("COLLAB1: master article size:"+ map.size());
		return map;
	}

	/**
	 * This method prints the query lines. 
	 * These queries has to be manually ran on Web of Science 
	 * web service to get the citation data for these articles.
	 * Process to run the query using web of science web service is given in file
	 * data/lookups/wos_webservice_query_run_process.txt 
	 * 
	 * @param articleIds_new2
	 * @throws FileNotFoundException 
	 */
	private void print_wos_query_lines(Map<String, String> newArticleIds, String outputFile) throws FileNotFoundException {
		LOGGER.info("COLLAB1: Generating query lines....");
		int pubmedCount = 0;
		int wosCount = 0;
		String pubmedquery="PMID = (";
		String wosquery="UT = (";

		PrintWriter printWriter;
		printWriter = new PrintWriter (outputFile);	
		Set<String> keys = newArticleIds.keySet();		
		for(String key: keys){
			String source = newArticleIds.get(key);
			if(source.trim().equals("Web of Science (Lite)")){
				wosCount++;
				if(wosCount == 1000){
					wosquery = wosquery + key.trim() + ")";
					printWriter.println(wosquery);
					wosquery="UT = (";
					wosCount = 0;
				}else{
					wosquery = wosquery + key.trim() + " OR ";
				}
			}else if(source.trim().equals("PubMed")){
				pubmedCount++;
				if(pubmedCount == 1000){
					pubmedquery = pubmedquery + key.trim() + ")";
					printWriter.println(pubmedquery);
					pubmedquery="PMID = (";
					pubmedCount = 0;
				}else{
					pubmedquery =pubmedquery + key.trim() + " OR ";
				}
			}
		}
		if(wosquery.contains("OR")){
			wosquery = wosquery.substring(0, wosquery.lastIndexOf("OR")).trim().concat(")");
			printWriter.println(wosquery);
		}
		if(pubmedquery.contains("OR")){
			pubmedquery = pubmedquery.substring(0, pubmedquery.lastIndexOf("OR")).trim().concat(")");
			printWriter.println(pubmedquery);
		}
		printWriter.close();
		LOGGER.info("COLLAB1: Generating query lines....completed");
	}

}
