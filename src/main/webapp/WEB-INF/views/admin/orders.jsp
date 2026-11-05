<c:set var="title" value="Admin orders"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/admin/_nav.jspf" %>
<div>
  <h1 class="page-title">Orders</h1>
  <nav class="tabs">
    <a class="${empty status ? 'cur' : ''}" href="${ctx}/admin/orders">All</a>
    <c:forTokens var="s" items="PLACED,CONFIRMED,SHIPPED,DELIVERED,CANCELLED" delims=","><a class="${status == s ? 'cur' : ''}" href="${ctx}/admin/orders?status=${s}">${s}</a></c:forTokens>
  </nav>
  <section class="glass"><div class="table-wrap"><table>
    <tr><th>Order</th><th>Customer</th><th>Date</th><th>Payment</th><th>Total</th><th>Status</th></tr>
    <c:forEach var="o" items="${orders}">
      <tr><td><a href="${ctx}/admin/orders?id=${o.id}">#<c:out value="${o.orderNo}"/></a></td><td><c:out value="${o.userName}"/></td>
        <td><fmt:formatDate value="${o.createdAt}" pattern="d MMM yyyy, h:mm a"/></td><td>${o.paymentMethod} &middot; ${o.paymentStatus}</td>
        <td><e:money v="${o.total}"/></td><td><span class="st ${o.status}">${o.status}</span></td></tr>
    </c:forEach>
    <c:if test="${empty orders}"><tr><td colspan="6" class="muted">No orders.</td></tr></c:if>
  </table></div></section>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
