package edu.cornell.scholars.config;

public class Configuration {

	
	public static String date = "01172017";
	
	
	public static String QUERY_RESULTSET_FOLDER = 		"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/QUERY_RESULTS";
	public static String POSTPROCESS_RESULTSET_FOLDER = "/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/POSTPROCESS_RESULTS";
	public static String SUPPL_FOLDER = 				"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/SUPPL_FOLDER";
	public static String WOS_DATA_FOLDER = QUERY_RESULTSET_FOLDER + "/"+ date + "/"+ "wosdata";
	
	public static String ALL_KEYWORDS_FILENAME = "AllFreeTextKeywords.psv";
	public static String ALL_MESHTERMS_FILENAME = "AllMeshTerms.xml";
	public static String ALL_ORGANIZATION_MAP_FILENAME = "AllOrganizationsMap.psv";
	public static String ALL_SUBJECTAREAS_FILENAME = "AllSubjectAreas.psv";
	public static String ARTICLE_2_KEYWORDSET_MAP_FILENAME = "Article2KeywordSetMap.psv";
	public static String ARTICLE_2_MESHTERM_MAP_FILENAME = "Article2MeSHTermsMap.psv";
	public static String ARTICLE_2_TITLE_ABSTRACT_MAP_FILENAME = "Article2TitleAbstractMap.psv";
	public static String JOURNAL_2_ISSN_EISSN_SUBJECTAREA_MAP_FILENAME = "Journal2IssnEissnSubjectAreaMap.psv";
	public static String PERSON_2_DEPT_UNIT_MAP_FILENAME = "Person2DepartmentUnitMap.psv";
	
	public static String HOMEPAGE_KEYWORD_CLOUD_FOLDER = "kwclouds";
	public static String GRANTS_FOLDER = "grant";
	public static String COLLABORATION_FOLDER = "collab";
	public static String INFERRED_KEYWORDS_FOLDER = "inferredkeywords";
	public static String SUBJECTAREA_FOLDER = "subjectarea";
	
	
	public static String ARTICLE_2_INFERREDKEYWORD_FILENAME = "Article2InferredKeywordSetMap.psv";
	public static String ARTICLE_2_PERSON_MAP_FILENAME = "Article2PersonMap.csv";
	public static String HOMEPAGE_KEYWORD_CLOUD = "HomepageKWCloud.json";
	
	public static String JOURNAL_2_SUBJECTAREA_CSV = "Journal2SubjectArea.csv";
	public static String JOURNAL_2_SUBJECTAREA_NT = "Journal2SubjectArea.nt";
	
	public static String INF_KEYWORDS_CSV = POSTPROCESS_RESULTSET_FOLDER +"/"+ date 
			+"/"+ INFERRED_KEYWORDS_FOLDER +"/"+ "ArticleMapWithMinedKeywordsAndMeSH.csv";
	public static String INF_KEYWORDS_NT =  POSTPROCESS_RESULTSET_FOLDER +"/"+ date 
			+"/"+ INFERRED_KEYWORDS_FOLDER +"/"+ "ArticleMapWithMinedKeywordsAndMeSH.nt";
	
	public static String ALL_GRANTS_FILENAME = "AllGrants.csv";
	public static String OSP_AWARDS_FILENAME = "Awards.xml";
	public static String OSP_INV_FILENAME = "Investigators.xml";
	public static String OSP_ADMNDEPT_FILENAME ="AdministringDepartmentMapper.csv";
	public static String OSP_GRANT_TXT = POSTPROCESS_RESULTSET_FOLDER + "/" + date +"/"+ GRANTS_FOLDER +"/"+ "AwdInv-all.txt";
	public static String OSP_GRANT_NT = POSTPROCESS_RESULTSET_FOLDER + "/" + date  +"/"+ GRANTS_FOLDER +"/"+ "AwdInv-all.nt";
	
	public static String FOR_JOURNAL_CLSFCN_FILENAME ="forJournalClassification.csv";
	public static String WOS_JOURNAL_CLSFCN_FILENAME ="wosJournalClassification.csv";
	
	public static String ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV = QUERY_RESULTSET_FOLDER + "/" + date + "/" + "Article2WosPubmedIdMap.csv";
	public static String ARTICLEID_MASTER_FILENAME = "ARTICLEID_MASTER.csv";  // it will be nice if master file also have added data column as well
	public static String PERSON_2_ARTICLE_MAP_FILENAME = "Person2ArticleArticleIdMap.csv";
	public static String WOS_QUERY_FILENAME = "WOSQueryLineFile.txt";
	public static String WOS_DATA_FILENAME = "WOSDataFile.csv";
	public static String WOS_DATA_FILE = WOS_DATA_FOLDER +"/"+WOS_DATA_FILENAME;
	
	public static String COUNTRIES_LIST_FILENAME = "countries.tsv";
	public static String US_STATE_LIST_FILENAME = "usa_state.txt";
	public static String COUNTRIES_FILE = SUPPL_FOLDER +"/"+COUNTRIES_LIST_FILENAME;
	public static String AFF_GRID_MAP_FILENAME = "AffiliationGridMapper.csv";
	public static String AFF_GRID_MAP = SUPPL_FOLDER +"/"+ AFF_GRID_MAP_FILENAME;
	public static String GRID_FILENAME = "grid.csv";
	public static String USA_STATE_FILE = SUPPL_FOLDER +"/"+US_STATE_LIST_FILENAME;
	
}
