package edu.cornell.scholars.config;

public class Configuration {
	
public static String date = null;
	
	public static String QUERY_RESULTSET_FOLDER = null;		  //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/QUERY_RESULTS";
	public static String POSTPROCESS_RESULTSET_FOLDER = null; //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/POSTPROCESS_RESULTS";
	public static String SUPPL_FOLDER = 	null;             //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/SUPPL_FOLDER";

	// FOLDERS
	public static String WEBLINK_FOLDER = "weblinks";
	
	// FILES
	public static String ELEMENTS_PERSON_WEBLINK_INPUT_FILE =  "weblinks.xml";
	public static String ELEMENTS_PERSON_WEBLINK_OUTPUT_FILE = "weblinks.nt";
	
	public static void setDate(String date) {
		Configuration.date = date;
	}
	public static void setPOSTPROCESS_RESULTSET_FOLDER(String pOSTPROCESS_RESULTSET_FOLDER) {
		POSTPROCESS_RESULTSET_FOLDER = pOSTPROCESS_RESULTSET_FOLDER;
	}
	public static void setQUERY_RESULTSET_FOLDER(String qUERY_RESULTSET_FOLDER) {
		QUERY_RESULTSET_FOLDER = qUERY_RESULTSET_FOLDER;
	}
	public static void setSUPPL_FOLDER(String sUPPL_FOLDER) {
		SUPPL_FOLDER = sUPPL_FOLDER;
	}
}
