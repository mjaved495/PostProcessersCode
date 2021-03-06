package edu.cornell.scholars.workflow21;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.cornell.scholars.collaboration.harvester.CollabHarvesterMainEntryPoint;
import edu.cornell.scholars.config.Configuration;

public class MainEntryPoint_WorkFlow2_1 {

	private static final Logger LOGGER = Logger.getLogger(MainEntryPoint_WorkFlow2_1.class.getName());
	
	public static void main(String[] args) {
		
		try {
			if(args.length > 0){
				init(args[0]);
			}else{
				String propFilePath = "resources/setup.properties";
				init(propFilePath);
			}
			MainEntryPoint_WorkFlow2_1 mep = new MainEntryPoint_WorkFlow2_1();
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
	}


	private void runWOSQueryBuilder() throws IOException {
		CollabHarvesterMainEntryPoint harvester = new CollabHarvesterMainEntryPoint();
		harvester.runProcess();
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
		createFolder(new File(Configuration.QUERY_RESULTSET_FOLDER+"/"+date+"/"+Configuration.WOS_DATA_FOLDER));

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
	
	private static void createFolder(File file) {
		if (!file.exists()) {
			if (file.mkdirs()) {
				LOGGER.info(file.getAbsolutePath()+" folder created!");
			} else {
				LOGGER.throwing("MainEntryPoint_WorkFlow2_1", "createFolder", new Throwable("EXCEPTION: Could not create folder..."));
			}
		}
	}
}
