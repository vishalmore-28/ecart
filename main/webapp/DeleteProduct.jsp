<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="com.Bean.AdminBean"%>
<%@ page import="com.Util.Html"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Delete Product</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="topbar">Online Electronic Store — Admin</div>
<div class="page">
	<div class="card">
		<%
			AdminBean ab = (AdminBean)session.getAttribute("adminbean");
			if (ab == null) {
				response.sendRedirect("AdminLogin.html");
				return;
			}
			String data = (String)request.getAttribute("data");
		%>
		<h1>Hello <%=Html.esc(ab.getAfname())%></h1>
		<div class="notice"><%=Html.esc(data)%></div>

		<div class="menu row">
			<a class="btn" href="adminhome">Dashboard</a>
			<a class="btn" href="addProduct.html">Add Product</a>
			<a class="btn" href="view">View Product</a>
			<a class="btn secondary" href="logout">Logout</a>
		</div>
	</div>
</div>
</body>
</html>