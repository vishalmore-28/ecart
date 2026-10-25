<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Add Product Status</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="topbar">Online Electronic Store — Admin</div>
<div class="page">
	<div class="card">
		<%
			if (session.getAttribute("adminbean") == null) {
				response.sendRedirect("AdminLogin.html");
				return;
			}
			String data = (String)request.getAttribute("msg");
		%>
		<div class="notice"><%=com.Util.Html.esc(data)%></div>

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