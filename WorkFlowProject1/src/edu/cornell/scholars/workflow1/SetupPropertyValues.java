package edu.cornell.scholars.workflow1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SetupPropertyValues {
	
	InputStream inputStream = null;
	Map<String, String> map = new HashMap<String, String>();
	
	public Map<String, String> getPropValues(String propFilePath) throws IOException {
		Properties prop = new Properties();
		inputStream = new FileInputStream(propFilePath);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFilePath + "' not found in the classpath");
		}
		String queryResultSet = prop.getProperty("QUERY_RESULTSET_FOLDER");
		map.put("QUERY_RESULTSET_FOLDER", queryResultSet);
		String PostprocessResultSet = prop.getProperty("POSTPROCESS_RESULTSET_FOLDER");
		map.put("POSTPROCESS_RESULTSET_FOLDER", PostprocessResultSet);
		String SupplFolder = prop.getProperty("SUPPL_FOLDER");
		map.put("SUPPL_FOLDER", SupplFolder);
		inputStream.close();
		
		return map;
	}
}
