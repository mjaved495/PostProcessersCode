package edu.cornell.scholars.collaboration.collabharvester;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.scholars.collaboration.datamodel.data.AffiliationModel;
import edu.cornell.scholars.collaboration.datamodel.data.CollaborationHead;
import edu.cornell.scholars.collaboration.datamodel.data.Department;
import edu.cornell.scholars.collaboration.datamodel.data.Person;
import edu.cornell.scholars.collaboration.datamodel.data.Unit;
import edu.cornell.scholars.collaboration.gridmapper.Article_TSV;

public class CollaborationDataAnalyzer {

	private static List<WOSPerson_NetIdMap> wosperson_netid_maplist = null;
	private static Map<String, AffiliationModel> affiliation_college_map = null;
	private static Set<String> NF_Affiliation_Set = new HashSet<String>();
	public static Set<CollaborationHead> collaborations = new HashSet<CollaborationHead>();

	//purpose : To log affiliation count
	public static int affiliationCount = 0;
	//purpose : To log College's affiliation count
	public static Map<String, Integer> aff_count_map = new HashMap<String, Integer>();
	
	//purpose : To record Not mapped Person/College data
	private static PrintWriter printWriter = null;

	/**
	 * This passed list represents list of Cornellian entries of one article Article_TSV.
	 * @param articles
	 */
	public static void foundCollaboration(List<WOSPerson_NetIdMap> personNetIdMapList, Map<String, AffiliationModel> map, List<Article_TSV> articles_tsv) {
		wosperson_netid_maplist = personNetIdMapList;
		affiliation_college_map = map;

		CollaborationHead collaborationHead = new CollaborationHead();
		Set<String> collegeNameSet = new HashSet<String>();
		for(Article_TSV art: articles_tsv){ // These are entries for authors of an article.

			String personNetId = findPersonNetId(art.getAuthor()); 
			String collaboratingCollege = findCollege(art.getAffiliation()); // get the College
			collaborationHead.setYear(art.getYear().trim());
			collaborationHead.setWosId(art.getWosId().trim());
			collaborationHead.setPubmedId(art.getPubmedId().trim());
			collaborationHead.setArticleTitle(art.getTitle().trim());

			// We cannot rely on the VIVO person's departments, because person may have different 
			// department affiliation at the time of publication vs current affiliations.

			String collaboratingDepartment = affiliation_college_map.get(art.getAffiliation()).getDept(); // Get Collaborative Department
			// Others - a place where non-mapped departments data will go 
			if(collaboratingDepartment == null || collaboratingDepartment.trim().isEmpty()){
				collaboratingDepartment = "Others";
			}
			String author = art.getAuthor();
			Set<Unit> units = collaborationHead.getUnits();

			if(units == null || units.size() == 0){ // if no units are yet added

				Unit unit = new Unit(); // College does not exist
				unit.setUnitName(collaboratingCollege);

				// creating new department of this unit.
				Department newDept = new Department();
				newDept.setDeptName(collaboratingDepartment);

				//if the department is new, so the person.
				Person per = new Person();
				per.setPersonName(author);
				per.setNetId(personNetId);

				newDept.addPerson(per);      // add person to department
				unit.addDepartments(newDept);// add department to unit
				collaborationHead.addUnit(unit); // add unit to collaboration

			}else{ // if some units exists

				boolean unitFoundFlag = false;
				for(Unit unit: units){
					if(unit.getUnitName().equalsIgnoreCase(collaboratingCollege)){
						// unit already exists
						unitFoundFlag = true;
						Set<Department> departments = unit.getDepartments();
						boolean departFoundFlag = false;
						for(Department department: departments){
							if(department.getDeptName().equalsIgnoreCase(collaboratingDepartment)){
								// dept already exists
								departFoundFlag = true;
								Set<Person> personSet = department.getPersonList();
								//if department already exist there must be at least one person in the person list. 
								//personSet cannot be null.
								Person person = new Person();
								person.setPersonName(author);
								person.setNetId(personNetId);
								personSet.add(person);
							}
						}
						if(!departFoundFlag){ // creating new department of existing unit.

							Department newDepartment = new Department();
							newDepartment.setDeptName(collaboratingDepartment);

							Person per = new Person();
							per.setPersonName(author);
							per.setNetId(personNetId);

							newDepartment.addPerson(per); 		// add person to new department
							unit.addDepartments(newDepartment); // add new department to unit
						}
					}
				} // iteration over units finish.

				if(!unitFoundFlag){ // unit not found.

					// creating new unit. 
					Unit unit = new Unit();
					unit.setUnitName(collaboratingCollege);

					// creating new department for the new unit.
					Department newDept = new Department();
					newDept.setDeptName(collaboratingDepartment);

					// creating new person of this department.
					Person per = new Person();
					per.setPersonName(author);
					per.setNetId(personNetId);

					newDept.addPerson(per);    		// add person to department
					unit.addDepartments(newDept);	// add department to unit
					collaborationHead.addUnit(unit);  	// add unit to collaboration
				}
			}

			if(collaboratingCollege.isEmpty()){
				NF_Affiliation_Set.add("\""+art.getAuthor()+"\",\""+personNetId+"\",\""+art.getAffiliation()+"\"");
			}

//			if(false){ // if person not found
//				printWriter.println("\""+art.getWosId()+"\",\""+art.getAuthor() +"\",\"\",\"\",\""+art.getAffiliation()+"\",\""+collaboratingCollege+"\",\""+art.getTitle()+"\"");
//				System.out.println("\""+art.getWosId()+"\",\""+art.getAuthor() +"\",\"\",\"\",\""+art.getAffiliation()+"\",\""+collaboratingCollege+"\",\""+art.getTitle()+"\"");
//				if(collaboratingCollege.isEmpty()){
//					NF_Affiliation_Set.add("\""+art.getAuthor()+"\",\"\",\""+art.getAffiliation()+"\"");
//				}
//			}

			if(!collaboratingCollege.isEmpty()){ 
				collegeNameSet.add(collaboratingCollege);
			}

		}// iteration over articles_tsv completed

		logAffiliationCounts(collegeNameSet);
		
	//	printIdentifiedCollaboration(collaborationHead);
	
		collaborations.add(collaborationHead);
	}

