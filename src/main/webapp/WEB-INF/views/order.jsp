<c:set var="title" value="Order ${o.orderNo}"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<c:if test="${placed}"><div class="glass success"><h2>&#10003; Thank you! Your order is placed.</h2><p>We've sent it to our warehouse. You can track it below.</p></div></c:if>
<div class="sec-head"><h1 class="page-title">Order #<c:out value="${o.orderNo}"/></h1><span class="st ${o.status}">${o.status}</span></div>
<div class="cols">
  <div>
    <section class="glass form-card">
      <h2>Items</h2>
      <c:forEach var="i" items="${o.items}">
        <div class="sum-line"><span><a href="${ctx}/product?id=${i.productId}"><c:out value="${i.name}"/></a> &times; ${i.qty}<br><span class="muted small">Sold by <c:out value="${empty i.sellerName ? 'ecart' : i.sellerName}"/> &middot; ${empty i.sellerName ? o.status : i.itemStatus}<c:if test="${not empty i.trackingNo}"> &middot; <c:out value="${i.courier}"/> <c:out value="${i.trackingNo}"/></c:if></span></span><span><e:money v="${i.lineTotal}"/></span></div>
      </c:forEach>
    </section>
    <section class="glass form-card">
      <h2>Tracking</h2>
      <ol class="timeline">
        <c:forEach var="ev" items="${o.events}">
          <li class="${ev.status}"><strong>${ev.status}</strong> <span class="muted small"><fmt:formatDate value="${ev.createdAt}" pattern="d MMM, h:mm a"/></span><br><span class="muted small"><c:out value="${ev.note}"/></span></li>
        </c:forEach>
      </ol>
    </section>
  </div>
  <aside>
    <div class="glass summary">
      <h2>Payment</h2>
      <dl class="totals">
        <dt>Subtotal</dt><dd><e:money v="${o.subtotal}"/></dd>
        <c:if test="${o.discount.signum() > 0}"><dt>Discount<c:if test="${not empty o.couponCode}"> (<c:out value="${o.couponCode}"/>)</c:if></dt><dd class="green">&minus; <e:money v="${o.discount}"/></dd></c:if>
        <dt>GST</dt><dd><e:money v="${o.tax}"/></dd>
        <dt>Shipping</dt><dd><c:choose><c:when test="${o.shipping.signum() == 0}">Free</c:when><c:otherwise><e:money v="${o.shipping}"/></c:otherwise></c:choose></dd>
        <dt class="grand">Total</dt><dd class="grand"><e:money v="${o.total}"/></dd>
      </dl>
      <p class="muted small">Method: <strong>${o.paymentMethod}</strong> &middot; Status: <strong>${o.paymentStatus}</strong>
        <c:if test="${not empty o.txnRef}"><br>Ref: <c:out value="${o.txnRef}"/></c:if>
        <c:if test="${not empty o.cardLast4}"><br>Card ending <c:out value="${o.cardLast4}"/></c:if></p>
    </div>
    <div class="glass summary">
      <h2>Delivering to</h2>
      <p><strong><c:out value="${o.shipName}"/></strong><br><c:out value="${o.shipAddress}"/><br><span class="muted small">Phone: <c:out value="${o.shipPhone}"/></span></p>
    </div>
    <c:if test="${o.cancellable}">
      <form method="post" action="${ctx}/order" data-confirm="Cancel this order?">
        <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="cancel"><input type="hidden" name="id" value="${o.id}">
        <button class="btn danger block" type="submit">Cancel order</button>
      </form>
    </c:if>
  </aside>
</div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
