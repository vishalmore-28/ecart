<c:set var="title" value="Admin dashboard"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/admin/_nav.jspf" %>
<div>
  <h1 class="page-title">Dashboard</h1>
  <c:if test="${pendingSellers > 0}"><div class="flash err">${pendingSellers} seller application<c:if test="${pendingSellers != 1}">s are</c:if><c:if test="${pendingSellers == 1}"> is</c:if> waiting for review. <a href="${ctx}/admin/sellers?status=PENDING">Review now &rarr;</a></div></c:if>
  <div class="stats">
    <div class="glass stat"><span>Revenue</span><strong><e:money v="${stats.revenue}"/></strong></div>
    <div class="glass stat"><span>Orders</span><strong>${stats.orders}</strong></div>
    <div class="glass stat"><span>Today</span><strong>${stats.today}</strong></div>
    <div class="glass stat"><span>Customers</span><strong>${stats.customers}</strong></div>
  </div>
  <div class="cols even">
    <section class="glass form-card"><h2>Orders by status</h2>
      <c:forEach var="s" items="${stats.byStatus}"><div class="sum-line"><span class="st ${s.key}">${s.key}</span><strong>${s.value}</strong></div></c:forEach>
    </section>
    <section class="glass form-card"><h2>Best sellers</h2>
      <c:if test="${empty stats.top}"><p class="muted">No sales yet.</p></c:if>
      <c:forEach var="t" items="${stats.top}"><div class="sum-line"><span><c:out value="${t[0]}"/></span><strong>${t[1]} sold</strong></div></c:forEach>
    </section>
  </div>
  <section class="glass form-card"><h2>Low stock (5 or fewer)</h2>
    <c:if test="${empty lowStock}"><p class="muted">All products are well stocked.</p></c:if>
    <c:forEach var="p" items="${lowStock}"><div class="sum-line"><a href="${ctx}/admin/products?edit=${p.id}"><c:out value="${p.name}"/></a><strong class="${p.stock == 0 ? 'red' : ''}">${p.stock} left</strong></div></c:forEach>
  </section>
  <section class="glass form-card"><h2>Recent orders</h2>
    <div class="table-wrap"><table>
      <tr><th>Order</th><th>Customer</th><th>Date</th><th>Total</th><th>Status</th></tr>
      <c:forEach var="o" items="${recent}" end="7">
        <tr><td><a href="${ctx}/admin/orders?id=${o.id}">#<c:out value="${o.orderNo}"/></a></td><td><c:out value="${o.userName}"/></td>
          <td><fmt:formatDate value="${o.createdAt}" pattern="d MMM, h:mm a"/></td><td><e:money v="${o.total}"/></td><td><span class="st ${o.status}">${o.status}</span></td></tr>
      </c:forEach>
    </table></div>
  </section>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
