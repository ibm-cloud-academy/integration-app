package com.ibmcloud.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.ibmcloud.data.Operation;
import com.ibmcloud.data.ToDo;

/**
 * Servlet implementation class ToDoServlet
 */
@WebServlet("/ToDoServlet")
public class ToDoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(lookup = "jdbc/postgreSQL")
	DataSource ds;

	public ToDoServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String action = request.getParameter("Action");

		if (action.equals("Delete")) {
			int id = Integer.parseInt(request.getParameter("id"));
			String category = request.getParameter("category");
			String description = request.getParameter("description");
			Operation op = this.delToDo(name, id, category, description);
	    	response.sendRedirect("ToDoListServlet?name="+name+"&msg="+op.getMessage());
		} else if (action.equals("Add")) {
			String category = request.getParameter("category");
			String description = request.getParameter("description");
			Operation op = this.addToDo(name, category, description);
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
	
    public Operation addToDo(String name, String category, String description) {
    	Operation op = new Operation();
    	// check name from user table
    	// get largest id 
		Statement stmt = null;
		String namecheck = "SELECT count(*) FROM users WHERE name = '"+name+"'";
		String getId = "SELECT max(id) FROM todos WHERE name = '"+name+"'";
	    try {
		    Connection con = ds.getConnection();
	    	stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(namecheck);
	    	rs.next();
	    	int numrows = rs.getInt(1);
	    	if (numrows==1) { // name found
	    		stmt = con.createStatement();
	    		rs = stmt.executeQuery(getId);
	    		rs.next();
	    		int newId = rs.getInt(1) + 1;
	    	    String insert = "INSERT into todos VALUES ('"+name+"','"+category+"',"+newId+",'"+description+"')";
	    		stmt = con.createStatement();
	    		stmt.executeUpdate(insert);
	        	ToDo todo = new ToDo(name,category,newId,description);
	        	op.setResult(todo);
	    	} else {
	    		op.setMessage("User does not exist");
	    		op.setRC(10);
	    	}
	    } catch (SQLException e ) {
	    	e.printStackTrace();
    		op.setMessage("SQL exception: "+e.getMessage()+"; SQL state: "+e.getSQLState());
    		op.setRC(11);
	    } 

    	return op;
    }


	
    public Operation delToDo(String name, int id, String category, String description) {
    	Operation op = new Operation();
    	
    	String todoCheck = "SELECT count(*) FROM todos WHERE NAME = '"+name+"' AND id = "+id+" AND category = '"+category+"' AND description = '"+description+"'";
	    try {
		    Connection con = ds.getConnection();
	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(todoCheck);
	    	rs.next();
	    	int numrows = rs.getInt(1);
	    	if (numrows==1) {
	    		Statement stmt2 = con.createStatement();
	    	    String delete = "DELETE from todos WHERE NAME = '"+name+"' AND id = "+id+" AND category = '"+category+"' AND description = '"+description+"'";
	    		stmt2.executeUpdate(delete);
	    		stmt2.close();
	    	} else {
	    		op.setMessage("To Do item does not exist");
	    		op.setRC(10);
	    	}
	    } catch (SQLException e ) {
	    	e.printStackTrace();
    		op.setMessage("SQL exception: "+e.getMessage()+"; SQL state: "+e.getSQLState());
    		op.setRC(11);
	    } 

    	return op;
    }


}
