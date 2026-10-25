<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.Bean.CustomerBean"%>
<%@ page import="com.Util.Html"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Change Password</title>
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
		String msg = (String) request.getAttribute("msg");
		if (msg != null) {
		%>
		<div class="notice"><%=Html.esc(msg)%></div>
		<%
		}
		%>
		<h1>Change Password</h1>

		<form action="changepwd" method="post">
			<label>Old Password</label>
			<input type="password" name="oldpwd" required>

			<label>New Password</label>
			<input type="password" name="newpwd" minlength="4" required>

			<label>Confirm New Password</label>
			<input type="password" name="confirmpwd" minlength="4" required>

			<div style="text-align:center;">
				<input type="submit" value="Change Password">
			</div>
		</form>

		<div class="menu row">
			<a class="btn secondary" href="CustomerHome.jsp">Back</a>
		</div>
	</div>
</div>
</body>
</html>
