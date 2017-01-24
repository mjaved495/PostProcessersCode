package edu.cornell.scholars.workflow2;

import edu.cornell.scholars.collaboration.collabharvester.WOSTSVFileReader;
import edu.cornell.scholars.collaboration.gridmapper.WOSDataFileReaderGRIDMapper;
import edu.cornell.scholars.collaboration.gridmapper.WosQueryResultsSetDataParser;
import edu.cornell.scholars.workflow2.MainEntryPoint_WorkFlow2;

public class MainEntryPoint_WorkFlow2 {


	public static void main(String[] args) {
		MainEntryPoint_WorkFlow2 mep = new MainEntryPoint_WorkFlow2();
		mep.runProcess();
	}

	private void runProcess() {

		//Step 1: run the WOS queries (in WorkFlowProject1)
		// Manual process.

		//Step 2: Read the WOS queried data and save it in (WOS_DATA_FILE) csv file.
		WosQueryResultsSetDataParser parser = new WosQueryResultsSetDataParser();
		parser.runProcess();

		//Step 3: Read the WOS_DATA_FILE file and save Affiliation-GRID map
		WOSDataFileReaderGRIDMapper obj = new WOSDataFileReaderGRIDMapper();
		obj.runProcess();
		
		//Step 4: Read the WOS_DATA_FILE file and process and save the Collaboration Wheel and Global Collaboration Process
		System.out.println("\n\n---------- STARTING COLLABORATIN WHEEL AND GLOBAL COLLABORATION PROCESS----------");
		runCollaborationProcess();
		
		System.out.println("\n\n---------- ALL PROCESSES COMPLETED----------");
	}

	private void runCollaborationProcess() {
		WOSTSVFileReader obj = new WOSTSVFileReader();
		obj.runProcess();
	}
}





