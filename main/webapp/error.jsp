<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Error</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="topbar">Online Electronic Store</div>
<div class="page">
	<div class="card">
		<%
			String msg = (String)request.getAttribute("msg");
			if (msg == null) {
				Object status = request.getAttribute("javax.servlet.error.status_code");
				msg = "404".equals(String.valueOf(status)) ? "Page not found" : "Something went wrong";
			}
			msg = msg.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
		%>
		<h1>Error</h1>
		<div class="notice"><%=msg%></div>

		<div class="menu">
			<a class="btn" href="AdminLogin.html">Admin Login</a>
			<a class="btn" href="CustomerLogin.html">Customer Login</a>
			<a class="btn secondary" href="logout">Logout</a>
		</div>
	</div>
</div>
</body>
</html>
