<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.Bean.ProductBean" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Update Product</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<%
ProductBean pb = (ProductBean)request.getAttribute("pbean");

if(pb == null){
    response.sendRedirect("view");
    return;
}
%>
<div class="topbar">Online Electronic Store — Admin</div>
<div class="page">
	<div class="card">
		<h2>Update Product</h2>

		<form action="update" method="post">

			<label>Product Code</label>
			<input type="text" name="pcode" value="<%=pb.getPcode()%>" readonly>

			<label>Product Name</label>
			<input type="text" name="pname" value="<%=pb.getPname()%>">

			<label>Company</label>
			<input type="text" name="pcompany" value="<%=pb.getPcompany()%>">

			<label>Price</label>
			<input type="text" name="price" value="<%=pb.getPrice()%>">

			<label>Quantity</label>
			<input type="text" name="pqty" value="<%=pb.getPqty()%>">

			<div style="text-align:center;">
				<input type="submit" value="Update Product">
			</div>

		</form>
	</div>
</div>
</body>
</html>