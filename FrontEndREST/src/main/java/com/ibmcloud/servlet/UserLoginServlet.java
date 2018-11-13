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
 * Servlet implementation class UserLoginServlet
 */
@WebServlet("/UserLoginServlet")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 100L;
	private String server;
	private String port;
	private String protocol;
	
	public UserLoginServlet() {
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
		String action=request.getParameter("Action");
		String user=request.getParameter("User");
		String pass=request.getParameter("PW");
	    String message = "";
	    int rc = 0;
    	JsonObjectBuilder jsonReq = Json.createObjectBuilder();
    	JsonObject jsonResp;;
    	Client client = ClientBuilder.newClient();
		WebTarget target = client.target(protocol+"://"+server+":"+port+"/BackEndREST/ToDoRESTApp/ToDoREST");
		Invocation.Builder builder = target.request("application/json");
		jsonReq.add("name", user);
		jsonReq.add("password", pass);
	    if (action.equals("Login")) {
	    	jsonReq.add("action", "authUser");
		} else if (action.equals("Register")) {
	    	jsonReq.add("action", "addUser");
		}
		Response result = builder.post(Entity.entity(jsonReq.build(), "application/json"));
		jsonResp = result.readEntity(JsonObject.class);
		System.out.println(jsonResp.toString());
		rc = jsonResp.getInt("rc");
    	message = jsonResp.getString("message");
	    System.out.println(message+" "+rc);
	    if (rc!=0) { 
	    	response.sendRedirect("UserLogin.jsp?msg="+message);
	    } else {
	    	response.sendRedirect("ToDoListServlet?name="+user+"&msg="+message);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
