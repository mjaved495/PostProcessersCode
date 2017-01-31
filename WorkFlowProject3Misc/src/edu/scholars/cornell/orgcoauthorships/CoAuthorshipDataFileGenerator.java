package edu.scholars.cornell.orgcoauthorships;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CoAuthorshipDataFileGenerator {

	public static void main(String[] args) {
		CoAuthorshipDataFileGenerator obj = new CoAuthorshipDataFileGenerator();
		List<CoauthData> data = obj.readFile("resources/CoauthorshipsInBME.csv");
		List<CoauthData> rdata = removeDuplicates(data);
		generateFiles(rdata);
	}

	private static void generateFiles(List<CoauthData> rdata) {
		List<Node> nodes = generateNodeFileData(rdata);
		try {
			saveNodesInJSONFile(nodes, "resources/nodesBME.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			saveCoauthDataInJSONFile(rdata, "resources/coauthBME.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void saveCoauthDataInJSONFile(List<CoauthData> data, String filePath) 
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
			mapper.writeValue(new File(filePath), data);
			jsonInString = mapper.writeValueAsString(data);
	}

	private static void saveNodesInJSONFile(List<Node> nodes, String filePath) 
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
			mapper.writeValue(new File(filePath), nodes);
			jsonInString = mapper.writeValueAsString(nodes);
			//System.out.println(jsonInString);
	}

	private static List<Node> generateNodeFileData(List<CoauthData> rdata) {
		List<Node>  n = new ArrayList<Node>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(CoauthData d: rdata){
			String s = d.getSource();
			String t = d.getTarget();
			
			if(map.get(s) == null){
				map.put(s, 1);
			}else{
				map.put(s, map.get(s)+1);
			}
			if(map.get(t) == null){
				map.put(t, 1);
			}else{
				map.put(t, map.get(t)+1);
			}
		}
		System.out.println("Map size: "+ map.size());
		Set<String> keys = map.keySet();
		for(String key: keys){
			Integer i = map.get(key);
			n.add(new Node(key, i));
		}
		return n;
	}

	private static List<CoauthData> removeDuplicates(List<CoauthData> data) {
		List<CoauthData> rd = new ArrayList<CoauthData>();
		Set<String> set = new HashSet<String>();
		for(CoauthData row: data){
			String str1 = row.getSource()+","+row.getTarget()+","+row.getArticleURI();
			if(!set.contains(str1)){
				rd.add(row);
				String str2 = row.getTarget()+","+row.getSource()+","+row.getArticleURI();
				set.add(str1);
				set.add(str2);
			}else{
				//System.out.println(str1);
			}
		}
		System.out.println("No duplicated: "+ rd.size());
		return rd;
	}

	private List<CoauthData> readFile(String input) {
		List<CoauthData> data = new ArrayList<CoauthData>();              
		Reader in;
		try {
			in = new FileReader(input);
			Iterable<CSVRecord> records = null;
			records = CSVFormat.EXCEL.withDelimiter(',').withQuote('"').parse(in);
			for (CSVRecord record : records) {
				String s = record.get(0);
				s = s.substring(s.lastIndexOf("/")+1);
				String t = record.get(1);
				t = t.substring(t.lastIndexOf("/")+1);
				String uri = record.get(2);
				CoauthData d = new CoauthData(s,t,uri);
				data.add(d);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File row size: "+ data.size());
		return data;
	}

}
