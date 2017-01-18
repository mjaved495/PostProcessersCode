package edu.cornell.scholars.workflow1;

import java.util.logging.Logger;

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
		System.out.println("\n\n---------- STARTING INFERRED KEYWORD HARVEST PROCESS----------");
		runInferredKeywordProcess();
		
		//Run the Homepage WordCloud Generator Process
		System.out.println("\n\n---------- STARTING HOMEPAGE LEVEL KEYWORD CLOUD GENERATOR PROCESS----------");
		runHomepageKeywordCloudProcess();
		
		//Run the Grants data process
		System.out.println("\n\n---------- STARTING OSP GRANTS DATA PROCESS----------");
		runGrantsDataProcess();
		
		//Run the journal to subject area map process
		System.out.println("\n\n---------- STARTING JOURNAL TO SUBJECT AREA MAP PROCESS----------");
		runJournalSubjectAreaMapProcess();
		
		//Run the WOS query builder process
		System.out.println("\n\n---------- STARTING WOS QUERY BUILDER PROCESS----------");
		runWOSQueryBuilder();
		
		
		System.out.println("\n\n---------- ALL PROCESSES COMPLETED----------");
	}
	
	private void runHomepageKeywordCloudProcess() {
		UniversityLevelKeywordCloudGenerator obj = new UniversityLevelKeywordCloudGenerator();
		obj.runProcess();	
	}

	private void runWOSQueryBuilder(){
		CollabHarvesterMainEntryPoint harvester = new CollabHarvesterMainEntryPoint();
		harvester.runProcess();
	}
	
	private void runJournalSubjectAreaMapProcess() {
		JournalToSubjectAreaMapEntryPoint jrnlep = new JournalToSubjectAreaMapEntryPoint();
		jrnlep.runProcess();
	}

	private void runInferredKeywordProcess() {
		KeywordMinerEntryPoint kmep = new KeywordMinerEntryPoint();
		kmep.runProcess();
	}
	
	private void runGrantsDataProcess() {
		OSPGrantsEntryPoint ospep = new OSPGrantsEntryPoint();
		ospep.runProcess();
	}

}
