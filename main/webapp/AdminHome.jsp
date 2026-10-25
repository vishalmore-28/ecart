
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.Bean.AdminBean"%>
<%@ page import="com.Util.Html"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="theme-color" content="#101d35">
<title>Admin Dashboard | eCart</title>
<link rel="stylesheet" href="style.css">

<style>
/* Admin dashboard design only */
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
    padding: 20px 5%;
    color: white;
    background: linear-gradient(120deg, #101d35, #233e70);
    box-shadow: 0 5px 20px rgba(16, 29, 53, .15);
}

.brand {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 21px;
    font-weight: 800;
    letter-spacing: -.5px;
}

.brand-icon {
    display: grid;
    place-items: center;
    width: 42px;
    height: 42px;
    border-radius: 13px;
    background: #4168f5;
    color: white;
    font-size: 25px;
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
    border-radius: 30px;
    color: #e4ebfa;
    font-size: 12px;
    font-weight: 700;
}

.dashboard {
    max-width: 1200px;
    margin: auto;
    padding: 40px 22px;
}

.welcome {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
    margin-bottom: 30px;
}

.eyebrow {
    margin: 0 0 8px;
    color: #4168e8;
    font-size: 11px;
    font-weight: 800;
    letter-spacing: 2px;
    text-transform: uppercase;
}

.welcome h1 {
    margin: 0;
    color: #17243d;
    font-size: clamp(27px, 4vw, 38px);
    letter-spacing: -1px;
}

.welcome p {
    margin: 10px 0 0;
    color: #78849a;
    font-size: 14px;
}

.overview-label {
    padding: 11px 15px;
    border: 1px solid #e0e6f0;
    border-radius: 12px;
    background: white;
    color: #59677e;
    font-size: 12px;
    font-weight: 700;
    white-space: nowrap;
}

.stats {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 17px;
    margin-bottom: 35px;
}

.stat {
    position: relative;
    overflow: hidden;
    min-height: 130px;
    padding: 22px;
    border: 1px solid #e5eaf3;
    border-radius: 18px;
    background: white;
    box-shadow: 0 8px 25px rgba(23, 39, 69, .04);
}

.stat::after {
    position: absolute;
    top: -28px;
    right: -25px;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    background: var(--tint);
    content: "";
}

.stat-top {
    position: relative;
    z-index: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 10px;
    color: #6c7890;
    font-size: 13px;
    font-weight: 700;
}

.stat-icon {
    display: grid;
    place-items: center;
    width: 38px;
    height: 38px;
    border-radius: 12px;
    background: var(--tint);
    color: var(--accent);
    font-size: 20px;
}

.num {
    position: relative;
    z-index: 1;
    margin-top: 15px;
    color: #17243d;
    font-size: 32px;
    font-weight: 800;
    letter-spacing: -1px;
    overflow-wrap: anywhere;
}

.lbl {
    position: relative;
    z-index: 1;
    margin-top: 7px;
    color: #8a95a7;
    font-size: 11px;
}

.stat.blue {
    --accent: #315efb;
    --tint: #edf1ff;
}

.stat.green {
    --accent: #07836f;
    --tint: #e2f8f2;
}

.stat.orange {
    --accent: #b87508;
    --tint: #fff3d9;
}

.stat.red {
    --accent: #c03936;
    --tint: #ffebea;
}

.section-heading {
    margin: 0 0 15px;
    color: #25324b;
    font-size: 18px;
    font-weight: 800;
}

.menu.row {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 15px;
}

.menu.row .btn {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: center;
    min-height: 140px;
    margin: 0;
    padding: 20px;
    border: 1px solid #e3e9f3;
    border-radius: 17px;
    background: white;
    color: #1c2942;
    text-align: left;
    text-decoration: none;
    box-shadow: 0 7px 24px rgba(23, 39, 69, .035);
    transition: transform .2s, box-shadow .2s, border-color .2s;
}

.menu.row .btn:hover {
    transform: translateY(-3px);
    border-color: #c9d4ff;
    background: white;
    color: #315efb;
    box-shadow: 0 13px 28px rgba(32, 55, 105, .1);
}

.action-icon {
    display: grid;
    place-items: center;
    width: 42px;
    height: 42px;
    margin-bottom: 15px;
    border-radius: 12px;
    background: #edf1ff;
    color: #315efb;
    font-size: 21px;
}

.action-title {
    font-size: 14px;
    font-weight: 800;
}

.action-desc {
    margin-top: 7px;
    color: #8490a4;
    font-size: 11px;
    font-weight: 400;
    line-height: 1.5;
}

.menu.row .btn.secondary {
    border-color: #f2d5d5;
    background: #fff;
    color: #b52c2c;
}

