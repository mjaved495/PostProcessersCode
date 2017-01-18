package edu.cornell.scholars.collaboration.datamodel.json;

public enum UnitAbbreviation {
	
	CAS("COLLEGE OF ARTS AND SCIENCES", "CAS"),
	CALS("COLLEGE OF AGRICULTURE AND LIFE SCIENCES", "CALS"),
	SIPS("School of Integrative Plant Science", "SIPS"),
	AEM("Charles H. Dyson School of Applied Economics and Management","AEM"),
	CCB("Chemistry and Chemical Biology", "CCB"),
	PHY("Physics","PHY");
	
	private String name;
	private String abbr;
	
	UnitAbbreviation(String name, String abbr){
		this.name = name;
		this.abbr = abbr;
	}
	
	public static String getAbbreviation(String name){
		UnitAbbreviation[] abbreviations = UnitAbbreviation.values();
		for(UnitAbbreviation abb: abbreviations){
			if(abb.getName().equals(name)){
				return abb.getAbbr();
			}
		}
		return null;
	}

	public String getAbbr() {
		return abbr;
	}

	public String getName() {
		return name;
	}
	
}
