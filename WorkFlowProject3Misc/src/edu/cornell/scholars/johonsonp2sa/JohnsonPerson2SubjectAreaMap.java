package edu.cornell.scholars.johonsonp2sa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVReader;

public class JohnsonPerson2SubjectAreaMap {

	private final static String WOS_DataFile = "resources/wosJournalClassification.csv";		
	private final static String FOR_DataFile = "resources/forJournalClassification.csv";	
	private Map<String, String> forISSNMap = new HashMap<String, String>(); // <issn, subjectArea>
	private Map<String, String> forEISSNMap = new HashMap<String, String>();// <eissn,subjectArea>
	private Map<String, Set<String>> wosISSNMap =  new HashMap<String, Set<String>>(); //<issn, list of subjectArea>
	private Map<String, Set<String>> wosEISSNMap = new HashMap<String, Set<String>>();// <eissn,list of subjectArea>
	
	private Map<String, String> uriToNameMap = new HashMap<String, String>();
	
	public static void main(String[] args) {
		JohnsonPerson2SubjectAreaMap obj = new JohnsonPerson2SubjectAreaMap();
		obj.process();

	}

	private void process() {
		
		uriToNameMap = readURI2NameMap(new File("resources/URI2NameMap.csv"));
		
		String inputFilePath1 = "resources/personIssnMap.csv";
		Map<String, Set<String>> IssnMap = readMap(inputFilePath1);

		String inputFilePath2 = "resources/personEissnMap.csv";
		Map<String, Set<String>> EissnMap = readMap(inputFilePath2);

		Map<String, Set<String>> mergedMap = new HashMap<String, Set<String>>();
		mergedMap.putAll(IssnMap);
		mergedMap.putAll(EissnMap);
		
		// Read WOS file
		readWOSJournalCSVFile(new File(WOS_DataFile));

		// Read FOR file
		readFORJournalCSVFile(new File(FOR_DataFile));

		Map<String, Set<String>> resultMap = process(mergedMap);
		
		//print(map);
		
		Set<PersonRAData> set = convertToObject(resultMap, uriToNameMap);
		
		saveJSONMap(set, new File("resources/output.json"));
	}

