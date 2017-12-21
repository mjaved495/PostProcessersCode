package edu.cornell.scholars.config;

public class Configuration {
	
public static String date = null;
	
	public static String QUERY_RESULTSET_FOLDER = null;		  //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/QUERY_RESULTS";
	public static String POSTPROCESS_RESULTSET_FOLDER = null; //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/POSTPROCESS_RESULTS";
	public static String SUPPL_FOLDER = 	null;             //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/SUPPL_FOLDER";

	// FOLDERS
	public static String GRANTS_FOLDER = "grant";
	
	// GRANTS DATA
	public static String OSP_AWARDS_FILENAME = "Awards.xml";
	public static String OSP_INV_FILENAME = "Investigators.xml";
	public static String PERSON_2_DEPT_UNIT_MAP_FILENAME = "Person2DepartmentUnitMap.psv";
	public static String OSP_ADMNDEPT_FILENAME ="GrantAdministringDepartmentMapper.csv";
	public static String ALL_GRANTS_FILENAME = "AllGrants.csv";
	public static String OSP_GRANT_TXT = "AwdInv-all.txt";
	public static String ALL_ORGANIZATION_MAP_FILENAME = "AllOrganizationsMap.psv";
	public static String OSP_GRANT_NT = "AwdInv-all.nt";
	
	
	// GRANT KEYWORD MINER
	public static String GRANT_2_TITLE_ABSTRACT_MAP_FILENAME = "Grant2TitleAbstractMap.xml";
	public static String GRANTID_MASTER_KEYWORDMINER_FILENAME = "MASTER_KeywordMiner_GRANTID.csv";
	public static String GRANTS_INF_KEYWORDS_CSV = "GrantMapWithMinedKeywordsAndMeSH.csv";
	public static String GRANTS_INF_KEYWORDS_NT =  "GrantMapWithMinedKeywordsAndMeSH.nt";
	
	

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
