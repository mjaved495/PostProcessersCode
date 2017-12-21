package edu.cornell.scholars.ospgrants;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.config.SetupPropertyValues;


public class OSPGrantsEntryPoint {


	private static final Logger LOGGER = Logger.getLogger( OSPGrantsEntryPoint.class.getName() );
	
	public static void main(String[] args) {
		try {
			if(args.length > 0){
				init(args[0]);
			}else{
				String propFilePath = "resources/setup.properties";
				init(propFilePath);
			}
			OSPGrantsEntryPoint osp = new OSPGrantsEntryPoint();
			osp.runProcess();
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
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
		createFolder(new File(Configuration.POSTPROCESS_RESULTSET_FOLDER+"/"+date+"/"+Configuration.GRANTS_FOLDER));
	}

	
	
	
	public void runProcess() throws IOException, ParserConfigurationException, SAXException, NoSuchAlgorithmException {
		LOGGER.info("Runing OSP File Reader and TSV file generator process.");
		
		InputFilesReader reader = new InputFilesReader();
		reader.runProcess();
		
		LOGGER.info("Runing OSP File Reader and TSV file generator process...Completed");
		
		LOGGER.info("Runing OSP RDF file generator process...");
		
		RDFBuilder builder = new RDFBuilder();
		builder.runProcess();
		
		LOGGER.info("Runing OSP RDF file generator process...Completed");
	}






}