	private Map<String, String> readURI2NameMap(File input) {
		Map<String, String> map =  new HashMap<String, String>();
		Reader in;
		try {
			in = new FileReader(input);
			Iterable<CSVRecord> records = null;
			records = CSVFormat.EXCEL.withDelimiter(',').withQuote('"').parse(in);
			for (CSVRecord record : records) {
				String personURI = record.get(0);
				String name = record.get(1);
				map.put(personURI, name);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Set<PersonRAData> convertToObject(Map<String, Set<String>> resultMap, Map<String, String> uriToNameMap2) {
		Set<PersonRAData> set = new HashSet<PersonRAData>();
		Set<String> personURIs = resultMap.keySet();
		for(String uri: personURIs){
			Set<String> sas = resultMap.get(uri);
			if(sas.size() == 0) continue;
			PersonRAData obj = new PersonRAData();
			String netId = uri.substring(uri.lastIndexOf("/")+1);
			obj.setNetid(netId);
			obj.setName(uriToNameMap2.get(uri));
			obj.setCount(Integer.toString(sas.size()));
			obj.setDept("Johnsons");
			obj.setRa(sas);
			set.add(obj);
		}
		return set;
	}

	private void print(Map<String, Set<String>> map) {
		Set<String> saz = new HashSet<String>();
		Set<String> per = map.keySet();
		for(String p : per){
			System.out.println(p);
			Set<String> subAs = map.get(p);
			for(String s : subAs){
				saz.add(s);
				System.out.println(s);
			}
			System.out.println("\n\n");
		}
		System.out.println(saz.size());
	}

	private Map<String, Set<String>> process(Map<String, Set<String>> mergedMap) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		Set<String> persons = mergedMap.keySet();
		for(String person: persons){
			Set<String> ieissns = mergedMap.get(person);
			Set<String> subjectAreas = new HashSet<String>();
			for(String misc: ieissns){
				String subA = forISSNMap.get(misc);
				String subB = forEISSNMap.get(misc);
				Set<String> subC = wosISSNMap.get(misc);
				Set<String> subD = wosEISSNMap.get(misc);
				if(subA != null){
					subjectAreas.add(subA);
				}else if (subB != null){
					subjectAreas.add(subB);
				}else if (subC != null){
					subjectAreas.addAll(subC);
				}else if (subD != null){
					subjectAreas.addAll(subD);
				}
			}
			map.put(person, subjectAreas);
		}
		return map;
	}

	private Map<String, Set<String>> readMap(String input) {
		Map<String, Set<String>> map =  new HashMap<String, Set<String>>();
		Reader in;
		try {
			in = new FileReader(input);
			Iterable<CSVRecord> records = null;
			records = CSVFormat.EXCEL.withDelimiter(',').withQuote('"').parse(in);
			for (CSVRecord record : records) {
				String personURI = record.get(0);
				String issn = record.get(1);
				String issns[] = issn.split(";;");
				Set<String> values = new HashSet<String>();
				for(String jj : issns){
					values.add(jj.trim());
				}
				map.put(personURI, values);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private void readWOSJournalCSVFile(File file) {
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;

		Set<String> issns = new HashSet<String>();
		Set<String> eissns = new HashSet<String>();

		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				lineCount++;
				if(lineCount == 1 || line.trim().length() == 0) continue; //header
				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					try {
						String issn = tokens[5];
						String eissn = tokens[6];
						String subjectArea = tokens[12];

						if(issns.contains(issn)){
							System.err.println("WOS: "+issn+" issn already exists.");
						}
						if(eissns.contains(issn)){
							System.err.println("WOS: "+eissn+" eissn already exists.");
						}

						if(wosISSNMap.get(issn) != null){
							Set<String> subjectAreas = wosISSNMap.get(issn);
							subjectAreas.add(subjectArea);
						}else{
							Set<String> subjectAreas = new HashSet<String>();
							subjectAreas.add(subjectArea);
							wosISSNMap.put(issn, subjectAreas);
						}
						if(wosEISSNMap.get(eissn) != null){
							Set<String> subjectAreas = wosEISSNMap.get(eissn);
							subjectAreas.add(subjectArea);
						}else{
							Set<String> subjectAreas = new HashSet<String>();
							subjectAreas.add(subjectArea);
							wosEISSNMap.put(eissn, subjectAreas);
						}

						lineCount++;
					}catch (ArrayIndexOutOfBoundsException exp) {
						for (String s : tokens) {
							System.out.println("Exception: "+ lineCount+" :"+ s);
						}
						System.out.println();
						continue;
					}
				}
			}
		}catch (FileNotFoundException e) {
			System.err.println(line);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(line);
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
		System.out.println("WOS line count:"+lineCount);
	}

	private void readFORJournalCSVFile(File file) {	
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;

		Set<String> issns = new HashSet<String>();
		Set<String> eissns = new HashSet<String>();

		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				lineCount++;
				if(lineCount == 1 || line.trim().length() == 0) continue; //header
				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),',','\"');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					try {
						//System.out.println(tokens[4]);

						if(issns.contains(tokens[5].trim())){
							System.err.println("FOR: "+tokens[5]+" issn already exists.");
						}else if(!tokens[5].trim().isEmpty()){
							issns.add(tokens[5].trim());
							forISSNMap.put(tokens[5].trim(), tokens[2].trim()); // <issn,subjectArea>
						}

						if(eissns.contains(tokens[6].trim())){
							System.err.println("FOR: "+tokens[6]+" eissn already exists.");
						}else if(!tokens[6].trim().isEmpty()){
							eissns.add(tokens[6].trim());
							forEISSNMap.put(tokens[6].trim(), tokens[2].trim());  // <eissn,subjectArea>
						}
					}catch (ArrayIndexOutOfBoundsException exp) {
						for (String s : tokens) {
							System.out.println("Exception: "+ lineCount+" :"+ s);
						}
						System.out.println();
						continue;
					}
				}
			}
		}catch (FileNotFoundException e) {
			System.err.println(line);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(line);
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
		System.out.println("FOR line count:"+lineCount);
	}

	private void saveJSONMap(Set<PersonRAData> set, File file) {
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter (file.getAbsolutePath());	
			int count = 0;
			Set<String> ras = new HashSet<String>();
			printWriter.println("var flaredata = { "+ 
					"\"ditems\":[");
			for(PersonRAData entry: set){
				printWriter.println(entry.toJSONString(count++));
				ras.addAll(entry.getRa());
			}
			printWriter.println("],");
			
			printWriter.println("\"themes\":[");
			ArrayList<String> list = new ArrayList<String>(ras);     
			Collections.sort(list);
			for(String l: list){
				printWriter.println(getThemeString(l));
			}	
			printWriter.println("]};");
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
	}
	
	private String getThemeString(String ra) {
		String str = "{"+ 
				"\"type\":\"theme\","+
				"\"name\":\""+ra+"\","+
				"\"description\":\"\","+
				"\"slug\":\""+ra+
				"\"},";		
		return str;
	}

}
