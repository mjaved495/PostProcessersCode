package edu.cornell.scholars.collaboration.gridreader;

public class GridModel {

	private String gridId;
	private String gridOrg;
	private String gridCity;
	private String gridState;
	private String gridCountry;
	
	public GridModel(){
		
	}
	
	public GridModel(String gridId, String gridOrg, String gridCity, String gridState, String gridCountry) {
		super();
		this.gridId = gridId;
		this.gridOrg = gridOrg;
		this.gridCity = gridCity;
		this.gridState = gridState;
		this.gridCountry = gridCountry;
	}


	public String getGridId() {
		return gridId;
	}
	public void setGridId(String gridId) {
		this.gridId = gridId;
	}
	public String getGridOrg() {
		return gridOrg;
	}
	public void setGridOrg(String gridOrg) {
		this.gridOrg = gridOrg;
	}
	public String getGridCity() {
		return gridCity;
	}
	public void setGridCity(String gridCity) {
		this.gridCity = gridCity;
	}
	public String getGridState() {
		return gridState;
	}
	public void setGridState(String gridState) {
		this.gridState = gridState;
	}
	public String getGridCountry() {
		return gridCountry;
	}
	public void setGridCountry(String gridCountry) {
		this.gridCountry = gridCountry;
	}


	@Override
	public String toString() {
		return gridId + ",\"" + gridOrg + "\",\"" + gridCity + "\",\"" + gridState + "\",\"" + gridCountry+"\"";
	}
	
	
}
