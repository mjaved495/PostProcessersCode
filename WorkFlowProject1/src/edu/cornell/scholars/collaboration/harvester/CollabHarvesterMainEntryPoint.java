package edu.cornell.scholars.collaboration.harvester;

public class CollabHarvesterMainEntryPoint {

	public static void main(String[] args) {
		CollabHarvesterMainEntryPoint harvester = new CollabHarvesterMainEntryPoint();
		harvester.runProcess();
	}

	public void runProcess() {
		//Step 1: read scholars file for person and article id data
		ScholarsArticleDataReader obj = new ScholarsArticleDataReader();
		obj.runProcess();
		
		//Step 2: run the WOS queries
		// Manual process.
		
	}

}
