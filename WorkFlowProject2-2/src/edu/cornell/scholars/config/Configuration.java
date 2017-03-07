package edu.cornell.scholars.config;

public class Configuration {

	public static String date = null;
	
	public static String QUERY_RESULTSET_FOLDER = null;		  //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/QUERY_RESULTS";
	public static String POSTPROCESS_RESULTSET_FOLDER = null; //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/POSTPROCESS_RESULTS";
	public static String SUPPL_FOLDER = 	null;             //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/SUPPL_FOLDER";

	public static String COLLABORATION_FOLDER = "collab";
	public static String COLLAB_INTERNAL_FOLDER = "internal";
	public static String COLLAB_EXTERNAL_FOLDER = "external";
	public static String WOS_DATA_FOLDER = "WOS_FILES";
	
	public static String NETID_2_UNITS_MAP_FILE = "NetId2UnitMap.csv";
	public static String WOS_DATA_FILENAME = "WOSDataFile.csv";
	public static String COUNTRIES_FILE = "countries.tsv";
	public static String USA_STATE_FILE ="usa_state.txt";
	public static String AFF_GRID_MAP = "AffiliationGridMapper.csv";
	public static String GRID_FILENAME = "grid.csv";
	public static String ORG_ORGCODE_MAP_FILE = "org-orgcode-map.csv";
	public static String ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV = "Article2WosPubmedIdMap.csv";
	public static String ARTICLE_2_SUBJECTAREA_FILENAME = "ArticleURI2SubjectAreas.csv";
	public static String PERSON_2_ARTICLE_MAP_FILENAME = "Person2ArticleArticleIdMap.csv";
	
	public static String EXT_COLLABORATIONS_FILE_STATE_JSON = "ExternalCollaborations-State.json";
	public static String EXT_COLLABORATIONS_FILE_COUNTRY_JSON = "ExternalCollaborations-Country.json";

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
