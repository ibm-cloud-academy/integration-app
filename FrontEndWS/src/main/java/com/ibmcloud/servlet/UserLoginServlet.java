package com.ibmcloud.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ibmcloud.ws.Operation;
import com.ibmcloud.ws.ToDoWebServicesService;

/**
 * Servlet implementation class UserLoginServlet
 */
@WebServlet("/UserLoginServlet")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ToDoWebServicesService todo;

    public UserLoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action=request.getParameter("Action");
		String name=request.getParameter("User");
		String pass=request.getParameter("PW");
	    String message = "";
	    todo = new ToDoWebServicesService();
	    int rc = 0;
	    Operation op;
	    if (action.equals("Login")) {
	    	op = todo.getToDoWebServicesPort().authUser(name, pass);
	    	rc = op.getRC();
	    	message = op.getMessage();
		} else if (action.equals("Register")) {
	    	op = todo.getToDoWebServicesPort().addUser(name, pass);
	    	rc = op.getRC();
	    	message = op.getMessage();
		}
	    System.out.println(message+" "+rc);
	    if (rc!=0) { 
	    	response.sendRedirect("UserLogin.jsp?msg="+message);
	    } else {
	    	response.sendRedirect("ToDoListServlet?name="+name+"&msg="+message);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
