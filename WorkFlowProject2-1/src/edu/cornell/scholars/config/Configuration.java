package edu.cornell.scholars.config;

public class Configuration {
	
public static String date = null;
	
	public static String QUERY_RESULTSET_FOLDER = null;		  //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/QUERY_RESULTS";
	public static String POSTPROCESS_RESULTSET_FOLDER = null; //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/POSTPROCESS_RESULTS";
	public static String SUPPL_FOLDER = 	null;             //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/SUPPL_FOLDER";

	public static String ARTICLEID_MASTER_FILENAME = "MASTER_ARTICLEID.csv";
	public static String WOS_QUERY_FILENAME = "WOSQueryLineFile.txt";
	public static String ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV = "Article2WosPubmedIdMap.csv";
	public static String COLLABORATION_FOLDER = "collab";
	public static String COLLAB_EXTERNAL_FOLDER = "external";
	public static String WOS_DATA_FOLDER = "wosdata";
	

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
