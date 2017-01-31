package edu.cornell.scholars.collaboration.harvester;

import java.io.IOException;

import edu.cornell.scholars.workflow1.MainEntryPoint_WorkFlow1;

public class CollabHarvesterMainEntryPoint {

	public static void main(String[] args) {
		CollabHarvesterMainEntryPoint harvester = new CollabHarvesterMainEntryPoint();
		try {
			MainEntryPoint_WorkFlow1.init("resources/setup.properties");
			harvester.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runProcess() throws IOException {
		//Step 1: read scholars file for person and article id data
		ScholarsArticleDataReader obj = new ScholarsArticleDataReader();
		obj.runProcess();
		
		//Step 2: run the WOS queries
		// Manual process.
		
	}

}
