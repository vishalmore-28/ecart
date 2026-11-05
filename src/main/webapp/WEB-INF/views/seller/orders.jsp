<c:set var="title" value="Seller orders"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/seller/_nav.jspf" %>
<div>
  <h1 class="page-title">Orders</h1>
  <p class="muted small">You only see the items you sold. Filter by the status of your items.</p>
  <nav class="tabs">
    <a class="${empty status ? 'cur' : ''}" href="${ctx}/seller/orders">All</a>
    <c:forTokens var="s" items="PLACED,CONFIRMED,SHIPPED,DELIVERED" delims=","><a class="${status == s ? 'cur' : ''}" href="${ctx}/seller/orders?status=${s}">${s}</a></c:forTokens>
  </nav>
  <section class="glass"><div class="table-wrap"><table>
    <tr><th>Order</th><th>Date</th><th>Ship to</th><th>My items</th><th>My value (incl. GST)</th><th>My status</th></tr>
    <c:forEach var="o" items="${orders}">
      <tr><td><a href="${ctx}/seller/orders?id=${o.id}">#<c:out value="${o.orderNo}"/></a></td>
        <td><fmt:formatDate value="${o.createdAt}" pattern="d MMM yyyy, h:mm a"/></td>
        <td><c:out value="${o.shipName}"/></td>
        <td>${o.itemCount}</td>
        <td><e:money2 v="${o.mineTotal}"/></td>
        <td><c:forEach var="i" items="${o.items}" end="0"><span class="st ${i.itemStatus}">${i.itemStatus}</span></c:forEach></td></tr>
    </c:forEach>
    <c:if test="${empty orders}"><tr><td colspan="6" class="muted">No orders yet.</td></tr></c:if>
  </table></div></section>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
