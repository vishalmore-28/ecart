<c:set var="title" value="Order ${o.orderNo}"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/admin/_nav.jspf" %>
<div>
  <div class="sec-head"><h1 class="page-title">Order #<c:out value="${o.orderNo}"/></h1><span class="st ${o.status}">${o.status}</span></div>
  <div class="cols">
    <div>
      <section class="glass form-card"><h2>Items</h2>
        <c:forEach var="i" items="${o.items}"><div class="sum-line"><span><c:out value="${i.name}"/> &times; ${i.qty}<br><span class="muted small">Sold by <c:out value="${empty i.sellerName ? 'ecart' : i.sellerName}"/> &middot; ${empty i.sellerName ? o.status : i.itemStatus}<c:if test="${not empty i.trackingNo}"> &middot; <c:out value="${i.courier}"/> <c:out value="${i.trackingNo}"/></c:if></span></span><span><e:money v="${i.lineTotal}"/></span></div></c:forEach>
        <dl class="totals"><dt>Subtotal</dt><dd><e:money v="${o.subtotal}"/></dd><dt>Discount</dt><dd><e:money v="${o.discount}"/></dd><dt>GST</dt><dd><e:money v="${o.tax}"/></dd><dt>Shipping</dt><dd><e:money v="${o.shipping}"/></dd><dt class="grand">Total</dt><dd class="grand"><e:money v="${o.total}"/></dd></dl>
      </section>
      <section class="glass form-card"><h2>Timeline</h2>
        <ol class="timeline"><c:forEach var="ev" items="${o.events}"><li class="${ev.status}"><strong>${ev.status}</strong> <span class="muted small"><fmt:formatDate value="${ev.createdAt}" pattern="d MMM, h:mm a"/></span><br><span class="muted small"><c:out value="${ev.note}"/></span></li></c:forEach></ol>
      </section>
    </div>
    <aside>
      <div class="glass summary"><h2>Customer</h2><p><strong><c:out value="${o.userName}"/></strong><br><c:out value="${o.shipName}"/><br><c:out value="${o.shipAddress}"/><br><span class="muted small">Phone: <c:out value="${o.shipPhone}"/></span></p>
        <p class="muted small">Payment: <strong>${o.paymentMethod}</strong> &middot; <strong>${o.paymentStatus}</strong><c:if test="${not empty o.txnRef}"><br>Ref: <c:out value="${o.txnRef}"/></c:if></p></div>
      <c:if test="${o.status != 'DELIVERED' && o.status != 'CANCELLED'}">
      <div class="glass summary"><h2>Update status</h2>
        <form method="post" action="${ctx}/admin/orders">
          <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${o.id}">
          <label>New status
            <select name="status">
              <c:if test="${o.status == 'PLACED'}"><option value="CONFIRMED">CONFIRMED</option></c:if>
              <c:if test="${o.status == 'CONFIRMED'}"><option value="SHIPPED">SHIPPED</option></c:if>
              <c:if test="${o.status == 'SHIPPED'}"><option value="DELIVERED">DELIVERED</option></c:if>
              <c:if test="${o.status == 'PLACED' || o.status == 'CONFIRMED'}"><option value="CANCELLED">CANCELLED</option></c:if>
            </select>
          </label>
          <label>Note (optional)<input type="text" name="note" maxlength="200" placeholder="e.g. Courier tracking no."></label>
          <button class="btn block" type="submit">Update</button>
        </form>
      </div></c:if>
    </aside>
  </div>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
