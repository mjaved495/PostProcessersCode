package edu.cornell.scholars.workflow1;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.cornell.scholars.collaboration.harvester.CollabHarvesterMainEntryPoint;
import edu.cornell.scholars.journalsubjectareamapper.JournalToSubjectAreaMapEntryPoint;
import edu.cornell.scholars.keywordcloudgenerator.UniversityLevelKeywordCloudGenerator;
import edu.cornell.scholars.keywordminer.KeywordMinerEntryPoint;
import edu.cornell.scholars.ospgrants.OSPGrantsEntryPoint;

public class MainEntryPoint_WorkFlow1 {

	private static final Logger LOGGER = Logger.getLogger( MainEntryPoint_WorkFlow1.class.getName() );
	
	public static void main(String[] args) {
		MainEntryPoint_WorkFlow1 mep = new MainEntryPoint_WorkFlow1();
		mep.runProcess();
	}

	private void runProcess() {
		
		//Run the InferredKeywords Process
		LOGGER.info("\n\n---------- STARTING INFERRED KEYWORD HARVEST PROCESS----------");
		try{
			runInferredKeywordProcess();
		}catch(Exception exp){
			LOGGER.log(Level.WARNING, "\n\n---------ERROR OCCURED:----------", exp);
		}

		//Run the Home page WordCloud Generator Process
		LOGGER.info("\n\n---------- STARTING HOMEPAGE LEVEL KEYWORD CLOUD GENERATOR PROCESS----------");
		try{
			runHomepageKeywordCloudProcess();
		}catch(Exception exp){
			LOGGER.log(Level.WARNING, "\n\n---------ERROR OCCURED:----------", exp);
		}

		//Run the Grants data process
		LOGGER.info("\n\n---------- STARTING OSP GRANTS DATA PROCESS----------");
		try{
			runGrantsDataProcess();
		}catch(Exception exp){
			LOGGER.log(Level.WARNING, "\n\n---------ERROR OCCURED:----------", exp);
		}
		
		//Run the journal to subject area map process
		LOGGER.info("\n\n---------- STARTING JOURNAL TO SUBJECT AREA MAP PROCESS----------");
		try{
			runJournalSubjectAreaMapProcess();
		}catch(Exception exp){
			LOGGER.log(Level.WARNING, "\n\n---------ERROR OCCURED:----------", exp);
		}
		
		//Run the WOS query builder process
		LOGGER.info("\n\n---------- STARTING WOS QUERY BUILDER PROCESS----------");
		try{
			runWOSQueryBuilder();
		}catch(Exception exp){
			LOGGER.log(Level.WARNING, "\n\n---------ERROR OCCURED:----------", exp);
		}

		// SAVE THE FILES (DOWNLOADED FROM WOS) IN MAC-UTF-8 FORMAT.
		// SAVE THE FILES (DOWNLOADED FROM WOS) IN MAC-UTF-8 FORMAT.
		// SAVE THE FILES (DOWNLOADED FROM WOS) IN MAC-UTF-8 FORMAT.
		// SAVE THE FILES (DOWNLOADED FROM WOS) IN MAC-UTF-8 FORMAT.
		
		LOGGER.info("\n\n---------- ALL PROCESSES COMPLETED----------");
	}

	private void runInferredKeywordProcess() throws IOException, ParserConfigurationException, SAXException {
		KeywordMinerEntryPoint kmep = new KeywordMinerEntryPoint();
		kmep.runProcess();
	}
	
	private void runHomepageKeywordCloudProcess() throws IOException, ParserConfigurationException, SAXException {
		UniversityLevelKeywordCloudGenerator obj = new UniversityLevelKeywordCloudGenerator();
		obj.runProcess();	
	}
	
	private void runGrantsDataProcess() {
		OSPGrantsEntryPoint ospep = new OSPGrantsEntryPoint();
		ospep.runProcess();
	}
	
	private void runWOSQueryBuilder() {
		CollabHarvesterMainEntryPoint harvester = new CollabHarvesterMainEntryPoint();
		harvester.runProcess();
	}
	
	private void runJournalSubjectAreaMapProcess() {
		JournalToSubjectAreaMapEntryPoint jrnlep = new JournalToSubjectAreaMapEntryPoint();
		jrnlep.runProcess();
	}

	


}
