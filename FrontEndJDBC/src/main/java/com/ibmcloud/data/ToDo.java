package com.ibmcloud.data;

public class ToDo {
	private String name;
	private String category;
	private int id;
	private String description;
	
	public ToDo(String nm, String cat, int i, String desc) {
		name = nm;
		id = i;
		category = cat;
		description = desc;
	}
	
	public String toString() { return name+"-"+id+"-"+category+":"+description; }
	
	public String getName() { return name; }
	public int getId() { return id; }
	public String getCateglory() { return category; }
	public String getDescription() { return description; }
	
	public void setDescription(String desc) { description = desc; }
	public void setCategory(String cat) { category = cat; }
}