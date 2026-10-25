<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.Bean.ProductBean"%>
<%@ page import="com.Bean.CustomerBean"%>
<%@ page import="com.Util.Html"%>
<%@ page import="com.Util.ProductUtil"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Buy Product</title>
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

		ProductBean pb = (ProductBean) request.getAttribute("product");
		if (pb == null) {
		    response.sendRedirect("viewproducts");
		    return;
		}
		String msg = (String) request.getAttribute("msg");
		if (msg != null) {
		%>
		<div class="notice"><%=Html.esc(msg)%></div>
		<%
		}
		%>

		<h1>Buy Product</h1>

		<form action="buy" method="post">

			<label>Product Code</label>
			<input type="text" name="pcode" value="<%=Html.esc(pb.getPcode())%>" readonly>

			<label>Product Name</label>
			<input type="text" name="pname" value="<%=Html.esc(pb.getPname())%>" readonly>

			<label>Company</label>
			<input type="text" name="pcompany" value="<%=Html.esc(pb.getPcompany())%>" readonly>

			<label>Price</label>
			<input type="text" name="price" value="<%=Html.esc(pb.getPrice())%>" readonly>

			<label>Quantity (available: <%=ProductUtil.qty(pb.getPqty())%>)</label>
			<input type="number" name="pqty" min="1" max="<%=ProductUtil.qty(pb.getPqty())%>" step="1" required>

			<label>Reg No</label>
			<input type="text" name="regno" value="<%=Html.esc(cb.getCuName())%>" readonly>

			<div style="text-align:center;">
				<input type="submit" value="Buy">
			</div>

		</form>
	</div>
</div>
</body>
</html>