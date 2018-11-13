package com.ibmcloud.data;

public class Operation {
	private String message;
	private int rc;
	private Object result;
	private Object[] results;
	
	public Operation() {
		rc=0;
		message = new String("");
	}
	
	public Object getResult() { return result; }
	public Object[] getResults() { return results; }
	public String getMessage() {return message;}
	public int getRC() {return rc;}
	public void setMessage(String msg) {message = msg;}
	public void setRC(int code) {rc=code;}
	public void setResult(Object res) { result = res; }
	public void setResults(Object[] res) { results = res; }
}
