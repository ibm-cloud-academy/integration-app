package com.ibmcloud.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.json.Json;
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

/**
 * Servlet implementation class ToDoServlet
 */
@WebServlet("/ToDoServlet")
public class ToDoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String server;
	private String port;
	private String protocol;
	
	public ToDoServlet() {
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
		String action = request.getParameter("Action");

		if (action.equals("Delete")) {
			int id = Integer.parseInt(request.getParameter("id"));
			String category = request.getParameter("category");
			String description = request.getParameter("description");
			JsonObject op = this.delToDo(name, id, category, description);
	    	response.sendRedirect("ToDoListServlet?name="+name+"&msg="+op.getString("message"));
		} else if (action.equals("Add")) {
			String category = request.getParameter("category");
			String description = request.getParameter("description");
			JsonObject op = this.addToDo(name, category, description);
	    	response.sendRedirect("ToDoListServlet?name="+name+"&msg="+op.getString("message"));
		} else if (action.equals("Logoff")) {
	    	response.sendRedirect("UserLogin.jsp?msg=Logged out");
		} else {
			System.out.println("ToDoServlet - action unknown = "+action);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
    public JsonObject addToDo(String name, String category, String description) {
    	JsonObjectBuilder jsonReq = Json.createObjectBuilder();
    	JsonObject jsonResp;
    	Client client = ClientBuilder.newClient();
		WebTarget target = client.target(protocol+"://"+server+":"+port+"/BackEndREST/ToDoRESTApp/ToDoREST");
		Invocation.Builder builder = target.request("application/json");
		jsonReq.add("name", name);
		jsonReq.add("category", category);
		jsonReq.add("description", description);
    	jsonReq.add("action", "addToDo");
		Response result = builder.post(Entity.entity(jsonReq.build(), "application/json"));
		jsonResp = result.readEntity(JsonObject.class);

    	return jsonResp;
    }


	
    public JsonObject delToDo(String name, int id, String category, String description) {
    	JsonObjectBuilder jsonReq = Json.createObjectBuilder();
    	JsonObject jsonResp;
    	Client client = ClientBuilder.newClient();
		WebTarget target = client.target(protocol+"://"+server+":"+port+"/BackEndREST/ToDoRESTApp/ToDoREST");
		Invocation.Builder builder = target.request("application/json");
		jsonReq.add("name", name);
		jsonReq.add("id", id);
		jsonReq.add("category", category);
		jsonReq.add("description", description);
    	jsonReq.add("action", "delToDo");
		Response result = builder.post(Entity.entity(jsonReq.build(), "application/json"));
		jsonResp = result.readEntity(JsonObject.class);

    	return jsonResp;
    }


}
