package edu.cornell.scholars.config;

public class Configuration {

	
	public static String date = "20161214";
	
	public static String QUERY_RESULTSET_FOLDER = "/Users/mj495/Box Sync/Box-Desktop/VIVO-Scholar/PhaseIII-Launch/QUERY_FOLDER/QUERY_RESULTS";
	public static String POSTPROCESS_RESULTSET_FOLDER = "/Users/mj495/Box Sync/Box-Desktop/VIVO-Scholar/PhaseIII-Launch/QUERY_FOLDER/POSTPROCESS_RESULTS";
	public static String SUPPL_FOLDER = "/Users/mj495/Box Sync/Box-Desktop/VIVO-Scholar/PhaseIII-Launch/QUERY_FOLDER/SUPPL_FOLDER";
	public static String WOS_DATA_FOLDER = QUERY_RESULTSET_FOLDER + "/"+ date + "/"+ "wosdata";
	
	public static String ALL_KEYWORDS_FILENAME = "AllFreeTextKeywords.psv";
	public static String ALL_MESHTERMS_FILENAME = "AllMeshTerms.psv";
	public static String ALL_ORGANIZATION_MAP_FILENAME = "AllOrganizationsMap.psv";
	public static String ALL_SUBJECTAREAS_FILENAME = "AllSubjectAreas.psv";
	public static String ARTICLE_2_KEYWORDSET_MAP_FILENAME = "Article2KeywordSetMap.psv";
	public static String ARTICLE_2_MESHTERM_MAP_FILENAME = "Article2MeSHTermsMap.psv";
	public static String ARTICLE_2_TITLE_ABSTRACT_MAP_FILENAME = "Article2TitleAbstractMap.psv";
	public static String JOURNAL_2_ISSN_EISSN_SUBJECTAREA_MAP_FILENAME = "Journal2IssnEissnSubjectAreaMap.psv";
	public static String PERSON_2_DEPT_UNIT_MAP_FILENAME = "Person2DepartmentUnitMap.psv";
	
	public static String OSP_AWARDS_FILENAME = "Awards.xml";
	public static String OSP_INV_FILENAME = "Investigators.xml";
	public static String OSP_ADMNDEPT_FILENAME ="AdministringDepartmentMapper.csv";
	
	public static String FOR_JOURNAL_CLSFCN_FILENAME ="forJournalClassification.csv";
	public static String WOS_JOURNAL_CLSFCN_FILENAME ="wosJournalClassification.csv";
	
	public static String PERSON_2_ARTICLE_MAP_FILENAME = "Person2ArticleArticleIdMap.csv";
	public static String ARTICLEID_MASTER_FILENAME = "ARTICLEID_MASTER.csv";
	public static String WOS_QUERY_FILENAME = "WOSQueryLineFile.txt";
	public static String WOS_DATA_FILENAME = "WOSDataFile.csv";
	public static String WOS_DATA_FILE = WOS_DATA_FOLDER +"/"+WOS_DATA_FILENAME;
	
	public static String COUNTRIES_LIST_FILENAME = "countries.tsv";
	public static String COUNTRIES_FILE = SUPPL_FOLDER +"/"+COUNTRIES_LIST_FILENAME;
	public static String US_STATE_LIST_FILENAME = "usa_state.txt";
	public static String USA_STATE_FILE = SUPPL_FOLDER +"/"+US_STATE_LIST_FILENAME;
	
	public static String AFF_GRID_MAP_FILENAME = "AffiliationGridMapper.csv";
	public static String AFF_GRID_MAP = SUPPL_FOLDER +"/"+ AFF_GRID_MAP_FILENAME;
	public static String GRID_FILENAME = "grid.csv";
	
	public static String AFFILIATION_STRING_TO_COLLEGE_MAP_FILE = SUPPL_FOLDER +"/"+"CornellAffiliationOrganization.csv";
	public static String ORG_ORGCODE_MAP_FILE = SUPPL_FOLDER +"/"+"org-orgcode-map.csv";
	public static String WOS_PERSON_NETID_MASTER_FILE = SUPPL_FOLDER                   + "/" + "person_netId_master.csv";
	public static String WOS_PERSON_NETID_CURRENT_FILE = QUERY_RESULTSET_FOLDER        + "/" + date + "/"                 + "person_netId_current.csv";
	public static String ARTICLE_2_WOS_PUBMED_ID_MAP_FILE_CSV = QUERY_RESULTSET_FOLDER + "/" + date + "/"                 + "Article2WosPubmedIdMap.csv";
	
	public static String INT_COLLABORATIONS_FILE_CSV  = POSTPROCESS_RESULTSET_FOLDER   + "/" + date + "/collab/internal/" + "InternalCollaborations.csv";
	public static String EXT_COLLABORATIONS_FILE_CSV  = POSTPROCESS_RESULTSET_FOLDER   + "/" + date + "/collab/external/" + "ExternalCollaborations.csv";
	public static String EXT_COLLABORATIONS_FILE_JSON = POSTPROCESS_RESULTSET_FOLDER   + "/" + date + "/collab/external/" + "ExternalCollaborations.json";
	public static String EXT_COLLABORATIONS_COUNT_CSV = POSTPROCESS_RESULTSET_FOLDER   + "/" + date + "/collab/external/" + "ExternalCollaborationsCount.csv";
	public static String COLLABORATIONS_FILEPATH_JSON = POSTPROCESS_RESULTSET_FOLDER   + "/" + date + "/collab/internal/";
	public static String NOTFOUND_AFFILIATION_SET_FILE = POSTPROCESS_RESULTSET_FOLDER  + "/" + date + "/collab/internal/" + "notFoundAffiliationList.csv";
	
	
	
	
}