.menu.row .btn.secondary .action-icon {
    background: #ffebea;
    color: #b52c2c;
}

.menu.row .btn.secondary:hover {
    background: #fff6f5;
}

.dashboard-footer {
    display: flex;
    justify-content: space-between;
    gap: 15px;
    margin-top: 28px;
    padding: 18px 20px;
    border: 1px solid #e3e9f3;
    border-radius: 14px;
    background: white;
    color: #8792a5;
    font-size: 12px;
}

.dashboard-footer strong {
    color: #33415a;
}

@media (max-width: 850px) {
    .stats {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .menu.row {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}

@media (max-width: 520px) {
    .admin-header {
        padding: 15px;
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

    .dashboard {
        padding: 26px 13px;
    }

    .welcome {
        align-items: flex-start;
        flex-direction: column;
    }

    .stats {
        gap: 10px;
    }

    .stat {
        min-height: 120px;
        padding: 14px;
    }

    .stat-top {
        font-size: 11px;
    }

    .stat-icon {
        width: 31px;
        height: 31px;
        flex-shrink: 0;
    }

    .num {
        font-size: 25px;
    }

    .menu.row {
        gap: 10px;
    }

    .menu.row .btn {
        min-height: 135px;
        padding: 14px;
    }

    .dashboard-footer {
        flex-direction: column;
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

if (request.getAttribute("statProducts") == null) {
    response.sendRedirect("adminhome");
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

<main class="dashboard">

    <section class="welcome">
        <div>
            <div class="eyebrow">Overview / Dashboard</div>
            <h1>Welcome back, <%=Html.esc(ab.getAfname())%>!</h1>
            <p>Here’s what’s happening with your store today.</p>
        </div>

        <div class="overview-label">STORE OVERVIEW &nbsp; · &nbsp; LIVE DATA</div>
    </section>

    <section class="stats">

        <div class="stat blue">
            <div class="stat-top">
                <span>Total Products</span>
                <span class="stat-icon">▦</span>
            </div>
            <div class="num"><%=request.getAttribute("statProducts")%></div>
            <div class="lbl">Products in your catalogue</div>
        </div>

        <div class="stat green">
            <div class="stat-top">
                <span>Units in Stock</span>
                <span class="stat-icon">▤</span>
            </div>
            <div class="num"><%=request.getAttribute("statUnits")%></div>
            <div class="lbl">Combined available quantity</div>
        </div>

        <div class="stat orange">
            <div class="stat-top">
                <span>Low Stock</span>
                <span class="stat-icon">◷</span>
            </div>
            <div class="num"><%=request.getAttribute("statLow")%></div>
            <div class="lbl">Products to review soon</div>
        </div>

        <div class="stat red">
            <div class="stat-top">
                <span>Out of Stock</span>
                <span class="stat-icon">!</span>
            </div>
            <div class="num"><%=request.getAttribute("statOut")%></div>
            <div class="lbl">Products needing replenishment</div>
        </div>

        <div class="stat blue">
            <div class="stat-top">
                <span>Total Orders</span>
                <span class="stat-icon">▣</span>
            </div>
            <div class="num"><%=request.getAttribute("statOrders")%></div>
            <div class="lbl">Orders recorded in your store</div>
        </div>

        <div class="stat green">
            <div class="stat-top">
                <span>Total Sales</span>
                <span class="stat-icon">₹</span>
            </div>
            <div class="num" style="font-size:clamp(23px, 2.7vw, 31px);">
                ₹ <%=request.getAttribute("statSales")%>
            </div>
            <div class="lbl">Sales value from recorded orders</div>
        </div>

    </section>

    <h2 class="section-heading">Quick Actions</h2>

    <nav class="menu row">

        <a class="btn" href="addProduct.html">
            <span class="action-icon">＋</span>
            <span class="action-title">Add Product</span>
            <span class="action-desc">Add new inventory to your catalogue.</span>
        </a>

        <a class="btn" href="view">
            <span class="action-icon">▤</span>
            <span class="action-title">View Products</span>
            <span class="action-desc">Review products and manage stock.</span>
        </a>

        <a class="btn" href="allorders">
            <span class="action-icon">▣</span>
            <span class="action-title">All Orders</span>
            <span class="action-desc">Review customer purchases and order records.</span>
        </a>

        <a class="btn secondary" href="logout">
            <span class="action-icon">↪</span>
            <span class="action-title">Logout</span>
            <span class="action-desc">Securely leave your admin session.</span>
        </a>

    </nav>

    <footer class="dashboard-footer">
        <span><strong>eCart Admin</strong> &nbsp;·&nbsp; Inventory and order management</span>
        <span>Administrator Dashboard</span>
    </footer>

</main>

</body>
</html>
