package edu.cornell.scholars.datareader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.cornell.scholars.config.Configuration;
import edu.cornell.scholars.config.SetupPropertyValues;
import edu.cornell.scholars.ospgrants.GrantModel;

public class DataReader {
	
	private static String INPUT_TXT_FILE = null;
	private static String netId[] = {
			 "pav5",
			 "ri25",  
			 "rz26",  
			 "kag73",
			 "ts23",
			 "mdf93" , 
			 "mjp329" ,
			 "rwr3"   ,
			 "grh68"  ,
			 "eab35"  ,
			 "sao27"  ,
			 "spm13"  ,
			 "ibw7"   ,
			 "flw2"   ,
			 "sm682"  ,
			 "pfl5"   ,
			 "jp256"  ,
			 "go74"   ,
			 "jl3673" ,
			 "mjw248" ,
			 "qw86"   ,
			 "pm389"  ,
			 "klb72"  ,
			 "mlw298" ,
			 "ytg1"   ,
			 "eb58"   ,
			 "ml872"  ,
			 "btn26"  ,
			 "jlb582" ,
			 "jc3263" ,
			 "ajt86"  ,
			 "gg363"  ,
			 "pdv3"   ,
			 "elb36"  ,
			 "ejd5"   ,
			 "yc2235" ,
			 "meh345" ,
			 "ss999"  ,
			 "mg864"  ,
			 "ldm65"  ,
			 "qdz2"   ,
			 "lb25"   ,
			 "ldw3"   ,
			 "llb27"  ,
			 "lsk82"  ,
			 "mjz6"   ,
			 "mas858" ,
			 "alg52"  ,
			 "cgs1"   ,
			 "elg25"  ,
			 "smg253" ,
			 "dh553"  ,
			 "ca223"  ,
			 "ks277"  ,
			 "lw658"  ,
			 "yc42"   ,
			 "mbb9"   ,
			 "alw43"  ,
			 "bjs24"  ,
			 "wh13"   ,
			 "grm79"  ,
			 "mpr97"  ,
			 "mb464"  ,
			 "bt42"   ,
			 "sep233" ,
			 "ged36"  ,
			 "mlt42"  ,
			 "ass233" ,
			 "kjc39"  ,
			 "hgd29"  ,
			 "mls497" ,
			 "lt432"  ,
			 "tls93"  ,
			 "jj34"   ,
			 "fal43"  ,
			 "cby6"   ,
			 "sec294" ,
			 "njp27"  ,
			 "sly3"   ,
			 "kf326"  ,
			 "ks833"  ,
			 "jlc77"  ,
			 "ltm25"  ,
			 "rcb28"  ,
			 "bw73"   ,
			 "js448"  ,
			 "yk683"  ,
			 "rmr299" ,
			 "mcs8"   ,
			 "rdw32"  ,
			 "dvn2"   ,
			 "mjs297" ,
			 "cg82"   ,
			 "mph223" ,
			 "emb54"  ,
			 "ca223"  ,
			 "ap19"   ,
			 "kah47"  ,
			 "jmr9"   ,
			 "hom1"   ,
			 "kah47"  ,
			 "alg8"   ,
			 "cby6" };
	private List<String> netIds;
	
	public static void main(String[] args) {
		DataReader obj = new DataReader();
		try {
			obj.netIds = new ArrayList<String>(Arrays.asList(netId));
			init("resources/setup.properties"); 
			obj.runProcess();
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

	public void runProcess() throws NoSuchAlgorithmException, IOException {
		setLocalDirectories();
		if(!new File(INPUT_TXT_FILE).exists()){
			System.out.println("GRANTS: AwdInv-all.txt does not exists....RETURINING.");
			return;
		}
		File inputFile = new File(INPUT_TXT_FILE);
		List<GrantModel> data = readFile(inputFile);
		//System.out.println(data.size());
		for(GrantModel d : data){
			if(netIds.contains(d.getNetId())){
				System.out.println(
						d.getNetId()
						+"\t"+d.getGrantTitle()
						+"\t"+d.getDepartmentName()
						);
			}
		}	
	}
	
	private List<GrantModel> readFile(File file) throws IOException {
		System.out.println("GRANTS: Reading Grants TSV file..."+ file.getAbsolutePath());
		List<GrantModel> data = new ArrayList<GrantModel>();
		FileInputStream is = new FileInputStream(file.getAbsolutePath());
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader buf = new BufferedReader(isr);
		ArrayList<String[]> rows = new ArrayList<String[]>();
		String lineJustFetched = null;
		String[] wordsArray;
		while(true){
			lineJustFetched = buf.readLine();
			if(lineJustFetched == null){  
				break; 
			}else{
				if(lineJustFetched.trim().length() == 0){
					continue;
				}
				//System.out.println(lineJustFetched);
				wordsArray = lineJustFetched.split("\t");
				rows.add(wordsArray);
			}
		}
		GrantModel obj  = null;
		for(int index = 0; index<rows.size();index++){
			String[] nextLine = rows.get(index);
			obj = new GrantModel();
			obj.setUnit(nextLine[0].trim());
			obj.setPersonURI(nextLine[1].trim());
			obj.setDepartmentURI(nextLine[4].trim());
			obj.setNetId(getLowercase(nextLine[5].trim()));
			obj.setProjectId(nextLine[16].trim());
			obj.setPersonRole(nextLine[12].trim());
			obj.setInvId(nextLine[15].trim());
			obj.setProposalId(nextLine[17].trim()); // not much useful
			obj.setGrantTitle(nextLine[18].trim());
			obj.setDepartmentId(nextLine[19].trim());
			obj.setDepartmentName(nextLine[20].trim());
			obj.setSponsorName(nextLine[21].trim());
			obj.setSponsorId(nextLine[22].trim());
			obj.setStartDate(nextLine[23].trim());
			obj.setEndDate(nextLine[24].trim());
			String amount = nextLine[25].trim();
			if(amount.isEmpty()){
				amount = "0";
			}
			obj.setGrantTotal(Double.parseDouble(amount));
			obj.setGrantType(nextLine[26].trim());
			obj.setSpLevel1(nextLine[29].trim());
			obj.setSpLevel2(nextLine[30].trim());
			obj.setSpLevel3(nextLine[31].trim());
			obj.setRollupDeptName(nextLine[32].trim());
			obj.setAwardStatus(nextLine[33].trim());
			data.add(obj);
		}
		buf.close();

		System.out.println("GRANTS: Grants TSV file size:"+ data.size());
		return data;
	}
	
	private void setLocalDirectories() {
		INPUT_TXT_FILE = Configuration.POSTPROCESS_RESULTSET_FOLDER + "/" + Configuration.date  +"/"+ 
				Configuration.GRANTS_FOLDER +"/"+ Configuration.OSP_GRANT_TXT;
	}

	private static void generateDirectories(String date, String propFilePath) throws IOException {
		// SET FILEPATHS
		SetupPropertyValues properties = new SetupPropertyValues();
		Map<String, String> map = properties.getPropValues(propFilePath);
		Configuration.setQUERY_RESULTSET_FOLDER(map.get("QUERY_RESULTSET_FOLDER"));
		Configuration.setPOSTPROCESS_RESULTSET_FOLDER(map.get("POSTPROCESS_RESULTSET_FOLDER"));
		Configuration.setSUPPL_FOLDER(map.get("SUPPL_FOLDER"));

	}
	
	private String getLowercase(String trim) {
		return trim.toLowerCase();
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
		return date;
	}
}
