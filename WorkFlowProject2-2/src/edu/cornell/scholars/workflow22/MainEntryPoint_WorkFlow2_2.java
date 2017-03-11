package edu.cornell.scholars.workflow22;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import edu.cornell.scholars.collaboration.globalcollabharvester.GlobalCollaborationHarvesterEntryPoint;
import edu.cornell.scholars.collaboration.gridmapper.WOSDataFileReaderGRIDMapper;
import edu.cornell.scholars.collaboration.gridmapper.WosQueryResultsSetDataParser;
import edu.cornell.scholars.config.Configuration;

public class MainEntryPoint_WorkFlow2_2 {

	private static final Logger LOGGER = Logger.getLogger( MainEntryPoint_WorkFlow2_2.class.getName() );

	public static void main(String[] args) {
		try {
			if(args.length > 0){
				init(args[0]);
			}else{
				String propFilePath = "resources/setup.properties";
				init(propFilePath);
			}
			MainEntryPoint_WorkFlow2_2 mep = new MainEntryPoint_WorkFlow2_2();
			mep.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void init(String propFilePath) throws IOException{
		String date = getCurrentDate();
		Configuration.setDate(date);
		generateDirectories(date, propFilePath);
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
		LOGGER.info("---------- STARTING COLLABORATIN WHEEL AND GLOBAL COLLABORATION PROCESS----------");
		runCollaborationProcess();

		LOGGER.info("---------- ALL PROCESSES COMPLETED----------");
	}

	private void runCollaborationProcess() {
		try {
			GlobalCollaborationHarvesterEntryPoint obj = new GlobalCollaborationHarvesterEntryPoint();
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getCurrentDate() {
		String date = null;
		Date now = new Date();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("E, y-M-d 'at' h:m:s a z");
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		date = dateFormatter.format(now);
		LOGGER.info(date);
		return date;
	}

	private static void generateDirectories(String date, String propFilePath) throws IOException {

		// SET FILEPATHS
		SetupPropertyValues properties = new SetupPropertyValues();
		Map<String, String> map = properties.getPropValues(propFilePath);
		Configuration.setQUERY_RESULTSET_FOLDER(map.get("QUERY_RESULTSET_FOLDER"));
		Configuration.setPOSTPROCESS_RESULTSET_FOLDER(map.get("POSTPROCESS_RESULTSET_FOLDER"));
		Configuration.setSUPPL_FOLDER(map.get("SUPPL_FOLDER"));

		// CREATE NEW DIRECTORIES
		createFolder(new File(Configuration.POSTPROCESS_RESULTSET_FOLDER+"/"+date+"/"+Configuration.COLLABORATION_FOLDER+"/"+Configuration.COLLAB_EXTERNAL_FOLDER));
		createFolder(new File(Configuration.SUPPL_FOLDER+"/"+Configuration.WOS_DATA_FOLDER));

	}

	private static void createFolder(File file) {
		if (!file.exists()) {
			if (file.mkdirs()) {
				LOGGER.info(file.getAbsolutePath()+" folder created!");
			} else {
				LOGGER.throwing("MainEntryPoint_WorkFlow1", "createFolder", new Throwable("EXCEPTION: Could not create folder..."));
			}
		}
	}
}