	private static void printIdentifiedCollaboration(CollaborationHead collaborationHead) {
		Set<Unit> units = collaborationHead.getUnits();
		System.out.println(collaborationHead.getWosId());
//		Set<String> distinctUnits = new HashSet<String>();
		for(Unit u: units){
//			if(!distinctUnits.contains(u.getUnitName())){
//				System.out.println(u.getUnitName());
//				distinctUnits.add(u.getUnitName());
//			}
			for(Department d: u.getDepartments()){
				System.out.println(u.getUnitName()+":"+d.getDeptName());
			}
		}
		System.out.print("\n\n");
	}

	private static void logAffiliationCounts(Set<String> collegeNameSet) {
		if(collegeNameSet.size() > 1){
			for(String s: collegeNameSet){
				if(aff_count_map.get(s) == null){
					aff_count_map.put(s, 1);
				}else{
					aff_count_map.put(s, aff_count_map.get(s)+1);
				}
			}
			affiliationCount++;  // to log affiliation count
		}
	}

	private static String findCollege(String affiliation) {
		AffiliationModel aff = affiliation_college_map.get(affiliation);
		String coll = aff.getUnit();
		return  coll != null ? coll:"";
	}

	private static String findPersonNetId(String nam) {
		String netId = "";
		for(WOSPerson_NetIdMap obj: wosperson_netid_maplist){
			String personName = obj.getName();
			if(personName.equals(nam.trim())){
				netId = obj.getNetId() != null ? obj.getNetId():"";
				break;
			}
		}
		return netId;
	}

	public static void initializePrintWriter() {
		try {
			printWriter = new PrintWriter (AffiliationDataAnalyzer.NF_AFFILIATION_SET_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	

	}

	public static void terminatePrintWriter() {
		printWriter.close();
	}
}
