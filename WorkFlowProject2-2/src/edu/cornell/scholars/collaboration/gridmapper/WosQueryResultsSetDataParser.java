package edu.cornell.scholars.collaboration.gridmapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cornell.scholars.config.Configuration;

public class WosQueryResultsSetDataParser {
	private static final Logger LOGGER = Logger.getLogger(WosQueryResultsSetDataParser.class.getName());
			
	private List<Article_TSV> articles_tsv = new ArrayList<Article_TSV>();
	private Set<String> wosIds = null;

	public static void main(String[] args) {
		WosQueryResultsSetDataParser obj = new WosQueryResultsSetDataParser();
		obj.runProcess();
	}

	public void runProcess() {
		articles_tsv = readWOSFiles(new File(Configuration.SUPPL_FOLDER + "/"+Configuration.WOS_DATA_FOLDER));
		String filePath = Configuration.SUPPL_FOLDER + "/"+Configuration.WOS_DATA_FOLDER+"/"+Configuration.WOS_DATA_FILENAME;
		saveData(articles_tsv, filePath);
	}


	/**
	 * reads the wos-data folder and parse the data.
	 * @param string
	 */
	private List<Article_TSV> readWOSFiles(File folder) {
		wosIds = new HashSet<String>();
		List<Article_TSV> data = new ArrayList<Article_TSV>();
		if (folder.isDirectory()){
			File[] files = folder.listFiles();
			for(File file: files){
				if (file.getName().startsWith(".") || file.isDirectory() || file.getName().startsWith("WOSDataFile")) continue;
				data.addAll(readWOSFile(file));
			}
		}
		LOGGER.info("Total WOS_ArticleTSV Size:"+ data.size());
		LOGGER.info("Total WOS_ArticleTSV Size (Distinct WOSIDs): "+ wosIds.size());
	
		return data;
	}

	private List<Article_TSV> readWOSFile(File file) {
		List<Article_TSV> data = new ArrayList<Article_TSV>();
		//System.out.println("reading file: "+ file.getName());
		String r = null;
		int index = 0;
		try{
			FileInputStream is = new FileInputStream(file.getAbsolutePath());
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader buf = new BufferedReader(isr);
			ArrayList<String[]> rows = new ArrayList<String[]>();
			String lineJustFetched = null;
			String[] wordsArray;
			while(true){
				
				lineJustFetched = buf.readLine();
				
				if(lineJustFetched == null){  
					break; 
				}else{
					if(lineJustFetched.trim().length() == 0){
						continue;
					}
					
					wordsArray = lineJustFetched.split("\t");
					
//					if(lineJustFetched.contains("Droplet combustion characteristics of algae-derived renewable diesel")){
//						System.out.println(lineJustFetched);
//					}
					
					rows.add(wordsArray);
				}
			}
			index = 1;    // index 0 is header
			// every line is a single citation record. line has to split into multiple rows with one author in a row.
			//System.out.println("row count:"+ rows.size());
			for(; index<rows.size();index++){
				String[] each = rows.get(index);
				//printSingleEntry(each);
				String title = each[8]; // for error print only
				String t = each[8];			// index 8: Title
				if(title.contains("Isotope constraints on brine formation in closed basin salars, NW Argentina")){
					//System.out.println(title);
				}
				String journal = each[9];		// index 9: Journa	
				String language =each[12];
				String type = each[13];			// index 13: Type of work.
				String keywords = each[20];
				String issn = each[38];	
				String eissn = each[39];
				String year = each[44];			// index 44: Year
				String doi = each[54];
				String wosCategory = each[57];
				String researcharea = each[58];
				String wos = each[60];			// index 60: WOS
				wosIds.add(wos.trim());
				String pubmed = null;
				if(each.length > 61){
					pubmed = each[61];		// index 61: pubmed	
				}
				data.addAll(generateArticleData(each, title, journal, language, type, keywords, issn, eissn, year, doi, wosCategory, researcharea, wos, pubmed));
			}
			buf.close();
		}catch(Exception e){
			LOGGER.severe(file.getName()+"-"+index);
			LOGGER.severe(r);
			e.printStackTrace();
		}
		return data;
	}

