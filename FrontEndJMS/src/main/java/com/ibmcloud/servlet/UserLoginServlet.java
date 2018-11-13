package com.ibmcloud.servlet;

import java.io.IOException;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.jms.*;

/**
 * Servlet implementation class UserLoginServlet
 */
@WebServlet("/UserLoginServlet")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 100L;
	//Session session;
	Queue queue;
	Queue replyQueue;
	JMSContext jmsContext;
	JMSProducer producer;
	JMSConsumer consumer;
	
	public UserLoginServlet() {
        super();
        try {
        	QueueConnectionFactory cf1 = (QueueConnectionFactory) new InitialContext().lookup("jms/cloudQCF");
        	queue = (Queue) new InitialContext().lookup("jms/cloudQ");
        	replyQueue = (Queue) new InitialContext().lookup("jms/replyCloudQ");
        	jmsContext = cf1.createContext();
        	producer = jmsContext.createProducer();
        	consumer = jmsContext.createConsumer(replyQueue);
        } catch (Exception e) {
        	System.out.println("######## Exception looking up JMS resources "+e.getMessage()+" ########");
        	e.printStackTrace();
        }
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String message = "";
	    int rc = 0;
		String user=request.getParameter("User");
		try {
			String action=request.getParameter("Action");
			String pass=request.getParameter("PW");
			JsonObjectBuilder jsonReq = Json.createObjectBuilder();
			jsonReq.add("name", user);
			jsonReq.add("password", pass);
			if (action.equals("Login")) {
				jsonReq.add("action", "authUser");
			} else if (action.equals("Register")) {
				jsonReq.add("action", "addUser");
			}

			TextMessage txtmsg = jmsContext.createTextMessage(jsonReq.build().toString());
			txtmsg.setJMSReplyTo(replyQueue);
			producer.send(queue, txtmsg);

			TextMessage reply = (TextMessage) consumer.receive();
			System.out.println(reply.getText());
			JsonReader jsonReader = Json.createReader(new StringReader(reply.getText()));
			JsonObject jsonResp = jsonReader.readObject();
			jsonReader.close();
			System.out.println(jsonResp.toString());
			rc = jsonResp.getInt("rc");
			message = jsonResp.getString("message");
			System.out.println(message+" "+rc);
		} catch (Exception e) {
			rc = 200;
			message = e.getMessage();
			e.printStackTrace();
		}
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
