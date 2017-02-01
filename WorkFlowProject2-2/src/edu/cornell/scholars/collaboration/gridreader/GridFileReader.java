package edu.cornell.scholars.collaboration.gridreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

import edu.cornell.scholars.config.Configuration;

public class GridFileReader {

	private static final Logger LOGGER = Logger.getLogger(GridFileReader.class.getName()); 
			
	public static void main(String[] args) {
		//<gridOrg, GRID>
		Map<String, GridModel> gridMap = new HashMap<String, GridModel>();
		//<country, GRIDOrgSet>
		Map<String, Set<String>> map = GridFileReader.readGRIDFile(new File(Configuration.SUPPL_FOLDER+"/grid.csv"), gridMap);
	}


	public static Map<String, Set<String>> readGRIDFile(File file, Map<String, GridModel> gridMap) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				lineCount++;
				if(lineCount == 1 || line.trim().length() == 0) continue; //header
				@SuppressWarnings("resource")
				CSVReader reader = new CSVReader(new StringReader(line),',');	
				String[] tokens;
				while ((tokens = reader.readNext()) != null) {
					try {
						String gridId = tokens[0];
						String gridOrganization = tokens[1];
						String gridCity = tokens[2];
						String gridState = tokens[3];
						String gridCountry = tokens[4].toUpperCase();
						GridModel grid = new GridModel(gridId, gridOrganization, gridCity, gridState, gridCountry);
						gridMap.put(gridOrganization+"---"+gridCountry, grid);
						
						if(map.get(gridCountry) != null){
							Set<String> values = map.get(gridCountry);
							values.add(gridOrganization);
						}else{
							Set<String> set = new HashSet<String>();
							set.add(gridOrganization);
							map.put(gridCountry, set);
						}
						lineCount++;
					}catch (ArrayIndexOutOfBoundsException exp) {
						for (String s : tokens) {
							LOGGER.warning("Exception: "+ lineCount+" :"+ s);
						}
						continue;
					}
				}
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
		LOGGER.info("GRID line count:"+lineCount);
		return map;
	}
}
