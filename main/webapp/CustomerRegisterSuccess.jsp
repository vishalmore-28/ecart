<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Registration Status</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="topbar">Online Electronic Store</div>
<div class="page">
	<div class="card">
		<%
		String msg = (String)request.getAttribute("msg");
		%>
		<div class="notice"><%=com.Util.Html.esc(msg)%></div>

		<div class="link-row">
			<a href="CustomerLogin.html">Customer Login</a>
		</div>
	</div>
</div>
</body>
</html>