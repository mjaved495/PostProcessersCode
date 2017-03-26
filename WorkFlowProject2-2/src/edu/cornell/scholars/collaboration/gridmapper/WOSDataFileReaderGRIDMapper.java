package edu.cornell.scholars.collaboration.gridmapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import edu.cornell.scholars.collaboration.gridreader.EditDistance;
import edu.cornell.scholars.collaboration.gridreader.GridFileReader;
import edu.cornell.scholars.collaboration.gridreader.GridModel;
import edu.cornell.scholars.config.Configuration;

public class WOSDataFileReaderGRIDMapper {
	
	private static final Logger LOGGER = Logger.getLogger(WOSDataFileReaderGRIDMapper.class.getName()); 
			
	//input files
	private final static String WOS_DATA_FILE = Configuration.SUPPL_FOLDER + "/"+Configuration.WOS_DATA_FOLDER+"/"+Configuration.WOS_DATA_FILENAME;
	private final static String GRID_FILENAME = Configuration.SUPPL_FOLDER+"/"+Configuration.GRID_FILENAME;
	private final static String COUNTRIES_FILE = Configuration.SUPPL_FOLDER +"/"+Configuration.COUNTRIES_FILE;
	private final static String USA_STATE_FILE = Configuration.SUPPL_FOLDER +"/"+Configuration.USA_STATE_FILE;
	
	//output file
	private final static String AFFILIATION_GRID_MAPPER_FILE = Configuration.SUPPL_FOLDER+"/"+Configuration.AFF_GRID_MAP;
	
	private Map<String, GridModel> gridMap = new HashMap<String, GridModel>();
	private Map<String, GridModel> gridDataMap = new HashMap<String, GridModel>();

	private Set<String> gridNFString = new HashSet<String>();
	private Set<String> affiliationStringSet = new HashSet<String>();

	

	public static void main(String args[]){
		WOSDataFileReaderGRIDMapper obj = new WOSDataFileReaderGRIDMapper();
		obj.runProcess();
	}

	public void runProcess(){
		List<Article_TSV> data = readFile(WOS_DATA_FILE);
		List<String> countries = readCountriesList(COUNTRIES_FILE);
		List<String> usaStateList = readUsaStateFile(USA_STATE_FILE);
		createAffiliationGRIDMap(affiliationStringSet, countries, usaStateList);
		saveAffilitionGridMap(AFFILIATION_GRID_MAPPER_FILE);
	}

	public Map<String, GridModel> runProcess(String filePath){
		List<Article_TSV> data = readFile(filePath);
		List<String> countries = readCountriesList(COUNTRIES_FILE);
		createAffiliationGRIDMap(affiliationStringSet, countries, null);
		return gridDataMap;
	}

