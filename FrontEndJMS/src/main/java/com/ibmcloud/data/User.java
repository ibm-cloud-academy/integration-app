package com.ibmcloud.data;

public class User {
	private String name;
	private String password;
	
	public User(String name, String pw) {
		this.name = name;
		this.password = pw;
	}
	public String toString() { return name+"##"+password+"##"; }
	public String getName() { return name; }
	public String getPassword() { return password; }
	public void setName(String nm) { name = nm; }
	public void setPassword(String pw) { password = pw; }
}
