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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cornell.scholars.config.Configuration;

public class WosQueryResultsSetDataParser {
	
	private List<Article_TSV> articles_tsv = new ArrayList<Article_TSV>();
	private Set<String> wosIds = null;

	public static void main(String[] args) {
		WosQueryResultsSetDataParser obj = new WosQueryResultsSetDataParser();
		obj.runProcess();
	}

	public void runProcess() {
		articles_tsv = readWOSFiles(new File(Configuration.WOS_DATA_FOLDER));
		saveData(articles_tsv, Configuration.WOS_DATA_FILE);
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
		System.out.println("Total WOS_ArticleTSV Size:"+ data.size());
		System.out.println("Total WOS_ArticleTSV Size (Distinct WOSIDs): "+ wosIds.size());
	
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
					
					//System.out.println(lineJustFetched);
					wordsArray = lineJustFetched.split("\t");
					rows.add(wordsArray);
				}
			}
			index = 1;    // index 0 is header
			// every line is a single citation record. line has to split into multiple rows with one author in a row.
			//System.out.println("row count:"+ rows.size());
			for(; index<rows.size();index++){
				String[] each = rows.get(index);

				//printSingleEntry(each);

				r = each[8]; // for error print only

				String t = each[8];			// index 8: Title
				char ar[] = t.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String title = "";
				for(char a: ar){
					if((int)a != 0){
						title+=a;
					}
				}

				String j = each[9];		// index 9: Journal
				char jr[] = j.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String journal = "";
				for(char a: jr){
					if((int)a != 0){
						journal+=a;
					}
				}

				String l = each[12];	
				char lang[] = l.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String language = "";
				for(char a: lang){
					if((int)a != 0){
						language+=a;
					}
				}

				String tp = each[13];			// index 13: Type of work.
				char ty[] = tp.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String type = "";
				for(char a: ty){
					if((int)a != 0){
						type+=a;
					}
				}

				String kw = each[20];	
				char kw_ar[] = kw.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String keywords = "";
				for(char a: kw_ar){
					if((int)a != 0){
						keywords+=a;
					}
				}

				String isn = each[38];	
				char isn_ar[] = isn.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String issn = "";
				for(char a: isn_ar){
					if((int)a != 0){
						issn+=a;
					}
				}

				String eisn = each[39];
				char eisn_ar[] = eisn.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String eissn = "";
				for(char a: eisn_ar){
					if((int)a != 0){
						eissn+=a;
					}
				}

				String y = each[44];			// index 44: Year
				char yr_ar[] = y.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String year = "";
				for(char a: yr_ar){
					if((int)a != 0){
						year+=a;
					}
				}

				String d = each[54];
				char doi_ar[] = d.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String doi = "";
				for(char a: doi_ar){
					if((int)a != 0){
						doi+=a;
					}
				}

				String wosCat = each[57];
				char cat_ar[] = wosCat.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String wosCategory = "";
				for(char a: cat_ar){
					if((int)a != 0){
						wosCategory+=a;
					}
				}

				String rsa = each[58];
				char rsa_ar[] = rsa.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String researcharea = "";
				for(char a: rsa_ar){
					if((int)a != 0){
						researcharea+=a;
					}
				}

				String w = each[60];			// index 60: WOS
				char wosar[] = w.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String wos = "";
				for(char a: wosar){
					if((int)a != 0){
						wos+=a;
					}
				}
				wosIds.add(wos.trim());

				String pub = each[61];		// index 61: pubmed	
				char pm[] = pub.trim().toCharArray();  // There are invisible null characters within the value and have to be removed.
				String pubmed = "";
				for(char p: pm){
					if((int)p != 0){
						pubmed+=p;
					}
				}
				
				data.addAll(generateArticleData(each, title, journal, language, type, keywords, issn, eissn, year, doi, wosCategory, researcharea, wos, pubmed));
			}
			buf.close();
		}catch(Exception e){
			System.err.println(file.getName()+"-"+index);
			System.err.println(r);
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
