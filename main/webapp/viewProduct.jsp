
<%@ page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.Bean.ProductBean"%>
<%@ page import="com.Bean.AdminBean"%>
<%@ page import="com.Util.Html"%>
<%@ page import="com.Util.ProductUtil"%>
<%@ page import="java.util.ArrayList"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="theme-color" content="#101d35">
<title>Manage Products | eCart Admin</title>
<link rel="stylesheet" href="style.css">

<style>
body {
    margin: 0;
    background: #f3f6fb;
    color: #19243a;
    font-family: "Segoe UI", Arial, sans-serif;
}

.admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 15px;
    padding: 18px 5%;
    color: white;
    background: linear-gradient(120deg, #101d35, #233e70);
    box-shadow: 0 5px 20px rgba(16,29,53,.15);
}

.brand {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 20px;
    font-weight: 800;
}

.brand-icon {
    display: grid;
    place-items: center;
    width: 40px;
    height: 40px;
    border-radius: 13px;
    background: #4168f5;
    color: white;
    font-size: 24px;
}

.brand small {
    display: block;
    margin-top: 3px;
    color: #c4d1ec;
    font-size: 10px;
    letter-spacing: 2px;
}

.admin-label {
    padding: 8px 13px;
    border: 1px solid rgba(255,255,255,.2);
    border-radius: 25px;
    color: #e4ebfa;
    font-size: 12px;
    font-weight: 700;
}

.products-page {
    max-width: 1250px;
    margin: auto;
    padding: 38px 22px 45px;
}

.page-heading {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    gap: 18px;
    margin-bottom: 26px;
}

.eyebrow {
    margin-bottom: 8px;
    color: #4168e8;
    font-size: 11px;
    font-weight: 800;
    letter-spacing: 2px;
    text-transform: uppercase;
}

.page-heading h1 {
    margin: 0;
    color: #17243d;
    font-size: clamp(28px, 4vw, 37px);
    letter-spacing: -1px;
}

.page-heading p {
    margin: 9px 0 0;
    color: #78849a;
    font-size: 14px;
}

.count-label {
    padding: 10px 14px;
    border: 1px solid #e0e6f0;
    border-radius: 12px;
    background: white;
    color: #59677e;
    font-size: 12px;
    font-weight: 700;
    white-space: nowrap;
}

.products-panel {
    padding: 24px;
    border: 1px solid #e5eaf3;
    border-radius: 19px;
    background: white;
    box-shadow: 0 8px 25px rgba(23,39,69,.04);
}

.panel-heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 15px;
    margin-bottom: 20px;
}

.panel-heading h2 {
    margin: 0;
    color: #25324b;
    font-size: 17px;
}

.panel-heading p {
    margin: 5px 0 0;
    color: #8490a4;
    font-size: 12px;
}

.search-row {
    display: flex;
    gap: 10px;
    width: min(100%, 470px);
    margin: 0;
}

.search-row input[type="text"] {
    flex: 1;
    min-width: 0;
    height: 44px;
    padding: 0 14px;
    border: 1px solid #dce3ef;
    border-radius: 10px;
    outline: none;
    background: #f9faff;
    font-size: 13px;
}

.search-row input[type="text"]:focus {
    border-color: #7891ff;
    box-shadow: 0 0 0 3px rgba(49,94,251,.1);
}

.search-row button {
    min-height: 44px;
    margin: 0;
    padding: 0 20px;
    border: 0;
    border-radius: 10px;
    background: #315efb;
    color: white;
    font-size: 13px;
    font-weight: 750;
    cursor: pointer;
}

.search-row button:hover {
    background: #2448d8;
}

.table-wrap {
    overflow-x: auto;
    border: 1px solid #e5eaf3;
    border-radius: 13px;
}

table.product-table {
    width: 100%;
    min-width: 760px;
    border-collapse: separate;
    border-spacing: 0;
}

