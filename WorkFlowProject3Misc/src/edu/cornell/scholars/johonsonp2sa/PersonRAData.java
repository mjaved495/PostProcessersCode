package edu.cornell.scholars.johonsonp2sa;

import java.util.Set;

public class PersonRAData {

	String name;
	String netid;
	String dept;
	String count;
	Set<String> topic;
	
	public PersonRAData(String name, String netid, String dept, String count, Set<String> topic) {
		super();
		this.name = name;
		this.netid = netid;
		this.dept = dept;
		this.count = count;
		this.topic = topic;
	}
	
	public PersonRAData() {
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetid() {
		return netid;
	}
	public void setNetid(String netid) {
		this.netid = netid;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public Set<String> getRa() {
		return topic;
	}
	public void setRa(Set<String> ra) {
		this.topic = ra;
	}
	@Override
	public String toString() {
		return "\"" + name + "\",\"" + netid + "\",\"" + dept + "\",\"" + count + "\",\"" + topic+"\"";
	}
	
	public String toJSONString(int count) {
		return "{" + 
			"\"type\":\"ditem\","+
			"\"name\":\""+name +"\","+
			"\"url\":\"http://dev.scholars.cornell.edu/scholars/display/"+netid +"\","+
			"\"ditem\":" + count +","+
			"\"links\":" + getquotedString(topic) +
			"},";
	}

	private String getquotedString(Set<String> topic2) {
		String s="[";
		for(String topic: topic2){
			s = s+"\""+topic+"\",";
		}
		s = s.substring(0, s.length()-1) + "]";
		return s;
	}
	
//	{  
//  "type":"ditem",
//  "name":"webmetro.com/about.htm",
//  "ditem":1,
//  "links":[  
//
//  ]
//},
	
}
