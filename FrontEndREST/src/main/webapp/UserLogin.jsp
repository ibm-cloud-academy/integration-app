<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ToDoApp-REST-LoginPage</title>
<link rel="stylesheet" href="ToDo.css">
</head>
<body>
<div id="AppTitle" class="AppTitle"><p>Login page</p></div>
<div id="AppBody" class="AppBody">
<FORM METHOD="post" action="/FrontEndREST/UserLoginServlet">
<INPUT type="hidden" name="Login" id="Login" value="true"></INPUT>
<P>Enter user name: <INPUT type="text" name="User" id="User"></INPUT></P>
<P>Enter password: <INPUT type="password" name="PW" id="PW"></INPUT></P>
<INPUT TYPE="submit" name="Action" id="Action" value="Login" />
<INPUT TYPE="submit" name="Action" id="Action" value="Register" />
</FORM>
<P><A HREF="/FrontEndSelector/SelectionServlet">Back to Selector</A></P>
</div>
<div id="AppMsg" class="AppMsg">
<%
    String message = request.getParameter( "msg" );
	if (message==null) message = new String("");
%>
<p><%= message %></p>
</div>
</body>
</html>