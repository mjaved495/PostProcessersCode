package edu.cornell.scholars.ospgrants;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class OSPGrantsEntryPoint {


	private static final Logger LOGGER = Logger.getLogger( OSPGrantsEntryPoint.class.getName() );
	
	public static void main(String[] args) {
		OSPGrantsEntryPoint osp = new OSPGrantsEntryPoint();
		try {
			osp.runProcess();
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
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
