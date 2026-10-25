<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Logged Out</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<div class="topbar">Online Electronic Store</div>
<div class="page">
	<div class="card">
		<%
			session.invalidate();
		%>
		<div class="notice">You have been logged out successfully</div>
		<div class="menu">
			<a class="btn" href="index.html">Back to Home</a>
		</div>
	</div>
</div>
</body>
</html>