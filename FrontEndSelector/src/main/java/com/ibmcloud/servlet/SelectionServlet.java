package com.ibmcloud.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SelectionServlet")
public class SelectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private int selCount;
	private String message;
	SelectionEntry[] entries;
	
	private class SelectionEntry {
		private String warFile;
		private String method;
		private String hostname;
		public SelectionEntry(String warFile,String method,String hostname) {
			this.warFile = warFile;
			this.method = method;
			this.hostname = hostname;
		}
		public String getWarFile() { return warFile; }
		public String getMethod() { return method; }
		@SuppressWarnings("unused")
		public String getHostname() { return hostname; }
		public String toString() { return warFile+"|"+method+"|"+hostname; }
	}
	
    public SelectionServlet() {
        super();
        message = new String(""); 
        Properties prop = new Properties();
        int count = 0;
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			prop.load(classLoader.getResourceAsStream("todoApp.properties"));
	        count = Integer.parseInt(prop.getProperty("count"));
	    	entries = new SelectionEntry[count];
	        for (int i=0;i<count; i++) {
	        	entries[i] = new SelectionEntry(prop.getProperty("warname."+i),prop.getProperty("method."+i),prop.getProperty("hostname."+i));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        selCount = count;
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter prWriter = response.getWriter();
		prWriter.println("<head>");
		prWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
		prWriter.println("<title>ToDoApp-Method Selection</title>");
		prWriter.println("<link rel=\"stylesheet\" href=\"ToDo.css\">");
		prWriter.println("</head>");
		prWriter.println("<body>");
		prWriter.println("<DIV id=\"AppTitle\" class=\"AppTitle\"><p>Method Selection</p></div>");
    	 
		prWriter.println("<div id=\"AppBody\" class=\"AppBody\">");
		prWriter.println("<TABLE border=1><TR><TH>Select a connection method</TH></TR>");

		for (int i=0;i<selCount;i++) {
			prWriter.println("<TR><TD><A HREF=\"/"+entries[i].getWarFile()+"/UserLogin.jsp\">"+entries[i].getMethod()+"</A></TD></TR>"); 
		}
		
    	prWriter.println("</TABLE>");
    	prWriter.println("</div>");

    	prWriter.println("<DIV id=\"AppMsg\" class=\"AppMsg\">");
    	prWriter.println("<p>"+message+"</p>");
    	prWriter.println("</div>");
    	prWriter.println("</BODY>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
