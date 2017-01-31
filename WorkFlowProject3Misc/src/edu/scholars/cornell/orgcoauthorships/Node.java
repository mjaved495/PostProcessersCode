package edu.scholars.cornell.orgcoauthorships;

public class Node {

	private String name;
	private Integer size;
	
	public Node(String name, Integer size) {
		super();
		this.name = name;
		this.size = size;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	
	
}
