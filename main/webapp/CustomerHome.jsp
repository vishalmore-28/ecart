
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.Bean.CustomerBean" %>
<%@ page import="com.Util.Html" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Customer Dashboard | Online Electronic Store</title>

<style>
* { box-sizing:border-box; margin:0; padding:0; }

body {
    font-family:"Segoe UI",Arial,sans-serif;
    background:#f3f6fb;
    color:#172033;
}

.topbar {
    background:linear-gradient(120deg,#111827,#253858);
    color:white;
    padding:22px 5%;
    display:flex;
    justify-content:space-between;
    align-items:center;
    flex-wrap:wrap;
    gap:12px;
    box-shadow:0 4px 18px rgba(15,23,42,.12);
}

.brand { font-size:21px; font-weight:750; }
.topbar small { color:#cbd5e1; }

.page { width:92%; max-width:1150px; margin:35px auto; }

.welcome {
    background:linear-gradient(120deg,#1d4ed8,#3b82f6);
    color:white;
    border-radius:18px;
    padding:35px;
    margin-bottom:25px;
    box-shadow:0 12px 28px rgba(37,99,235,.16);
}

.welcome .eyebrow {
    font-size:12px;
    text-transform:uppercase;
    letter-spacing:1.5px;
    color:#dbeafe;
    margin-bottom:12px;
}

.welcome h1 { font-size:32px; margin-bottom:10px; }
.welcome p { color:#eff6ff; font-size:14px; line-height:1.7; }

.section-title { font-size:21px; margin-bottom:17px; }

.grid {
    display:grid;
    grid-template-columns:repeat(2,minmax(0,1fr));
    gap:18px;
}

.action-card {
    display:block;
    background:white;
    padding:25px;
    border:1px solid #e7edf5;
    border-radius:15px;
    text-decoration:none;
    color:#172033;
    box-shadow:0 6px 20px rgba(15,23,42,.04);
    transition:transform .2s,box-shadow .2s,border-color .2s;
}

.action-card:hover {
    transform:translateY(-3px);
    box-shadow:0 12px 25px rgba(15,23,42,.08);
    border-color:#bfdbfe;
}

.icon {
    width:48px;
    height:48px;
    display:grid;
    place-items:center;
    border-radius:13px;
    background:#dbeafe;
    color:#1d4ed8;
    font-size:23px;
    margin-bottom:18px;
}

.action-card h2 { font-size:17px; margin-bottom:8px; }
.action-card p { font-size:13px; color:#64748b; line-height:1.7; }
.arrow { display:block; margin-top:18px; color:#2563eb; font-size:13px; font-weight:700; }

.bottom {
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:15px;
    flex-wrap:wrap;
    margin-top:25px;
    padding:18px 0;
    color:#64748b;
    font-size:13px;
}

.logout {
    display:inline-block;
    padding:11px 17px;
    background:white;
    border:1px solid #dbe3ee;
    border-radius:9px;
    color:#334155;
    text-decoration:none;
    font-weight:650;
}

.logout:hover { background:#f1f5f9; }

.footer { text-align:center; color:#94a3b8; font-size:12px; padding:25px 10px; }

@media(max-width:650px) {
    .grid { grid-template-columns:1fr; }
    .welcome { padding:25px; }
    .welcome h1 { font-size:26px; }
    .brand { font-size:18px; }
}
</style>
</head>

<body>

<div class="topbar">
    <div class="brand">Online Electronic Store</div>
    <small>Customer Portal</small>
</div>

<%
CustomerBean cb = (CustomerBean) session.getAttribute("cbean");
if (cb == null) {
    response.sendRedirect("CustomerLogin.html");
    return;
}
%>

<div class="page">

    <section class="welcome">
        <div class="eyebrow">Customer Dashboard</div>
        <h1>Hello, <%= Html.esc(cb.getfName()) %>!</h1>
        <p>Welcome to your online store. Explore products, check your orders,
        and manage your account from one place.</p>
    </section>

    <h2 class="section-title">What would you like to do?</h2>

    <div class="grid">

        <a class="action-card" href="viewproducts">
            <div class="icon">&#128722;</div>
            <h2>Browse Products</h2>
            <p>Explore available electronics, compare prices, and find products you need.</p>
            <span class="arrow">Explore catalogue &rarr;</span>
        </a>

        <a class="action-card" href="myorders">
            <div class="icon">&#128203;</div>
            <h2>My Orders</h2>
            <p>Review your order history and check the purchases associated with your account.</p>
            <span class="arrow">View order history &rarr;</span>
        </a>

        <a class="action-card" href="changepwd">
            <div class="icon">&#128273;</div>
            <h2>Change Password</h2>
            <p>Open the password-change page to update your account credentials.</p>
            <span class="arrow">Manage password &rarr;</span>
        </a>

        <a class="action-card" href="logout">
            <div class="icon">&#10132;</div>
            <h2>Logout</h2>
            <p>Securely leave your customer account when you finish shopping.</p>
            <span class="arrow">Sign out &rarr;</span>
        </a>

    </div>

    <div class="bottom">
        <span>Shop smart. Shop with confidence.</span>
        <a class="logout" href="logout">Logout</a>
    </div>

</div>

<div class="footer">Online Electronic Store &copy; 2026</div>

</body>
</html>