	private void saveAffilitionGridMap(String outputFileName) {
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (outputFileName);
			Set<String> keys = gridDataMap.keySet();
			for(String key: keys){
				printWriter.println("\""+key +"\","+gridDataMap.get(key).toString());
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}

	private void createAffiliationGRIDMap(Set<String> affiliationSet, List<String> countries, List<String> stateList) {
		LOGGER.info("Affiliation: "+affiliationSet.size());
		int count=0;  int countZero = 0; int countOne = 0; int countryNF=0; int gridNFC = 0;

		Map<String, Set<String>> map = 
				GridFileReader.readGRIDFile(new File(GRID_FILENAME), gridMap);	
		Set<String> grid = map.keySet();

		for(String a: affiliationSet){
			if(a.contains("SUNY Binghamton")){
				//System.out.println(a);
			}	
			String affiliation = a;
			if (a.isEmpty()) continue;	
			Set<String> affSet = null;
			String country = findCountryName(a, countries);
			if (country == null && findUSAState(a, stateList)){
				country = "United States";
			}
			if(country == null){
				LOGGER.warning("Country not found: "+a);
				countryNF++;
				affSet = grid;
			}else{
				country = replaceCountryName(country);
				affSet = map.get(country.toUpperCase());
				if(affSet == null){
					LOGGER.warning("Country not in GRID: "+country);
					continue;
				}
			}

			a = a.substring(0, a.indexOf(","));
			//replace abbreviations
			String a1 = replaceAbbreviations(a);
			a1 = removeStopCharacters(a1);
			boolean gridNF = true;
			for(String g: affSet){
				String g1 = removeStopCharacters(g);	
				long i = EditDistance.minDistance(a1.toUpperCase(), g1.toUpperCase());
				if(i < 2){
					count++;
					gridNF = false;
					if(i == 0) {
						gridDataMap.put(affiliation, gridMap.get(g+"---"+country.toUpperCase()));
						countZero++;
						break;
					}else if(i == 1){
						if(gridDataMap.get(affiliation) == null){
							gridDataMap.put(affiliation, gridMap.get(g+"---"+country.toUpperCase()));
							countOne++;
						}
					}
				}
			}
			if(gridNF){
				gridNFString.add(a);
				gridNFC++;
			}
		}
		LOGGER.info("COUNTRY NF: "+countryNF);
		LOGGER.info("GRID NF: "+gridNFC);
		LOGGER.info("countZero "+countZero);
		LOGGER.info("countOne "+countOne);
		//		for(String str: gridNFString){
		//			if(str.toUpperCase().contains("UNIV")){
		//				System.out.println(str);
		//			}
		//		}
	}

	private String removeStopCharacters(String a1) {
		a1 = a1.replaceAll("-", ""); 
		a1 = a1.replaceAll("'", ""); 
		a1 = a1.replaceAll("L'", "");
		a1 = a1.replaceAll(" at ", " ");
		a1 = a1.replaceAll(" of ", " ");
		a1 = a1.replaceAll(" ", "");
		return a1;
	}

	private boolean findUSAState(String entry, List<String> stateList) {
		String regex1 = ".*[A-Z]{2} [0-9]{5}$";
		String regex2 = ".*[A-Z]{2}$";
		if(Pattern.matches(regex1, entry) || Pattern.matches(regex2, entry)){
			if(entry.length()>15){
				entry = entry.substring(entry.length()-15);
			}
			for(String state: stateList){
				if(entry.contains(state)){
					return true;
				}
			}
		}
		return false;
	}

	private String findCountryName(String a, List<String> countries) {
		//System.out.println(a);
		a = replaceCountryName(a);
		a = a.replaceAll(";","").trim();
		String words[] = a.split(" ");
		if(words.length < 3) return null;
		int i = words.length-1;
		String c = words[i];
		if(c.indexOf(",")>0){
			c = c.substring(c.lastIndexOf(",")+1).trim();
		}
		int attempt = 1;
		while(!countries.contains(c.toUpperCase())){
			c = words[i-attempt]+ " "+c;
			if(c.indexOf(",")>0){
				c = c.substring(c.lastIndexOf(",")+1).trim();
			}
			if(attempt>1) return null;
			attempt++;
		}
		//System.out.println(c);
		return c;
	}

	private List<Article_TSV> readFile(String filePath){
		int count = 0;
		List<Article_TSV> articles = new ArrayList<Article_TSV>();
		try{
			FileInputStream is = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader buf = new BufferedReader(isr);
			String lineJustFetched = null;
			while(true){
				lineJustFetched = buf.readLine();
				Article_TSV article = new Article_TSV();
				if(lineJustFetched == null){  
					break; 
				}else{
					if(lineJustFetched.trim().length() == 0){
						continue;
					}
					//System.out.println(lineJustFetched);
					String[] nextLine = lineJustFetched.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
					article.setAuthor(nextLine[0].replaceAll("\\\"", ""));
					article.setAffiliation(nextLine[1].replaceAll("\\\"", ""));
					article.setTitle(nextLine[2].replaceAll("\\\"", ""));
					article.setJournal(nextLine[3].replaceAll("\\\"", ""));
					article.setLanguage(nextLine[4].replaceAll("\\\"", ""));
					article.setPublicationType(nextLine[5].replaceAll("\\\"", ""));
					article.setWosId(nextLine[6].replaceAll("\\\"", ""));
					article.setPubmedId(nextLine[7].replaceAll("\\\"", ""));
					article.setYear(nextLine[8].replaceAll("\\\"", ""));
					article.setIssn(nextLine[9].replaceAll("\\\"", ""));
					article.setEissn(nextLine[10].replaceAll("\\\"", ""));
					article.setDoi(nextLine[11].replaceAll("\\\"", ""));
					article.setKeywords(getSplitList(nextLine[12].replaceAll("\\\"", "")));
					article.setWosCategories(getSplitList(nextLine[13].replaceAll("\\\"", "")));
					article.setResearchAreas(getSplitList(nextLine[14].replaceAll("\\\"", "")));	
					articles.add(article); 

					if(!affiliationStringSet.contains(article.getAffiliation())){
						affiliationStringSet.add(article.getAffiliation());
						//System.out.println(article.getAffiliation());
					}
					count++;
				}
			}
			buf.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		LOGGER.info(count+" lines read.");
		return articles;
	}	

	private List<String> readCountriesList(String filePath) {
		List<String> list = new ArrayList<String>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {
				if(line.trim().length() == 0) continue;
				list.add(line);
			}
		}catch (FileNotFoundException e) {
			LOGGER.severe(line);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe(line);
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	private ArrayList<String> readUsaStateFile(String filePath){
		ArrayList<String> rows = new ArrayList<String>();
		FileInputStream is;
		try {
			is = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader buf = new BufferedReader(isr);
			String lineJustFetched = null;

			while(true){
				lineJustFetched = buf.readLine();
				if(lineJustFetched == null){  
					break; 
				}else{
					if(lineJustFetched.trim().length() == 0){
						continue;
					}
					rows.add(lineJustFetched.trim().toUpperCase());
				}
			}
			buf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rows;
	}
	private List<String> getSplitList(String string) {
		string = string.replaceAll("[\\[\\]]", "").trim();
		List<String> data = new ArrayList<String>(Arrays.asList(string.split(",")));
		return data;
	}

	private String replaceAbbreviations(String a) {
		a = " ".concat(a).concat(" ");
		a = a.replace(" univ ", " University ");
		a = a.replace(" Univ ", " University ");
		a = a.replace(" Uinvers ", "University");
		a = a.replace(" Inst ", " Institute ");
		a = a.replace(" Nucl ", " Nuclear ");
		a = a.replace(" Sci ", " Science ");
		a = a.replace(" Nucl ", " Nuclear ");
		a = a.replace(" Technol ", " Technology ");
		a = a.replace(" Org ", " Organnisation ");
		a = a.replace(" Ctr ", " Center ");
		a = a.replace(" Med ", " Medical ");
		a = a.replace(" Agr ", " Agricultural ");
		a = a.replace(" Hlth ", " Health ");
		a = a.replace(" Clin ", " Clinic ");
		a = a.replace(" No ", " Northern ");
		a = a.replace(" N ", " North ");
		a = a.replace(" S ", " South ");
		a = a.replace(" E ", " East ");
		a = a.replace(" W ", " West ");
		a = a.replace(" Int ", " International ");
		a = a.replace(" Calif "," California ");
		a = a.replace("Fdn", " Foundation ");
		a = a.replace(" Natl ", "National ");
		
		return a.trim();
	}

	private String replaceCountryName(String country) {
		country = country.replace("ENGLAND", "United Kingdom");
		country = country.replace("England", "United Kingdom");
		country = country.replace("Scotland", "United Kingdom");
		country = country.replace("Wales", "United Kingdom");
		country = country.replace("U Arab", "United Arab");
		country = country.replace("USA","United States");
		country = country.replaceAll("Cote Ivoire","Ivory Coast");
		country = country.replace("Surinam", "Suriname");
		country = country.replace("Surinamee", "Suriname");

		return country;           
	}
}
