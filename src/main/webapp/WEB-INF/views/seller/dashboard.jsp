<c:set var="title" value="Seller dashboard"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/seller/_nav.jspf" %>
<div>
  <div class="sec-head"><h1 class="page-title"><c:out value="${shop.shopName}"/></h1><span class="st ${shop.chip}">${shop.status}</span></div>

  <c:if test="${shop.pending}"><section class="glass form-card"><h2>Application under review</h2>
    <p>Thanks for registering. The ecart team is checking your GSTIN, PAN and business details. You can add products and receive orders as soon as the shop is approved.</p>
    <p class="muted small">Submitted <fmt:formatDate value="${shop.createdAt}" pattern="d MMM yyyy, h:mm a"/>. Check your details on the <a href="${ctx}/seller/profile">shop profile</a>.</p></section></c:if>
  <c:if test="${shop.status == 'REJECTED'}"><section class="glass form-card"><h2>Application not approved</h2>
    <p><c:out value="${empty shop.reviewNote ? 'Please contact ecart support.' : shop.reviewNote}"/></p>
    <p class="muted small">Contact ecart support to correct your details and re-open the application.</p></section></c:if>
  <c:if test="${shop.status == 'SUSPENDED'}"><section class="glass form-card"><h2>Shop suspended</h2>
    <p>Your products are hidden from the store and you cannot update orders.</p>
    <p><c:out value="${shop.reviewNote}"/></p></section></c:if>

  <c:if test="${shop.approved}">
  <div class="stats">
    <div class="glass stat"><span>Sales (excl. GST)</span><strong><e:money v="${stats.sales}"/></strong></div>
    <div class="glass stat"><span>GST collected</span><strong><e:money v="${stats.gst}"/></strong></div>
    <div class="glass stat"><span>Orders</span><strong>${stats.orders}</strong></div>
    <div class="glass stat"><span>To ship</span><strong>${stats.toShip}</strong></div>
    <div class="glass stat"><span>Live products</span><strong>${counts[1]} / ${counts[0]}</strong></div>
  </div>
  <div class="cols even">
    <section class="glass form-card"><h2>My items by status</h2>
      <c:forEach var="s" items="${stats.byStatus}"><div class="sum-line"><span class="st ${s.key}">${s.key}</span><strong>${s.value}</strong></div></c:forEach>
    </section>
    <section class="glass form-card"><h2>Low stock (5 or fewer)</h2>
      <c:if test="${empty lowStock}"><p class="muted">All your products are well stocked.</p></c:if>
      <c:forEach var="p" items="${lowStock}"><div class="sum-line"><a href="${ctx}/seller/products?edit=${p.id}"><c:out value="${p.name}"/></a><strong class="${p.stock == 0 ? 'red' : ''}">${p.stock} left</strong></div></c:forEach>
    </section>
  </div>
  <section class="glass form-card"><h2>Recent orders</h2>
    <c:if test="${empty recent}"><p class="muted">No orders yet. <a href="${ctx}/seller/products?new=1">Add your first product</a>.</p></c:if>
    <c:if test="${not empty recent}"><div class="table-wrap"><table>
      <tr><th>Order</th><th>Date</th><th>My value</th><th>Order status</th></tr>
      <c:forEach var="o" items="${recent}" end="5">
        <tr><td><a href="${ctx}/seller/orders?id=${o.id}">#<c:out value="${o.orderNo}"/></a></td><td><fmt:formatDate value="${o.createdAt}" pattern="d MMM, h:mm a"/></td>
          <td><e:money2 v="${o.mineTotal}"/></td><td><span class="st ${o.status}">${o.status}</span></td></tr>
      </c:forEach>
    </table></div></c:if>
  </section>
  </c:if>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
