package com.ibmcloud.servlet;

import java.io.IOException;
import java.io.StringReader;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;
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

@WebServlet("/ToDoServlet")
public class ToDoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Queue queue;
	Queue replyQueue;
	JMSContext jmsContext;
	JMSProducer producer;
	JMSConsumer consumer;

	public ToDoServlet() {
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
    	try {
    		JsonObjectBuilder jsonReq = Json.createObjectBuilder();
    		jsonReq.add("name", name);
    		jsonReq.add("category", category);
    		jsonReq.add("description", description);
    		jsonReq.add("action", "addToDo");
    		TextMessage txtmsg = jmsContext.createTextMessage(jsonReq.build().toString());
    		txtmsg.setJMSReplyTo(replyQueue);
    		producer.send(queue, txtmsg);

    		TextMessage reply = (TextMessage) consumer.receive();

    		JsonReader jsonReader = Json.createReader(new StringReader(reply.getText()));
    		JsonObject jsonResp = jsonReader.readObject();
    		jsonReader.close();

    		return jsonResp;
    	} catch (Exception e) {
    		System.out.println("#### "+e.getMessage()+" ####");
    		return null;
    	}
    }


	
    public JsonObject delToDo(String name, int id, String category, String description) {
    	try {
    		JsonObjectBuilder jsonReq = Json.createObjectBuilder();
    		jsonReq.add("name", name);
    		jsonReq.add("category", category);
    		jsonReq.add("description", description);
    		jsonReq.add("id", id);
    		jsonReq.add("action", "delToDo");
    		TextMessage txtmsg = jmsContext.createTextMessage(jsonReq.build().toString());
    		txtmsg.setJMSReplyTo(replyQueue);
    		producer.send(queue, txtmsg);

    		TextMessage reply = (TextMessage) consumer.receive();

    		JsonReader jsonReader = Json.createReader(new StringReader(reply.getText()));
    		JsonObject jsonResp = jsonReader.readObject();
    		jsonReader.close();

    		return jsonResp;
    	} catch (Exception e) {
    		System.out.println("#### "+e.getMessage()+" ####");
    		return null;
    	}
    }


}
