package edu.cornell.scholars.config;

public class Configuration {
	
public static String date = null;
	
	public static String QUERY_RESULTSET_FOLDER = null;		  //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/QUERY_RESULTS";
	public static String POSTPROCESS_RESULTSET_FOLDER = null; //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/POSTPROCESS_RESULTS";
	public static String SUPPL_FOLDER = 	null;             //"/Users/mj495/Box Sync/Box-Desktop/GitHub/PostProcesses/SUPPL_FOLDER";

	// FOLDERS
	public static String GRANTS_FOLDER = "grant";
	public static String COLLABORATION_FOLDER = "collab";
	public static String COLLAB_INTERNAL_FOLDER = "internal";
	public static String COLLAB_EXTERNAL_FOLDER = "external";
	public static String INFERRED_KEYWORDS_FOLDER = "inferredkeywords";
	public static String HOMEPAGE_KEYWORD_CLOUD_FOLDER = "kwclouds";
	public static String SUBJECTAREA_FOLDER = "subjectarea";
	public static String SCOPUS_FOLDER = "scopus";
	
	// GENERAL
	public static String ALL_KEYWORDS_FILENAME = "AllFreeTextKeywords.psv";
	public static String ALL_MESHTERMS_FILENAME = "AllMeshTerms.xml";
	public static String ARTICLE_2_KEYWORDSET_MAP_FILENAME = "Article2KeywordSetMap.psv";
	public static String ARTICLE_2_MESHTERM_MAP_FILENAME = "Article2MeSHTermsMap.psv";
	
	
	// ARTICLE KEYWORD MINER
	public static String ARTICLE_2_TITLE_ABSTRACT_MAP_FILENAME = "Article2TitleAbstractMap.xml";
	public static String ARTICLEID_MASTER_KEYWORDMINER_FILENAME = 	"MASTER_KeywordMiner_ARTICLEID.csv";
	public static String INF_KEYWORDS_CSV = "ArticleMapWithMinedKeywordsAndMeSH.csv";
	public static String INF_KEYWORDS_NT =  "ArticleMapWithMinedKeywordsAndMeSH.nt";
	public static String ARTICLE_2_SCOPUS_ABSTRACT_MAP_FILENAME = "Article2ScopusAbstractMap.xml";
	
	// HOMEPAGE KEYWORD CLOUD GENERATOR
	public static String ARTICLE_2_PERSON_MAP_FILENAME = "Article2PersonMap.csv";
	public static String ARTICLE_2_INFERREDKEYWORD_FILENAME = "Article2InferredKeywordSetMap.psv";
	public static String HOMEPAGE_KEYWORD_CLOUD_FILENAME = "HomepageKWCloud.json";
	
	
	// GRANTS DATA
	public static String OSP_AWARDS_FILENAME = "Awards.xml";
	public static String OSP_INV_FILENAME = "Investigators.xml";
	public static String PERSON_2_DEPT_UNIT_MAP_FILENAME = "Person2DepartmentUnitMap.psv";
	public static String OSP_ADMNDEPT_FILENAME ="GrantAdministringDepartmentMapper.csv";
	public static String ALL_GRANTS_FILENAME = "AllGrants.csv";
	public static String OSP_GRANT_TXT = "AwdInv-all.txt";
	public static String ALL_ORGANIZATION_MAP_FILENAME = "AllOrganizationsMap.psv";
	public static String OSP_GRANT_NT = "AwdInv-all.nt";
	
	
	// JOURNAL SUBJECT AREA
	public static String JOURNAL_2_ISSN_EISSN_SUBJECTAREA_MAP_FILENAME = "Journal2IssnEissnSubjectAreaMap.psv";
	public static String JOURNAL_MASTER_FILENAME = 		"MASTER_JOURNALID.csv";
	public static String ALL_SUBJECTAREAS_FILENAME = "AllSubjectAreas.psv";
	public static String WOS_JOURNAL_CLSFCN_FILENAME ="JournalClassification-WOS.csv";
	public static String FOR_JOURNAL_CLSFCN_FILENAME ="JournalClassification-FOR.csv";
	public static String JOURNAL_2_SUBJECTAREA_CSV = "Journal2SubjectArea.csv";
	public static String JOURNAL_2_SUBJECTAREA_NT = "Journal2SubjectArea.nt";
	
	
	// INTERNAL COLLABORATIONS
	public static String ORG_ORGCODE_MAP_FILE = "org-orgcode-map.csv";
	
	
	// GRANT KEYWORD MINER
	public static String GRANT_2_TITLE_ABSTRACT_MAP_FILENAME = "Grant2TitleAbstractMap.xml";
	public static String GRANTID_MASTER_KEYWORDMINER_FILENAME = "MASTER_KeywordMiner_GRANTID.csv";
	public static String GRANTS_INF_KEYWORDS_CSV = "GrantMapWithMinedKeywordsAndMeSH.csv";
	public static String GRANTS_INF_KEYWORDS_NT =  "GrantMapWithMinedKeywordsAndMeSH.nt";
	
	//SCOPUS AUTHOR KEYWORDS
	public static String SCOPUS_FREETEXTKEYWORDS_OUTPUT_FILE = "scopusFreeTextKeywords.nt";
	public static String SCOPUS_ARTICLEID_MASTER_KEYWORDMINER_FILENAME = "MASTER_KeywordMiner_ARTICLEID_SCOPUS.csv";
	public static String SCOPUS_ARTICLEID_2_FREETEXTKEYWORDS = "scopusAuthorKeywords.csv";
	
	
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
