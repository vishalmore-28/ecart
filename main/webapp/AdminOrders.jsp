
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.Bean.OrderBean"%>
<%@ page import="com.Util.Html"%>
<%@ page import="com.Util.ProductUtil"%>
<%@ page import="java.util.ArrayList"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Order Management | Online Electronic Store</title>

<style>
* {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

body {
    font-family: "Segoe UI", Arial, sans-serif;
    background: #f3f6fb;
    color: #172033;
}

.topbar {
    background: linear-gradient(120deg, #111827, #253858);
    color: #fff;
    padding: 22px 5%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 15px;
    flex-wrap: wrap;
    box-shadow: 0 4px 18px rgba(15, 23, 42, 0.15);
}

.brand {
    font-size: 21px;
    font-weight: 750;
    letter-spacing: 0.3px;
}

.topbar small {
    color: #cbd5e1;
    font-size: 13px;
}

.page {
    width: 94%;
    max-width: 1450px;
    margin: 32px auto;
}

.heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 15px;
    margin-bottom: 24px;
}

.heading h1 {
    font-size: 29px;
    margin-bottom: 7px;
    color: #172033;
}

.heading p {
    color: #64748b;
    font-size: 14px;
}

.order-count {
    background: #e0eaff;
    color: #1d4ed8;
    padding: 10px 16px;
    border-radius: 10px;
    font-size: 13px;
    font-weight: 700;
}

.card {
    background: #fff;
    border: 1px solid #e7edf5;
    border-radius: 17px;
    padding: 25px;
    box-shadow: 0 8px 28px rgba(15, 23, 42, 0.05);
}

.table-heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
    margin-bottom: 20px;
}

.table-heading h2 {
    font-size: 18px;
}

.table-heading span {
    color: #64748b;
    font-size: 13px;
}

.table-wrap {
    width: 100%;
    overflow-x: auto;
    border: 1px solid #e8edf4;
    border-radius: 12px;
}

.product-table {
    width: 100%;
    border-collapse: collapse;
    min-width: 900px;
}

.product-table th {
    background: #f8fafc;
    color: #64748b;
    text-transform: uppercase;
    font-size: 11px;
    letter-spacing: 0.7px;
    text-align: left;
    padding: 17px 16px;
    border-bottom: 1px solid #e2e8f0;
    white-space: nowrap;
}

.product-table td {
    padding: 17px 16px;
    border-bottom: 1px solid #edf1f6;
    font-size: 13px;
    color: #334155;
    white-space: nowrap;
}

.product-table tbody tr:last-child td {
    border-bottom: none;
}

.product-table tbody tr:hover {
    background: #f8fbff;
}

.customer-name {
    color: #172033;
    font-weight: 650;
}

.product-code {
    display: inline-block;
    background: #f1f5f9;
    color: #475569;
    padding: 5px 8px;
    border-radius: 6px;
    font-size: 12px;
    font-weight: 650;
}

.price {
    font-weight: 600;
}

.total {
    color: #15803d !important;
    font-weight: 750;
}

.notice {
    padding: 35px 20px;
    background: #f8fafc;
    border: 1px dashed #cbd5e1;
    border-radius: 12px;
    text-align: center;
    color: #64748b;
    line-height: 1.8;
}

.notice strong {
    display: block;
    color: #334155;
    font-size: 17px;
    margin-bottom: 5px;
}

.menu {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    margin-top: 25px;
}

.btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    text-decoration: none;
    background: #2563eb;
    color: #fff;
    padding: 12px 18px;
    border-radius: 9px;
    font-size: 13px;
    font-weight: 650;
    transition: 0.2s ease;
}

.btn:hover {
    background: #1d4ed8;
    transform: translateY(-1px);
}

.btn.secondary {
    background: #fff;
    color: #334155;
    border: 1px solid #dbe3ee;
}

.btn.secondary:hover {
    background: #f1f5f9;
}

.btn.danger {
    background: #dc2626;
}

.btn.danger:hover {
    background: #b91c1c;
}

.footer {
    text-align: center;
    color: #94a3b8;
    font-size: 12px;
    padding: 25px 10px;
}

@media (max-width: 650px) {
    .topbar {
        padding: 18px 5%;
    }

    .brand {
        font-size: 18px;
    }

    .page {
        margin: 22px auto;
    }

    .heading h1 {
        font-size: 24px;
    }

    .card {
        padding: 16px;
    }

    .menu .btn {
        flex: 1;
    }
}
</style>
</head>

<body>

<div class="topbar">
    <div class="brand">Online Electronic Store</div>
    <small>Admin Panel &nbsp; / &nbsp; Order Management</small>
</div>

<%
if (session.getAttribute("adminbean") == null) {
    response.sendRedirect("AdminLogin.html");
    return;
}

@SuppressWarnings("unchecked")
ArrayList<OrderBean> al =
    (ArrayList<OrderBean>) request.getAttribute("orderList");

if (al == null) {
    response.sendRedirect("allorders");
    return;
}
%>

<div class="page">

    <div class="heading">
        <div>
            <h1>Order Management</h1>
            <p>Review customer purchases and order totals.</p>
        </div>

        <div class="order-count">
            Total Orders: <%= al.size() %>
        </div>
    </div>

    <div class="card">

        <div class="table-heading">
            <h2>All Orders</h2>
            <span>Order records from your store</span>
        </div>

        <%
        if (al.size() == 0) {
        %>

        <div class="notice">
            <strong>No orders yet</strong>
            Customer orders will appear here when purchases are made.
        </div>

        <%
        } else {
        %>

        <div class="table-wrap">
            <table class="product-table">
                <thead>
                    <tr>
                        <th>Customer</th>
                        <th>Product Code</th>
                        <th>Product Name</th>
                        <th>Company</th>
                        <th>Unit Price</th>
                        <th>Quantity</th>
                        <th>Order Total</th>
                    </tr>
                </thead>

                <tbody>
                <%
                for (OrderBean ob : al) {
                    double total =
                        ProductUtil.price(ob.getPrice())
                        * ProductUtil.qty(ob.getQty());
                %>

                    <tr>
                        <td class="customer-name">
                            <%= Html.esc(ob.getCustomer()) %>
                        </td>

                        <td>
                            <span class="product-code">
                                <%= Html.esc(ob.getPcode()) %>
                            </span>
                        </td>

                        <td>
                            <%= Html.esc(ob.getPname()) %>
                        </td>

                        <td>
                            <%= Html.esc(ob.getPcompany()) %>
                        </td>

                        <td class="price">
                            ₹<%= Html.esc(ob.getPrice()) %>
                        </td>

                        <td>
                            <%= Html.esc(ob.getQty()) %>
                        </td>

                        <td class="total">
                            ₹<%= String.format("%.2f", total) %>
                        </td>
                    </tr>

                <%
                }
                %>
                </tbody>
            </table>
        </div>

        <%
        }
        %>

        <div class="menu">
            <a class="btn" href="adminhome">Dashboard</a>
            <a class="btn secondary" href="view">View Products</a>
            <a class="btn danger" href="logout">Logout</a>
        </div>

    </div>

</div>

<div class="footer">
    Online Electronic Store &copy; 2026
</div>

</body>
</html>