	private List<Article_TSV> generateArticleData(String[] each, String title, String journal, String language, String type, String keywords, String issn, 
			String eissn, String year, String doi, String wosCategory, String researcharea, String wos, String pubmed) {

		List<Article_TSV> data = new ArrayList<Article_TSV>();
		
		//System.out.println(each[22]);
		
		// GET AFFILIATION DATA
		String l = each[22];
		char l_ar[] = l.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
		String line = "";
		for(char a: l_ar){
			if((int)a != 0){
				line+=a;
			}
		}
		//System.out.println("LINE IS:"+line);
		Pattern r1 = Pattern.compile("\\[(.*?)\\]");
		Pattern r2 = Pattern.compile("\\](.*?)\\[");
		Matcher m1 = r1.matcher(line);
		Matcher m2 = r2.matcher(line);
		boolean entryFound = false;
		String author = null;
		String aff = null;
		while (m1.find()) {
			author = null;
			aff = null;
			entryFound = true;
			int start = m1.start();
			int end = m1.end();
			//System.out.println(m1.group()+"*****" +start +"-"+end );
			String list = line.substring(start+1, end-1);
			String[] p_list = list.split(";");
			if(!m2.find()){
				aff = line.substring(end+1).trim();
				//System.out.println("affiliation:"+aff);
			}else{
				int s2 = m2.start();
				int e2 = m2.end();
				aff = line.substring(s2+1, e2-1).trim();
				//System.out.println("affiliation:"+aff);
			}	

			for(String p: p_list){
				author = p.trim();
				//System.out.println("author:"+ author);	
				Article_TSV entry = new Article_TSV();
				entry.setAuthor(author);
				entry.setAffiliation(aff);
				entry.setTitle(title);
				entry.setJournal(journal);
				entry.setLanguage(language);
				entry.setJournalType(type);
				entry.setKeywords(getSplitList(keywords));
				entry.setIssn(issn);
				entry.setEissn(eissn);
				entry.setYear(year);
				entry.setDoi(doi);
				entry.setWosCategories(getSplitList(wosCategory));
				entry.setResearchAreas(getSplitList(researcharea));
				entry.setWosId(wos);
				entry.setPubmedId(pubmed);
				//System.out.println(entry.toString());
				data.add(entry);
			}	
		}
		if(!entryFound){
			// x people, x affiliations
			String pl = each[5];
			char ppl_ar[] = pl.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
			String ppl = "";
			for(char a: ppl_ar){
				if((int)a != 0){
					ppl+=a;
				}
			}
			String[] affiliations= line.trim().split(";");
			String[] authors = ppl.split(";");
			if(affiliations.length != authors.length){
				//				System.out.println("No of Authors is different from No of Affiliations");
				//				System.out.println(line);
				//				System.out.println(ppl);
			}else{
				for(int indx = 0; indx< authors.length; indx++){
					author = authors[indx].trim();
					//System.out.println(author);
					aff = affiliations[indx].trim();
					//System.out.println(aff);
					Article_TSV entry = new Article_TSV();
					entry.setAuthor(author);
					entry.setAffiliation(aff);
					entry.setTitle(title);
					entry.setJournal(journal);
					entry.setLanguage(language);
					entry.setJournalType(type);
					entry.setKeywords(getSplitList(keywords));
					entry.setIssn(issn);
					entry.setEissn(eissn);
					entry.setYear(year);
					entry.setDoi(doi);
					entry.setWosCategories(getSplitList(wosCategory));
					entry.setResearchAreas(getSplitList(researcharea));
					entry.setWosId(wos);
					entry.setPubmedId(pubmed);
					//System.out.println(entry.toString());
					data.add(entry);
				}
				//System.out.println(line);
			}
		}
		return data;
	}

	private List<String> getSplitList(String list) {
		List<String> splitList = new ArrayList<String>();
		String ras[]= list.split(";");
		for(String ra: ras){
			splitList.add(ra.trim());
		}
		return splitList;
	}

	private void saveData(List<Article_TSV> data, String filePath) {
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (filePath);	
			for(Article_TSV article_tsv: data){
				printWriter.println(article_tsv.toString()); 
				//name, netid, articleid
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}

	//	private void printSingleEntry(String[] each) {
	//		for(int index=0; index<each.length; index++){
	//			String e = each[index];
	//			System.out.println(index+": "+e);
	//		}
	//	}

}
