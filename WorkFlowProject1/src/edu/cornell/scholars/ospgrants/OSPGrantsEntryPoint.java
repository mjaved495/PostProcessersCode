package edu.cornell.scholars.ospgrants;

public class OSPGrantsEntryPoint {



	public static void main(String[] args) {
		OSPGrantsEntryPoint osp = new OSPGrantsEntryPoint();
		osp.runProcess();
	}

	public void runProcess() {
		InputFilesReader reader = new InputFilesReader();
		reader.runProcess();

		RDFBuilder builder = new RDFBuilder();
		builder.runProcess();
	}






}
