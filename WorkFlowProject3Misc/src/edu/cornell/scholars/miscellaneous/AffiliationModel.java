package edu.cornell.scholars.miscellaneous;

public class AffiliationModel {

	private String unit;
	private String dept;
	private String wosentry;
	
	public AffiliationModel(String wosentry, String dept, String unit) {
		this.wosentry = wosentry;
		this.dept =dept;
		this.unit = unit;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getWosentry() {
		return wosentry;
	}
	public void setWosentry(String wosentry) {
		this.wosentry = wosentry;
	}
	
	
	
}
