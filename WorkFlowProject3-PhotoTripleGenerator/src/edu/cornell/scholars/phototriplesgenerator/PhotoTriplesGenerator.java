package edu.cornell.scholars.phototriplesgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import edu.cornell.scholars.config.Configuration;

public class PhotoTriplesGenerator {

	private static final Logger LOGGER = Logger.getLogger(PhotoTriplesGenerator.class.getName());
	private static Boolean newDataExists = false;
	
	public void generateTriples(String photosFolder, String triplesFilePath, String outputFolder) {

		Model rdfmodel = ModelFactory.createDefaultModel();	

		Property mainImage = rdfmodel.createProperty("http://vitro.mannlib.cornell.edu/ns/vitro/public#mainImage");
		Property mimeType  = rdfmodel.createProperty("http://vitro.mannlib.cornell.edu/ns/vitro/public#mimeType");
		Property filename  = rdfmodel.createProperty("http://vitro.mannlib.cornell.edu/ns/vitro/public#filename");

		Property modTime          = rdfmodel.createProperty("http://vitro.mannlib.cornell.edu/ns/vitro/0.7#modTime");
		Property thumbnailImage   = rdfmodel.createProperty("http://vitro.mannlib.cornell.edu/ns/vitro/public#thumbnailImage");
		Property downloadLocation = rdfmodel.createProperty("http://vitro.mannlib.cornell.edu/ns/vitro/public#downloadLocation");
		Property directDownloadUrl = rdfmodel.createProperty("http://vitro.mannlib.cornell.edu/ns/vitro/public#directDownloadUrl");

		Resource File = rdfmodel.createResource("http://vitro.mannlib.cornell.edu/ns/vitro/public#File");
		Resource FileByteStream = rdfmodel.createResource("http://vitro.mannlib.cornell.edu/ns/vitro/public#FileByteStream");

		File inputFolder = new File(photosFolder);
		if(!inputFolder.isDirectory()) {
			System.err.println(inputFolder.getAbsolutePath() +" is not a directoty");
			return;
		}
		
		Set<String> newFoldersToBePushed = new HashSet<String>();
		
		for(File file: inputFolder.listFiles()){
			if(file.isDirectory() || file.getName().startsWith(".")) continue;
			String netId  = file.getName().substring(0, file.getName().indexOf("."));
			//LOGGER.info(netId);
			if(personAlreadyProcessed(netId, inputFolder)){
				LOGGER.info(netId + " already processed..continuing!");
				continue; // already exists
			}else{
				newFoldersToBePushed.add(netId.substring(0, 3));
				newDataExists = true;
			}
			
			Resource person = rdfmodel.createResource("http://scholars.cornell.edu/individual/"+netId);
			Resource person_mainImage = rdfmodel.createResource("http://scholars.cornell.edu/individual/"+netId+"_mainImage");
			person.addProperty(mainImage, person_mainImage);
			person_mainImage.addProperty(mimeType, "image/jpeg");
			person_mainImage.addProperty(filename, netId+".jpg");
			person_mainImage.addProperty(RDF.type, File);

			person_mainImage.addProperty(modTime, Configuration.date);

			Resource person_thumbnailImage = rdfmodel.createResource("http://scholars.cornell.edu/individual/"+netId+"_thumbnailImage");
			person_mainImage.addProperty(thumbnailImage, person_thumbnailImage);
		
			person_thumbnailImage.addProperty(mimeType, "image/jpeg");
			person_thumbnailImage.addProperty(filename, netId+".jpg");
			person_thumbnailImage.addProperty(RDF.type, File);
			person_thumbnailImage.addProperty(modTime, Configuration.date);

			// PATH 1
			Resource person_thumbnailImage_downloadLocation = rdfmodel.createResource("http://scholars.cornell.edu/individual/"+"n"+netId+"tdL77");
			person_thumbnailImage.addProperty(downloadLocation, person_thumbnailImage_downloadLocation);
			String localName = person_thumbnailImage_downloadLocation.getLocalName();
			
			String path = getPath(localName);
			File filedir = new File(new File(photosFolder), "file/"+path);
			filedir.mkdirs();
			Path src = FileSystems.getDefault().getPath(photosFolder, netId+".jpg");
			Path tgt = FileSystems.getDefault().getPath(filedir.getAbsolutePath()+"/");
			
			try {
				if(!tgt.resolve(src.getFileName()).toFile().exists()){
					Files.copy(src, tgt.resolve(src.getFileName()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	
			person_thumbnailImage_downloadLocation.addProperty(directDownloadUrl, "/file/"+localName+"/"+netId+".jpg");
			person_thumbnailImage_downloadLocation.addProperty(RDF.type, FileByteStream);
			person_thumbnailImage_downloadLocation.addProperty(modTime, Configuration.date);

			
			// PATH 2
			Resource person_downloadLocation = rdfmodel.createResource("http://scholars.cornell.edu/individual/"+"n"+netId+"dL77");
			person_mainImage.addProperty(downloadLocation, person_downloadLocation);
			String localName2 = person_downloadLocation.getLocalName();
			String path2 = getPath(localName2);
			File filedir2 = new File(new File(photosFolder), "file/"+path2);
			filedir2.mkdirs();
			Path src2 = FileSystems.getDefault().getPath(photosFolder, netId+".jpg");
			Path tgt2 = FileSystems.getDefault().getPath(filedir2.getAbsolutePath()+"/");
			try {
				if(!tgt2.resolve(src2.getFileName()).toFile().exists()){
					Files.copy(src2, tgt2.resolve(src2.getFileName()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			person_downloadLocation.addProperty(directDownloadUrl, "/file/"+localName+"/"+netId+".jpg");
			person_downloadLocation.addProperty(RDF.type, FileByteStream);
			person_downloadLocation.addProperty(modTime, Configuration.date);	
			
		}
		
		if(newDataExists){
			//Move the file folder to the date photo folder
			LOGGER.info("Moving file folders...");
			
			for(String folderName:newFoldersToBePushed){
				File srcFolder = new File(new File(photosFolder), "file/"+folderName);
				File targetFolder = new File(new File(triplesFilePath).getParentFile(), "file/"+folderName);
				try {
					//Files.copy(srcFolder.toPath(), targetFolder.toPath());
					FileUtils.copyDirectory(srcFolder, targetFolder);
					LOGGER.info("file folders moved successfully");
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			}
//			File srcFolder = new File(new File(photosFolder), "file");
//			File targetFolder = new File(new File(triplesFilePath).getParentFile(), "file");
//			try {
//				//Files.copy(srcFolder.toPath(), targetFolder.toPath());
//				FileUtils.copyDirectory(srcFolder, targetFolder);
//				LOGGER.info("file folders moved successfully");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			// save RDF model.
			try {
				LOGGER.info("Save Photo Triples RDF Model...");
				saveRDFModel(rdfmodel, triplesFilePath);
				LOGGER.info("Save Photo Triples RDF Model...completed");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else{
			LOGGER.info("Photo Triples: NO NEW DATA FOUND!!...");
		}
		
	}

	private boolean personAlreadyProcessed(String netId, File inputFolder) {
		String subNetId = netId.substring(0, 3);
		File folder = new File (inputFolder, "file/"+subNetId);
		return (folder.exists());
	}

	private String getPath(String localName) {
		//System.out.println(localName);
		String path = "";
		int start = 1;
		int end = 4;
		while(localName.length() > 3){
			String split = localName.substring(start, end);
			path = path.concat(split+"/");
			localName = localName.substring(end);
			start = 0;
			end = 3;
		}
		path = path + localName+"/";
		//System.out.println(path);
		return path;
	}

	private void saveRDFModel(Model rdfModel, String filePath) throws FileNotFoundException {
		PrintWriter printer = null;
		printer = new PrintWriter(filePath);
		rdfModel.write(printer, "N-Triples");
		printer.flush();
		printer.close();
	}
}
