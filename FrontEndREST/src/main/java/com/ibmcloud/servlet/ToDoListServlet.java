package com.ibmcloud.servlet;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.io.PrintWriter;
import java.util.Properties;

/**
 * Servlet implementation class ToDoListServlet
 */
@WebServlet("/ToDoListServlet")
public class ToDoListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String server;
	private String port;
	private String protocol;
	
    public ToDoListServlet() {
        super();
        Properties prop = new Properties();
        try {
        	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			prop.load(classLoader.getResourceAsStream("todoREST.properties"));
	        server = prop.getProperty("server");
	    	port   = prop.getProperty("port");
	    	protocol = prop.getProperty("protocol");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
    	String message = request.getParameter("msg");
    	if (message.equals(null)) message=new String("");
		PrintWriter prWriter = response.getWriter();
		prWriter.println("<head>");
		prWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
		prWriter.println("<title>ToDoApp-REST-ToDoList</title>");
		prWriter.println("<link rel=\"stylesheet\" href=\"ToDo.css\">");
		prWriter.println("</head>");
		prWriter.println("<body>");
		prWriter.println("<DIV id=\"AppTitle\" class=\"AppTitle\"><p>To Do List</p></div>");
    	 
		prWriter.println("<div id=\"AppBody\" class=\"AppBody\">");
		prWriter.println("<TABLE border=1><TR><TH>Select</TH><TH>Category</TH><TH colspan=3>Description</TH></TR>");

		String user=request.getParameter("name");
	    int rc = 0;
    	JsonObjectBuilder jsonReq = Json.createObjectBuilder();
    	JsonObject jsonResp;;
    	Client client = ClientBuilder.newClient();
		WebTarget target = client.target(protocol+"://"+server+":"+port+"/BackEndREST/ToDoRESTApp/ToDoREST");
		Invocation.Builder builder = target.request("application/json");
		jsonReq.add("name", user);
    	jsonReq.add("action", "listToDo");
		Response result = builder.post(Entity.entity(jsonReq.build(), "application/json"));
		jsonResp = result.readEntity(JsonObject.class);
		rc = jsonResp.getInt("rc");
		message = jsonResp.getString("message");
		if (rc==0) {
			JsonArray results = jsonResp.getJsonArray("results");
			int numrows = results.size();
			for (int i=0;i<numrows;i++) {
				JsonObject todoObject = results.getJsonObject(i);
				prWriter.println("<FORM METHOD=\"POST\" action=\"/FrontEndREST/ToDoServlet\">");
				prWriter.println("<INPUT TYPE=\"hidden\" name=\"id\" value="+todoObject.getInt("id")+" />");
				prWriter.println("<INPUT TYPE=\"hidden\" name=\"name\" value="+todoObject.getString("name")+" />");
				prWriter.println("<TR><TD><INPUT TYPE=\"submit\" name=\"Action\" value=\"Delete\"/></TD>");
				prWriter.println("<TD>"+todoObject.getString("category")+"</TD><INPUT TYPE=\"hidden\" name=\"category\" value=\""+todoObject.getString("category")+"\" /></TD>");
				prWriter.println("<TD>"+todoObject.getString("description")+"</TD><INPUT TYPE=\"hidden\" name=\"description\" value=\""+todoObject.getString("description")+"\" /></TD></TR>");
				prWriter.println("</FORM>");
			}
		}
		prWriter.println("</TABLE>");
    	prWriter.println("<DIV class=\"addForm\" id=\"addForm\">");
		prWriter.println("<P><FORM METHOD=\"post\" action=\"/FrontEndREST/ToDoServlet\">");
		prWriter.println("<INPUT TYPE=\"hidden\" name=\"name\" value="+name+" />");
		prWriter.println("Category: <INPUT TYPE=\"text\" name=\"category\"  /><br/>");
		prWriter.println("Description: <INPUT TYPE=\"text\" name=\"description\"  /><br/>");
    	prWriter.println("<INPUT TYPE=\"submit\" name=\"Action\" id=\"Action\" value=\"Add\" />");
    	prWriter.println("</FORM>");
    	prWriter.println("</DIV>");
		prWriter.println("<P><FORM METHOD=\"post\" action=\"/FrontEndREST/ToDoServlet\">");
		prWriter.println("<INPUT TYPE=\"hidden\" name=\"name\" value="+name+" />");
    	prWriter.println("<INPUT TYPE=\"submit\" name=\"Action\" id=\"Action\" value=\"Logoff\" />");
    	prWriter.println("</FORM>");
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
