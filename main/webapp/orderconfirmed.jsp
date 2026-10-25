<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.Bean.CustomerBean"%>
<%@ page import="com.Util.Html"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Order Confirmed</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="topbar">Online Electronic Store — Customer</div>
<div class="page">
	<div class="card">
		<%
		CustomerBean cb = (CustomerBean) session.getAttribute("cbean");
		if (cb == null) {
		    response.sendRedirect("CustomerLogin.html");
		    return;
		}

		Object total = request.getAttribute("total");
		%>

		<h1>Hello <%=Html.esc(cb.getfName())%></h1>

		<div class="notice">You have charged Rs <%=total%> — your order is placed successfully</div>

		<div class="menu row">
			<a class="btn" href="viewproducts">View Product</a>
			<a class="btn" href="myorders">My Orders</a>
			<a class="btn secondary" href="logout">Logout</a>
		</div>
	</div>
</div>
</body>
</html>