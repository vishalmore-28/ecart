
<%@ page import="com.Bean.ProductBean" %>
<%@ page import="com.Bean.AdminBean" %>
<%@ page import="com.Util.Html" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Edit Product | Online Electronic Store</title>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }

body {
    font-family: "Segoe UI", Arial, sans-serif;
    background: #f3f6fb;
    color: #172033;
}

.topbar {
    background: linear-gradient(120deg, #111827, #253858);
    color: white;
    padding: 22px 5%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    box-shadow: 0 4px 18px rgba(15,23,42,.12);
}

.brand { font-size: 21px; font-weight: 750; }
.topbar small { color: #cbd5e1; }

.page { width: 92%; max-width: 760px; margin: 38px auto; }

.card {
    background: white;
    padding: 32px;
    border: 1px solid #e7edf5;
    border-radius: 17px;
    box-shadow: 0 8px 28px rgba(15,23,42,.05);
}

.heading { margin-bottom: 25px; }
.heading h1 { font-size: 28px; margin-bottom: 8px; }
.heading p { color: #64748b; font-size: 14px; line-height: 1.6; }

.product-info {
    padding: 17px;
    margin-bottom: 25px;
    background: #eff6ff;
    border: 1px solid #dbeafe;
    border-radius: 11px;
}

.product-info small {
    display: block;
    color: #64748b;
    margin-bottom: 5px;
}

.product-info strong { color: #1d4ed8; font-size: 17px; }

label {
    display: block;
    margin: 18px 0 8px;
    font-size: 13px;
    font-weight: 700;
    color: #334155;
}

input[type="number"] {
    width: 100%;
    padding: 13px 14px;
    border: 1px solid #dbe3ee;
    border-radius: 9px;
    font-size: 14px;
    outline: none;
    background: #fff;
}

input[type="number"]:focus {
    border-color: #2563eb;
    box-shadow: 0 0 0 3px rgba(37,99,235,.10);
}

.actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    margin-top: 27px;
}

.btn {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    padding: 12px 19px;
    border: none;
    border-radius: 9px;
    text-decoration: none;
    font-size: 13px;
    font-weight: 700;
    cursor: pointer;
    background: #2563eb;
    color: white;
}

.btn:hover { background: #1d4ed8; }

.btn.secondary {
    color: #334155;
    background: white;
    border: 1px solid #dbe3ee;
}

.btn.secondary:hover { background: #f1f5f9; }

.footer {
    text-align: center;
    color: #94a3b8;
    font-size: 12px;
    padding: 25px 10px;
}

@media(max-width: 550px) {
    .card { padding: 21px; }
    .heading h1 { font-size: 24px; }
    .actions .btn { flex: 1; }
}
</style>
</head>

<body>

<div class="topbar">
    <div class="brand">Online Electronic Store</div>
    <small>Admin Panel / Edit Product</small>
</div>

<%
    AdminBean ab = (AdminBean) session.getAttribute("adminbean");
    ProductBean pb = (ProductBean) request.getAttribute("pb");

    if (ab == null) {
        response.sendRedirect("AdminLogin.html");
        return;
    }

    if (pb == null) {
        response.sendRedirect("view");
        return;
    }
%>

<div class="page">
    <div class="card">

        <div class="heading">
            <h1>Edit Product</h1>
            <p>Hello <strong><%= Html.esc(ab.getAfname()) %></strong>,
            update the price and available quantity below.</p>
        </div>

        <div class="product-info">
            <small>Selected product</small>
            <strong><%= Html.esc(pb.getPname()) %></strong>
            <div style="margin-top:7px;color:#64748b;font-size:13px;">
                Product Code: <%= Html.esc(pb.getPcode()) %>
            </div>
        </div>

        <form action="update" method="post">

            <label for="price">Product Price (₹)</label>
            <input id="price" type="number" name="price"
                min="0" step="0.01"
                value="<%= Html.esc(pb.getPrice()) %>" required>

            <label for="pqty">Available Quantity</label>
            <input id="pqty" type="number" name="pqty"
                min="0" step="1"
                value="<%= Html.esc(pb.getPqty()) %>" required>

            <input type="hidden" name="pcode"
                value="<%= Html.esc(pb.getPcode()) %>">

            <div class="actions">
                <button class="btn" type="submit">Update Product</button>
                <a class="btn secondary" href="view">Cancel</a>
            </div>

        </form>
    </div>
</div>

<div class="footer">Online Electronic Store &copy; 2026</div>

</body>
</html>
