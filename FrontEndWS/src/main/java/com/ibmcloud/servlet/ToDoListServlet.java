package com.ibmcloud.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibmcloud.ws.ToDo;
import com.ibmcloud.ws.Operation;
import com.ibmcloud.ws.ToDoWebServicesService;

import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet implementation class ToDoListServlet
 */
@WebServlet("/ToDoListServlet")
public class ToDoListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ToDoWebServicesService todo;

    public ToDoListServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
	    String message = "";
	    todo = new ToDoWebServicesService();
	    Operation op = todo.getToDoWebServicesPort().listToDo(name);
    	int rc = op.getRC();
    	message = op.getMessage();
    	if (message.equals(null)) message=new String("");

		PrintWriter prWriter = response.getWriter();
		prWriter.println("<head>");
		prWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
		prWriter.println("<title>ToDoApp-WS-ToDoList</title>");
		prWriter.println("<link rel=\"stylesheet\" href=\"ToDo.css\">");
		prWriter.println("</head>");
		prWriter.println("<body>");
		prWriter.println("<DIV id=\"AppTitle\" class=\"AppTitle\"><p>To Do List</p></div>");
    	 
		prWriter.println("<div id=\"AppBody\" class=\"AppBody\">");
		prWriter.println("<TABLE border=1><TR><TH>Select</TH><TH>Category</TH><TH colspan=3>Description</TH></TR>");

    	if (rc==0) try {
    		List<Object> results = op.getResults();
    		int numrows = results.size();
	    	for (int i=0;i<numrows;i++) {
	    		ToDo rs = (ToDo)results.get(i);
	    		prWriter.println("<FORM METHOD=\"POST\" action=\"/FrontEndWS/ToDoServlet\">");
	    		prWriter.println("<INPUT TYPE=\"hidden\" name=\"id\" value="+rs.getId()+" />");
	    		prWriter.println("<INPUT TYPE=\"hidden\" name=\"name\" value="+rs.getName()+" />");
	    		prWriter.println("<TR><TD><INPUT TYPE=\"submit\" name=\"Action\" value=\"Delete\"/></TD>");
	    		String cat = rs.getCategory();
	    		String des = rs.getDescription();
	    		prWriter.println("<TD>"+cat+"</TD><INPUT TYPE=\"hidden\" name=\"category\" value=\""+cat+"\" /></TD>");
	    		prWriter.println("<TD>"+des+"</TD><INPUT TYPE=\"hidden\" name=\"description\" value=\""+des+"\" /></TD></TR>");
	    		prWriter.println("</FORM>");
	    	}
	    } catch (ClassCastException e ) {
	    	message=message+"\n"+e.getMessage();
	    } 
    	prWriter.println("</TABLE>");
    	prWriter.println("<DIV class=\"addForm\" id=\"addForm\">");
		prWriter.println("<P><FORM METHOD=\"post\" action=\"/FrontEndWS/ToDoServlet\">");
		prWriter.println("<INPUT TYPE=\"hidden\" name=\"name\" value="+name+" />");
		prWriter.println("Category: <INPUT TYPE=\"text\" name=\"category\"  /><br/>");
		prWriter.println("Description: <INPUT TYPE=\"text\" name=\"description\"  /><br/>");
    	prWriter.println("<INPUT TYPE=\"submit\" name=\"Action\" id=\"Action\" value=\"Add\" />");
    	prWriter.println("</FORM>");
    	prWriter.println("</DIV>");
		prWriter.println("<P><FORM METHOD=\"post\" action=\"/FrontEndWS/ToDoServlet\">");
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
