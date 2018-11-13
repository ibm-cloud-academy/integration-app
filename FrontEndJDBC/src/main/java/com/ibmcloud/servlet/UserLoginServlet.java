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

/**
 * Servlet implementation class UserLoginServlet
 */
@WebServlet("/UserLoginServlet")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(lookup = "jdbc/postgreSQL")
	DataSource ds;

    public UserLoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action=request.getParameter("Action");
		String user=request.getParameter("User");
		String pass=request.getParameter("PW");
	    String message = "";
	    int rc = 0;
	    if (action.equals("Login")) {
			Statement stmt = null;
			String check = "SELECT count(*) FROM users WHERE name = '"+user+"'";
			String getpw = "SELECT password FROM users WHERE name = '"+user+"'";
		    
		    try {
			    Connection con = ds.getConnection();
		    	stmt = con.createStatement();
		    	ResultSet rs = stmt.executeQuery(check);
		    	rs.next();
		    	int numrows = rs.getInt(1);
		    	if (numrows==1) {
		    		stmt = con.createStatement();
		    		ResultSet rs1 = stmt.executeQuery(getpw);
		    		rs1.next();
		    		String pw = rs1.getString(1);
		    		if (!pw.equals(pass)) {
		    			message = "Wrong password";
		    			rc = 100;
		    		} else {
		    			message = "Login successful";
		    		}
		    	} else {
		    		message = "User does not exist";
		    		rc = 101;
		    	}
		    } catch (SQLException e ) {
		    	e.printStackTrace();
	    		message= e.getMessage();
	    		rc = 999;
		    } 
		} else if (action.equals("Register")) {
			Statement stmt = null;
			String check = "SELECT count(*) FROM users WHERE name = '"+user+"'";
		    String insert = "INSERT into users VALUES ('"+user+"','"+pass+"')";
		    
		    try {
			    Connection con = ds.getConnection();
		    	stmt = con.createStatement();
		    	ResultSet rs = stmt.executeQuery(check);
		    	rs.next();
		    	int numrows = rs.getInt(1);
		    	if (numrows==0) {
		    		stmt = con.createStatement();
		    		stmt.executeUpdate(insert);
		    		message = "Execution succesful";
		    	} else {
		    		message = "User already exists";
		    		rc = 102;
		    	}
		    } catch (SQLException e ) {
		    	e.printStackTrace();
		    	message = e.getMessage();
		    	rc = 999;
		    } 
		}
	    System.out.println(message+" "+rc);
	    if (rc!=0) { 
	    	// go back to login page
	    	response.sendRedirect("UserLogin.jsp?msg="+message);
	    } else {
	    	response.sendRedirect("ToDoListServlet?name="+user+"&msg="+message);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
