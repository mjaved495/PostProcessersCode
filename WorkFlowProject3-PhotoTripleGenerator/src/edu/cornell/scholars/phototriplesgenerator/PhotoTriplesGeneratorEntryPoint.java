package edu.cornell.scholars.phototriplesgenerator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.config.SetupPropertyValues;

/**
 * Photo Triples and folders creator. 
 * @author mj495
 *
 */
public class PhotoTriplesGeneratorEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(PhotoTriplesGeneratorEntryPoint.class.getName());
	private static String INPUT_FOLDER;
	private static String OUTPUT_FILEPATH;
	private static String OUTPUT_DIR_FOLDER;

	public static void main(String[] args) {
		try {
			if(args.length > 0){
				init(args[0]);
			}else{
				String propFilePath = "resources/setup.properties";
				init(propFilePath);
			}
			PhotoTriplesGeneratorEntryPoint obj = new PhotoTriplesGeneratorEntryPoint();
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runProcess() {
		setLocalDirectories();
		PhotoTriplesGenerator obj = new PhotoTriplesGenerator();
		obj.generateTriples(INPUT_FOLDER, OUTPUT_FILEPATH, OUTPUT_DIR_FOLDER);
	}

	private void setLocalDirectories() {
		// input folder
		INPUT_FOLDER = 	Configuration.SUPPL_FOLDER +"/"+ Configuration.PHOTOS_FOLDER;	
		//output file names
		OUTPUT_FILEPATH = 	Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+Configuration.PHOTOS_FOLDER+"/"+ Configuration.ELEMENTS_PERSON_PHOTO_TRIPLES_FILE;
		OUTPUT_DIR_FOLDER =Configuration.POSTPROCESS_RESULTSET_FOLDER +"/"+ Configuration.date 
				+"/"+Configuration.PHOTOS_FOLDER+"/"+ Configuration.PERSON_PHOTO_DIR_FOLDER;
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
		createFolder(new File(Configuration.POSTPROCESS_RESULTSET_FOLDER+"/"+date+"/"+Configuration.PHOTOS_FOLDER));
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
}
