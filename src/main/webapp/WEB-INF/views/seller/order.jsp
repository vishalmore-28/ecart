<c:set var="title" value="Order ${o.orderNo}"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/seller/_nav.jspf" %>
<div>
  <div class="sec-head"><h1 class="page-title">Order #<c:out value="${o.orderNo}"/></h1><span class="st ${o.status}">${o.status}</span></div>
  <div class="cols">
    <div>
      <section class="glass form-card"><h2>My items &amp; GST</h2>
        <div class="table-wrap"><table>
          <tr><th>Item</th><th>Qty</th><th>Taxable value</th><th>GST %</th>
            <c:choose><c:when test="${o.sameState}"><th>CGST</th><th>SGST</th></c:when><c:otherwise><th>IGST</th></c:otherwise></c:choose><th>Total</th></tr>
          <c:forEach var="i" items="${o.items}">
            <tr class="${i.itemStatus == 'CANCELLED' ? 'off-row' : ''}">
              <td><c:out value="${i.name}"/><div class="muted small">
                <c:if test="${not empty i.hsnCode}">HSN <c:out value="${i.hsnCode}"/> &middot; </c:if><span class="st ${i.itemStatus}">${i.itemStatus}</span>
                <c:if test="${not empty i.trackingNo}"> &middot; <c:out value="${i.courier}"/> <c:out value="${i.trackingNo}"/></c:if></div></td>
              <td>${i.qty}</td>
              <td><e:money2 v="${i.taxable}"/></td>
              <td><fmt:formatNumber value="${i.gstPercent}" maxFractionDigits="1"/>%</td>
              <c:choose>
                <c:when test="${o.sameState}"><td><e:money2 v="${i.tax div 2}"/></td><td><e:money2 v="${i.tax div 2}"/></td></c:when>
                <c:otherwise><td><e:money2 v="${i.tax}"/></td></c:otherwise>
              </c:choose>
              <td><e:money2 v="${i.taxable + i.tax}"/></td></tr>
          </c:forEach>
        </table></div>
        <dl class="totals"><dt>Taxable value</dt><dd><e:money2 v="${o.mineTaxable}"/></dd><dt>GST</dt><dd><e:money2 v="${o.mineTax}"/></dd><dt class="grand">My total</dt><dd class="grand"><e:money2 v="${o.mineTotal}"/></dd></dl>
        <p class="muted small">${o.sameState ? 'Seller and delivery state are the same: GST is split into CGST + SGST.' : 'Delivery is to another state: GST is charged as IGST.'} Shipping and coupon discounts are handled by ecart.</p>
      </section>
      <section class="glass form-card"><h2>Timeline</h2>
        <ol class="timeline"><c:forEach var="ev" items="${o.events}"><li class="${ev.status}"><strong>${ev.status}</strong> <span class="muted small"><fmt:formatDate value="${ev.createdAt}" pattern="d MMM, h:mm a"/></span><br><span class="muted small"><c:out value="${ev.note}"/></span></li></c:forEach></ol>
      </section>
    </div>
    <aside>
      <div class="glass summary"><h2>Deliver to</h2><p><strong><c:out value="${o.shipName}"/></strong><br><c:out value="${o.shipAddress}"/><br><span class="muted small">Phone: <c:out value="${o.shipPhone}"/></span></p>
        <p class="muted small">Payment: <strong>${o.paymentMethod}</strong> &middot; <strong>${o.paymentStatus}</strong></p></div>
      <c:if test="${o.status != 'CANCELLED' && o.status != 'DELIVERED' && stage != 'DELIVERED' && stage != 'CANCELLED'}">
      <div class="glass summary"><h2>Update my items</h2>
        <form method="post" action="${ctx}/seller/orders">
          <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${o.id}">
          <c:choose>
            <c:when test="${stage == 'PLACED'}"><input type="hidden" name="status" value="CONFIRMED"><button class="btn block" type="submit">Confirm order</button></c:when>
            <c:when test="${stage == 'CONFIRMED'}"><input type="hidden" name="status" value="SHIPPED">
              <label>Courier<input type="text" name="courier" required maxlength="60" placeholder="e.g. Delhivery"></label>
              <label>Tracking / AWB number<input type="text" name="tracking" required maxlength="40" pattern="[A-Za-z0-9\-/]{4,40}"></label>
              <button class="btn block" type="submit">Mark as shipped</button></c:when>
            <c:when test="${stage == 'SHIPPED'}"><input type="hidden" name="status" value="DELIVERED"><button class="btn block" type="submit">Mark as delivered</button></c:when>
          </c:choose>
        </form>
      </div></c:if>
      <p><a class="btn ghost block" href="${ctx}/seller/orders">&larr; All orders</a></p>
    </aside>
  </div>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
