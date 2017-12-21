package edu.cornell.scholars.optingraphbuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.config.SetupPropertyValues;

public class OptInGraphEntryPoint {

	private static final Logger LOGGER = Logger.getLogger( OptInGraphEntryPoint.class.getName() );
	private static String OPTIN_CONTROL_FILE =  null;
	private static String OPTIN_QUERY_FILE =  null;
	
	private static String OPTIN_OUTPUT_AGENT_NT =  null;
	private static String OPTIN_OUTPUT_NT =  null;
	
	public static void main(String[] args) {
		try {
			if(args.length > 0){
				init(args[0]);
			}else{
				String propFilePath = "resources/setup.properties";
				init(propFilePath);
			}
			OptInGraphEntryPoint osp = new OptInGraphEntryPoint();
			osp.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runProcess() {
		setLocalDirectories();
		OptInGraphBuilder obj = new OptInGraphBuilder();
		obj.runProcess(OPTIN_CONTROL_FILE, OPTIN_QUERY_FILE, OPTIN_OUTPUT_AGENT_NT, OPTIN_OUTPUT_NT);
	}

	private void setLocalDirectories() {
		
		// input file
		OPTIN_CONTROL_FILE = Configuration.SUPPL_FOLDER + "/" + Configuration.OPTIN_CONTROL_FILENAME;	
		OPTIN_QUERY_FILE = Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.PERSON_QUERY_FILENAME;	
		
		//output file names
		OPTIN_OUTPUT_AGENT_NT = Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+Configuration.OPTIN_FOLDER+"/"+ Configuration.OPTIN_AGENT_NT;
		OPTIN_OUTPUT_NT = Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+Configuration.OPTIN_FOLDER+"/"+ Configuration.OPTIN_PERSON_NT;
	}

	public static void init(String propFilePath) throws IOException{
		String date = getCurrentDate();
		Configuration.setDate(date);
		generateDirectories(date, propFilePath);
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
				LOGGER.throwing("PersonUriLinkGenerator", "createFolder", new Throwable("EXCEPTION: Could not create folder..."));
			}
		}
	}

	private static void generateDirectories(String date, String propFilePath) throws IOException {
		// SET FILEPATHS
		SetupPropertyValues properties = new SetupPropertyValues();
		Map<String, String> map = properties.getPropValues(propFilePath);
		Configuration.setQUERY_RESULTSET_FOLDER(map.get("QUERY_RESULTSET_FOLDER"));
		Configuration.setPOSTPROCESS_RESULTSET_FOLDER(map.get("POSTPROCESS_RESULTSET_FOLDER"));
		Configuration.setSUPPL_FOLDER(map.get("SUPPL_FOLDER"));

		// CREATE NEW DIRECTORIES
		createFolder(new File(Configuration.POSTPROCESS_RESULTSET_FOLDER+"/"+date+"/"+Configuration.OPTIN_FOLDER));
	}


}
