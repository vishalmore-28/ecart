<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.Bean.CustomerBean"%>
<%@ page import="com.Bean.OrderBean"%>
<%@ page import="com.Util.Html"%>
<%@ page import="com.Util.ProductUtil"%>
<%@ page import="java.util.ArrayList"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>My Orders</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="topbar">Online Electronic Store — Customer</div>
<div class="page">
	<div class="card wide">
		<%
		CustomerBean cb = (CustomerBean) session.getAttribute("cbean");
		if (cb == null) {
			response.sendRedirect("CustomerLogin.html");
			return;
		}
		@SuppressWarnings("unchecked")
		ArrayList<OrderBean> al = (ArrayList<OrderBean>) request.getAttribute("orderList");
		if (al == null) {
			response.sendRedirect("myorders");
			return;
		}
		%>
		<h1>My Orders</h1>
		<%
		if (al.size() == 0) {
			out.println("<div class='notice'>You have not placed any orders yet</div>");
		} else {
		%>
		<div class="table-wrap">
		<table class="product-table">
			<tr>
				<th>Code</th>
				<th>Name</th>
				<th>Company</th>
				<th>Price</th>
				<th>Qty</th>
				<th>Total</th>
			</tr>
		<%
			for (OrderBean ob : al) {
		%>
			<tr>
				<td><%=Html.esc(ob.getPcode())%></td>
				<td><%=Html.esc(ob.getPname())%></td>
				<td><%=Html.esc(ob.getPcompany())%></td>
				<td><%=Html.esc(ob.getPrice())%></td>
				<td><%=Html.esc(ob.getQty())%></td>
				<td><%=String.format("%.2f", ProductUtil.price(ob.getPrice()) * ProductUtil.qty(ob.getQty()))%></td>
			</tr>
		<%
			}
		%>
		</table>
		</div>
		<%
		}
		%>
		<div class="menu row">
			<a class="btn" href="viewproducts">View Product</a>
			<a class="btn secondary" href="logout">Logout</a>
		</div>
	</div>
</div>
</body>
</html>
