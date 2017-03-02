package edu.cornell.scholars.config;

public class Configuration {
	
public static String date = null;
	
	public static String QUERY_RESULTSET_FOLDER = null;		  //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/QUERY_RESULTS";
	public static String POSTPROCESS_RESULTSET_FOLDER = null; //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/POSTPROCESS_RESULTS";
	public static String SUPPL_FOLDER = 	null;             //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/SUPPL_FOLDER";

	public static String HOMEPAGE_KEYWORD_CLOUD_FOLDER = "kwclouds";
	public static String GRANTS_FOLDER = "grant";
	public static String COLLABORATION_FOLDER = "collab";
	public static String COLLAB_INTERNAL_FOLDER = "internal";
	public static String COLLAB_EXTERNAL_FOLDER = "external";
	public static String INFERRED_KEYWORDS_FOLDER = "inferredkeywords";
	public static String SUBJECTAREA_FOLDER = "subjectarea";
	public static String WOS_DATA_FOLDER = "wosdata";
	public static String COLLAB_WHEEL_DATA_FOLDER = "collabwheel";
	
	public static String ALL_KEYWORDS_FILENAME = "AllFreeTextKeywords.psv";
	public static String ALL_MESHTERMS_FILENAME = "AllMeshTerms.xml";
	public static String ALL_ORGANIZATION_MAP_FILENAME = "AllOrganizationsMap.psv";
	public static String ALL_SUBJECTAREAS_FILENAME = "AllSubjectAreas.psv";
	public static String ARTICLE_2_KEYWORDSET_MAP_FILENAME = "Article2KeywordSetMap.psv";
	public static String ARTICLE_2_MESHTERM_MAP_FILENAME = "Article2MeSHTermsMap.psv";
	public static String ARTICLE_2_TITLE_ABSTRACT_MAP_FILENAME = "Article2TitleAbstractMap.psv";
	public static String JOURNAL_2_ISSN_EISSN_SUBJECTAREA_MAP_FILENAME = "Journal2IssnEissnSubjectAreaMap.psv";
	public static String PERSON_2_DEPT_UNIT_MAP_FILENAME = "Person2DepartmentUnitMap.psv";
	
	public static String JOURNAL_MASTER_FILENAME = 		"MASTER_JOURNALID.csv";
	public static String ARTICLEID_MASTER_FILENAME = 	"MASTER_ARTICLEID.csv";
	public static String ARTICLEID_MASTER_KEYWORDMINER_FILENAME = 	"MASTER_KeywordMiner_ARTICLEID.csv";
	public static String GRANTID_MASTER_KEYWORDMINER_FILENAME = 	"MASTER_KeywordMiner_GRANTID.csv";
	
	public static String GRANT_2_TITLE_ABSTRACT_MAP_FILENAME = "Grant2TitleAbstractMap.xml";
	public static String ALL_GRANTS_FILENAME = "AllGrants.csv";
	public static String OSP_AWARDS_FILENAME = "Awards.xml";
	public static String OSP_INV_FILENAME = "Investigators.xml";
	public static String OSP_ADMNDEPT_FILENAME ="AdministringDepartmentMapper.csv";
	
	public static String FOR_JOURNAL_CLSFCN_FILENAME ="JournalClassification-FOR.csv";
	public static String WOS_JOURNAL_CLSFCN_FILENAME ="JournalClassification-WOS.csv";
	
	public static String PERSON_2_ARTICLE_MAP_FILENAME = "Person2ArticleArticleIdMap.csv";
	
	public static String WOS_QUERY_FILENAME = "WOSQueryLineFile.txt";
	public static String WOS_DATA_FILENAME = "WOSDataFile.csv";
	
	public static String COUNTRIES_LIST_FILENAME = "countries.tsv";
	public static String COUNTRIES_FILE = COUNTRIES_LIST_FILENAME;
	public static String US_STATE_LIST_FILENAME = "usa_state.txt";
	public static String USA_STATE_FILE = US_STATE_LIST_FILENAME;
	
	public static String AFF_GRID_MAP_FILENAME = "AffiliationGridMapper.csv";
	public static String AFF_GRID_MAP = AFF_GRID_MAP_FILENAME;
	public static String GRID_FILENAME = "grid.csv";
	
	public static String AFFILIATION_STRING_TO_COLLEGE_MAP_FILE = "CornellAffiliationOrganization.csv";
	public static String ORG_ORGCODE_MAP_FILE = "org-orgcode-map.csv";
	public static String WOS_PERSON_NETID_MASTER_FILE = "person_netId_master.csv";
	public static String WOS_PERSON_NETID_CURRENT_FILE = "person_netId_current.csv";
	public static String ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV = "Article2WosPubmedIdMap.csv";
	
	public static String INT_COLLABORATIONS_FILE_CSV  = "InternalCollaborations.csv";
	public static String EXT_COLLABORATIONS_FILE_CSV  = "ExternalCollaborations.csv";
	public static String EXT_COLLABORATIONS_FILE_JSON = "ExternalCollaborations.json";
	public static String EXT_COLLABORATIONS_COUNT_CSV = "ExternalCollaborationsCount.csv";
	public static String NOTFOUND_AFFILIATION_SET_FILE = "notFoundAffiliationList.csv";
	public static String INTERDEPT_EN_COLLAB_QUERY_RESULT_FILENAME_CSV = "interdept-en.csv";
	public static String INTERDEPT_EN_COLLAB_QUERY_RESULT_FILENAME_JSON = "fake_collab_data_interdept_viz.json";
	public static String CROSSUNIT_EN_COLLAB_QUERY_RESULT_FILENAME_CSV = "crossunit-en.csv";
	public static String CROSSUNIT_EN_COLLAB_QUERY_RESULT_FILENAME_JSON = "fake_collab_data_intercollege_viz.json";
	
	
	public static String ARTICLE_2_INFERREDKEYWORD_FILENAME = "Article2InferredKeywordSetMap.psv";
	public static String ARTICLE_2_PERSON_MAP_FILENAME = "Article2PersonMap.csv";
	public static String HOMEPAGE_KEYWORD_CLOUD = "HomepageKWCloud.json";
	
	public static String ARTICLE_2_PERSON_MAP_ENG_FILENAME = "Article2PersonMapENG.csv";
	public static String ARTICLE_2_KEYWORDSET_MAP_ENG_FILENAME = "Article2KeywordSetMapENG.csv";
	public static String ENG_KEYWORD_CLOUD = "ENGKWCloud.json";
	
	public static String JOURNAL_2_SUBJECTAREA_CSV = "Journal2SubjectArea.csv";
	public static String JOURNAL_2_SUBJECTAREA_NT = "Journal2SubjectArea.nt";
	
	public static String INF_KEYWORDS_CSV = "ArticleMapWithMinedKeywordsAndMeSH.csv";
	public static String INF_KEYWORDS_NT =  "ArticleMapWithMinedKeywordsAndMeSH.nt";
	
	public static String GRANTS_INF_KEYWORDS_CSV = "GrantMapWithMinedKeywordsAndMeSH.csv";
	public static String GRANTS_INF_KEYWORDS_NT =  "GrantMapWithMinedKeywordsAndMeSH.nt";
	public static String OSP_GRANT_TXT = "AwdInv-all.txt";
	public static String OSP_GRANT_NT = "AwdInv-all.nt";
		

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
