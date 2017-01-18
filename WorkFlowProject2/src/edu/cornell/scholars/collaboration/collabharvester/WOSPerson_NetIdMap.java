package edu.cornell.scholars.collaboration.collabharvester;

public class WOSPerson_NetIdMap {

	private String name;
	private String netId;
	
	public WOSPerson_NetIdMap(){
		
	}

	public WOSPerson_NetIdMap(String name, String netId) {
		super();
		this.name = name;
		this.netId = netId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetId() {
		return netId;
	}
	public void setNetId(String netId) {
		this.netId = netId;
	}

	@Override
	public String toString() {
		return "\"" + name + "\",\"" + netId + "\"";
	}
	
	
}
