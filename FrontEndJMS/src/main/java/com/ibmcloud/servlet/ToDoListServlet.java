package com.ibmcloud.servlet;


import java.io.IOException;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * Servlet implementation class ToDoListServlet
 */
@WebServlet("/ToDoListServlet")
public class ToDoListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Queue queue;
	Queue replyQueue;
	JMSContext jmsContext;
	JMSProducer producer;
	JMSConsumer consumer;

	public ToDoListServlet() {
		super();
		try {
			QueueConnectionFactory cf1 = (QueueConnectionFactory) new InitialContext().lookup("jms/cloudQCF");
			queue = (Queue) new InitialContext().lookup("jms/cloudQ");
			replyQueue = (Queue) new InitialContext().lookup("jms/replyCloudQ");
			jmsContext = cf1.createContext();
			producer = jmsContext.createProducer();
			consumer = jmsContext.createConsumer(replyQueue);
		} catch (Exception e) {
			System.out.println("######## Exception looking up JMS resources ########");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String name = request.getParameter("name");
			String message = request.getParameter("msg");
			if (message.equals(null)) message=new String("");
			PrintWriter prWriter = response.getWriter();
			prWriter.println("<head>");
			prWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
			prWriter.println("<title>ToDoApp-JMS-ToDoList</title>");
			prWriter.println("<link rel=\"stylesheet\" href=\"ToDo.css\">");
			prWriter.println("</head>");
			prWriter.println("<body>");
			prWriter.println("<DIV id=\"AppTitle\" class=\"AppTitle\"><p>To Do List</p></div>");

			prWriter.println("<div id=\"AppBody\" class=\"AppBody\">");
			prWriter.println("<TABLE border=1><TR><TH>Select</TH><TH>Category</TH><TH colspan=3>Description</TH></TR>");

			String user=request.getParameter("name");
			int rc = 0;

			JsonObjectBuilder jsonReq = Json.createObjectBuilder();
			jsonReq.add("name", user);
			jsonReq.add("action", "listToDo");
			TextMessage txtmsg = jmsContext.createTextMessage(jsonReq.build().toString());
			txtmsg.setJMSReplyTo(replyQueue);
			producer.send(queue, txtmsg);

			TextMessage reply = (TextMessage) consumer.receive();

			JsonReader jsonReader = Json.createReader(new StringReader(reply.getText()));
			JsonObject jsonResp = jsonReader.readObject();
			jsonReader.close();

			rc = jsonResp.getInt("rc");
			message = jsonResp.getString("message");
			JsonArray results = jsonResp.getJsonArray("results");
			if (rc==0) { 
				int numrows = results.size();
				for (int i=0;i<numrows;i++) {
					JsonObject todoObject = results.getJsonObject(i);
					prWriter.println("<FORM METHOD=\"POST\" action=\"/FrontEndJMS/ToDoServlet\">");
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
			prWriter.println("<P><FORM METHOD=\"post\" action=\"/FrontEndJMS/ToDoServlet\">");
			prWriter.println("<INPUT TYPE=\"hidden\" name=\"name\" value="+name+" />");
			prWriter.println("Category: <INPUT TYPE=\"text\" name=\"category\"  /><br/>");
			prWriter.println("Description: <INPUT TYPE=\"text\" name=\"description\"  /><br/>");
			prWriter.println("<INPUT TYPE=\"submit\" name=\"Action\" id=\"Action\" value=\"Add\" />");
			prWriter.println("</FORM>");
			prWriter.println("</DIV>");
			prWriter.println("<P><FORM METHOD=\"post\" action=\"/FrontEndJMS/ToDoServlet\">");
			prWriter.println("<INPUT TYPE=\"hidden\" name=\"name\" value="+name+" />");
			prWriter.println("<INPUT TYPE=\"submit\" name=\"Action\" id=\"Action\" value=\"Logoff\" />");
			prWriter.println("</FORM>");
			prWriter.println("</div>");

			prWriter.println("<DIV id=\"AppMsg\" class=\"AppMsg\">");
			prWriter.println("<p>"+message+"</p>");
			prWriter.println("</div>");
			prWriter.println("</BODY>");
		} catch (Exception e) {
			System.out.println("######## Exception: "+e.getMessage()+" ########");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
