package edu.cornell.scholars.personweblinktriples;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.config.SetupPropertyValues;


/**
 * Personal URL data generator Entry Point
 * @author mj495
 *
 */
public class PersonUriLinkGeneratorCore {

	private static final Logger LOGGER = Logger.getLogger(PersonUriLinkGeneratorCore.class.getName());
	private static String INPUT_FILEPATH;
	private static String OUTPUT_FILEPATH;
	
	public static void main(String[] args) {
		try {
			if(args.length > 0){
				init(args[0]);
			}else{
				String propFilePath = "resources/setup.properties";
				init(propFilePath);
			}
			PersonUriLinkGeneratorCore obj = new PersonUriLinkGeneratorCore();
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void init(String propFilePath) throws IOException{
		String date = getCurrentDate();
		Configuration.setDate(date);
		generateDirectories(date, propFilePath);
	}

	private static void generateDirectories(String date, String propFilePath) throws IOException {
		// SET FILEPATHS
		SetupPropertyValues properties = new SetupPropertyValues();
		Map<String, String> map = properties.getPropValues(propFilePath);
		Configuration.setQUERY_RESULTSET_FOLDER(map.get("QUERY_RESULTSET_FOLDER"));
		Configuration.setPOSTPROCESS_RESULTSET_FOLDER(map.get("POSTPROCESS_RESULTSET_FOLDER"));
		Configuration.setSUPPL_FOLDER(map.get("SUPPL_FOLDER"));

		// CREATE NEW DIRECTORIES
		createFolder(new File(Configuration.POSTPROCESS_RESULTSET_FOLDER+"/"+date+"/"+Configuration.WEBLINK_FOLDER));
	}

	private void runProcess() {
		setLocalDirectories();
		PersonUriLinkGenerator obj = new PersonUriLinkGenerator();
		obj.runProcess(INPUT_FILEPATH, OUTPUT_FILEPATH);
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

	private void setLocalDirectories() {
		// input file
		INPUT_FILEPATH = 	Configuration.QUERY_RESULTSET_FOLDER +"/"+ Configuration.date +"/"+
				Configuration.ELEMENTS_PERSON_WEBLINK_INPUT_FILE;	
		//output file names
		OUTPUT_FILEPATH = 	Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+Configuration.WEBLINK_FOLDER+"/"+ Configuration.ELEMENTS_PERSON_WEBLINK_OUTPUT_FILE;
	}

}
