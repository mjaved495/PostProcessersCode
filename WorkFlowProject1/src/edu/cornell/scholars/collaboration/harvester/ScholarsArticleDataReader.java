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

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;

//SELECT ?name ?netId ?art ?wosId ?pmId
//WHERE
//{
//    ?p a foaf:Person .
//    ?p rdfs:label ?name .
//    ?p hr:netId ?netId .
//    ?p vivo:relatedBy ?pos .
//    ?pos a vivo:Position .
//    ?pos hr:positionInUnit ?unit .
//    ?unit rdfs:label "Samuel Curtis Johnson Graduate School of Management" .
//    ?p vivo:relatedBy ?auth .
//    ?auth a vivo:Authorship .
//    ?auth vivo:relates ?art .
//    ?art a bibo:Article .
//    OPTIONAL {?art vivoc:wosId ?wosId .}
//    OPTIONAL {?art bibo:pmid ?pmId .}
//}
public class ScholarsArticleDataReader {

	//input file names
	private final static String ARTICLEID_MASTER_FILENAME = Configuration.SUPPL_FOLDER +"/"+ Configuration.ARTICLEID_MASTER_FILENAME;
	private final static String ARTICLEID_CURRENT_FILENAME = Configuration.ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV;
	
	private final static String WOS_QUERY_FILENAME = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
			Configuration.WOS_QUERY_FILENAME;
	
	
	private Map<String,String> articleIds = null;   //<articleId, source>
	private Map<String,String> articleIds_master = null; //<id, source> 
	private Map<String,String> articleIds_new = null; //<id, source>
	
	
	public static void main(String[] args) {
		ScholarsArticleDataReader obj = new ScholarsArticleDataReader();
		obj.runProcess();
	}

	public void runProcess() {	
		articleIds = readAllArticleIdFile(ARTICLEID_CURRENT_FILENAME);
		articleIds_master = readMasterArticleFile(new File(ARTICLEID_MASTER_FILENAME));
		articleIds_new = getNewArticleIds(articleIds, articleIds_master);
		print_wos_query_lines(articleIds_new, WOS_QUERY_FILENAME);
		updateMasterArticleFile(articleIds_new, new File(ARTICLEID_MASTER_FILENAME));
	}
	
	private Map<String, String> readAllArticleIdFile(String filePath) {
		Map<String, String>  currentIdsMap = new HashMap<String, String>();
		CSVReader reader;
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return currentIdsMap;
	}

	private Map<String, String> getNewArticleIds(Map<String, String> articleIds2,
			Map<String, String> articleIds_master2) {
		Map<String, String>  newIdsMap = new HashMap<String, String>();
		Set<String> articleIdSet = articleIds2.keySet();
		System.out.println("Article Ids (Read): "+articleIdSet.size());
		Set<String> articleIds_masterSet = articleIds_master2.keySet();
		System.out.println("Article Ids (Master): "+articleIds_masterSet.size());
		for(String id: articleIdSet){
			if(!articleIds_masterSet.contains(id)){
				newIdsMap.put(id, articleIds2.get(id));
			}
		}
		System.out.println("Article Ids (New): "+newIdsMap.size());
		return newIdsMap;
	}

	/**
	 * Updating Master Article File.
	 * @param articleIds_new
	 * @param masterFile
	 */
	private void updateMasterArticleFile(Map<String, String> articleIds_new, File masterFile) {
		// Update the MASTER ARTICLE FILE.
		int counter=0;
		PrintWriter pw = null;
		try {
			FileWriter fw = new FileWriter(masterFile, true);
			pw = new PrintWriter(fw);
			Set<String> keys = articleIds_new.keySet();
			for(String key:keys){
				pw.print("\n\""+key +"\",\""+articleIds_new.get(key)+"\"");  //id, source
				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		System.out.println(counter+" new rows added in "+ masterFile.getName());
	}
	
	private Map<String, String> readMasterArticleFile(File file){
		Map<String, String> map = new HashMap<String, String>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file),',','\"');
			String [] nextLine;	
			while ((nextLine = reader.readNext()) != null) {
				if(nextLine[0].isEmpty()) continue;
				map.put(nextLine[0], nextLine[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 */
	private void print_wos_query_lines(Map<String, String> newArticleIds, String outputFile) {
		int pubmedCount = 0;
		int wosCount = 0;
		String pubmedquery="PMID = (";
		String wosquery="UT = (";
		
		PrintWriter printWriter;
		try {
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}

}