table.product-table th {
    padding: 15px 16px;
    background: #f3f6fc;
    color: #66738b;
    font-size: 11px;
    font-weight: 800;
    letter-spacing: .7px;
    text-align: left;
    text-transform: uppercase;
    white-space: nowrap;
}

table.product-table td {
    padding: 16px;
    border-top: 1px solid #edf0f6;
    color: #46536b;
    font-size: 13px;
    vertical-align: middle;
}

table.product-table tbody tr:hover td {
    background: #f8faff;
}

.product-code {
    display: inline-block;
    padding: 5px 8px;
    border-radius: 7px;
    background: #edf1ff;
    color: #3152c5;
    font-size: 11px;
    font-weight: 800;
}

.product-name {
    color: #1e2b43;
    font-weight: 750;
}

.product-price {
    color: #17243d;
    font-weight: 800;
    white-space: nowrap;
}

.stock-number {
    margin-right: 4px;
    color: #344158;
    font-weight: 750;
}

.badge {
    display: inline-block;
    margin-top: 5px;
    padding: 4px 8px;
    border-radius: 20px;
    font-size: 10px;
    font-weight: 800;
    white-space: nowrap;
}

.badge.low {
    background: #fff3d9;
    color: #9a6500;
}

.badge.out {
    background: #ffebea;
    color: #b52c2c;
}

.action-links {
    display: flex;
    align-items: center;
    gap: 8px;
}

.action-links a,
.action-links button {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    min-height: 32px;
    margin: 0;
    padding: 6px 10px;
    border: 1px solid #dce5ff;
    border-radius: 8px;
    background: #f2f5ff;
    color: #3154d5;
    font-size: 11px;
    font-weight: 800;
    text-decoration: none;
    cursor: pointer;
}

.action-links a:hover,
.action-links button:hover {
    background: #e5ebff;
}

.action-links button.delete-link {
    border-color: #f4d9d7;
    background: #fff5f4;
    color: #b52c2c;
}

.action-links button.delete-link:hover {
    background: #ffe8e6;
}

.empty-state {
    padding: 45px 20px;
    color: #78849a;
    text-align: center;
}

.empty-icon {
    display: grid;
    place-items: center;
    width: 54px;
    height: 54px;
    margin: 0 auto 14px;
    border-radius: 16px;
    background: #edf1ff;
    color: #315efb;
    font-size: 24px;
}

.empty-state strong {
    display: block;
    margin-bottom: 6px;
    color: #25324b;
    font-size: 16px;
}

.empty-state p {
    margin: 0;
    font-size: 13px;
}

.notice {
    margin: 15px 0;
    padding: 15px;
    border-radius: 11px;
    background: #f0f4ff;
    color: #2543a7;
    text-align: center;
}

.bottom-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 11px;
    margin-top: 23px;
}

.bottom-actions a {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-height: 42px;
    padding: 10px 16px;
    border: 1px solid #315efb;
    border-radius: 10px;
    background: #315efb;
    color: white;
    font-size: 12px;
    font-weight: 750;
    text-decoration: none;
}

.bottom-actions a:hover {
    background: #2448d8;
}

.bottom-actions a.secondary {
    border-color: #dfe5ef;
    background: white;
    color: #43516a;
}

.bottom-actions a.secondary:hover {
    background: #f3f6fb;
}

.bottom-actions a.logout {
    margin-left: auto;
    border-color: #f2d5d5;
    background: #fff5f4;
    color: #b52c2c;
}

@media (max-width: 700px) {
    .admin-header {
        padding: 14px 16px;
    }

    .brand {
        font-size: 17px;
    }

    .brand-icon {
        width: 35px;
        height: 35px;
    }

    .admin-label {
        font-size: 10px;
    }

    .products-page {
        padding: 25px 12px 35px;
    }

    .page-heading {
        align-items: flex-start;
        flex-direction: column;
    }

    .products-panel {
        padding: 15px;
    }

    .panel-heading {
        align-items: flex-start;
        flex-direction: column;
    }

    .search-row {
        width: 100%;
    }

    .bottom-actions a.logout {
        margin-left: 0;
    }
}

