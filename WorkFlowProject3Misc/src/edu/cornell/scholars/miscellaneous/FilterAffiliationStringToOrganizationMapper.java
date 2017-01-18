package edu.cornell.scholars.miscellaneous;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;

public class FilterAffiliationStringToOrganizationMapper {

	private static Map<String, AffiliationModel> affiliation_college_map = null;

	public static void main(String[] args) {
		String AFFILIATION_COLLEGE_MAP_FILE = Configuration.AFFILIATION_STRING_TO_COLLEGE_MAP_FILE;
		affiliation_college_map = getAffiliationCollegeMap(new File(AFFILIATION_COLLEGE_MAP_FILE));
		saveFile(affiliation_college_map, AFFILIATION_COLLEGE_MAP_FILE);
	}


	private static void saveFile(Map<String, AffiliationModel> affiliation_college_map2, String filePath) {
		PrintWriter writer;
		try {
			writer = new PrintWriter (filePath);
			Set<String> keys = affiliation_college_map.keySet();
			for(String key: keys){
				AffiliationModel model = affiliation_college_map.get(key);
				writer.println("\""+model.getWosentry().trim()+"\",\""+ model.getDept().trim()+"\",\""+ model.getUnit()+"\"");
			}
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  

	}


	private static Map<String, AffiliationModel> getAffiliationCollegeMap(File file) {
		Map<String, AffiliationModel> map = new HashMap<String, AffiliationModel>();
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file),',','\"');
			String [] nextLine;	
			while ((nextLine = reader.readNext()) != null) {
				map.put(nextLine[0], new AffiliationModel(nextLine[0], nextLine[1],nextLine[2]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(map.size());
		return map;
	}
}
