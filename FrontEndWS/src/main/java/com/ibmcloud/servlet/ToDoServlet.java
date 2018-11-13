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
 * Servlet implementation class ToDoServlet
 */
@WebServlet("/ToDoServlet")
public class ToDoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ToDoWebServicesService todo;
	
	public ToDoServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String action = request.getParameter("Action");
		todo = new ToDoWebServicesService();
		if (action.equals("Delete")) {
			int id = Integer.parseInt(request.getParameter("id"));
			String category = request.getParameter("category");
			String description = request.getParameter("description");
			Operation op = todo.getToDoWebServicesPort().delToDo(name, id, category, description);
	    	response.sendRedirect("ToDoListServlet?name="+name+"&msg="+op.getMessage());
		} else if (action.equals("Add")) {
			String category = request.getParameter("category");
			String description = request.getParameter("description");
			Operation op = todo.getToDoWebServicesPort().addToDo(name, category, description);
	    	response.sendRedirect("ToDoListServlet?name="+name+"&msg="+op.getMessage());
		} else if (action.equals("Logoff")) {
	    	response.sendRedirect("UserLogin.jsp?msg=Logged out");
		} else {
			System.out.println("ToDoServlet - action unknown = "+action);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