@media (max-width: 420px) {
    .search-row {
        flex-direction: column;
    }

    .search-row button {
        min-height: 42px;
    }
}
</style>
</head>

<body>

<%
AdminBean ab = (AdminBean) session.getAttribute("adminbean");

if (ab == null) {
    response.sendRedirect("AdminLogin.html");
    return;
}

@SuppressWarnings("unchecked")
ArrayList<ProductBean> al =
    (ArrayList<ProductBean>) request.getAttribute("ProductView");

if (al == null) {
    response.sendRedirect("view");
    return;
}
%>

<header class="admin-header">
    <div class="brand">
        <span class="brand-icon">e</span>
        <span>
            eCart
            <small>STORE ADMINISTRATION</small>
        </span>
    </div>

    <div class="admin-label">● &nbsp; Admin Portal</div>
</header>

<main class="products-page">

    <section class="page-heading">
        <div>
            <div class="eyebrow">Inventory / Products</div>
            <h1>Product Management</h1>
            <p>View your catalogue, monitor stock, and manage product details.</p>
        </div>

        <div class="count-label">
            <%=al.size()%> product<%=al.size() == 1 ? "" : "s"%> found
        </div>
    </section>

    <section class="products-panel">

        <div class="panel-heading">
            <div>
                <h2>All Products</h2>
                <p>Search by product name, company, or product code.</p>
            </div>

            <form class="search-row" action="view" method="get">
                <input type="text"
                       name="q"
                       placeholder="Search products..."
                       value="<%=Html.esc(request.getAttribute("q"))%>">
                <button type="submit">Search</button>
            </form>
        </div>

        <% if (al.isEmpty()) { %>

            <div class="empty-state">
                <div class="empty-icon">▦</div>
                <strong>No products found</strong>
                <p>Try a different search or add a product to your catalogue.</p>
            </div>

        <% } else { %>

        <div class="table-wrap">
            <table class="product-table">
                <thead>
                    <tr>
                        <th>Product Code</th>
                        <th>Product Name</th>
                        <th>Company</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Actions</th>
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
                            <span class="product-code">
                                <%=Html.esc(pb.getPcode())%>
                            </span>
                        </td>

                        <td>
                            <span class="product-name">
                                <%=Html.esc(pb.getPname())%>
                            </span>
                        </td>

                        <td><%=Html.esc(pb.getPcompany())%></td>

                        <td class="product-price">
                            ₹ <%=Html.esc(pb.getPrice())%>
                        </td>

                        <td>
                            <span class="stock-number">
                                <%=Html.esc(pb.getPqty())%>
                            </span>

                            <% if (stock <= 0) { %>
                                <br><span class="badge out">Out of stock</span>
                            <% } else if (stock <= ProductUtil.LOW_STOCK) { %>
                                <br><span class="badge low">Low stock</span>
                            <% } %>
                        </td>

                        <td>
                            <div class="action-links">
                                <a href="edit?pcode=<%=java.net.URLEncoder.encode(pb.getPcode(), "UTF-8")%>">
                                    Edit
                                </a>

                                <form action="delete"
                                      method="post"
                                      style="display:inline"
                                      onsubmit="return confirm('Delete this product?');">

                                    <input type="hidden"
                                           name="pcode"
                                           value="<%=Html.esc(pb.getPcode())%>">

                                    <button type="submit" class="delete-link">
                                        Delete
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>

        <% } %>

    </section>

    <nav class="bottom-actions">
        <a href="adminhome">← Dashboard</a>
        <a href="addProduct.html">＋ Add Product</a>
        <a href="allorders" class="secondary">View All Orders</a>
        <a href="logout" class="logout">↪ Logout</a>
    </nav>

</main>
</body>
</html>
