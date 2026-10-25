
<%@ page import="java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.Bean.ProductBean" %>
<%@ page import="com.Bean.CustomerBean" %>
<%@ page import="com.Util.Html" %>
<%@ page import="com.Util.ProductUtil" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Product Catalogue | Online Electronic Store</title>

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

.page { width:94%; max-width:1450px; margin:32px auto; }

.heading {
    display:flex;
    justify-content:space-between;
    align-items:center;
    flex-wrap:wrap;
    gap:15px;
    margin-bottom:22px;
}

.heading h1 { font-size:29px; margin-bottom:7px; }
.heading p { font-size:14px; color:#64748b; line-height:1.6; }

.customer-tag {
    background:#e0eaff;
    color:#1d4ed8;
    border-radius:10px;
    padding:11px 15px;
    font-size:13px;
    font-weight:700;
}

.card {
    background:white;
    padding:25px;
    border:1px solid #e7edf5;
    border-radius:17px;
    box-shadow:0 8px 28px rgba(15,23,42,.05);
}

.search-row {
    display:flex;
    gap:10px;
    margin-bottom:23px;
}

.search-row input {
    flex:1;
    min-width:0;
    padding:13px 15px;
    border:1px solid #dbe3ee;
    border-radius:9px;
    font-size:14px;
    outline:none;
}

.search-row input:focus {
    border-color:#2563eb;
    box-shadow:0 0 0 3px rgba(37,99,235,.1);
}

.btn, .search-row button {
    display:inline-flex;
    align-items:center;
    justify-content:center;
    padding:12px 17px;
    border:0;
    border-radius:9px;
    background:#2563eb;
    color:white;
    font-size:13px;
    font-weight:700;
    text-decoration:none;
    cursor:pointer;
    white-space:nowrap;
}

.btn:hover, .search-row button:hover { background:#1d4ed8; }

.btn.secondary {
    color:#334155;
    background:white;
    border:1px solid #dbe3ee;
}

.btn.secondary:hover { background:#f1f5f9; }

.table-heading {
    display:flex;
    justify-content:space-between;
    align-items:center;
    flex-wrap:wrap;
    gap:12px;
    margin-bottom:17px;
}

.table-heading h2 { font-size:18px; }
.table-heading span { font-size:13px; color:#64748b; }

.table-wrap {
    width:100%;
    overflow-x:auto;
    border:1px solid #e8edf4;
    border-radius:12px;
}

.product-table {
    width:100%;
    min-width:850px;
    border-collapse:collapse;
}

.product-table th {
    background:#f8fafc;
    color:#64748b;
    text-transform:uppercase;
    letter-spacing:.6px;
    font-size:11px;
    text-align:left;
    padding:16px;
    border-bottom:1px solid #e2e8f0;
    white-space:nowrap;
}

.product-table td {
    padding:16px;
    border-bottom:1px solid #edf1f6;
    font-size:13px;
    color:#334155;
    white-space:nowrap;
}

.product-table tbody tr:last-child td { border-bottom:none; }
.product-table tbody tr:hover { background:#f8fbff; }

.product-code {
    display:inline-block;
    padding:5px 8px;
    border-radius:6px;
    background:#f1f5f9;
    color:#475569;
    font-size:12px;
    font-weight:650;
}

.product-name { font-weight:700; color:#172033 !important; }
.price { color:#15803d !important; font-weight:750; }

.badge {
    display:inline-block;
    padding:5px 8px;
    margin-left:5px;
    border-radius:6px;
    font-size:11px;
    font-weight:700;
}

.badge.low { background:#fef3c7; color:#92400e; }
.badge.out { background:#fee2e2; color:#b91c1c; }
.badge.available { background:#dcfce7; color:#166534; }

.buy-btn {
    display:inline-block;
    padding:8px 13px;
    border-radius:7px;
    background:#2563eb;
    color:white;
    text-decoration:none;
    font-size:12px;
    font-weight:700;
}

.buy-btn:hover { background:#1d4ed8; }

.disabled {
    display:inline-block;
    padding:8px 12px;
    border-radius:7px;
    background:#f1f5f9;
    color:#94a3b8;
    font-size:12px;
    font-weight:700;
}

.notice {
    padding:30px 20px;
    text-align:center;
    border:1px dashed #cbd5e1;
    border-radius:12px;
    background:#f8fafc;
    color:#64748b;
    line-height:1.8;
}

.notice strong {
    display:block;
    margin-bottom:5px;
    color:#334155;
    font-size:17px;
}

.menu {
    display:flex;
    gap:12px;
    flex-wrap:wrap;
    margin-top:23px;
}

.footer { text-align:center; color:#94a3b8; font-size:12px; padding:25px 10px; }

@media(max-width:650px) {
    .brand { font-size:18px; }
    .heading h1 { font-size:24px; }
    .card { padding:16px; }
    .search-row { flex-direction:column; }
    .search-row button { width:100%; }
    .menu .btn { flex:1; }
}
</style>
</head>

<body>

<div class="topbar">
    <div class="brand">Online Electronic Store</div>
    <small>Customer Portal / Product Catalogue</small>
</div>

<%
CustomerBean cb = (CustomerBean) session.getAttribute("cbean");
if (cb == null) {
    response.sendRedirect("CustomerLogin.html");
    return;
}
%>

<div class="page">

    <div class="heading">
        <div>
            <h1>Product Catalogue</h1>
            <p>Hello <strong><%= Html.esc(cb.getfName()) %></strong>,
            browse products and check current availability.</p>
        </div>
        <div class="customer-tag">Welcome, <%= Html.esc(cb.getfName()) %></div>
    </div>

    <div class="card">

        <form class="search-row" action="viewproducts" method="get">
            <input type="text" name="q"
                placeholder="Search by product name, company or code"
                value="<%= Html.esc(request.getAttribute("q")) %>">
            <button type="submit">Search Products</button>
        </form>

        <%
        @SuppressWarnings("unchecked")
        ArrayList<ProductBean> al =
            (ArrayList<ProductBean>) request.getAttribute("CustView");

        if (al == null) {
            response.sendRedirect("viewproducts");
            return;
        }

        if (al.size() == 0) {
        %>

        <div class="notice">
            <strong>No products found</strong>
            Try another search term or check back later.
        </div>

        <%
        } else {
        %>

        <div class="table-heading">
            <h2>Available Products</h2>
            <span><%= al.size() %> product record(s)</span>
        </div>

        <div class="table-wrap">
            <table class="product-table">
                <thead>
                    <tr>
                        <th>Product Code</th>
                        <th>Product Name</th>
                        <th>Company</th>
                        <th>Price</th>
                        <th>Availability</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                <%
                Iterator<ProductBean> i = al.iterator();
                while (i.hasNext()) {
                    ProductBean pb = i.next();
                    int stock = ProductUtil.qty(pb.getPqty());
                %>

                    <tr>
                        <td>
                            <span class="product-code"><%= Html.esc(pb.getPcode()) %></span>
                        </td>

                        <td class="product-name">
                            <%= Html.esc(pb.getPname()) %>
                        </td>

                        <td><%= Html.esc(pb.getPcompany()) %></td>

                        <td class="price">
                            ₹<%= Html.esc(pb.getPrice()) %>
                        </td>

                        <td>
                            <%= Html.esc(pb.getPqty()) %> units

                            <% if (stock <= 0) { %>
                                <span class="badge out">Out of stock</span>
                            <% } else if (stock <= ProductUtil.LOW_STOCK) { %>
                                <span class="badge low">Low stock</span>
                            <% } else { %>
                                <span class="badge available">Available</span>
                            <% } %>
                        </td>

                        <td>
                            <% if (stock <= 0) { %>
                                <span class="disabled">Unavailable</span>
                            <% } else { %>
                                <a class="buy-btn"
                                   href="buy?pcode=<%= java.net.URLEncoder.encode(pb.getPcode(), "UTF-8") %>">
                                   Buy Now
                                </a>
                            <% } %>
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
            <a class="btn" href="CustomerHome.jsp">Home</a>
            <a class="btn secondary" href="myorders">My Orders</a>
            <a class="btn secondary" href="logout">Logout</a>
        </div>

    </div>
</div>

<div class="footer">Online Electronic Store &copy; 2026</div>

</body>
</html>
